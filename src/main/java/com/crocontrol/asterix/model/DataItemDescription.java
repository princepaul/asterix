package com.crocontrol.asterix.model;

import java.nio.ByteBuffer;

public class DataItemDescription {
    public enum Rule {
        DATAITEM_UNKNOWN,
        DATAITEM_OPTIONAL,
        DATAITEM_MANDATORY
    }

    private String id;
    private int nID;
    private String name;
    private String definition;
    private String format;
    private String note;
    private DataItemFormat formatObj;
    private Rule rule;

    public DataItemDescription(String id) {
        this.id = id;
        this.rule = Rule.DATAITEM_UNKNOWN;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNID() {
        return nID;
    }

    public void setNID(int nID) {
        this.nID = nID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getDefinition() {
        return definition;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public void setFormatObj(DataItemFormat formatObj) {
        this.formatObj = formatObj;
    }

    public DataItemFormat getFormatObj() {
        return formatObj;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public String getText(int formatType, ByteBuffer data, long length) {
        if (formatObj == null) {
            return "";
        }
        return formatObj.getText(formatType, data, length);
    }

    public boolean filterOutItem(String name) {
        if (this.name != null && this.name.equals(name)) {
            return true;
        }
        return false;
    }

    public boolean isFiltered(String name) {
        if (this.name != null && this.name.equals(name)) {
            return true;
        }
        return false;
    }

    public String getDescription(String field, String value) {
        if (formatObj != null) {
            return formatObj.getDescription(field, value);
        }
        return null;
    }
}