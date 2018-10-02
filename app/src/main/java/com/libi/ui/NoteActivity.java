package com.libi.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.libi.R;
import com.libi.data.NoteData;
import com.libi.data.NoteListData;
import com.libi.ui.adapter.NoteAdapter;
import com.libi.ui.service.NoteSQLHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by surface on 2018/9/23.
 */

public class NoteActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,EventListener{
    public static final int CREATE_NEW_PAGE = -1;
    public static final int NEW_ID = -2;
    private NoteListData data;
    private SQLiteDatabase database;
    private ImageView iconImage;
    private ImageView addNewNoteImage;
    private ImageView saveImage;
    private Button sayBottom;
    private ListView activityNoteList;
    private EditText editText;
    private TextView timeTextView;
    private ScrollView scrollView;
    private Button noteDelete;

    private boolean isNew = false;
    private int thisPageId;
    private long thisPageTimeLine;
    private NoteClickListener clickListener;

    private EventManager asr;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_activity_layout);
        findView();
        setLisenter();
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        //隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //获取列表
        NoteSQLHelper helper = new NoteSQLHelper(this, null, 1);
        database = helper.getWritableDatabase();
        updateList();
        //获取传入的数据
        int count = getIntent().getIntExtra("count", 0);
        changeContext(count);
        //初始化百度语音控件
        asr = EventManagerFactory.create(this, "asr");
        asr.registerListener(this);
    }

    private void setLisenter() {
        activityNoteList.setOnItemClickListener(this);
        clickListener = new NoteClickListener();
        iconImage.setOnClickListener(clickListener);
        addNewNoteImage.setOnClickListener(clickListener);
        saveImage.setOnClickListener(clickListener);
        sayBottom.setOnClickListener(clickListener);
        scrollView.setOnClickListener(clickListener);
        timeTextView.setOnClickListener(clickListener);
        noteDelete.setOnClickListener(clickListener);
    }

    private void findView() {
        iconImage = findViewById(R.id.note_icon);
        addNewNoteImage = findViewById(R.id.note_activity_add_new_note);
        saveImage = findViewById(R.id.note_save);
        sayBottom = findViewById(R.id.note_say);
        activityNoteList = findViewById(R.id.note_left_list);
        editText = findViewById(R.id.note_activity_context);
        timeTextView = findViewById(R.id.note_activity_time);
        scrollView = findViewById(R.id.scroll);
        noteDelete = findViewById(R.id.note_activity_delete);
    }

    //开始发送语音
    private void start() {
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        String event = SpeechConstant.ASR_START;

        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        String json = null;
        json = new JSONObject(params).toString();
        asr.send(event, json, null, 0, 0);
        printLog("json:" + json);
    }

    //更新左侧的列表
    private void updateList() {
        data = NoteSQLHelper.selectAll(database);
        activityNoteList.setAdapter(new NoteAdapter(this, R.layout.note_item_layout,data));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        changeContext(i);
    }

    //改变右边的显示内容
    private void changeContext(int count) {
        if (count == CREATE_NEW_PAGE || data.getCount() == 0) {
            thisPageTimeLine = System.currentTimeMillis();
            timeTextView.setText(getTimeStr(thisPageTimeLine));
            editText.setText("");
            thisPageId = NEW_ID;
            isNew = true;
        }else {
            NoteData noteData = data.getDatas()[count];
            String timeStr = getTimeStr(noteData.getTimeLine());
            timeTextView.setText(timeStr);
            editText.setText(noteData.getContex());
            thisPageId = noteData.getId();
            isNew = false;
        }
    }

    //时间戳换成时间
    private String getTimeStr(Long timeLine) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        return simpleDateFormat.format(new Date(timeLine));
    }

    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        String logTxt = "name" + name;
        String result = "";

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
                if("final_result".equals(json.getString("result_type"))) {
                    result = json.get("best_result").toString();
                    editText.append(result);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        printLog("MY:params:" + params);
        printLog("MYLOG:" + logTxt);
    }

    private void printLog(String s) {
        Log.w("语音", s);
    }

    public class NoteClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.note_activity_add_new_note:       //创建
                    changeContext(CREATE_NEW_PAGE);
                    break;
                case R.id.note_save:                        //保存
                    if (thisPageId == NEW_ID) {
                        NoteSQLHelper.insert(database,editText.getText().toString(),thisPageTimeLine);
                    }else {
                        NoteSQLHelper.update(database,thisPageId,editText.getText().toString());
                    }
                    Toast.makeText(NoteActivity.this,"保存成功！",Toast.LENGTH_SHORT).show();
                    //刷新一下列表
                    updateList();
                    isNew = false;
                    thisPageId = data.getDatas()[data.getCount()-1].getId();
                    break;
                case R.id.note_icon:                        // 返回
                    finish();
                    break;
                case R.id.note_say:                         //开始调用语音
                    start();
                    Toast.makeText(NoteActivity.this,"请说话",Toast.LENGTH_LONG).show();
                    break;
//            case R.id.note_activity_time:
//            case R.id.scroll:                           //获得焦点
//                editText.setFocusable(true);
//                editText.setFocusableInTouchMode(true);
//                editText.requestFocus();
//                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//                Toast.makeText(this,"获取焦点",Toast.LENGTH_SHORT).show();
//                break;
                case R.id.note_activity_delete:                 //删除
                    int id = thisPageId;
                    NoteSQLHelper.delete(database,id);
                    updateList();
                    changeContext(0);
                    Toast.makeText(NoteActivity.this, "刚刚选中的界面已经删除!", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    }

    //废弃
//    @SuppressLint("ResourceAsColor")
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private void setStatusBarColor() {
//        Window window = getWindow();
////        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
////        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
////        window.setStatusBarColor(R.color.note_default_color);
////        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
////        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
////        View mChildView = mContentView.getChildAt(0);
////        if (mChildView != null) {
////            ViewCompat.setFitsSystemWindows(mChildView, false);
////            ViewCompat.requestApplyInsets(mChildView);
////        }
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//
//    }
}
