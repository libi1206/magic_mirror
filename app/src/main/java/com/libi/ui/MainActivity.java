package com.libi.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.libi.R;
import com.libi.connection.MusicListConnection;
import com.libi.connection.NewsConnection;
import com.libi.connection.RequestData;
import com.libi.connection.WeatherConnection;
import com.libi.data.MusicListData;
import com.libi.data.NewsListData;
import com.libi.data.WeatherData;
import com.libi.format.MusicFormat;
import com.libi.format.NewsFormat;
import com.libi.format.WeatherFormat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,EventListener {

    private Button mWeatherButton;
    private Button mNewsButton;
    private Button mMusicButtonutton;
    private Button mTestButton;
    private Button mASRButton;
    private TextView textView;

    private static final int WEATHER_SUCCESS = 0;
    private static final int NEWS_SUCCESS = 1;
    private static final int MUSIC_SUCCESS = 2;
    private static final int FALL = 9;
    private static final int TIME_OK = 100;

    private ProgressDialog progressDialog;

    private CloseDialogHandler mHandler;
    private TimeHandler mTimeHandler;

    private EventManager asr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        setLisenter();
        init();
        initPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
    }

    private void init() {
        mHandler = new CloseDialogHandler();
        mTimeHandler = new TimeHandler();
        progressDialog = new ProgressDialog(this);
        asr = EventManagerFactory.create(this, "asr");
        asr.registerListener(this);
        //开始计时
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true) {
                        Log.w("时间", "计时");
                        Message message = Message.obtain();
                        message.what = TIME_OK;
                        mTimeHandler.sendMessage(message);
                        Thread.sleep(1000);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setLisenter() {
        mMusicButtonutton.setOnClickListener(this);
        mNewsButton.setOnClickListener(this);
        mWeatherButton.setOnClickListener(this);
        mTestButton.setOnClickListener(this);
        mASRButton.setOnClickListener(this);
    }

    private void findView() {
        mWeatherButton = findViewById(R.id.weather);
        mNewsButton = findViewById(R.id.news);
        mMusicButtonutton = findViewById(R.id.music);
        mASRButton = findViewById(R.id.asr);
        mTestButton = findViewById(R.id.ui);
        textView = findViewById(R.id.result);
    }

    //动态申请权限
    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.

            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }


    @Override
    public void onClick(View view) {
        progressDialog.show();
        switch (view.getId()) {
            case R.id.weather:
                connect(10);
                break;
            case R.id.news:
                connect(11);
                break;
            case R.id.music:
                connect(12);
                break;
            case R.id.ui:
                Intent intent = new Intent(MainActivity.this,DeskTopActivity.class);
                startActivity(intent);
            case R.id.asr:
                start();
            default:
                break;
        }
    }

    private void start() {
        textView.append("\n");
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        String event = SpeechConstant.ASR_START;

        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        String json = null;
        json = new JSONObject(params).toString();
        asr.send(event, json, null, 0, 0);
        printLog("json:" + json);
    }

    private void connect(final int code) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                RequestData data = null;
                try {
                    switch (code) {
                        case 10:
                            Log.e("测试连接", "天气");
                            data = new WeatherConnection("成都").connect();
                            message.what = WEATHER_SUCCESS;
                            break;
                        case 11:
                            Log.e("测试连接", "新闻");
                            data = new NewsConnection("1").connect();
                            message.what = NEWS_SUCCESS;
                            break;
                        case 12:
                            Log.e("测试连接", "音乐");
                            data = new MusicListConnection().connect();
                            message.what = MUSIC_SUCCESS;
                            break;
                        default:
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (data.getRequestCode() == 200) {
                    message.obj = data.getRequestData();
                } else {
                    message.what = FALL;
                    message.obj = new Integer(data.getRequestCode());
                }
                mHandler.sendMessage(message);
            }
        }).start();
    }

    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        String logTxt = "name" + name;

        if (params != null && !params.isEmpty()) {
            logTxt += "params:" + params;
        }

        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
            if (params.contains("\"nlu_result\"")) {
                if (length > 0 && data.length > 0) {
                    logTxt += ", 语义解析结果：" + new String(data, offset, length);
                }
            }
        } else if (data != null) {
            logTxt += " ;data length=" + data.length;
        }
        try {
            if (params != null) {
                JSONObject json = new JSONObject(params);
                textView.setText(json.get("best_result").toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        textView.append(logTxt+"\n");
        printLog("MY:params:" + params);
        printLog("MYLOG:" + logTxt);

    }

    private void printLog(String s) {
        Log.w("语音", s);
    }

    class CloseDialogHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDialog.dismiss();
            switch (msg.what) {
                case WEATHER_SUCCESS:
                    try {
                        WeatherData data = (WeatherData) new WeatherFormat().format(msg.obj.toString());
                        textView.setText(data.getToday().getDate() + "," + data.getToday().getHigh() + "," + data.getToday().getLow() + "," + data.getToday().getType());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    textView.setText(msg.obj.toString());
                    break;
                case NEWS_SUCCESS:
                    try {
                        NewsListData data = (NewsListData) new NewsFormat().format(msg.obj.toString());
                        textView.setText(data.getNewsItemDatas()[0].getTitle());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //textView.setText(msg.obj.toString());
                    break;
                case MUSIC_SUCCESS:
                    try {
                        MusicListData data = (MusicListData) new MusicFormat().format(msg.obj.toString());
                        textView.setText(data.getMusicDatas()[0].getSongName() + "," + data.getMusicDatas()[0].getSingerName());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case FALL:
                    Toast.makeText(MainActivity.this, "失败，返回码：" + msg.obj, Toast.LENGTH_LONG).show();
                    break;
                default:
            }
        }
    }

    class TimeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME_OK:
                    long time = System.currentTimeMillis();
                    CharSequence timeStr = DateFormat.format("hh:mm:ss", time);
                    textView.setText(timeStr);
                    break;
                default:
                    break;
            }
        }
    }
}