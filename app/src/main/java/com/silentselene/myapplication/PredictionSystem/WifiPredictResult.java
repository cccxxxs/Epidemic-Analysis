package com.silentselene.myapplication.PredictionSystem;

import java.util.HashMap;
import java.util.Map;

public class WifiPredictResult {
    Map<Long, WifiPredictOneTimeResult> resultMap;

    public WifiPredictResult() {
        resultMap = new HashMap<>();
    }
}
