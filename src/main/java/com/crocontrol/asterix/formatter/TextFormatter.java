package com.crocontrol.asterix.formatter;

import com.crocontrol.asterix.model.DataItem;
import com.crocontrol.asterix.model.DataRecord;

public class TextFormatter extends OutputFormatter {
    
    private boolean compact;

    public TextFormatter(boolean compact) {
        this.compact = compact;
    }

    @Override
    public String format(DataRecord record) {
        if (record == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        
        if (!compact) {
            sb.append("Category: ").append(record.getCategory().getId()).append("\n");
            sb.append("Time: ").append(record.getTimestamp()).append("\n");
        }

        for (DataItem item : record.getDataItems()) {
            sb.append(item.getText(0)); // 0 for TXT
            if (!compact) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}