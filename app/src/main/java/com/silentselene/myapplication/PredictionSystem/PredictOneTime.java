package com.silentselene.myapplication.PredictionSystem;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PredictOneTime {

    Date visitTime;
    private List<AccessRecord> accessRecordList;

    public PredictOneTime(Date visitTime, List<AccessRecord> accessRecordList) {
        this.visitTime = visitTime;
        this.accessRecordList = accessRecordList;
    }

    private float combinePredict(List<Float> predictList) {
        float maxUnit = 0;
        for (Float data : predictList) {
            maxUnit = Math.max(maxUnit, data);
        }
        float ret = 0;
        if (maxUnit == 0) return 0;
        for (Float data : predictList) {
            if (maxUnit == 0) return 0;
            ret += (data / maxUnit) * (data / maxUnit) * data;
        }
        return ret;
    }

    float getPredict() {
        Map<String, List<AccessRecord>> map = new HashMap<>();
        for (AccessRecord accessRecord : accessRecordList) {
            if (map.containsKey(accessRecord.Bssid)) {
                Objects.requireNonNull(map.get(accessRecord.Bssid)).add(accessRecord);
            } else {
                map.put(accessRecord.Bssid, new ArrayList<AccessRecord>());
                Objects.requireNonNull(map.get(accessRecord.Bssid)).add(accessRecord);
            }
        }
        List<Float> predictList = new ArrayList<>();
        for (List<AccessRecord> accessRecordList : map.values()) {
            predictList.add(new PredictOneNode(accessRecordList, visitTime).getPrediction());
        }
        return combinePredict(predictList);
    }


    public int getJudgeLevel() {
        float predicton = getPredict();
        if (predicton > 150)
            return AccessRecord.HIGH_RISK;
        else if (predicton <= 150)
            return AccessRecord.SAFE;
        return 0;
    }
}
