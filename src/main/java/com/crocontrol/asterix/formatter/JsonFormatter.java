package com.crocontrol.asterix.formatter;

import com.crocontrol.asterix.model.DataItem;
import com.crocontrol.asterix.model.DataRecord;
import java.util.List;

public class JsonFormatter extends OutputFormatter {
    
    private boolean compact;

    public JsonFormatter(boolean compact) {
        this.compact = compact;
    }
    
    public JsonFormatter() {
        this(false);
    }

    @Override
    public String format(DataRecord record) {
        if (record == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        
        sb.append("{");
        sb.append("\"id\":").append(record.getId()).append(",");
        sb.append("\"cat\":").append(record.getCategory().getId()).append(",");
        sb.append("\"length\":").append(record.getLength()).append(",");
        sb.append("\"timestamp\":").append(record.getTimestamp()).append(",");
        sb.append("\"hexdata\":\"").append(record.getHexData() != null ? record.getHexData() : "").append("\"");
        sb.append(",");
        
        String catKey = "CAT" + String.format("%03d", record.getCategory().getId());
        sb.append("\"").append(catKey).append("\":{");
        
        List<DataItem> items = record.getDataItems();
        for (int i = 0; i < items.size(); i++) {
            DataItem item = items.get(i);
            String itemId = item.getDescription().getId();
            sb.append("\"").append(itemId).append("\":");
            sb.append(item.getText(1)); // 1 for JSON
            if (i < items.size() - 1) {
                sb.append(",");
            }
        }
        
        sb.append("}}");
        
        if (!compact) {
            sb.append("\n");
        }
        
        return sb.toString();
    }
}