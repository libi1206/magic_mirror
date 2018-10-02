package com.libi.ui.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.libi.data.NoteData;
import com.libi.data.NoteListData;

import java.util.ArrayList;

/**
 * Created by surface on 2018/9/22.
 */

public class NoteSQLHelper extends SQLiteOpenHelper {
    private static final String CREATE_SQL =
            "create table note(" +
                    "id integer primary key autoincrement," +
                    "context text," +
                    "timeLine text" +
                    ")";
    private static final String TAG = "数据库";
    private Context mContext;

    public NoteSQLHelper(Context context, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "note", factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_SQL);
        Log.w(TAG, "创建数据库");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    //增加
    public static void insert(SQLiteDatabase database,String text,long timeLine) {
        ContentValues values = new ContentValues();
        values.put("context",text);
        values.put("timeLine",String.valueOf(timeLine));
        database.insert("note", null, values);
        Log.w(TAG, "添加数据库");
//        Cursor cursor = database.query("note",new String[]{"id"},"context",new String[]{text},null,null,null);
//        return cursor.getInt(cursor.getColumnIndex("id"));
    }

    //删除
    public static void delete(SQLiteDatabase database, int id) {
        ContentValues values = new ContentValues();
        values.put("id", id);
        database.delete("note", "id=?", new String[]{id + ""});
        Log.w(TAG, "删除数据库");
    }

    //修改
    public static void update(SQLiteDatabase database, int id, String text) {
        ContentValues values = new ContentValues();
        values.put("context",text);
        database.update("note", values, "id=?", new String[]{id + ""});
        Log.w(TAG, "修改数据库");
    }

    //查询（所有）
    public static NoteListData selectAll(SQLiteDatabase database) {
        Cursor cursor = database.query("note", null, null, null, null, null, null);
        ArrayList<NoteData> arrayList = new ArrayList<NoteData>();
        if (cursor.moveToFirst()) {
            do {
                NoteData itemData = new NoteData();
                itemData.setContex(cursor.getString(cursor.getColumnIndex("context")));
                itemData.setTimeLine(cursor.getLong(cursor.getColumnIndex("timeLine")));
                itemData.setId(cursor.getInt(cursor.getColumnIndex("id")));
                arrayList.add(itemData);
            } while (cursor.moveToNext());
        }
        NoteListData listData = new NoteListData();
        NoteData[] datas = new NoteData[arrayList.size()];
        for(int i=0;i<arrayList.size();i++) {
            datas[i] = arrayList.get(i);
        }
        listData.setDatas(datas);
        listData.setCount(datas.length);
        Log.w(TAG, "查询数据库");
        return listData;
    }
}
