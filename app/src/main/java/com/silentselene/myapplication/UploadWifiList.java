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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
        JSONArray jsonArray = new JSONArray();

        try {
            for (WiFiRecord wiFiRecord : wiFiRecords) {
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
                JSONObject item = new JSONObject();
                item.put("mac", wiFiRecord.getBSSID());
                item.put("visitTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(wiFiRecord.getTime()));
                item.put("state", stateString);
                jsonArray.put(item);
                if (wiFiRecord.getTime().compareTo(date) > 0) {
                    date = wiFiRecord.getTime();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        doUpload(context, String.valueOf(userId), jsonArray.toString(), date);
    }

    static void doUpload(final Context context, final String userId, final String extra, final Date date) {
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", String.valueOf(userId))
                .add("extra", extra)
                .build();
        Request request = new Request.Builder().url("http://123.56.117.101:8081/uploadLogs").post(requestBody).build();
        Call call = okHttpClient.newCall(request);//发送请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("POST_updateLog_faild", "result: " + extra);
                doUpload(context, userId, extra, date);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                localHistory.setLastUpdateDate(context, date.getTime());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "wifi数据上传成功", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d("POST_updateLog_success", "result: " + extra + response.body().string());
            }
        });
    }
}

