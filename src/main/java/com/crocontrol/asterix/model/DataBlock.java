package com.crocontrol.asterix.model;

import java.util.ArrayList;
import java.util.List;

public class DataBlock {
    private List<DataRecord> records;
    private double timestamp;

    public DataBlock() {
        this.records = new ArrayList<>();
    }

    public List<DataRecord> getRecords() {
        return records;
    }

    public void addRecord(DataRecord record) {
        this.records.add(record);
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

    public String getText(int formatType) {
        StringBuilder sb = new StringBuilder();
        for (DataRecord record : records) {
            sb.append(record.getText(formatType));
        }
        return sb.toString();
    }
}