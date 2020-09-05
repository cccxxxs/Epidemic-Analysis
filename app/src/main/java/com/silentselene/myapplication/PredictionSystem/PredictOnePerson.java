package com.silentselene.myapplication.PredictionSystem;

import android.text.format.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PredictOnePerson {
    private List<AccessRecord> accessRecordList, myAccessRecordList;


    public PredictOnePerson(List<AccessRecord> myAccessRecordList, List<AccessRecord> accessRecordList) {
        this.myAccessRecordList = myAccessRecordList;
        this.accessRecordList = accessRecordList;
    }

    public void addRecord(List<AccessRecord> accessRecordList) {
        this.accessRecordList.addAll(accessRecordList);
    }

    private float combinePredict(List<Float> predictList) {
        float maxUnit = 0;
        for (Float data : predictList) {
            maxUnit = Math.max(maxUnit, data);
        }
        float ret = 0;
        for (Float data : predictList) {
            if (maxUnit==0)return 0;
            ret += (data / maxUnit) * (data / maxUnit) * (data / maxUnit) * data;
        }
        return ret;
    }

    private Date getPredictTime() {
        Map<Long, List<AccessRecord>> map = new HashMap<>();
        for (AccessRecord accessRecord : myAccessRecordList) {
            map.put(accessRecord.visitTime.getTime(), new ArrayList<AccessRecord>());
        }
        for (AccessRecord accessRecord : accessRecordList) {
            for (AccessRecord myAccessRecord : myAccessRecordList) {
                if (Math.abs(myAccessRecord.visitTime.getTime() - accessRecord.visitTime.getTime()) < DateUtils.DAY_IN_MILLIS) {
                    Objects.requireNonNull(map.get(myAccessRecord.visitTime.getTime())).add(accessRecord);
                }
            }
        }
        long earliestDate = Long.MAX_VALUE;
        for (Long myAccessTime : map.keySet()) {
            if (new PredictOneTime(new Date(myAccessTime), map.get(myAccessTime)).getJudgeLevel() == AccessRecord.HIGH_RISK) {
                earliestDate = Math.min(earliestDate, myAccessTime);
            }

        }
        if (earliestDate == Long.MAX_VALUE) {
            return null;
        } else {
            return new Date(earliestDate);
        }
    }


    public float getPredict() {
        Map<Long, List<AccessRecord>> map = new HashMap<>();
        for (AccessRecord accessRecord : myAccessRecordList) {
            map.put(accessRecord.visitTime.getTime(), new ArrayList<AccessRecord>());
        }
        for (AccessRecord accessRecord : accessRecordList) {
            for (AccessRecord myAccessRecord : myAccessRecordList) {
                if (Math.abs(myAccessRecord.visitTime.getTime() - accessRecord.visitTime.getTime()) < DateUtils.DAY_IN_MILLIS) {
                    Objects.requireNonNull(map.get(myAccessRecord.visitTime.getTime())).add(accessRecord);
                }
            }
        }
        List<Float> predictList = new ArrayList<>();
        for (Long myAccessTime : map.keySet()) {
            predictList.add(new PredictOneTime(new Date(myAccessTime), map.get(myAccessTime)).getPredict());
        }
        return combinePredict(predictList);
    }


    public int getJudgeLevel() {
        float predicton = getPredict();
        if (predicton > 500)
            return AccessRecord.HIGH_RISK;
        else if (predicton <= 500)
            return AccessRecord.SAFE;
        return 0;
    }
}
