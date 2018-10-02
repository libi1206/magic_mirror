package com.libi.connection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by surface on 2018/8/22.
 */

public class MyConnection {
    private HttpURLConnection connection;
    private URL url;

    protected MyConnection(String urlStr) throws IOException {
        url = new URL(urlStr);
        connection = (HttpURLConnection) url.openConnection();


        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        connection.setDoOutput(false);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod("GET");
    }

    public MyConnection(String urlStr, String method) throws IOException {
        this(urlStr);
        connection.setRequestMethod(method);
    }

    protected RequestData getResult() throws IOException {
        int requestCode;
        String requestData;
        connection.connect();
        requestCode = connection.getResponseCode();
        if(requestCode == HttpURLConnection.HTTP_OK){
            InputStream inputStream = connection.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while (-1!=(len = inputStream.read(buffer))){
                byteArrayOutputStream.write(buffer,0,len);
                byteArrayOutputStream.flush();
            }
            requestData = byteArrayOutputStream.toString("utf-8");
        }
        else {
            requestData = "请求有误";
        }

        RequestData data = new RequestData();
        data.setRequestCode(requestCode);
        data.setRequestData(requestData);
        return data;
    }



}
