package com.crocontrol.asterix.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DataItemBits extends DataItemFormat {
    public enum Encoding {
        DATAITEM_ENCODING_UNSIGNED,
        DATAITEM_ENCODING_SIGNED,
        DATAITEM_ENCODING_SIX_BIT_CHAR,
        DATAITEM_ENCODING_HEX_BIT_CHAR,
        DATAITEM_ENCODING_OCTAL,
        DATAITEM_ENCODING_ASCII
    }

    public static class BitsValue {
        private int value;
        private String description;

        public BitsValue(int val) {
            this.value = val;
        }

        public BitsValue(int val, String description) {
            this.value = val;
            this.description = description;
        }

        public int getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }
    }

    private String shortName;
    private String name;
    private int from;
    private int to;
    private Encoding encoding;
    private boolean isConst;
    private int constValue;
    private String unit;
    private double scale;
    private boolean maxValueSet;
    private double maxValue;
    private boolean minValueSet;
    private double minValue;
    private boolean extension;
    private int presenceOfField;
    private List<BitsValue> values;
    private boolean filtered;

    public DataItemBits(int id) {
        super(id);
        this.values = new ArrayList<>();
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public Encoding getEncoding() {
        return encoding;
    }

    public void setEncoding(Encoding encoding) {
        this.encoding = encoding;
    }

    public boolean isConst() {
        return isConst;
    }

    public void setConst(boolean isConst) {
        this.isConst = isConst;
    }

    public int getConstValue() {
        return constValue;
    }

    public void setConstValue(int constValue) {
        this.constValue = constValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public boolean isMaxValueSet() {
        return maxValueSet;
    }

    public void setMaxValueSet(boolean maxValueSet) {
        this.maxValueSet = maxValueSet;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public boolean isMinValueSet() {
        return minValueSet;
    }

    public void setMinValueSet(boolean minValueSet) {
        this.minValueSet = minValueSet;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public boolean isExtension() {
        return extension;
    }

    public void setExtension(boolean extension) {
        this.extension = extension;
    }

    public int getPresenceOfField() {
        return presenceOfField;
    }

    public void setPresenceOfField(int presenceOfField) {
        this.presenceOfField = presenceOfField;
    }

    public List<BitsValue> getValues() {
        return values;
    }

    public void addValue(BitsValue value) {
        this.values.add(value);
    }

    public boolean isFiltered() {
        return filtered;
    }

    public void setFiltered(boolean filtered) {
        this.filtered = filtered;
    }

    @Override
    public DataItemBits clone() {
        DataItemBits clone = new DataItemBits(this.id);
        clone.setShortName(this.shortName);
        clone.setName(this.name);
        clone.setFrom(this.from);
        clone.setTo(this.to);
        clone.setEncoding(this.encoding);
        clone.setConst(this.isConst);
        clone.setConstValue(this.constValue);
        return clone;
    }

    @Override
    public long getLength(ByteBuffer data) {
        return (to - from + 1);
    }

    @Override
    public boolean isBits() {
        return true;
    }

    @Override
    public String getText(int formatType, ByteBuffer data, long length) {
        StringBuilder sb = new StringBuilder();
        sb.append(shortName).append(": ");
        sb.append(" [").append(from).append("-").append(to).append("] ");
        
        // Basic extraction logic - simplified
        long rawValue = extractBits(data, from, to);
        
        if (isConst) {
            sb.append("Const: ").append(constValue);
        } else {
            sb.append(rawValue);
            if (scale != 0) {
                sb.append(" (").append(rawValue * scale).append(")");
            }
        }
        
        // Check for descriptions
        for (BitsValue v : values) {
            if (v.getValue() == rawValue) {
                sb.append(" - ").append(v.getDescription());
            }
        }
        
        return sb.toString();
    }

    private long extractBits(ByteBuffer data, int fromBit, int toBit) {
        if (data == null) return 0;
        
        int startByte = fromBit / 8;
        int endByte = toBit / 8;
        
        if (startByte >= data.capacity() || endByte >= data.capacity()) {
            return 0;
        }
        
        int bits = toBit - fromBit + 1;
        long mask = (1L << bits) - 1;
        
        // Simple extraction for now
        return (data.get(startByte) & (0xFF >> (8 - (toBit % 8) - 1)));
    }

    @Override
    public String printDescriptors(String header) {
        return header + "Bits " + shortName + " " + from + "-" + to + "\n";
    }

    @Override
    public boolean filterOutItem(String name) {
        if (this.name.equals(name) || this.shortName.equals(name)) {
            this.filtered = true;
            return true;
        }
        return false;
    }

    @Override
    public String getDescription(String field, String value) {
        return null;
    }

    @Override
    public boolean isFiltered(String name) {
        return filtered;
    }
}