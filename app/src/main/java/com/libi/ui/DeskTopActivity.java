package com.libi.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.libi.R;
import com.libi.connection.MusicListConnection;
import com.libi.connection.NewsConnection;
import com.libi.connection.RequestData;
import com.libi.connection.WeatherConnection;
import com.libi.data.MusicData;
import com.libi.data.MusicListData;
import com.libi.data.NewsListData;
import com.libi.data.NoteData;
import com.libi.data.NoteListData;
import com.libi.data.SingleDayWeatherData;
import com.libi.data.WeatherData;
import com.libi.data.WeatherDetalData;
import com.libi.format.MusicFormat;
import com.libi.format.NewsFormat;
import com.libi.format.WeatherFormat;
import com.libi.ui.adapter.NewsAdapter;
import com.libi.ui.adapter.NoteAdapter;
import com.libi.ui.fragment.NewsFragment;
import com.libi.ui.service.MusicMediaHelper;
import com.libi.ui.service.NoteSQLHelper;
//import com.libi.ui.service.MusicService;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by surface on 2018/9/11.
 */

public class DeskTopActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private TextView time;
    private TextView date;
    private TextView week;

    private MusicMediaHelper helper;
    private MusicListData musicListData;

    private static final int CONNECT_WEATHER = 10;
    private static final int CONNECT_NEWS = 11;
    private static final int CONNECT_MUSIC = 12;
    private static final int CONNECT_NOTE = 13;
    private static final int WEATHER_SUCCESS = 0;
    private static final int NEWS_SUCCESS = 1;
    private static final int MUSIC_SUCCESS = 2;
    private static final int NOTE_SUCCESS = 3;
    private static final int FALL = 9;
    private static final int TIME_OK = 100;

    private ProgressDialog progressDialog;

    private CloseDialogHandler mHandler;
    private TimeHandler mTimeHandler;
    private ListView newsList;
    private ListView noteList;
    private ImageView todayWeatherImage;
    private ImageView day1WeatherImage;
    private ImageView day2WeatherImage;
    private ImageView day3WeatherImage;
    private TextView air;
    private TextView number;
    private TextView day1Week;
    private TextView day2Week;
    private TextView day3Week;
    private TextView day1Number;
    private TextView day2Number;
    private TextView day3Number;
    private TextView musicNamew;
    private TextView musicTime;
    private Button musicPlay;
    private Button musicNext;
    private SeekBar musicSeekbar;
    private NewsListData newsListData;
    private TextView newsDetail;
    private Button musicDetail;
    private Button noteDetail;

    private NoteSQLHelper noteSQLHelper;
    private SQLiteDatabase database;
    private NoteListData noteListData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.desktop_layout);
        findView();
        setListener();
        init();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        connectAll();
    }

    private void init() {
        mHandler = new CloseDialogHandler();
        mTimeHandler = new TimeHandler();
        //加载数据库
        noteSQLHelper = new NoteSQLHelper(this, null, 1);
        database = noteSQLHelper.getWritableDatabase();

        //开始计时
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        //Log.w("时间", "计时");
                        Message message = Message.obtain();
                        message.what = TIME_OK;
                        mTimeHandler.sendMessage(message);
                        Thread.sleep(1000);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        connectAll();


        //测试代码------------------
//        NoteSQLHelper.insert(database,"sqlite1",System.currentTimeMillis());
//        NoteSQLHelper.insert(database,"sqlite2",System.currentTimeMillis());
//        NoteSQLHelper.insert(database,"sqlite3",System.currentTimeMillis());

        //测试代码-------------------
    }

    //重新加载所有东西
    private void connectAll() {
        noteListData = NoteSQLHelper.selectAll(database);
        //加载新闻
        connect(CONNECT_NEWS);
        //加载天气
        connect(CONNECT_WEATHER);
        //加载音乐
        connect(CONNECT_MUSIC);
        //加载便签
        connect(CONNECT_NOTE);

    }

    private void setListener() {
        musicPlay.setOnClickListener(this);
        musicNext.setOnClickListener(this);
        musicDetail.setOnClickListener(this);

        newsDetail.setOnClickListener(this);

        noteDetail.setOnClickListener(this);
        newsList.setOnItemClickListener(this);
        noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO 点击进入编辑页面
                Intent startNote = new Intent(DeskTopActivity.this, NoteActivity.class);
                NoteData data = noteListData.getDatas()[i];
                startNote.putExtra("count", i);
                startActivity(startNote);
            }
        });
    }

    private void findView() {
        //时间
        time = findViewById(R.id.time);
        date = findViewById(R.id.date);
        week = findViewById(R.id.week);

        //天气
        todayWeatherImage = findViewById(R.id.today_weather_image);
        day1WeatherImage = findViewById(R.id.day1_weather);
        day2WeatherImage = findViewById(R.id.day2_weather);
        day3WeatherImage = findViewById(R.id.day3_weather);
        air = findViewById(R.id.today_air);
        number = findViewById(R.id.today_number);
        day1Week = findViewById(R.id.day1_week);
        day2Week = findViewById(R.id.day2_week);
        day3Week = findViewById(R.id.day3_week);
        day1Number = findViewById(R.id.day1_number);
        day2Number = findViewById(R.id.day2_number);
        day3Number = findViewById(R.id.day3_number);

        //新闻
        newsList = findViewById(R.id.news_list);
        newsDetail = findViewById(R.id.news_detail);

        //音乐
        musicNamew = findViewById(R.id.music_name);
        musicTime = findViewById(R.id.music_time);
        musicPlay = findViewById(R.id.music_play);
        musicNext = findViewById(R.id.music_next);
        musicSeekbar = findViewById(R.id.music_seek_bar);
        musicDetail = findViewById(R.id.music_detail);

        //便签
        noteList = findViewById(R.id.note_list);
        noteDetail = findViewById(R.id.note_detail);

    }

    /**
     * 每做一次连接，相应的ui都会更新
     * 具体的执行代码在对应的handler里
     *
     * @param code
     */
    private void connect(final int code) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                RequestData data = null;
                try {
                    switch (code) {
                        case CONNECT_WEATHER:
                            Log.e("连接", "天气");
                            data = new WeatherConnection("101210101").connect();
                            message.what = WEATHER_SUCCESS;
                            break;
                        case CONNECT_NEWS:
                            Log.e("连接", "新闻");
                            data = new NewsConnection("了").connect();
                            message.what = NEWS_SUCCESS;
                            break;
                        case CONNECT_MUSIC:
                            Log.e("连接", "音乐");
                            data = new MusicListConnection().connect();
                            message.what = MUSIC_SUCCESS;
                            break;
                        case CONNECT_NOTE:
                            Log.e("连接", "便签");
                            /* 手动200 */
                            data = new RequestData();
                            data.setRequestCode(200);

                            message.what = NOTE_SUCCESS;
                            break;
                        default:
                            Log.e("连接", "没有这个命令");
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (data != null && data.getRequestCode() == 200) {
                    message.obj = data.getRequestData();
                } else {
                    message.what = FALL;
                    message.obj = Integer.valueOf(data.getRequestCode());
                }
                mHandler.sendMessage(message);
            }
        }).start();
    }

    private void setNewsAdapter(NewsListData data) {
        newsList.setAdapter(new NewsAdapter(DeskTopActivity.this, R.layout.news_item_layout, data));
    }

    private void setNoteAdapter(NoteListData data) {
        //-------------------测试代码------------------------
//        data = null;
//        data = new NoteListData();
//        NoteData[] dataItem = new NoteData[2];
//        NoteData data1 = new NoteData();
//        data1.setContex("123456");
//        data1.setTimeLine(System.currentTimeMillis());
//        NoteData data2 = new NoteData();
//        data2.setContex("1234567");
//        data2.setTimeLine(System.currentTimeMillis());
//        dataItem[0] = data1;
//        dataItem[1] = data2;
//        data.setCount(2);
//        data.setDatas(dataItem);
        //--------------------测试代码--------------------------
        noteList.setAdapter(new NoteAdapter(DeskTopActivity.this, R.layout.note_item_layout, data));
        Log.w("桌面", "调用适配器");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateWeatherView(WeatherData data) {
        SingleDayWeatherData today = data.getToday();
        SingleDayWeatherData day1 = data.getTomorrow();
        SingleDayWeatherData day2 = data.getDayAfterTomorrow();
        SingleDayWeatherData day3 = data.getDayAfterTomorrow();
        WeatherDetalData deta1 = data.getDetalData();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));

        //设置天气的周数
        calendar.add(Calendar.DAY_OF_WEEK, 1);
        day1Week.setText(getWeek(calendar.get(Calendar.DAY_OF_WEEK)));
        calendar.add(Calendar.DAY_OF_WEEK, 1);
        day2Week.setText(getWeek(calendar.get(Calendar.DAY_OF_WEEK)));
        calendar.add(Calendar.DAY_OF_WEEK, 1);
        day3Week.setText(getWeek(calendar.get(Calendar.DAY_OF_WEEK)));
        //设置天气图片
        todayWeatherImage.setImageDrawable(setWeatherDrawable(today.getType()));
        day1WeatherImage.setImageDrawable(setWeatherDrawable(day1.getType()));
        day2WeatherImage.setImageDrawable(setWeatherDrawable(day2.getType()));
        day3WeatherImage.setImageDrawable(setWeatherDrawable(day3.getType()));
        //设置具体数据
        air.setText("空气" + deta1.getQuality());
        number.setText(deta1.getWendu() + "℃ 湿度:" + deta1.getShidu());
        day1Number.setText(day1.getLow() + "-" + day1.getHigh());
        day2Number.setText(day2.getLow() + "-" + day2.getHigh());
        day3Number.setText(day3.getLow() + "-" + day3.getHigh());
    }

    // TODO 更新音乐的数据，暂时废弃
