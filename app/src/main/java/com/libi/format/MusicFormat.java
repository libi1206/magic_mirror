package com.libi.format;

import com.libi.data.Data;
import com.libi.data.MusicData;
import com.libi.data.MusicListData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by surface on 2018/9/8.
 */

public class MusicFormat implements Format {
    @Override
    public Data format(String data) throws JSONException {
        JSONObject firstFormat = new JSONObject(data);
        JSONArray songList = new JSONArray(firstFormat.getString("songlist"));
        MusicData[] musicDatas = new MusicData[songList.length()];
        MusicListData musicListData = new MusicListData();

        for(int i=0;i<songList.length();i++) {
            musicDatas[i] = getSongData(songList.getString(i));
        }
        musicListData.setCount(songList.length());
        musicListData.setMusicDatas(musicDatas);
        return musicListData;
    }

    private MusicData getSongData(String songData) throws JSONException {
        MusicData musicData = new MusicData();

        JSONObject firstFormat = new JSONObject(songData);
        JSONObject secondFormat = new JSONObject(firstFormat.getString("data"));
        musicData.setSongMid(secondFormat.getString("songmid"));
        musicData.setSongName(secondFormat.getString("songname"));
        musicData.setAlbuMMid(secondFormat.getString("albummid"));

        JSONArray singerInf = new JSONArray(secondFormat.getString("singer"));
        String singer = "";
        for (int i=0;i<singerInf.length();i++) {
            JSONObject jsonObject = singerInf.getJSONObject(i);
            singer = singer + jsonObject.getString("name");
        }
        musicData.setSingerName(singer);

        return musicData;
    }
}
