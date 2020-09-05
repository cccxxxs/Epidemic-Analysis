package com.silentselene.myapplication.PredictionSystem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PredictOneNode {

    Date visitTime;
    private List<AccessRecord> accessRecordList;

    public PredictOneNode(List<AccessRecord> accessRecordList, Date visitTime) {
        this.accessRecordList = accessRecordList;
        this.visitTime = visitTime;
    }

    private float combinePrediction(List<Float> predictionList) {
        float ret = 0;
        for (Float prediction : predictionList) {
            ret += prediction;
        }
        return ret;
    }

    public float getPrediction() {
        List<Float> predictionList = new ArrayList<>();
        for (AccessRecord accessRecord : accessRecordList) {
            predictionList.add(new PredictOneRecord(visitTime, accessRecord).getPredict());
        }
        return combinePrediction(predictionList);
    }

    public int getJudgeLevel() {
        float predicton = getPrediction();
        if (predicton > 100)
            return AccessRecord.HIGH_RISK;
        else if (predicton <= 100)
            return AccessRecord.SAFE;
        return 0;
    }

}
