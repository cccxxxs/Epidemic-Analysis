package com.silentselene.myapplication.PredictionSystem;

import android.text.format.DateUtils;

import java.util.Date;

public class PredictOneRecord {
    Date date;
    AccessRecord accessRecord;

    public PredictOneRecord(Date date, AccessRecord accessRecord) {
        this.date = date;
        this.accessRecord = accessRecord;
    }

    private float getSavePredict(Date date, AccessRecord accessRecord) {
        return 0;
    }

    private float getHighRiskPredict(Date date, AccessRecord accessRecord) {
        if (Math.abs(accessRecord.visitTime.getTime() - date.getTime()) < DateUtils.HOUR_IN_MILLIS) {
            float ret = 20;
            long day = (date.getTime() - accessRecord.startTime.getTime()) / DateUtils.DAY_IN_MILLIS;
            for (int i = 0; i < day; i++) {
                if (i <= 5) {
                    ret = ret * 1.1f;
                } else {
                    ret = ret * 0.9f;
                }
            }
            return ret;
        } else {
            return 0;
        }
    }

    private float getConfirmedPredict(Date date, AccessRecord accessRecord) {
        if (Math.abs(accessRecord.visitTime.getTime() - date.getTime()) < DateUtils.HOUR_IN_MILLIS) {
            float ret = 100;
            long day = (date.getTime() - accessRecord.startTime.getTime()) / DateUtils.DAY_IN_MILLIS;
            long totDay = Long.MAX_VALUE;
            if (accessRecord.endTime.getTime() != 0) {
                totDay = (accessRecord.endTime.getTime() - accessRecord.startTime.getTime()) / DateUtils.DAY_IN_MILLIS;
            }
            for (int i = 0; i < day; i++) {
                if (i <= 7) {
                    ret *= 1.2;
                } else if (totDay - i < 5) {
                    ret *= 0.7;
                } else {
                    ret *= 0.95;
                }
            }
            return ret;
        } else {
            return 0;
        }
    }

    private float getCuredPredict(Date date, AccessRecord accessRecord) {
        if (Math.abs(accessRecord.visitTime.getTime() - date.getTime()) < DateUtils.HOUR_IN_MILLIS) {
            float ret = 10;
            long day = (date.getTime() - accessRecord.startTime.getTime()) / DateUtils.DAY_IN_MILLIS;
            while (day-- != 0) {
                ret *= 0.7;
            }
            return ret;
        } else {
            return 0;
        }
    }


    float getPredict() {
        switch (accessRecord.user_level) {
            case AccessRecord.SAFE:
                return getSavePredict(date, accessRecord);
            case AccessRecord.HIGH_RISK:
                return getHighRiskPredict(date, accessRecord);
            case AccessRecord.CONFIRMED:
                return getConfirmedPredict(date, accessRecord);
            case AccessRecord.CURED:
                return getCuredPredict(date, accessRecord);
            default:
                return 0;
        }
    }
}
