package com.silentselene.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.silentselene.myapplication.PredictionSystem.AccessRecord;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadInfoActivity extends AppCompatActivity {
    Intent intent;

    String formatTime(Calendar calendar) {
        return String.format("%tF 00:00:00", calendar);
    }

    void uploadSafe(final Calendar calendar) {
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", String.valueOf(intent.getIntExtra("userId", 0)))
                .add("ill_state", "ill")
                .add("username", ((EditText) findViewById(R.id.upload_name)).getText().toString())
                .add("phone", ((EditText) findViewById(R.id.upload_phone)).getText().toString())
                .add("ID_card", ((EditText) findViewById(R.id.upload_id_number)).getText().toString())
                .add("location", ((EditText) findViewById(R.id.upload_location)).getText().toString())
                .add("illStartTime", formatTime(calendar))
                .add("hospital", ((EditText) findViewById(R.id.upload_hospital)).getText().toString())
                .build();
        Request request = new Request.Builder().url("http://123.56.117.101:8080/generalUpdate").post(requestBody).build();

        Call call = okHttpClient.newCall(request);//发送请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UploadInfoActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("POST", "result: " + response.body().string());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UploadInfoActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                    }
                });
                Intent intent = new Intent();
                intent.putExtra("Uploaded", true);
                intent.putExtra("Date", calendar.getTimeInMillis());
                setResult(MainActivity.REQUEST_CODE_SAFE, intent);
                UploadInfoActivity.this.finish();
            }
        });
    }

    void uploadConfirmed(final Calendar calendar) {
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", String.valueOf(intent.getIntExtra("userId", 0)))
                .add("ill_state", "cured")
                .add("username", ((EditText) findViewById(R.id.upload_name)).getText().toString())
                .add("phone", ((EditText) findViewById(R.id.upload_phone)).getText().toString())
                .add("ID_card", ((EditText) findViewById(R.id.upload_id_number)).getText().toString())
                .add("location", ((EditText) findViewById(R.id.upload_location)).getText().toString())
                .add("illStartTime", formatTime(calendar))
                .add("hospital", ((EditText) findViewById(R.id.upload_hospital)).getText().toString())
                .build();
        Request request = new Request.Builder().url("http://123.56.117.101:8080/generalUpdate").post(requestBody).build();

        Call call = okHttpClient.newCall(request);//发送请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UploadInfoActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("POST", "result: " + response.body().string());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UploadInfoActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                    }
                });
                Intent intent = new Intent();
                intent.putExtra("Uploaded", true);
                intent.putExtra("Date", calendar.getTimeInMillis());
                setResult(MainActivity.REQUEST_CODE_CONFIRMED, intent);
                UploadInfoActivity.this.finish();
            }
        });
    }

    public void setSafe() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, new String[]{"上报确诊信息"});
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.upload_type)).setAdapter(adapter);
        ((TextView) findViewById(R.id.textView10)).setText("病情开始时间");

        final EditText upload_date = findViewById(R.id.upload_date);
        final Calendar calendar = Calendar.getInstance();
        upload_date.setText(String.format("%tF", calendar));
//        upload_date.setText(formatTime(calendar));
        final DatePickerDialog dialog = new DatePickerDialog(UploadInfoActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        upload_date.setText(String.format("%tF", calendar));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        upload_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        Button upload_button = findViewById(R.id.upload_button);
        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((EditText) findViewById(R.id.upload_name)).getText().toString().equals("")) {
                    Toast.makeText(UploadInfoActivity.this, "请输入姓名", Toast.LENGTH_SHORT).show();
                } else if (((EditText) findViewById(R.id.upload_id_number)).getText().toString().equals("")) {
                    Toast.makeText(UploadInfoActivity.this, "请输入证件号码", Toast.LENGTH_SHORT).show();
                } else if (((EditText) findViewById(R.id.upload_phone)).getText().toString().equals("")) {
                    Toast.makeText(UploadInfoActivity.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                } else if (((EditText) findViewById(R.id.upload_location)).getText().toString().equals("")) {
                    Toast.makeText(UploadInfoActivity.this, "请输入所在地", Toast.LENGTH_SHORT).show();
                } else if (((EditText) findViewById(R.id.upload_hospital)).getText().toString().equals("")) {
                    Toast.makeText(UploadInfoActivity.this, "请输入检测医院", Toast.LENGTH_SHORT).show();
                } else {
                    uploadSafe(calendar);
                }
            }
        });
    }

    void setConfirmed() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, new String[]{"上报治愈信息"});
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.upload_type)).setAdapter(adapter);
        ((TextView) findViewById(R.id.textView10)).setText("确认治愈时间");

        final EditText upload_date = findViewById(R.id.upload_date);
        final Calendar calendar = Calendar.getInstance();
        upload_date.setText(String.format("%tF", calendar));
