package com.libi.data;

/**
 * Created by surface on 2018/9/22.
 */

public class NoteListData implements Data {
    private int count;
    private NoteData[] datas;

    public int getCount() {
        return count;
    }
    public NoteData[] getDatas() {
        return datas;
    }

    public void setCount(int count) {
        this.count = count;
    }
    public void setDatas(NoteData[] datas) {
        this.datas = datas;
    }
}
