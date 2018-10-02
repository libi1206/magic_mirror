package com.libi.format;

import com.libi.data.Data;

import org.json.JSONException;

/**
 * Created by surface on 2018/8/24.
 */

public interface Format {
    Data format(String data) throws JSONException;
}
