package com.silentselene.myapplication.PredictionSystem;

import java.util.Date;

public class AccessRecord {
    public static final int SAFE = 0;
    public static final int HIGH_RISK = 1;
    public static final int CONFIRMED = 2;
    public static final int CURED = 3;
    String Bssid;
    int user_id;
    int user_level;
    Date startTime;
    Date endTime;
    Date visitTime;

    public AccessRecord(String bssid, int user_id, int user_level, Date startTime, Date endTime, Date visitTime) {
        Bssid = bssid;
        this.user_id = user_id;
        this.user_level = user_level;
        this.startTime = startTime;
        this.endTime = endTime;
        this.visitTime = visitTime;
    }
}
