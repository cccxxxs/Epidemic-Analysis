package com.silentselene.myapplication;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.silentselene.myapplication.PredictionSystem.AccessRecord;
import com.silentselene.myapplication.WifiPlugin.DBAdapter;
import com.silentselene.myapplication.WifiPlugin.WiFiRecord;
import com.silentselene.myapplication.data.localHistory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class UploadWifiList {
    static void uploadWifiList(final MainActivity context, int userId, int state) {
        DBAdapter dbAdapter = new DBAdapter(context);
        dbAdapter.open();
        Date date = new Date(localHistory.getLastUpdateDate(context));
        WiFiRecord[] wiFiRecords = dbAdapter.queryByTime(date);
        if (wiFiRecords == null) {
            return;
        }
        for (WiFiRecord wiFiRecord : wiFiRecords) {
            uploadOneWifi(context, wiFiRecord, userId, state);
            if (wiFiRecord.getTime().compareTo(date) > 0) {
                date = wiFiRecord.getTime();
//                try {
//                    Thread.sleep(200);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        }
        localHistory.setLastUpdateDate(context, date.getTime());
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, "wifi数据上传成功", Toast.LENGTH_SHORT).show();
            }
        });
    }


    static void uploadOneWifi(final MainActivity context, final WiFiRecord wiFiRecord, final int userId, final int state) {
        OkHttpClient okHttpClient = new OkHttpClient();
        String stateString = "";
        switch (state) {
            case AccessRecord.SAFE:
                stateString = "healthy";
                break;
            case AccessRecord.CONFIRMED:
                stateString = "ill";
                break;
            case AccessRecord.HIGH_RISK:
                stateString = "high_risk";
                break;
            case AccessRecord.CURED:
                stateString = "cured";
                break;
        }
        final String upstate = stateString;
        RequestBody requestBody = new FormBody.Builder()
                .add("fkid1", String.valueOf(userId))
                .add("mac", wiFiRecord.getBSSID())
                .add("visitTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(wiFiRecord.getTime()))
                .add("state", stateString)
                .build();
        Request request = new Request.Builder().url("http://123.56.117.101:8080/updateLog").post(requestBody).build();
        Call call = okHttpClient.newCall(request);//发送请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("POST_updateLog_faild", "result: " + wiFiRecord.toString() + upstate);
                uploadOneWifi(context, wiFiRecord, userId, state);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("POST_updateLog_success", "result: " + wiFiRecord.toString() + upstate+response.body().string());
            }
        });
    }
}
