package com.libi.ui;

import android.app.Activity;
import android.hardware.camera2.CameraAccessException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.ezvizuikit.open.EZUIError;
import com.ezvizuikit.open.EZUIKit;
import com.ezvizuikit.open.EZUIPlayer;
import com.libi.R;

import java.util.Calendar;

/**
 * Created by surface on 2018/11/12.
 * 用于打开摄像头
 */

public class CamearActivity extends AppCompatActivity {
    private static final String TAG = "摄像头";
    private EZUIPlayer ezuiPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camear_layout);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        findView();
        init();
    }

    private void findView() {
        ezuiPlayer = findViewById(R.id.ezui_camear);
    }

    private void init() {
        EZUIKit.initWithAppKey(getApplication(),getString(R.string.ezui_appkey));
        EZUIKit.setAccessToken(getString(R.string.ezui_token));
        ezuiPlayer = findViewById(R.id.ezui_camear);
        ezuiPlayer.setCallBack(new EZUIPlayer.EZUIPlayerCallBack() {
            @Override
            public void onPlaySuccess() {
                Log.w(TAG, "播放成功");
            }

            @Override
            public void onPlayFail(EZUIError ezuiError) {
                Log.w(TAG, "播放失败,原因:" + ezuiError.getErrorString());
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
                Log.w(TAG, "播放结束");
            }
        });
        ezuiPlayer.setUrl(getString(R.string.ezui_url_open));
        ezuiPlayer.startPlay();
        Toast.makeText(this,"开始播放",Toast.LENGTH_SHORT).show();
    }


}
