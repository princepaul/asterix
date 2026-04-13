package com.crocontrol.asterix.model;

import java.nio.ByteBuffer;

public class DataItem {
    private DataItemDescription description;
    private ByteBuffer data;
    private long length;

    public DataItem(DataItemDescription desc) {
        this.description = desc;
    }

    public DataItemDescription getDescription() {
        return description;
    }

    public ByteBuffer getData() {
        return data;
    }

    public void setData(ByteBuffer data) {
        this.data = data;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getText(int formatType) {
        if (description == null || data == null) {
            return "";
        }
        return description.getText(formatType, data, length);
    }
}