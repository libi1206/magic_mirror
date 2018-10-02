package com.libi.data;

/**
 * Created by surface on 2018/8/24.
 */

public class SingleDayWeatherData implements Data {
    public static final String CLOUDY = "多云";
    public static final String SUNNY = "晴";
    public static final String SHADY = "阴";
    public static final String RAIN = "阵雨";
    public static final String SMALL_RAIN = "小雨";
    public static final String MEDIUM_RAIN = "中雨";
    public static final String BIG_RAIN = "大雨";
    public static final String RAINSTORM = "暴雨";
    public static final String RAINSTORM_PLUS = "大暴雨";
    public static final String RAINSTORM_PLUS_PLUS = "特大暴雨";
    public static final String THUNDERSHOWER = "雷阵雨";
    public static final String ICE_RAIN = "冻雨";
    public static final String S2M_RAIN = "小到中雨";
    public static final String M2B_RAIN = "中到大雨";
    public static final String B2R_RAIN = "大到暴雨";
    public static final String R2P_RAIN = "暴雨到大暴雨";
    public static final String P2PP_RAIN = "大暴雨到特大暴雨";
    public static final String T_AND_ICE ="雷阵雨伴有冰雹";
    public static final String SLEET = "雨夹雪";
    public static final String SNOW = "阵雪";
    public static final String FOG = "雾";
    public static final String HAZE = "雾霾";
    public static final String SAND_STORM = "沙尘暴";
    public static final String DUST = "浮尘";
    public static final String SAND = "扬沙";
    public static final String SAND_STORM_PLUS = "强沙尘暴";
    public static final String NULL = "无天气类型";

    private String date;
    private String high;
    private String low;
    private String Type;

    public String getDate() {
        return date;
    }
    public String  getHigh() {
        return high;
    }
    public String  getLow() {
        return low;
    }
    public String getType() {
        return Type;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public void setHigh(String  high) {
        this.high = high;
    }
    public void setLow(String  low) {
        this.low = low;
    }
    public void setType(String type) {
        Type = type;
    }
}
