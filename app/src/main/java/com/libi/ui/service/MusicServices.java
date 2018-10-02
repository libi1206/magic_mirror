package com.libi.ui.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.libi.data.MusicListData;

import java.io.IOException;

/**
 * Created by surface on 2018/9/15.
 */

public class MusicServices extends Service {
    private static final String TAG = "音乐服务";
    private MusicListData data;
    private MediaPlayer mediaPlayer;
    public static int state;
    public static int count;
    public static boolean isInit = false;

    public static final int OPTION_START = 1000;
    public static final int OPTION_PAUSE = 1001;
    public static final int OPTION_CONTINUE = 1002;
    public static final int OPTION_STOP = 1003;
    public static final int OPTION_JUMP = 1004;
    public static final int OPTION_NEXT = 1005;
    public static final int OPTION_NULL = 1006;

    public static final int STATE_PLAY = 1005;
    public static final int STATE_PAUSE = 1006;
    public static final int STATE_STOP = 1007;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        state = STATE_STOP;
        count = 0;
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setLooping(false);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        Log.w(TAG, "创建服务");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w(TAG, "开始服务，id:" + startId);
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if(data == null) {
                data = (MusicListData) intent.getSerializableExtra("data");
                try {
                    setSong();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            switch (bundle.getInt("option")) {
                case OPTION_START:
                    if (!isInit) {
                        try {
                            prepare();
                            Log.w(TAG, "初始化");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        play();
                    }
                    Log.w(TAG, "OPTION_START");
                    break;
                case OPTION_CONTINUE:
                    play();
                    Log.w(TAG,"OPTION_CONTINUE");
                    break;
                case OPTION_STOP:
                    stop();
                    Log.w(TAG, "OPTION_STOP");
                    break;
                case OPTION_PAUSE:
                    pause();
                    Log.w(TAG, "OPTION_PAUSE");
                    break;
                case OPTION_NEXT:
                    try {
                        next();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.w(TAG, "OPTION_NEXT");
                    break;
                case OPTION_NULL:
                default:
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        stopSelf();
    }

    private void prepare() throws IOException {
        if (mediaPlayer != null) {
            mediaPlayer.prepare();
            state = STATE_PLAY;
        }
    }


    private void play() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            state = STATE_PLAY;
        }
    }

    private void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            state = STATE_PAUSE;
        }
    }

    private void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            state = STATE_STOP;
        }
    }

    private void next() throws IOException {
        if (mediaPlayer != null) {
            count++;
            if (count == data.getCount()) {
                count = 0;
            }
            setSong();
            mediaPlayer.start();
            state = STATE_PLAY;
        }
    }

    private void setSong() throws IOException {
        String songmid = data.getMusicDatas()[count].getSongMid();
        Log.e(TAG, "mid:" + songmid);
        mediaPlayer.setDataSource("http://ws.stream.qqmusic.qq.com/C100" + songmid + ".m4a?fromtag=0&guid=126548448\n");
    }
}
