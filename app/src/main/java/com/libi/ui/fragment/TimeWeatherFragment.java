package com.libi.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.libi.R;

/**
 * Created by surface on 2018/9/11.
 */

public class TimeWeatherFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.w("天气碎片", "开始加载试图");
        View root = inflater.inflate(R.layout.time_weather_layout, container, false);
        return root;
    }
}
