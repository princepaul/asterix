package com.crocontrol.asterix.formatter;

import com.crocontrol.asterix.model.DataRecord;

public abstract class OutputFormatter {
    public abstract String format(DataRecord record);
}