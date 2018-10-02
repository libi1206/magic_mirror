package com.libi.connection;

import java.io.IOException;

/**
 * Created by surface on 2018/8/22.
 */

public class WeatherConnection extends WorkConnection{

    public WeatherConnection(String cityCode) throws IOException {
//        连接已被弃用
//        String url = "https://www.sojson.com/open/api/weather/json.shtml?city=" + city;
        String url = "http://t.weather.sojson.com/api/weather/city/"+ cityCode;
        connection = new MyConnection(url,"GET");
    }
}
