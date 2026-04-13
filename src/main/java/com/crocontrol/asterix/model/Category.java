package com.crocontrol.asterix.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Category {
    private int id;
    private boolean filtered;
    private String name;
    private String version;
    private List<DataItemDescription> dataItems;
    private Map<String, DataItemDescription> dataItemsMap;
    private List<UAP> uaps;

    public Category(int id) {
        this.id = id;
        this.dataItems = new ArrayList<>();
        this.dataItemsMap = new HashMap<>();
        this.uaps = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public boolean isFiltered() {
        return filtered;
    }

    public void setFiltered(boolean filtered) {
        this.filtered = filtered;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<DataItemDescription> getDataItems() {
        return dataItems;
    }

    public void addDataItem(DataItemDescription item) {
        this.dataItems.add(item);
        this.dataItemsMap.put(item.getId(), item);
    }

    public DataItemDescription getDataItemDescription(String id) {
        return dataItemsMap.get(id);
    }

    public List<UAP> getUaps() {
        return uaps;
    }

    public void addUap(UAP uap) {
        this.uaps.add(uap);
    }

    public UAP newUAP() {
        UAP uap = new UAP();
        this.uaps.add(uap);
        return uap;
    }

    public String printDescriptors() {
        StringBuilder sb = new StringBuilder();
        sb.append("Category ").append(id).append(" ").append(name).append(" Ver: ").append(version).append("\n");
        for (DataItemDescription item : dataItems) {
            sb.append("  Item: ").append(item.getId()).append(" ").append(item.getName()).append(" (").append(item.getRule()).append(")\n");
        }
        return sb.toString();
    }

    public boolean filterOutItem(String item, String name) {
        DataItemDescription desc = dataItemsMap.get(item);
        if (desc != null) {
            return desc.filterOutItem(name);
        }
        return false;
    }

    public boolean isFiltered(String item, String name) {
        DataItemDescription desc = dataItemsMap.get(item);
        if (desc != null) {
            return desc.isFiltered(name);
        }
        return false;
    }

    public String getDescription(String item, String field, String value) {
        DataItemDescription desc = dataItemsMap.get(item);
        if (desc != null) {
            return desc.getDescription(field, value);
        }
        return null;
    }
}