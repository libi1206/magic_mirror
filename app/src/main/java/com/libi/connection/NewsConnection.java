package com.libi.connection;

import java.io.IOException;

/**
 * Created by surface on 2018/9/5.
 */

public class NewsConnection extends WorkConnection{
    private final static String API_KEY = "SvLt8z4yBTCsA3K6apRoFK43ctgRB8Qfn5vUqXEAcjMnSJazxnJH1Ct38Gu6MeSh";

    public NewsConnection(String keyWord) throws IOException {
        String url = "http://api01.bitspaceman.com:8000/news/qihoo?apikey=" + API_KEY + "&kw=" +keyWord;
        connection = new MyConnection(url,"GET");
    }
}
