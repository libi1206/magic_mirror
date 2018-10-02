package com.libi.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.libi.R;
import com.libi.data.NoteData;
import com.libi.data.NoteListData;
import com.libi.ui.NoteActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.Inflater;

/**
 * Created by surface on 2018/9/22.
 */

public class NoteAdapter extends ArrayAdapter<NoteData> {
    private NoteData[] data;
    private int res;
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日 HH:mm");

    public NoteAdapter(@NonNull Context context, int resource, @NonNull NoteListData datas) {
        super(context, resource, datas.getDatas());
        Log.w("便签适配器", "创建适配器 length:"+datas.getCount()+"\\"+datas.getDatas().length);
        res = resource;
        data = datas.getDatas();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        NoteData itemData = data[position];
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(res, parent, false);

        //找到控件
        TextView title = view.findViewById(R.id.note_item_title);
        TextView time = view.findViewById(R.id.note_item_time);
//        delete =
//                view.findViewById(R.id.note_item_delete);

        // 奇怪的空指针bug
        Log.w("便签适配器", "正在更新便签");
        title.setText(itemData.getContex());
        String timeStr = getTime(itemData.getTimeLine());
        time.setText(timeStr);
//        if(delete !=null) {
//            delete.setTag(position);
//        }
        return view;
    }

    private String getTime(Long timeLine) {
        String timeStr = simpleDateFormat.format(timeLine);
        Log.e("时间", timeStr);
        return timeStr;
    }
}
