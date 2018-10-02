package com.libi.connection;

import java.io.IOException;

/**
 * Created by surface on 2018/9/8.
 */

public class MusicListConnection extends WorkConnection {
    public MusicListConnection() throws IOException {
//        String url = "https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_cp.fcg?g_tk=5381&uin=0&format=json&inCharset=utf-8&outCharset=utf-8&notice=0&platform=h5&needNewCode=1&tpl=3&page=detail&type=top&topid=36&_=1520777874472";
        String url = "https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_cp.fcg?g_tk=5381&uin=0&format=json&inCharset=utf-8&outCharset=utf-8&notice=0&platform=h5&needNewCode=1&tpl=3&page=detail&type=top&topid=27&_=1519963122923";
        connection = new MyConnection(url);
    }
}
