package com.libi.data;

/**
 * Created by surface on 2018/9/6.
 */

public class NewsListData implements Data {
    private int count;
    private NewsItemData[] newsItemDatas;

    public int getCount() {
        return count;
    }
    public NewsItemData[] getNewsItemDatas() {
        return newsItemDatas;
    }

    public void setCount(int count) {
        this.count = count;
    }
    public void setNewsItemDatas(NewsItemData[] newsItemDatas) {
        this.newsItemDatas = newsItemDatas;
    }
}
