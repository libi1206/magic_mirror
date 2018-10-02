package com.libi.data;

/**
 * Created by surface on 2018/8/27.
 */

public class WeatherDetalData implements Data {
    private String shidu;
    private int pm25;
    private int pm10;
    private String quality;
    private String  wendu;
    private String gammao;

    public int getPm10() {
        return pm10;
    }
    public int getPm25() {
        return pm25;
    }
    public String getQuality() {
        return quality;
    }
    public String  getWendu() {
        return wendu;
    }
    public String getGammao() {
        return gammao;
    }
    public String getShidu() {
        return shidu;
    }

    public void setGammao(String gammao) {
        this.gammao = gammao;
    }
    public void setPm10(int pm10) {
        this.pm10 = pm10;
    }
    public void setPm25(int pm25) {
        this.pm25 = pm25;
    }
    public void setShidu(String shidu) {
        this.shidu = shidu;
    }
    public void setQuality(String quality) {
        this.quality = quality;
    }
    public void setWendu(String wendu) {
        this.wendu = wendu;
    }
}
