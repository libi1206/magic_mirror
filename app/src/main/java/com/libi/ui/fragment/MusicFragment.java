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
 * Created by surface on 2018/9/12.
 */

public class MusicFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.w("音乐碎片", "开始加载碎片");
        View view = inflater.inflate(R.layout.music_layout, container, false);
        return view;
    }
}
