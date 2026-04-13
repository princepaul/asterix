package com.crocontrol.asterix.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DataRecord {
    private Category category;
    private int id;
    private long length;
    private long fspecLength;
    private byte[] fspecData;
    private double timestamp;
    private long crc;
    private String hexData;
    private boolean formatOK;
    private List<DataItem> dataItems;

    public DataRecord(Category cat, int id, long len, byte[] data, double timestamp) {
        this.category = cat;
        this.id = id;
        this.length = len;
        this.timestamp = timestamp;
        this.dataItems = new ArrayList<>();
    }

    public Category getCategory() {
        return category;
    }

    public int getId() {
        return id;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getFspecLength() {
        return fspecLength;
    }

    public void setFspecLength(long fspecLength) {
        this.fspecLength = fspecLength;
    }

    public byte[] getFspecData() {
        return fspecData;
    }

    public void setFspecData(byte[] fspecData) {
        this.fspecData = fspecData;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

    public long getCrc() {
        return crc;
    }

    public void setCrc(long crc) {
        this.crc = crc;
    }

    public String getHexData() {
        return hexData;
    }

    public void setHexData(String hexData) {
        this.hexData = hexData;
    }

    public boolean isFormatOK() {
        return formatOK;
    }

    public void setFormatOK(boolean formatOK) {
        this.formatOK = formatOK;
    }

    public List<DataItem> getDataItems() {
        return dataItems;
    }

    public void addDataItem(DataItem item) {
        this.dataItems.add(item);
    }

    public DataItem getItem(String itemId) {
        for (DataItem item : dataItems) {
            if (item.getDescription().getId().equals(itemId)) {
                return item;
            }
        }
        return null;
    }

    public String getText(int formatType) {
        StringBuilder sb = new StringBuilder();
        for (DataItem item : dataItems) {
            sb.append(item.getText(formatType));
        }
        return sb.toString();
    }
}