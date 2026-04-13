package com.crocontrol.asterix.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DataItemFormatVariable extends DataItemFormat {
    private List<DataItemFormat> items;

    public DataItemFormatVariable(int id) {
        super(id);
        this.items = new ArrayList<>();
    }

    public List<DataItemFormat> getItems() {
        return items;
    }

    public void addItem(DataItemFormat item) {
        this.items.add(item);
    }

    @Override
    public DataItemFormatVariable clone() {
        DataItemFormatVariable clone = new DataItemFormatVariable(this.id);
        clone.setParentFormat(this.parentFormat);
        return clone;
    }

    @Override
    public long getLength(ByteBuffer data) {
        long totalLength = 0;
        if (data == null) return 0;
        
        for (DataItemFormat item : items) {
            totalLength += item.getLength(data);
        }
        return totalLength;
    }

    @Override
    public boolean isVariable() {
        return true;
    }

    @Override
    public String getText(int formatType, ByteBuffer data, long length) {
        StringBuilder sb = new StringBuilder();
        for (DataItemFormat item : items) {
            sb.append(item.getText(formatType, data, length));
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String printDescriptors(String header) {
        StringBuilder sb = new StringBuilder();
        sb.append(header).append("Variable\n");
        return sb.toString();
    }

    @Override
    public boolean filterOutItem(String name) {
        return false;
    }

    @Override
    public boolean isFiltered(String name) {
        return false;
    }

    @Override
    public String getDescription(String field, String value) {
        return null;
    }
}