//    private void updateMusic(MusicListData data) {
//        Intent intent = new Intent(this, MusicService.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("data",data);
//        bundle.putInt("option",MusicService.OPTION_NULL);
//        intent.putExtras(bundle);
//        startService(intent);
//
//        updateMusicUi(data);
//    }

    //更新歌曲的ui界面
    private void updateMusicUi(MusicListData data) {
        MusicData musicData = data.getMusicDatas()[MusicMediaHelper.count];
        musicNamew.setText(musicData.getSongName() + "-" + musicData.getSingerName());
        musicTime.setText("00:00/" + helper.getLength());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Drawable setWeatherDrawable(String weather) {
        Drawable drawable = null;
        switch (weather) {
            case "多云":
                drawable = getDrawable(R.drawable.ic_cloud);
                break;
            case "晴":
                drawable = getDrawable(R.drawable.ic_sun);
                break;
            case "阴":
                drawable = getDrawable(R.drawable.ic_no_sun);
                break;
            case "小雨":
                drawable = getDrawable(R.drawable.ic_rain_small);
                break;
            case "小到中雨":
            case "中雨":
                drawable = getDrawable(R.drawable.ic_rain_middle);
                break;
            case "大雨":
            case "中到大雨":
                drawable = getDrawable(R.drawable.ic_rain_big);
                break;
            case "大到暴雨":
            case "暴雨":
                drawable = getDrawable(R.drawable.ic_rain_plus);
                break;
            case "大暴雨":
            case "暴雨到大暴雨":
                drawable = getDrawable(R.drawable.ic_rain_plus_plus);
                break;
            case "特大暴雨":
            case "大暴雨到特到暴雨":
                drawable = getDrawable(R.drawable.ic_rain_plus_plus_plus);
                break;
            case "雨夹雪":
                drawable = getDrawable(R.drawable.ic_rain_snow);
                break;
            case "阵雪":
                drawable = getDrawable(R.drawable.ic_rain_snow);
                break;
            case "雾":
            case "沙尘暴":
            case "浮尘":
            case "扬沙":
            case "强沙尘暴":
            case "雾霾":
                drawable = getDrawable(R.drawable.ic_fog);
                break;
            case "冻雨":
                drawable = getDrawable(R.drawable.ic_rain_freeze);
                break;
            case "雷阵雨伴有冰雹":
                drawable = getDrawable(R.drawable.ic_rain_thunder_ice);
                break;
            case "阵雨":
                drawable = getDrawable(R.drawable.ic_rain);
                break;
            case "雷阵雨":
                drawable = getDrawable(R.drawable.ic_rain_thunder);
                break;
            case "无天气类型":
            default:
                drawable = getDrawable(R.drawable.detail);
                break;
        }
        return drawable;
    }

    private String getWeek(int code) {
        String weekStr = "周";
        switch (code) {
            case 1:
                weekStr = weekStr + "日";
                break;
            case 2:
                weekStr = weekStr + "一";
                break;
            case 3:
                weekStr = weekStr + "二";
                break;
            case 4:
                weekStr = weekStr + "三";
                break;
            case 5:
                weekStr = weekStr + "四";
                break;
            case 6:
                weekStr = weekStr + "五";
                break;
            case 7:
                weekStr = weekStr + "六";
                break;
            default:
                break;
        }
        return weekStr;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
//        Intent intent = new Intent(DeskTopActivity.this,MusicService.class);
//        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.music_play:
//                if(MusicService.state == MusicService.STATE_STOP || MusicService.state == MusicService.STATE_PAUSE) {
//                    bundle.putInt("option", MusicService.OPTION_START);
//                    musicPlay.setBackground(getDrawable(R.drawable.music_pause));
//                }
//                else if(MusicService.state == MusicService.STATE_PLAY){
//                    bundle.putInt("option",MusicService.OPTION_PAUSE);
//                    musicPlay.setBackground(getDrawable(R.drawable.music_play));
//                }
                if (MusicMediaHelper.isPlay == false) {
                    helper.start();
                    musicPlay.setBackground(getDrawable(R.drawable.music_pause));
                } else {
                    helper.pause();
                    musicPlay.setBackground(getDrawable(R.drawable.music_play));
                }
                break;
            case R.id.music_next:
                //bundle.putInt("option",MusicService.OPTION_NEXT);
                MusicMediaHelper.count++;
                if (MusicMediaHelper.count >= musicListData.getCount()) {
                    MusicMediaHelper.count = 0;
                }

                String url = getUrl(musicListData.getMusicDatas()[MusicMediaHelper.count]);
                Log.e("桌面", "next url:" + url);
                try {
                    helper.next(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                musicPlay.setBackground(getDrawable(R.drawable.music_pause));
                updateMusicUi(musicListData);
                break;
            case R.id.news_detail:
                Intent newsDetail = new Intent(DeskTopActivity.this, WebActivty.class);
                newsDetail.putExtra("url", "http://sh.qihoo.com/");
                startActivity(newsDetail);
                break;
            case R.id.music_detail:
                Intent musicDetail = new Intent(DeskTopActivity.this, WebActivty.class);
                musicDetail.putExtra("url", "https://y.qq.com/");
                startActivity(musicDetail);
                break;
            case R.id.note_detail:
                Intent startNote = new Intent(DeskTopActivity.this, NoteActivity.class);
                startNote.putExtra("count", 0);
                startActivity(startNote);
                break;
            default:
                break;
        }
//        intent.putExtras(bundle);
//        startService(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String url = newsListData.getNewsItemDatas()[i].getUrl();
        Log.e("点击新闻", "url:" + url + "count:" + i);
        Intent webIntent = new Intent(DeskTopActivity.this, WebActivty.class);
        webIntent.putExtra("url", url);
        startActivity(webIntent);
    }

    class TimeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME_OK:
                    long times = System.currentTimeMillis();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date(times));

                    CharSequence timeStr = DateFormat.format("HH:mm", times);
                    String dateStr = calendar.get(Calendar.MONTH) + 1 + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日";
                    String weekStr = getWeek(calendar.get(Calendar.DAY_OF_WEEK));
                    //更新ui
                    time.setText(timeStr);
                    date.setText(dateStr);
                    week.setText(weekStr);
                    break;
                default:
                    Log.e("时间", "出现了未知的情况");
                    break;
            }
        }
    }

    class CloseDialogHandler extends Handler {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //progressDialog.dismiss();
            switch (msg.what) {
                case WEATHER_SUCCESS:
                    try {
                        WeatherData data = (WeatherData) new WeatherFormat().format(msg.obj.toString());
                        updateWeatherView(data);
                        //textView.setText(data.getToday().getDate() + "," + data.getToday().getHigh() + "," + data.getToday().getLow() + "," + data.getToday().getType());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    textView.setText(msg.obj.toString());
                    break;
                case NEWS_SUCCESS:
                    try {
                        NewsListData data = (NewsListData) new NewsFormat().format(msg.obj.toString());
                        setNewsAdapter(data);
                        newsListData = data;
                        //textView.setText(data.getNewsItemDatas()[0].getTitle());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        //handleMessage(msg);
                    }
                    //textView.setText(msg.obj.toString());
                    break;
                case MUSIC_SUCCESS:
                    try {
                        MusicListData data = (MusicListData) new MusicFormat().format(msg.obj.toString());
                        musicListData = data;
                        String url = getUrl(data.getMusicDatas()[MusicMediaHelper.count]);
                        helper = new MusicMediaHelper(new NextMusicHandler(), url, musicSeekbar, musicTime);
                        updateMusicUi(data);
                        //Intent intent = new Intent(DeskTopActivity.this, MusicService.class);
                        //textView.setText(data.getMusicDatas()[0].getSongName() + "," + data.getMusicDatas()[0].getSingerName());
                        //updateMusic(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case NOTE_SUCCESS:
                    setNoteAdapter(noteListData);
                    break;
                case FALL:
                    Toast.makeText(DeskTopActivity.this, "失败，返回码：" + msg.obj, Toast.LENGTH_LONG).show();
                    break;
                default:
                    Log.w("处理器", "没有对应的消息序号");
            }
        }
    }

    public class NextMusicHandler extends Handler {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    MusicMediaHelper.count++;
                    if (MusicMediaHelper.count >= musicListData.getCount()) {
                        MusicMediaHelper.count = 0;
                    }

                    String url = getUrl(musicListData.getMusicDatas()[MusicMediaHelper.count]);
                    Log.e("桌面", "next url:" + url);
                    try {
                        helper.next(url);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    musicPlay.setBackground(getDrawable(R.drawable.music_pause));
                    updateMusicUi(musicListData);
                    super.handleMessage(msg);
                    break;
                case 1:
                    Toast.makeText(DeskTopActivity.this, "播放错误", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    }

    private String getUrl(MusicData musicData) {
        return "http://ws.stream.qqmusic.qq.com/C100" + musicData.getSongMid() + ".m4a?fromtag=0&guid=126548448";
    }
}
