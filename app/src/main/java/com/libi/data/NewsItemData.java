package com.libi.data;

/**
 * Created by surface on 2018/9/6.
 */

public class NewsItemData implements Data {
    private String posterId;
    private String content;
    private String posterScreenName;
    private String url;
    private String[] imageUrls;
    private String title;
    private long publishDate;
    private String dateStr;

    public String getContent() {
        return content;
    }
    public long getPublishDate() {
        return publishDate;
    }
    public String getPosterId() {
        return posterId;
    }
    public String getPosterScreenName() {
        return posterScreenName;
    }
    public String getTitle() {
        return title;
    }
    public String getUrl() {
        return url;
    }
    public String[] getImageUrls() {
        return imageUrls;
    }
    public String getDateStr() {
        return dateStr;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public void setImageUrls(String[] imageUrls) {
        this.imageUrls = imageUrls;
    }
    public void setPosterId(String posterId) {
        this.posterId = posterId;
    }
    public void setPosterScreenName(String posterScreenName) {
        this.posterScreenName = posterScreenName;
    }
    public void setPublishDate(long publishDate) {
        this.publishDate = publishDate;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }
}
