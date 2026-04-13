package com.crocontrol.asterix.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DataItemFormatRepetitive extends DataItemFormat {
    private DataItemFormat itemFormat;
    private int fixedLength;

    public DataItemFormatRepetitive(int id) {
        super(id);
    }

    public DataItemFormat getItemFormat() {
        return itemFormat;
    }

    public void setItemFormat(DataItemFormat itemFormat) {
        this.itemFormat = itemFormat;
    }

    public int getFixedLength() {
        return fixedLength;
    }

    public void setFixedLength(int fixedLength) {
        this.fixedLength = fixedLength;
    }

    @Override
    public DataItemFormatRepetitive clone() {
        DataItemFormatRepetitive clone = new DataItemFormatRepetitive(this.id);
        clone.setParentFormat(this.parentFormat);
        return clone;
    }

    @Override
    public long getLength(ByteBuffer data) {
        return 0;
    }

    @Override
    public boolean isRepetitive() {
        return true;
    }

    @Override
    public String getText(int formatType, ByteBuffer data, long length) {
        return "Repetitive";
    }

    @Override
    public String printDescriptors(String header) {
        return header + "Repetitive\n";
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