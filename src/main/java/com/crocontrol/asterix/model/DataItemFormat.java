package com.crocontrol.asterix.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public abstract class DataItemFormat {
    protected int id;
    protected DataItemFormat parentFormat;
    protected List<DataItemFormat> subItems;

    public DataItemFormat(int id) {
        this.id = id;
        this.subItems = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DataItemFormat getParentFormat() {
        return parentFormat;
    }

    public void setParentFormat(DataItemFormat parentFormat) {
        this.parentFormat = parentFormat;
    }

    public List<DataItemFormat> getSubItems() {
        return subItems;
    }

    public void setSubItems(List<DataItemFormat> subItems) {
        this.subItems = subItems;
    }

    public abstract DataItemFormat clone();

    public abstract long getLength(ByteBuffer data);

    public abstract String getText(int formatType, ByteBuffer data, long length);

    public abstract String printDescriptors(String header);

    public abstract boolean filterOutItem(String name);

    public abstract boolean isFiltered(String name);

    public abstract String getDescription(String field, String value);

    public boolean isFixed() { return false; }
    public boolean isRepetitive() { return false; }
    public boolean isBDS() { return false; }
    public boolean isVariable() { return false; }
    public boolean isExplicit() { return false; }
    public boolean isCompound() { return false; }
    public boolean isBits() { return false; }
}