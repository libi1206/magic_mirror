package com.libi.data;

/**
 * Created by surface on 2018/9/8.
 */

public class MusicData implements Data {
    private String songName;
    private String songMid;
    private String singerName;
    private String albuMMid;

    public String getSingerName() {
        return singerName;
    }
    public String getSongMid() {
        return songMid;
    }
    public String getSongName() {
        return songName;
    }
    public String getAlbuMMid() {
        return albuMMid;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }
    public void setSongMid(String songMid) {
        this.songMid = songMid;
    }
    public void setSongName(String songName) {
        this.songName = songName;
    }
    public void setAlbuMMid(String albuMMid) {
        this.albuMMid = albuMMid;
    }
}
