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
import com.ezvizuikit.open.EZUIError;
import com.ezvizuikit.open.EZUIKit;
import com.ezvizuikit.open.EZUIPlayer;
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
import com.libi.util.SpeakTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,EventListener {

    private Button mWeatherButton;
    private Button mNewsButton;
    private Button mMusicButtonutton;
    private Button mTestButton;
    private Button mASRButton;
    private Button mWeakUpButton;
    private Button mSpeak;
    private Button mPlayEZUI;
    private TextView textView;
    private EZUIPlayer ezuiPlayer;

    private static final int WEATHER_SUCCESS = 0;
    private static final int NEWS_SUCCESS = 1;
    private static final int MUSIC_SUCCESS = 2;
    private static final int FALL = 9;
    private static final int TIME_OK = 100;

    private ProgressDialog progressDialog;

    private CloseDialogHandler mHandler;
    private TimeHandler mTimeHandler;

    private EventManager asr;
    private EventManager weakUp;
    private SpeakTool speakTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        init();
        initEZUI();
        setLisenter();
        initPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
        weakUp.send(SpeechConstant.WAKEUP_STOP, "{}", null, 0, 0);
    }

    private void init() {
        mHandler = new CloseDialogHandler();
        mTimeHandler = new TimeHandler();
        progressDialog = new ProgressDialog(this);
        asr = EventManagerFactory.create(this, "asr");
        weakUp = EventManagerFactory.create(this, "wp");
        speakTool = new SpeakTool(this, "这是一段测试语音",textView);
        initEZUI();

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

    private void initEZUI() {
        EZUIKit.initWithAppKey(getApplication(),getString(R.string.ezui_appkey));
        EZUIKit.setAccessToken(getString(R.string.ezui_token));
        ezuiPlayer = findViewById(R.id.ezuiplayer);
        ezuiPlayer.setUrl(getString(R.string.ezui_url_open));
        ezuiPlayer.setCallBack(new EZUIPlayer.EZUIPlayerCallBack() {
            @Override
            public void onPlaySuccess() {
                textView.append("\n播放成功");
            }

            @Override
            public void onPlayFail(EZUIError ezuiError) {
                textView.append("\n播放失败\n"+ezuiError.getErrorString());
            }

            @Override
            public void onVideoSizeChange(int i, int i1) {

            }

            @Override
            public void onPrepared() {

            }

            @Override
            public void onPlayTime(Calendar calendar) {

            }

            @Override
            public void onPlayFinish() {

            }
        });
    }

    private void setLisenter() {
        mMusicButtonutton.setOnClickListener(this);
        mNewsButton.setOnClickListener(this);
        mWeatherButton.setOnClickListener(this);
        mTestButton.setOnClickListener(this);
        mASRButton.setOnClickListener(this);
        mWeakUpButton.setOnClickListener(this);
        mPlayEZUI.setOnClickListener(this);
        asr.registerListener(this);
        mSpeak.setOnClickListener(this);
        weakUp.registerListener(new EventListener() {
            @Override
            public void onEvent(String name, String params, byte[] data, int offset, int length) {
                String logTxt = "name: " + name;
                if (params != null && !params.isEmpty()) {
                    logTxt += " ;params :" + params;
                } else if (data != null) {
                    logTxt += " ;data length=" + data.length;
                }
                printLog(logTxt);
            }
        });
    }

    private void findView() {
        mWeatherButton = findViewById(R.id.weather);
        mNewsButton = findViewById(R.id.news);
        mMusicButtonutton = findViewById(R.id.music);
        mASRButton = findViewById(R.id.asr);
        mTestButton = findViewById(R.id.ui);
        mWeakUpButton = findViewById(R.id.weak_up);
        mPlayEZUI = findViewById(R.id.ezuiplay);
        mSpeak = findViewById(R.id.speak);
        textView = findViewById(R.id.result);
    }

    //动态申请权限
    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.MODIFY_PHONE_STATE,
                Manifest.permission.PROCESS_OUTGOING_CALLS,
                Manifest.permission.READ_CALL_LOG
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
                break;
            case R.id.asr:
                asrStart();
                Toast.makeText(this, "请说话", Toast.LENGTH_SHORT).show();
                break;
            case R.id.weak_up:
                weakStart();
                Toast.makeText(this, "开始唤醒", Toast.LENGTH_SHORT).show();
                break;
            case R.id.speak:
                speakTool.speak("这是更改后的语音");
                break;
            case R.id.ezuiplay:
                ezuiPlayer.startPlay();
                Intent startWeb = new Intent(MainActivity.this, WebActivty.class);
                startWeb.putExtra("url", getString(R.string.ezui_url_h5));
                startActivity(startWeb);
                break;
            default:
                break;
        }
    }

    private void asrStart() {
        textView.append("\n");
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        String event = SpeechConstant.ASR_START;

        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        String json = null;
        json = new JSONObject(params).toString();
        asr.send(event, json, null, 0, 0);
        printLog("json:" + json);
    }

    private void weakStart() {
        textView.setText("");
        Map<String, Object> params = new TreeMap<String, Object>();

        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin");
        // "assets:///WakeUp.bin" 表示WakeUp.bin文件定义在assets目录下

        String json = null; // 这里可以替换成你需要测试的json
        json = new JSONObject(params).toString();
        weakUp.send(SpeechConstant.WAKEUP_START, json, null, 0, 0);
        printLog("输入参数：" + json);
    }

    private void weakStop() {
        weakUp.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0);
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
        textView.append("\n"+s);
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
