package com.silentselene.myapplication.PredictionSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PredictWifisLevel {
    List<String> BssidList;
    List<AccessRecord> accessRecordList;

    public PredictWifisLevel(List<String> bssidList, List<AccessRecord> accessRecordList) {
        BssidList = bssidList;
        this.accessRecordList = accessRecordList;
    }

    Map<String, WifiPredictResult> getPrediction() {
        Map<String, WifiPredictResult> wifiPredictResultMap = new HashMap<>();
        Map<String, List<AccessRecord>> listMap = new HashMap<>();
        for (String Bssid : BssidList) {
            wifiPredictResultMap.put(Bssid, new WifiPredictResult());
            listMap.put(Bssid, new ArrayList<AccessRecord>());
        }
        for (AccessRecord accessRecord : accessRecordList) {
            if (listMap.containsKey(accessRecord.Bssid)) {
                Objects.requireNonNull(listMap.get(accessRecord.Bssid)).add(accessRecord);
            }
        }
        for (String Bssid : BssidList) {
            wifiPredictResultMap.put(Bssid, new PredictSingleWifiLevel(Bssid, listMap.get(Bssid)).getNumberCount());
        }
        return wifiPredictResultMap;
    }
}
