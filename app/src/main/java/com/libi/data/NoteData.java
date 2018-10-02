package com.libi.data;

/**
 * Created by surface on 2018/9/22.
 */

public class NoteData implements Data {
    private String contex;
    private Long timeLine;
    private int id;

    public int getId() {
        return id;
    }
    public Long getTimeLine() {
        return timeLine;
    }
    public String getContex() {
        return contex;
    }

    public void setContex(String contex) {
        this.contex = contex;
    }
    public void setTimeLine(Long timeLine) {
        this.timeLine = timeLine;
    }
    public void setId(int id) {
        this.id = id;
    }
}
