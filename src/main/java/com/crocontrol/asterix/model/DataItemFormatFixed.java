package com.crocontrol.asterix.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DataItemFormatFixed extends DataItemFormat {
    private int length;
    private List<DataItemBits> bits;

    public DataItemFormatFixed(int id) {
        super(id);
        this.bits = new ArrayList<>();
    }

    public DataItemFormatFixed(int id, int length) {
        super(id);
        this.length = length;
        this.bits = new ArrayList<>();
    }

    public int getLengthValue() {
        return length;
    }

    public void setLengthValue(int length) {
        this.length = length;
    }

    public List<DataItemBits> getBits() {
        return bits;
    }

    public void setBits(List<DataItemBits> bits) {
        this.bits = bits;
    }

    public void addBit(DataItemBits bit) {
        this.bits.add(bit);
    }
    
    public int getBitCount() {
        return bits.size();
    }
    
    @Override
    public DataItemFormatFixed clone() {
        DataItemFormatFixed clone = new DataItemFormatFixed(this.id, this.length);
        clone.setParentFormat(this.parentFormat);
        clone.setSubItems(this.subItems);
        return clone;
    }

    @Override
    public long getLength(ByteBuffer data) {
        return this.length;
    }

    @Override
    public boolean isFixed() {
        return true;
    }

    @Override
    public String getText(int formatType, ByteBuffer data, long length) {
        if (data == null || data.remaining() == 0) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        
        if (bits.isEmpty()) {
            // No detailed bits, just show raw length
            if (formatType == 2) { // Line
                 return "Len:" + length;
            }
            return "Fixed length: " + length;
        }
        
        for (DataItemBits bit : bits) {
            sb.append(bit.getText(formatType, data, length));
            if (formatType != 2) sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String printDescriptors(String header) {
        StringBuilder sb = new StringBuilder();
        sb.append(header).append("Fixed length=").append(length).append("\n");
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