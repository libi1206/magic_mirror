package com.libi.format;

import com.libi.data.Data;
import com.libi.data.SingleDayWeatherData;
import com.libi.data.WeatherData;
import com.libi.data.WeatherDetalData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by surface on 2018/8/27.
 */

public class WeatherFormat implements Format {
    @Override
    public Data format(String data) throws JSONException {
        JSONObject firstFormat = new JSONObject(data);
        JSONObject secondFormat = new JSONObject(firstFormat.getString("data"));

        WeatherData weatherData = new WeatherData();

        WeatherDetalData detalData = new WeatherDetalData();
        detalData.setGammao(secondFormat.getString("ganmao"));
        detalData.setPm10(secondFormat.getInt("pm10"));
        detalData.setPm25(secondFormat.getInt("pm25"));
        detalData.setQuality(secondFormat.getString("quality"));
        detalData.setWendu(secondFormat.getString("wendu"));
        detalData.setShidu(secondFormat.getString("shidu"));
        weatherData.setDetalData(detalData);

        JSONArray array = new JSONArray(secondFormat.getString("forecast"));
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            SingleDayWeatherData singleDayWeatherData = formatSingleDayData(jsonObject);

            switch (i) {
                case 0:
                    weatherData.setToday(singleDayWeatherData);
                    break;
                case 1:
                    weatherData.setTomorrow(singleDayWeatherData);
                    break;
                case 2:
                    weatherData.setDayAfterTomorrow(singleDayWeatherData);
                    break;
                case 3:
                    weatherData.setThreeDayFrom(singleDayWeatherData);
                    break;
                default:
                    break;
            }
        }
        return weatherData;
    }

    private SingleDayWeatherData formatSingleDayData(JSONObject jsonObject) throws JSONException {
        SingleDayWeatherData singleDayWeatherData = new SingleDayWeatherData();
        singleDayWeatherData.setDate(jsonObject.getString("date"));
        String high = jsonObject.getString("high").split("[ ]")[1];
        String low = jsonObject.getString("low").split("[ ]")[1];
        singleDayWeatherData.setHigh(high);
        singleDayWeatherData.setLow(low);
        singleDayWeatherData.setType(jsonObject.getString("type"));
        return singleDayWeatherData;
    }
}