//        upload_date.setText(formatTime(calendar));
        final DatePickerDialog dialog = new DatePickerDialog(UploadInfoActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        upload_date.setText(String.format("%tF", calendar));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        upload_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        Button upload_button = findViewById(R.id.upload_button);
        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((EditText) findViewById(R.id.upload_name)).getText().toString().equals("")) {
                    Toast.makeText(UploadInfoActivity.this, "请输入姓名", Toast.LENGTH_SHORT).show();
                } else if (((EditText) findViewById(R.id.upload_id_number)).getText().toString().equals("")) {
                    Toast.makeText(UploadInfoActivity.this, "请输入证件号码", Toast.LENGTH_SHORT).show();
                } else if (((EditText) findViewById(R.id.upload_phone)).getText().toString().equals("")) {
                    Toast.makeText(UploadInfoActivity.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                } else if (((EditText) findViewById(R.id.upload_location)).getText().toString().equals("")) {
                    Toast.makeText(UploadInfoActivity.this, "请输入所在地", Toast.LENGTH_SHORT).show();
                } else if (((EditText) findViewById(R.id.upload_hospital)).getText().toString().equals("")) {
                    Toast.makeText(UploadInfoActivity.this, "请输入检测医院", Toast.LENGTH_SHORT).show();
                } else {
                    uploadConfirmed(calendar);
                }
            }
        });
    }

    void uploadHighRiskToSafe(final Calendar calendar) {
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", String.valueOf(intent.getIntExtra("userId", 0)))
                .add("ill_state", "healthy")
                .add("username", ((EditText) findViewById(R.id.upload_name)).getText().toString())
                .add("phone", ((EditText) findViewById(R.id.upload_phone)).getText().toString())
                .add("ID_card", ((EditText) findViewById(R.id.upload_id_number)).getText().toString())
                .add("location", ((EditText) findViewById(R.id.upload_location)).getText().toString())
                .add("illStartTime", formatTime(calendar))
                .add("hospital", ((EditText) findViewById(R.id.upload_hospital)).getText().toString())
                .build();
        Request request = new Request.Builder().url("http://123.56.117.101:8080/generalUpdate").post(requestBody).build();

        Call call = okHttpClient.newCall(request);//发送请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UploadInfoActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("POST", "result: " + response.body().string());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UploadInfoActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                    }
                });
                Intent intent = new Intent();
                intent.putExtra("Uploaded", true);
                intent.putExtra("Date", calendar.getTimeInMillis());
                setResult(MainActivity.REQUEST_CODE_HIGH_RISK_TO_SAFE, intent);
                UploadInfoActivity.this.finish();
            }
        });
    }

    void uploadHighRisk(final Calendar calendar) {
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", String.valueOf(intent.getIntExtra("userId", 0)))
                .add("ill_state", "high_risk")
                .add("username", ((EditText) findViewById(R.id.upload_name)).getText().toString())
                .add("phone", ((EditText) findViewById(R.id.upload_phone)).getText().toString())
                .add("ID_card", ((EditText) findViewById(R.id.upload_id_number)).getText().toString())
                .add("location", ((EditText) findViewById(R.id.upload_location)).getText().toString())
                .add("illStartTime", formatTime(calendar))
                .add("hospital", "")
                .build();
        Request request = new Request.Builder().url("http://123.56.117.101:8080/generalUpdate").post(requestBody).build();

        Call call = okHttpClient.newCall(request);//发送请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UploadInfoActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("POST", "result: " + response.body().string());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UploadInfoActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                    }
                });
                Intent intent = new Intent();
                setResult(MainActivity.REQUEST_CODE_HIGH_RISK, intent);
                UploadInfoActivity.this.finish();
            }
        });
    }

    void uploadHighRiskToConfirmed(final Calendar calendar) {
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", String.valueOf(intent.getIntExtra("userId", 0)))
                .add("ill_state", "ill")
                .add("username", ((EditText) findViewById(R.id.upload_name)).getText().toString())
                .add("phone", ((EditText) findViewById(R.id.upload_phone)).getText().toString())
                .add("ID_card", ((EditText) findViewById(R.id.upload_id_number)).getText().toString())
                .add("location", ((EditText) findViewById(R.id.upload_location)).getText().toString())
                .add("illStartTime", formatTime(calendar))
                .add("hospital", ((EditText) findViewById(R.id.upload_hospital)).getText().toString())
                .build();
        Request request = new Request.Builder().url("http://123.56.117.101:8080/generalUpdate").post(requestBody).build();

        Call call = okHttpClient.newCall(request);//发送请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UploadInfoActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("POST", "result: " + response.body().string());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UploadInfoActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                    }
                });
                Intent intent = new Intent();
                intent.putExtra("Uploaded", true);
                intent.putExtra("Date", calendar.getTimeInMillis());
                setResult(MainActivity.REQUEST_CODE_HIGH_RISK_TO_CONFIRMED, intent);
                UploadInfoActivity.this.finish();
            }
        });
    }

    void setHighRisk() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, new String[]{"上报个人信息", "上报确诊信息", "上报无风险信息"});
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.upload_type)).setAdapter(adapter);
        ((TextView) findViewById(R.id.textView10)).setText("确认时间");

        findViewById(R.id.upload_hospital).setVisibility(View.GONE);
        findViewById(R.id.textView_hospital).setVisibility(View.GONE);
        ((Spinner) findViewById(R.id.upload_type)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    findViewById(R.id.upload_hospital).setVisibility(View.GONE);
                    findViewById(R.id.textView_hospital).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.upload_hospital).setVisibility(View.VISIBLE);
                    findViewById(R.id.textView_hospital).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final EditText upload_date = findViewById(R.id.upload_date);
        final Calendar calendar = Calendar.getInstance();
        upload_date.setText(String.format("%tF", calendar));
