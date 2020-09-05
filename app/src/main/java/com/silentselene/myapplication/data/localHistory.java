package com.silentselene.myapplication.data;

import android.content.Context;
import android.content.SharedPreferences;

public class localHistory {
    public static boolean isUploaded(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("flagInfo", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("isUploaded", false);
    }

    public static void setUploaded(Context context, boolean flag) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("flagInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isUploaded", flag);
        editor.apply();
    }

    public static long getRequestDate(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("flagInfo", Context.MODE_PRIVATE);
        return sharedPreferences.getLong("requestDate", -1);
    }

    public static void setRequestDate(Context context, long date) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("flagInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("requestDate", date);
        editor.apply();
    }

    public static long getLastUpdateDate(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("flagInfo", Context.MODE_PRIVATE);
        return sharedPreferences.getLong("lastUpdateDate", 0);
    }

    public static void setLastUpdateDate(Context context, long date) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("flagInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("lastUpdateDate", date);
        editor.apply();
    }

    public static long getLastUpdateID(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("flagInfo", Context.MODE_PRIVATE);
        return sharedPreferences.getLong("lastUpdateID", 0);
    }

    public static void setLastUpdateID(Context context, long id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("flagInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("lastUpdateID", id);
        editor.apply();
    }
    public static long getLastState(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("flagInfo", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("lastState", 0);
    }

    public static void setLastState(Context context, int id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("flagInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("lastState", id);
        editor.apply();
    }
}
