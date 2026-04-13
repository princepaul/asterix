package com.crocontrol.asterix.model;

import java.util.HashMap;
import java.util.Map;

public class AsterixDefinition {
    public static final int MAX_CATEGORIES = 257;
    public static final int BDS_CAT_ID = 256;

    private Map<Integer, Category> categories;

    public AsterixDefinition() {
        this.categories = new HashMap<>();
    }

    public Category getCategory(int i) {
        return categories.get(i);
    }

    public void setCategory(Category newCategory) {
        categories.put(newCategory.getId(), newCategory);
    }

    public boolean categoryDefined(int i) {
        return categories.containsKey(i);
    }

    public String printDescriptors() {
        StringBuilder sb = new StringBuilder();
        for (Category cat : categories.values()) {
            sb.append(cat.printDescriptors());
        }
        return sb.toString();
    }

    public boolean filterOutItem(int cat, String item, String name) {
        Category category = categories.get(cat);
        if (category != null) {
            return category.filterOutItem(item, name);
        }
        return false;
    }

    public boolean isFiltered(int cat, String item, String name) {
        Category category = categories.get(cat);
        if (category != null) {
            return category.isFiltered(item, name);
        }
        return false;
    }

    public String getDescription(int category, String item, String field, String value) {
        Category cat = categories.get(category);
        if (cat != null) {
            return cat.getDescription(item, field, value);
        }
        return null;
    }
}