package com.libi.format;

import com.libi.data.Data;
import com.libi.data.NewsItemData;
import com.libi.data.NewsListData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;

/**
 * Created by surface on 2018/9/6.
 */

public class NewsFormat implements Format {
    private JSONArray images;

    @Override
    public Data format(String data) throws JSONException {
        NewsListData newsListData = new NewsListData();

        JSONObject firstFormat = new JSONObject(data);
        JSONArray newsItems = new JSONArray(firstFormat.getString("data"));

        NewsItemData[] itemDatas = new NewsItemData[newsItems.length()];

        for(int i=0;i<newsItems.length();i++) {
            itemDatas[i] = getNewsItem(newsItems.getString(i));
        }
        newsListData.setCount(newsItems.length());
        newsListData.setNewsItemDatas(itemDatas);

        return newsListData;
    }

    private NewsItemData getNewsItem(String data) throws JSONException {
        JSONObject newsItem = new JSONObject(data);
        NewsItemData newsItemData = new NewsItemData();
        newsItemData.setContent(newsItem.getString("content"));
        newsItemData.setPosterId(newsItem.getString("posterId"));
        newsItemData.setPosterScreenName(newsItem.getString("posterScreenName"));
        newsItemData.setUrl(newsItem.getString("url"));
        newsItemData.setTitle(newsItem.getString("title"));
        newsItemData.setPublishDate(newsItem.getLong("publishDate"));
        newsItemData.setDateStr(newsItem.getString("publishDateStr").replaceAll("T"," "));
        JSONArray images;
        try {
            images = new JSONArray(newsItem.getString("imageUrls"));
        } catch (Exception e) {
            e.printStackTrace();
            images = new JSONArray();
        }
        String[] imagesStr = new String[images.length()];
        for (int i=0;i<images.length();i++) {
            imagesStr[i] = images.getString(i);
        }
        newsItemData.setImageUrls(imagesStr);

        return newsItemData;
    }
}
