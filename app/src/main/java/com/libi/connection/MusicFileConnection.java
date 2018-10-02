package com.libi.connection;

import java.io.IOException;

/**
 * Created by surface on 2018/9/8.
 */

public class MusicFileConnection extends WorkConnection {
    public MusicFileConnection(String songMid) throws IOException {
        String url = "http://ws.stream.qqmusic.qq.com/C100"+songMid+".m4a?fromtag=0&guid=126548448";
        connection = new MyConnection(url);
    }
}
