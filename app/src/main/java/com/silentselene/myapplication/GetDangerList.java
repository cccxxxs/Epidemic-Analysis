package com.silentselene.myapplication;

import android.os.Handler;
import android.os.Looper;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import com.silentselene.myapplication.PredictionSystem.AccessRecord;
import com.silentselene.myapplication.PredictionSystem.PredictOnePerson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GetDangerList {

    static void getDangerList(final MainActivity context, String mac) {
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("mac", mac)
                .build();
        Request request = new Request.Builder().url("http://123.56.117.101:8080/getDanger").post(requestBody).build();
        Call call = okHttpClient.newCall(request);//发送请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("POST_getDangerList_faild", "result: " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responsr_str = response.body().string();
                Log.d("POST_getDangerList_success", "result: " + responsr_str);
                try {
                    JSONArray jsonArray = new JSONArray(responsr_str);
                    final List<AccessRecord> accessRecordList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String mac = jsonObject.getString("mac");
                        int user_level = jsonObject.getInt("user_level");
                        String date_str = jsonObject.getString("visitTime");
                        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date_str);
                        assert date != null;
                        accessRecordList.add(new AccessRecord(mac, 0, user_level,
                                new Date(date.getTime() - DateUtils.DAY_IN_MILLIS * 3), new Date(date.getTime() + DateUtils.DAY_IN_MILLIS * 3), date));
                        Log.d("POST_danger_item", "mac: " + mac + " user_level: " + user_level + " date: " + date);
                    }
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (context.stateList.get(0).state == AccessRecord.HIGH_RISK) return;
                            PredictOnePerson predictOnePerson = new PredictOnePerson(context.getMyWifiList(), accessRecordList);
                            int res = predictOnePerson.getJudgeLevel();
                            float num = predictOnePerson.getPredict();
                            if (res == AccessRecord.HIGH_RISK) {
                                context.updateStateTo(new Date().getTime(), AccessRecord.HIGH_RISK);
                                context.updateUi();
                            }
                        }
                    });
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
