package com.silentselene.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.silentselene.myapplication.PredictionSystem.AccessRecord;
import com.silentselene.myapplication.WifiPlugin.DBAdapter;
import com.silentselene.myapplication.WifiPlugin.DBController;
import com.silentselene.myapplication.WifiPlugin.WiFiRecord;
import com.silentselene.myapplication.data.States;
import com.silentselene.myapplication.data.localHistory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_CODE_SAFE = 1;
    static final int REQUEST_CODE_HIGH_RISK = 2;
    static final int REQUEST_CODE_CONFIRMED = 3;
    static final int REQUEST_CODE_CURED = 4;
    static final int REQUEST_CODE_CHECKED = 5;
    static final int REQUEST_CODE_HIGH_RISK_TO_SAFE = 6;
    static final int REQUEST_CODE_HIGH_RISK_TO_CONFIRMED = 7;
    static final int WIFI_SCANNER_TIME = 300000;
    private static final String TAG = "MainActivity";
    final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    int userId;
    List<States.State> stateList;
    boolean uploaded;
    boolean isChecked;
    WiFiRecord wifi;
    List<ScanResult> wifiScanResult;
    private DBAdapter dbAdapter;
    private Timer timer;
    private TimerTask timerTask;
    private WifiManager mWifiManager;
    void updateIsChecked() {
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("user_id", String.valueOf(userId))
                .build();
        final Request request = new Request.Builder().url("http://123.56.117.101:8080/checkRes").post(requestBody).build();

        Call call = okHttpClient.newCall(request);//发送请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "请求审核状态失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.e("POST", "result: " + result);
                if (result.equals("false")) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "审核未通过", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "审核通过", Toast.LENGTH_SHORT).show();
                            isChecked = true;
                            updateState();
                        }
                    });
                }
            }
        });
    }

    void saveInformation() {
        SharedPreferences sharedPreferences = getSharedPreferences("SavedSetting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("userId", userId);
        editor.apply();

        States.updateStates(this, stateList);
    }

    void loadInformation() {
        SharedPreferences sharedPreferences = getSharedPreferences("SavedSetting", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1) == -1 ? (int) (System.currentTimeMillis() % Integer.MAX_VALUE) : sharedPreferences.getInt("userId", -1);

        stateList = States.getStates(this);
        saveInformation();
    }

    void setSafe() {
        ImageView risk_image = findViewById(R.id.risk_image);
        TextView risk_info = findViewById(R.id.risk_info);
        risk_image.setImageResource(R.drawable.ic_check_black_24dp);
        risk_info.setText(R.string.risk_info_safe);
        risk_image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                UploadWifiList.uploadWifiList(MainActivity.this, userId, stateList.get(0).state);
                return true;
            }
        });
        risk_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                predictState();
            }
        });

        if (uploaded) {
            ImageView upload_image = findViewById(R.id.upload_image);
            upload_image.setImageResource(R.drawable.ic_cloud_done_black_24dp);
            ((TextView) findViewById(R.id.textView_upload)).setText("您已成功申报确诊信息");
            ((TextView) findViewById(R.id.textView_upload_state)).setText("刷新审核状态");
            upload_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateIsChecked();
                }
            });
            upload_image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(MainActivity.this, UploadInfoActivity.class);
                    intent.putExtra("upload_type", AccessRecord.SAFE);
                    intent.putExtra("userId", userId);
                    startActivityForResult(intent, REQUEST_CODE_SAFE);
                    return true;
                }
            });
        } else {
            ((TextView) findViewById(R.id.textView_upload)).setText("申请上传确诊信息");
            ((TextView) findViewById(R.id.textView_upload_state)).setText("上传信息");
            ImageView upload_image = findViewById(R.id.upload_image);
            upload_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, UploadInfoActivity.class);
                    intent.putExtra("upload_type", AccessRecord.SAFE);
                    intent.putExtra("userId", userId);
                    startActivityForResult(intent, REQUEST_CODE_SAFE);
                }
            });
        }
    }

    void setHighRisk() {
        ImageView risk_image = findViewById(R.id.risk_image);
        TextView risk_info = findViewById(R.id.risk_info);
        risk_image.setImageResource(R.drawable.ic_clear_black_24dp);
        risk_info.setText(R.string.risk_info_high_risk);
        risk_image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        risk_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadWifiList.uploadWifiList(MainActivity.this, userId, stateList.get(0).state);
            }
        });

        if (uploaded) {
            ImageView upload_image = findViewById(R.id.upload_image);
            upload_image.setImageResource(R.drawable.ic_cloud_done_black_24dp);
            ((TextView) findViewById(R.id.textView_upload)).setText("您已成功申报无风险信息");
            ((TextView) findViewById(R.id.textView_upload_state)).setText("刷新审核状态");
            upload_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateIsChecked();
                }
            });
            upload_image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(MainActivity.this, UploadInfoActivity.class);
                    intent.putExtra("upload_type", AccessRecord.HIGH_RISK);
                    intent.putExtra("userId", userId);
                    startActivityForResult(intent, REQUEST_CODE_HIGH_RISK);
                    return true;
                }
            });
        } else {
            ((TextView) findViewById(R.id.textView_upload)).setText("申请上传个人/确诊/无风险信息");
            ((TextView) findViewById(R.id.textView_upload_state)).setText("上传信息");
            ImageView upload_image = findViewById(R.id.upload_image);
            upload_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, UploadInfoActivity.class);
                    intent.putExtra("upload_type", AccessRecord.HIGH_RISK);
                    intent.putExtra("userId", userId);
                    startActivityForResult(intent, REQUEST_CODE_HIGH_RISK);
                }
            });
        }
    }

    void setConfirmed() {
        ImageView risk_image = findViewById(R.id.risk_image);
        TextView risk_info = findViewById(R.id.risk_info);
        risk_image.setImageResource(R.drawable.ic_clear_black_24dp);
        risk_info.setText(R.string.risk_info_confirmed);
        risk_image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        risk_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadWifiList.uploadWifiList(MainActivity.this, userId, stateList.get(0).state);
            }
        });

        if (uploaded) {
            ImageView upload_image = findViewById(R.id.upload_image);
            upload_image.setImageResource(R.drawable.ic_cloud_done_black_24dp);
            ((TextView) findViewById(R.id.textView_upload)).setText("您已成功申报治愈信息");
            ((TextView) findViewById(R.id.textView_upload_state)).setText("刷新审核状态");
            upload_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateIsChecked();
                }
            });
            upload_image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(MainActivity.this, UploadInfoActivity.class);
                    intent.putExtra("upload_type", AccessRecord.CONFIRMED);
                    intent.putExtra("userId", userId);
                    startActivityForResult(intent, REQUEST_CODE_CONFIRMED);
                    return true;
                }
            });
        } else {
            ((TextView) findViewById(R.id.textView_upload)).setText("申请上传治愈信息");
            ((TextView) findViewById(R.id.textView_upload_state)).setText("上传信息");
            ImageView upload_image = findViewById(R.id.upload_image);
            upload_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, UploadInfoActivity.class);
                    intent.putExtra("upload_type", AccessRecord.CONFIRMED);
                    intent.putExtra("userId", userId);
                    startActivityForResult(intent, REQUEST_CODE_CONFIRMED);
                }
            });
        }
    }

    void updateState() {
        if (uploaded && isChecked) {
            localHistory.setUploaded(this, false);
            int nowstate = 0;
            switch (stateList.get(0).state) {
                case AccessRecord.SAFE:
                case AccessRecord.CURED:
                    nowstate = AccessRecord.CONFIRMED;
                    break;
                case AccessRecord.CONFIRMED:
                    nowstate = AccessRecord.CURED;
                    break;
                case AccessRecord.HIGH_RISK:
                    if (localHistory.getLastState(this) == REQUEST_CODE_HIGH_RISK_TO_CONFIRMED) {
                        nowstate = AccessRecord.CONFIRMED;
                    } else {
                        nowstate = AccessRecord.SAFE;
                    }
                    break;
            }

            long date = localHistory.getRequestDate(this);
            updateStateTo(date, nowstate);
        }
    }

    void updateStateTo(long date, int nowstate) {
        if (nowstate == AccessRecord.HIGH_RISK && stateList.get(0).state == AccessRecord.SAFE) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "预测完成，您的预测结果为：" + (stateList.get(0).state == AccessRecord.HIGH_RISK ? "高风险" : "正常"), Toast.LENGTH_SHORT).show();
                }
            });
        }
        uploaded = false;
        isChecked = false;
        stateList = States.getStates(this);
        while (stateList.size() > 0 && stateList.get(0).start_time.getTime() > date) {
            stateList.remove(0);
        }
        stateList.add(0, new States.State(new Date(date), nowstate));
        States.updateStates(this, stateList);
        updateUi();
    }

    void updateUi() {
        uploaded = localHistory.isUploaded(this);
        if (stateList.get(0).state == AccessRecord.SAFE || stateList.get(0).state == AccessRecord.CURED) {
            setSafe();
        } else if (stateList.get(0).state == AccessRecord.HIGH_RISK) {
            setHighRisk();
        } else if (stateList.get(0).state == AccessRecord.CONFIRMED) {
            setConfirmed();
        }
        uploadWifiList();
    }

    void uploadWifiList() {
        if (stateList.get(0).state == AccessRecord.HIGH_RISK || stateList.get(0).state == AccessRecord.CONFIRMED) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UploadWifiList.uploadWifiList(MainActivity.this, userId, stateList.get(0).state);
                }
            }).start();
        }
    }

    List<AccessRecord> getMyWifiList() {
        DBAdapter dbAdapter = new DBAdapter(this);
        dbAdapter.open();
        WiFiRecord[] wiFiRecords = dbAdapter.queryByTime(new Date(stateList.get(0).start_time.getTime() + DateUtils.HOUR_IN_MILLIS));
        if (wiFiRecords == null) {
            dbAdapter.close();
            return new ArrayList<>();
        } else {
            List<AccessRecord> accessRecordList = new ArrayList<>();
            for (WiFiRecord wiFiRecord : wiFiRecords) {
                accessRecordList.add(new AccessRecord(wiFiRecord.getBSSID(), userId, 0, wiFiRecord.getTime(), wiFiRecord.getTime(), wiFiRecord.getTime()));
            }
            dbAdapter.close();
            return accessRecordList;
        }
    }

    void predictState() {
        DBAdapter dbAdapter = new DBAdapter(this);
        dbAdapter.open();
        WiFiRecord[] wiFiRecords = dbAdapter.queryAllData();
        if (wiFiRecords != null) {
            Set<String> stringSet = new HashSet<>();
            for (WiFiRecord wiFiRecord : wiFiRecords) {
                stringSet.add(wiFiRecord.getBSSID());
            }
            for (String mac : stringSet)
                GetDangerList.getDangerList(this, mac);
        }
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    for (int i = 0; i < 10; i++) {
//                        Thread.sleep(1000);
//                        if (stateList.get(0).state == AccessRecord.HIGH_RISK) break;
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadInformation();
        updateUi();
        setupWifiScan();
        uploadWifiList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        if (resultCode == REQUEST_CODE_SAFE) {
            assert data != null;
            if (data.getBooleanExtra("Uploaded", false)) {
                localHistory.setUploaded(this, true);
                uploaded = true;
                isChecked = true;
                updateState();
                long date = data.getLongExtra("Date", 0);
                localHistory.setRequestDate(this, date);
            }
        } else if (resultCode == REQUEST_CODE_CONFIRMED) {
            assert data != null;
            if (data.getBooleanExtra("Uploaded", false)) {
                localHistory.setUploaded(this, true);
                uploaded = true;
                updateUi();
                long date = data.getLongExtra("Date", 0);
                localHistory.setRequestDate(this, date);
            }
        } else if (resultCode == REQUEST_CODE_HIGH_RISK_TO_CONFIRMED) {
            assert data != null;
            if (data.getBooleanExtra("Uploaded", false)) {
                localHistory.setUploaded(this, true);
                localHistory.setLastState(this, REQUEST_CODE_HIGH_RISK_TO_CONFIRMED);
                uploaded = true;
                isChecked = true;
                updateState();
                long date = data.getLongExtra("Date", 0);
                localHistory.setRequestDate(this, date);
            }
        } else if (resultCode == REQUEST_CODE_HIGH_RISK_TO_SAFE) {
            assert data != null;
            if (data.getBooleanExtra("Uploaded", false)) {
                localHistory.setUploaded(this, true);
                localHistory.setLastState(this, REQUEST_CODE_HIGH_RISK_TO_SAFE);
                uploaded = true;
                updateUi();
                long date = data.getLongExtra("Date", 0);
                localHistory.setRequestDate(this, date);
            }
        }
    }

    // 以下是Wi-Fi扫描部分
    @Override
    public void finish(){
        super.finish(); //activity永远不会自动退出了，而是处于后台。
        moveTaskToBack(true);
    }

    // 自动扫描（定时器）
    void setupWifiScan() {
        // 获取定位权限，否则wifiScanResult为空
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);

        // 初始化WifiManager，否则闪退
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiScanResult = new ArrayList<>();
        // 初始化DBAdapter
        dbAdapter = new DBAdapter(this);
        dbAdapter.open();
        wifi = new WiFiRecord();

        startAutoScan();

        // 测试窗口
        findViewById(R.id.debugView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent debug = new Intent(MainActivity.this, DBController.class);
                startActivity(debug);
            }
        });
        findViewById(R.id.debugView).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                scanWifi();
                return true;
            }
        });
    }

    public void startAutoScan() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                // 扫描Wi-Fi
                scanWifi();
                uploadWifiList();
                predictState();
            }
        };
        // 定时运行
        timer.schedule(timerTask, 0, WIFI_SCANNER_TIME);
    }

    // 定时运行停止
    public void stopAutoScan() {
        timer.cancel();
        timer = null;
    }

    public void scanWifi() {
        mWifiManager.startScan();
        wifiScanResult = mWifiManager.getScanResults();
        if (wifiScanResult != null) {
            String msg = "";
            for (ScanResult oneLine : wifiScanResult) {
                Date date = new Date(System.currentTimeMillis());
                msg += oneLine.SSID + "\t" + oneLine.BSSID + "\t" + "当前日期时间" + df.format(date) + "\n";
                addData(oneLine.SSID, oneLine.BSSID, date);
            }
            // 打log
            Log.i(TAG, msg);
        }
    }

    // 添加至数据库
    private void addData(String ssid, String bssid, Date time) {
        wifi.setSSID(ssid);
        wifi.setBSSID(bssid);
        wifi.setTime(time);
        long column = dbAdapter.insert(wifi);
        if (column == -1) {
            Log.i(TAG, "添加数据失败!");
        } else {
            Log.i(TAG, "已添加至数据库!");
        }
    }
}
