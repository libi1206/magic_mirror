package com.libi.data;

/**
 * Created by surface on 2018/9/8.
 */

public class MusicListData implements Data {
    private int count;
    private MusicData[] musicDatas;

    public void setCount(int count) {
        this.count = count;
    }
    public void setMusicDatas(MusicData[] musicDatas) {
        this.musicDatas = musicDatas;
    }

    public int getCount() {
        return count;
    }
    public MusicData[] getMusicDatas() {
        return musicDatas;
    }
}