//        upload_date.setText(formatTime(calendar));
        final DatePickerDialog dialog = new DatePickerDialog(UploadInfoActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        upload_date.setText(String.format("%tF", calendar));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        upload_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        Button upload_button = findViewById(R.id.upload_button);
        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((EditText) findViewById(R.id.upload_name)).getText().toString().equals("")) {
                    Toast.makeText(UploadInfoActivity.this, "请输入姓名", Toast.LENGTH_SHORT).show();
                } else if (((EditText) findViewById(R.id.upload_id_number)).getText().toString().equals("")) {
                    Toast.makeText(UploadInfoActivity.this, "请输入证件号码", Toast.LENGTH_SHORT).show();
                } else if (((EditText) findViewById(R.id.upload_phone)).getText().toString().equals("")) {
                    Toast.makeText(UploadInfoActivity.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                } else if (((EditText) findViewById(R.id.upload_location)).getText().toString().equals("")) {
                    Toast.makeText(UploadInfoActivity.this, "请输入所在地", Toast.LENGTH_SHORT).show();

                } else if (((Spinner) findViewById(R.id.upload_type)).getSelectedItemPosition() == 0) {
                    uploadHighRisk(calendar);
                } else if (((Spinner) findViewById(R.id.upload_type)).getSelectedItemPosition() == 1) {
                    if (((EditText) findViewById(R.id.upload_hospital)).getText().toString().equals("")) {
                        Toast.makeText(UploadInfoActivity.this, "请输入检测医院", Toast.LENGTH_SHORT).show();
                    } else {
                        uploadHighRiskToConfirmed(calendar);
                    }
                } else if (((Spinner) findViewById(R.id.upload_type)).getSelectedItemPosition() == 2) {
                    if (((EditText) findViewById(R.id.upload_hospital)).getText().toString().equals("")) {
                        Toast.makeText(UploadInfoActivity.this, "请输入检测医院", Toast.LENGTH_SHORT).show();
                    } else {
                        uploadHighRiskToSafe(calendar);
                    }
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_info);
        intent = getIntent();
        int type = intent.getIntExtra("upload_type", -1);
        if (type == AccessRecord.SAFE || type == AccessRecord.CURED) {
            setSafe();
        } else if (type == AccessRecord.CONFIRMED) {
            setConfirmed();
        } else if (type == AccessRecord.HIGH_RISK) {
            setHighRisk();
        }
    }
}