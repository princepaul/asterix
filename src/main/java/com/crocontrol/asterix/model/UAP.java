package com.crocontrol.asterix.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class UAP {
    private List<String> items;
    private ByteBuffer data;
    private long length;

    public UAP() {
        this.items = new ArrayList<>();
    }

    public List<String> getItems() {
        return items;
    }

    public void addItem(String item) {
        this.items.add(item);
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
}