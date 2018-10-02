package com.libi.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.libi.R;
import com.libi.data.NewsListData;
import com.libi.ui.adapter.NewsAdapter;

/**
 * Created by surface on 2018/9/13.
 */

public class NewsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.w("新闻碎片", "开始加载碎片");
        View view = inflater.inflate(R.layout.news_layout,container,false);
        return view;
    }
}
