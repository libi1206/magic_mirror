package com.libi.connection;

import java.io.IOException;

/**
 * Created by surface on 2018/9/5.
 */

public class WorkConnection {
    protected MyConnection connection;
    public RequestData connect() throws IOException {
        return connection.getResult();
    }
}
