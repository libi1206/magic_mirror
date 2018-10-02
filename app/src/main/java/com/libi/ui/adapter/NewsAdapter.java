package com.libi.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.libi.R;
import com.libi.data.NewsItemData;
import com.libi.data.NewsListData;
import com.libi.ui.view.URLImageView;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by surface on 2018/9/13.
 */

public class NewsAdapter extends ArrayAdapter<NewsItemData> {
    private NewsItemData[] datas;
    private int res;

    public NewsAdapter(@NonNull Context context, int resource, NewsListData data) {
        super(context, resource,data.getNewsItemDatas());
        res = resource;
        datas = data.getNewsItemDatas();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        NewsItemData data = datas[position];
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(res,parent,false);
        //找到控件
        TextView title = view.findViewById(R.id.news_title);
        TextView postName = view.findViewById(R.id.news_post_name);
        TextView newsDate = view.findViewById(R.id.news_date);
        URLImageView[] imageViews = new URLImageView[3];
        imageViews[0] = view.findViewById(R.id.news_img1);
        imageViews[1] = view.findViewById(R.id.news_img2);
        imageViews[2] = view.findViewById(R.id.news_img3);

        title.setText(fixText(data.getTitle()));
        postName.setText(data.getPosterScreenName());
        newsDate.setText(data.getDateStr());
        int imageCount = data.getImageUrls().length;
        for(int i=0;i<(3>imageCount?imageCount:3);i++) {
            imageViews[i].setImageUrl(data.getImageUrls()[i]);
        }

        return view;
    }

    private String fixText(String s) {
        return s.replaceAll("&quot;","\"");
    }



}
