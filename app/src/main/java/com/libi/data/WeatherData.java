package com.libi.data;

/**
 * Created by surface on 2018/8/27.
 */

public class WeatherData implements Data {
    private SingleDayWeatherData today;
    private SingleDayWeatherData tomorrow;
    private SingleDayWeatherData dayAfterTomorrow;
    private SingleDayWeatherData threeDayFrom;
    private WeatherDetalData detalData;

    public void setDetalData(WeatherDetalData detalData) {
        this.detalData = detalData;
    }
    public void setDayAfterTomorrow(SingleDayWeatherData dayAfterTomorrow) {
        this.dayAfterTomorrow = dayAfterTomorrow;
    }
    public void setThreeDayFrom(SingleDayWeatherData threeDayFrom) {
        this.threeDayFrom = threeDayFrom;
    }
    public void setToday(SingleDayWeatherData today) {
        this.today = today;
    }
    public void setTomorrow(SingleDayWeatherData tomorrow) {
        this.tomorrow = tomorrow;
    }

    public SingleDayWeatherData getToday() {
        return today;
    }
    public SingleDayWeatherData getDayAfterTomorrow() {
        return dayAfterTomorrow;
    }
    public SingleDayWeatherData getThreeDayFrom() {
        return threeDayFrom;
    }
    public SingleDayWeatherData getTomorrow() {
        return tomorrow;
    }
    public WeatherDetalData getDetalData() {
        return detalData;
    }
}
