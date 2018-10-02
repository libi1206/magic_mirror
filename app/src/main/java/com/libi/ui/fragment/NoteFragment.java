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
 * Created by surface on 2018/9/22.
 */

public class NoteFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.w("便签碎片", "正在加载碎片");
        View view = inflater.inflate(R.layout.note_layout, container, false);

        return view;
    }
}
