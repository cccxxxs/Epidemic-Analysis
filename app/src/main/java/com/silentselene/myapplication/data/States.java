package com.silentselene.myapplication.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.silentselene.myapplication.PredictionSystem.AccessRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class States {
    public static List<State> getStates(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("States", Context.MODE_PRIVATE);
        List<State> stateList = new ArrayList<>();
        int stateNumber = sharedPreferences.getInt("stateNumber", -1);
        if (stateNumber == -1) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("stateNumber", 1);
            editor.putLong("time0", 0);
            editor.putInt("state0", AccessRecord.SAFE);
            editor.apply();
            stateNumber = 1;
        }
        for (int i = 0; i < stateNumber; i++) {
            Date start_time = new Date(sharedPreferences.getLong("time" + i, 0));
            int state = sharedPreferences.getInt("state" + i, 0);
            stateList.add(new State(start_time, state));
        }
        return stateList;
    }

    public static void clearStates(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("States", Context.MODE_PRIVATE);
        Map<String, ?> map = sharedPreferences.getAll();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (String string : map.keySet()) {
            editor.remove(string);
        }
        editor.apply();
    }

    public static void updateStates(Context context, List<State> list) {
        clearStates(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences("States", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("stateNumber", list.size());
        for (int i = 0; i < list.size(); i++) {
            editor.putLong("time" + i, list.get(i).start_time.getTime());
            editor.putInt("state" + i, list.get(i).state);
        }
        editor.apply();
    }

    public static class State {
        public Date start_time;
        public int state;

        public State(Date start_time, int state) {
            this.start_time = start_time;
            this.state = state;
        }
    }
}
