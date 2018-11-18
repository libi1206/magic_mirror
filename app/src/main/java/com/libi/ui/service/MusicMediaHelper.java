package com.libi.ui.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.libi.R;
import com.libi.ui.DeskTopActivity;

import java.io.IOException;

/**
 * Created by surface on 2018/9/16.
 *
 */

public class MusicMediaHelper implements SeekBar.OnSeekBarChangeListener {
    public static int count = 0;

    private String dataSource;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private TimeHandler handler = new TimeHandler(this);
    private Timer timer = new Timer(this);
    private Thread clock = new Thread(timer);
    private TextView musicTime;
    private DeskTopActivity.NextMusicHandler nextHandler;
    private Activity activity;

    public static boolean isPlay = false;
    private static boolean isInit = false;

    public MusicMediaHelper(Activity activity, final DeskTopActivity.NextMusicHandler handler, String url, SeekBar seekBar, TextView musicTime) throws IOException {
        this.activity = activity;
        nextHandler = handler;
        dataSource = url;
        this.seekBar = seekBar;
        mediaPlayer = MediaPlayer.create(activity,R.raw.dan);
//        mediaPlayer.setDataSource(url);

        this.musicTime = musicTime;
        clock.start();
//        mediaPlayer.prepareAsync();
        mediaPlayer.pause();
        this.seekBar = seekBar;
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setMax(mediaPlayer.getDuration());

        //异常时触发
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
//                try {
//                    mediaPlayer.setDataSource(dataSource);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                mediaPlayer.reset();
//                initSeekbar();
                Log.e("音乐", "错误");
                if (isInit)
                    handler.sendEmptyMessage(1);
                return false;
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(isInit)
                    handler.sendEmptyMessage(0);

            }
        });
    }

    public String getLength() {
        if (mediaPlayer != null) {
            return showTime(mediaPlayer.getDuration());
        }
        return null;
    }

    public void start() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            isPlay = true;
            isInit = true;
            timer.gono();
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            isPlay = false;
            timer.stop();
        }
    }

    public void next(int raw) throws IOException {
        pause();
        mediaPlayer.stop();
        mediaPlayer.reset();
        // mediaPlayer = new MediaPlayer();
        //mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(activity, raw);
//        mediaPlayer.prepare();
        start();
        //mediaPlayer.start();
        //mediaPlayer.reset();
        //mediaPlayer.prepareAsync();
        initSeekbar();
    }

    private void initSeekbar() {
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setProgress(0);
        isPlay = true;
    }

    @SuppressLint("DefaultLocale")
    private String showTime(int time) {
        time /= 1000;
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }

    //拖动时的效果：改变时间显示
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {   musicTime.setText(showTime(seekBar.getProgress()) + "/" + showTime(mediaPlayer.getDuration()));
    }

    //开始拖动时的事件：啥都不干
      @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    //停止拖动时的事件：跳转音乐
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
//        mediaPlayer.reset();
        mediaPlayer.seekTo(seekBar.getProgress());
//        start();
    }

    static class TimeHandler extends Handler {
        private MusicMediaHelper helper;
        private int count = 0;

        public TimeHandler(MusicMediaHelper helper) {
            this.helper = helper;
        }

        @Override
        public void handleMessage(Message msg) {
            helper.seekBar.setMax(helper.mediaPlayer.getDuration());
            switch (msg.what) {
                case 1:
                    helper.seekBar.setProgress(helper.mediaPlayer.getCurrentPosition());
                    Log.e("音乐", "当前播放:" + helper.mediaPlayer.getCurrentPosition());
                    break;
                default:
                    break;
            }
        }
    }

    static class Timer implements Runnable {
        private boolean flag = false;
        private MusicMediaHelper helper;

        public Timer(MusicMediaHelper helper) {
            this.helper = helper;
        }

        @Override
        public void run() {
            while (true) {
                if (flag) {
                    helper.handler.sendEmptyMessage(1);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void stop() {
            flag = false;
        }

        public void gono() {
            flag = true;
        }
    }


}

