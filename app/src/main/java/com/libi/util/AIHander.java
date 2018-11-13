package com.libi.util;

import android.app.Activity;
import android.text.format.DateFormat;

import com.libi.R;
import com.libi.data.Data;
import com.libi.data.SingleDayWeatherData;
import com.libi.data.WeatherData;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by surface on 2018/10/26.
 *  用于伪人工智能的处理
 */

public class AIHander {
    public static final int NULL_CMD = -10;
    public static final int PAUSE_MUSIC = -11;
    public static final int PLAY_MUSIC = -12;
    public static final int NEXT_MUSIC = -13;
    public static final int EZUI = -14;
    private final Activity activity;

    private WeatherData weatherData;
    private SpeakTool speakTool;

    public AIHander(SpeakTool speakTool,Activity activity,WeatherData weatherData) {
        this.activity = activity;
        this.speakTool = speakTool;
        this.weatherData = weatherData;
    }

    public int handle(String command) {
        String reply = "";
        if (command.contains("天气")) {
            SingleDayWeatherData data;
            if (command.contains("今天")) {
                data = weatherData.getToday();
                reply += "今天的天气是：" + data.getType() + "，"+data.getHigh() + "到" + data.getLow();
                speakTool.speak(reply);
                return NULL_CMD;
            } else if (command.contains("明天")) {
                data = weatherData.getTomorrow();
                reply += "明天的天气是：" + data.getType() + "，"+data.getHigh() + "到" + data.getLow();
                speakTool.speak(reply);
                return NULL_CMD;
            } else if (command.contains("后天")) {
                data = weatherData.getDayAfterTomorrow();
                reply += "后天的天气是：" + data.getType() + "，"+data.getHigh() + "到" + data.getLow();
                speakTool.speak(reply);
                return NULL_CMD;
            } else if (command.contains("大后天")) {
                data = weatherData.getThreeDayFrom();
                reply += "今天的天气是：" + data.getType() + "，"+data.getHigh() + "到" + data.getLow();
                speakTool.speak(reply);
                return NULL_CMD;
            }
            data = weatherData.getToday();
            reply += "今天的天气是：" + data.getType() + "，"+data.getHigh() + "到" + data.getLow();
            speakTool.speak(reply);
            return NULL_CMD;
        }

        if (command.contains("几点了") || command.contains("时间")) {
            long times = System.currentTimeMillis();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(times));

            CharSequence timeStr = DateFormat.format("HH点mm分", times);
            String dateStr = calendar.get(Calendar.YEAR)+"年"+calendar.get(Calendar.MONTH) + 1 + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日";
            String weekStr = getWeek(calendar.get(Calendar.DAY_OF_WEEK));
            reply = "现在是"+dateStr+","+weekStr+","+timeStr;
            speakTool.speak(reply);
            return NULL_CMD;
        }

        if (command.contains("播放音乐") || command.contains("放一首歌") || command.contains("放首歌")) {
            speakTool.speak(activity.getString(R.string.reply_ok));
            return PLAY_MUSIC;
        }

        if (command.contains("关掉音乐") || command.contains("停止播放")) {
            speakTool.speak(activity.getString(R.string.reply_ok));
            return PAUSE_MUSIC;
        }

        if (command.contains("下一首") || command.contains("切歌") || command.contains("切割")) {
            speakTool.speak(activity.getString(R.string.reply_ok));
            return NEXT_MUSIC;
        }

        if (command.contains("打开摄像头")) {
            speakTool.speak(activity.getString(R.string.reply_ok));
            return EZUI;
        }

        speakTool.speak(activity.getString(R.string.reply_dont_know));
        return NULL_CMD;
    }

    private String getWeek(int code) {
        String weekStr = "星期";
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
}
