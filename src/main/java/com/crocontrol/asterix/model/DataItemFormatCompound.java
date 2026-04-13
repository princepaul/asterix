package com.crocontrol.asterix.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DataItemFormatCompound extends DataItemFormat {
    private List<DataItemFormat> items;

    public DataItemFormatCompound(int id) {
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
    public DataItemFormatCompound clone() {
        DataItemFormatCompound clone = new DataItemFormatCompound(this.id);
        clone.setParentFormat(this.parentFormat);
        return clone;
    }

    @Override
    public long getLength(ByteBuffer data) {
        return 0;
    }

    @Override
    public boolean isCompound() {
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
        sb.append(header).append("Compound\n");
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