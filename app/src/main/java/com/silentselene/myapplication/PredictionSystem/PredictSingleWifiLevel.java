package com.silentselene.myapplication.PredictionSystem;

import android.text.format.DateUtils;

import java.util.List;
import java.util.Objects;

public class PredictSingleWifiLevel {
    String Bssid;
    List<AccessRecord> accessRecordList;

    public PredictSingleWifiLevel(String bssid, List<AccessRecord> accessRecordList) {
        Bssid = bssid;
        this.accessRecordList = accessRecordList;
    }

    WifiPredictResult getNumberCount() {
        WifiPredictResult wifiPredictResult = new WifiPredictResult();
        for (AccessRecord accessRecord : accessRecordList) {
            switch (accessRecord.user_level) {
                case AccessRecord.SAFE:
                    if (wifiPredictResult.resultMap.containsKey(accessRecord.visitTime.getTime() / DateUtils.HOUR_IN_MILLIS)) {
                        Objects.requireNonNull(wifiPredictResult.resultMap.get(accessRecord.visitTime.getTime() / DateUtils.HOUR_IN_MILLIS)).SafeNumber++;
                    } else {
                        wifiPredictResult.resultMap.put(accessRecord.visitTime.getTime() / DateUtils.HOUR_IN_MILLIS, new WifiPredictOneTimeResult());
                        Objects.requireNonNull(wifiPredictResult.resultMap.get(accessRecord.visitTime.getTime() / DateUtils.HOUR_IN_MILLIS)).time = accessRecord.visitTime.getTime() / DateUtils.HOUR_IN_MILLIS;
                        Objects.requireNonNull(wifiPredictResult.resultMap.get(accessRecord.visitTime.getTime() / DateUtils.HOUR_IN_MILLIS)).SafeNumber++;
                    }
                    break;
                case AccessRecord.HIGH_RISK:
                    if (wifiPredictResult.resultMap.containsKey(accessRecord.visitTime.getTime() / DateUtils.HOUR_IN_MILLIS)) {
                        Objects.requireNonNull(wifiPredictResult.resultMap.get(accessRecord.visitTime.getTime() / DateUtils.HOUR_IN_MILLIS)).HighRiskNumber++;
                    } else {
                        wifiPredictResult.resultMap.put(accessRecord.visitTime.getTime() / DateUtils.HOUR_IN_MILLIS, new WifiPredictOneTimeResult());
                        Objects.requireNonNull(wifiPredictResult.resultMap.get(accessRecord.visitTime.getTime() / DateUtils.HOUR_IN_MILLIS)).time = accessRecord.visitTime.getTime() / DateUtils.HOUR_IN_MILLIS;
                        Objects.requireNonNull(wifiPredictResult.resultMap.get(accessRecord.visitTime.getTime() / DateUtils.HOUR_IN_MILLIS)).HighRiskNumber++;
                    }
                    break;
                case AccessRecord.CONFIRMED:
                    if (wifiPredictResult.resultMap.containsKey(accessRecord.visitTime.getTime() / DateUtils.HOUR_IN_MILLIS)) {
                        Objects.requireNonNull(wifiPredictResult.resultMap.get(accessRecord.visitTime.getTime() / DateUtils.HOUR_IN_MILLIS)).ConfirmedNumber++;
                    } else {
                        wifiPredictResult.resultMap.put(accessRecord.visitTime.getTime() / DateUtils.HOUR_IN_MILLIS, new WifiPredictOneTimeResult());
                        Objects.requireNonNull(wifiPredictResult.resultMap.get(accessRecord.visitTime.getTime() / DateUtils.HOUR_IN_MILLIS)).time = accessRecord.visitTime.getTime() / DateUtils.HOUR_IN_MILLIS;
                        Objects.requireNonNull(wifiPredictResult.resultMap.get(accessRecord.visitTime.getTime() / DateUtils.HOUR_IN_MILLIS)).ConfirmedNumber++;
                    }
                    break;
                case AccessRecord.CURED:
                    if (wifiPredictResult.resultMap.containsKey(accessRecord.visitTime.getTime() / DateUtils.HOUR_IN_MILLIS)) {
                        Objects.requireNonNull(wifiPredictResult.resultMap.get(accessRecord.visitTime.getTime() / DateUtils.HOUR_IN_MILLIS)).CuredNumber++;
                    } else {
                        wifiPredictResult.resultMap.put(accessRecord.visitTime.getTime() / DateUtils.HOUR_IN_MILLIS, new WifiPredictOneTimeResult());
                        Objects.requireNonNull(wifiPredictResult.resultMap.get(accessRecord.visitTime.getTime() / DateUtils.HOUR_IN_MILLIS)).time = accessRecord.visitTime.getTime() / DateUtils.HOUR_IN_MILLIS;
                        Objects.requireNonNull(wifiPredictResult.resultMap.get(accessRecord.visitTime.getTime() / DateUtils.HOUR_IN_MILLIS)).CuredNumber++;
                    }
                    break;
            }
        }
        return wifiPredictResult;
    }
}
