package com.crocontrol.asterix.parser;

import com.crocontrol.asterix.model.AsterixDefinition;
import com.crocontrol.asterix.model.Category;
import com.crocontrol.asterix.model.DataBlock;
import com.crocontrol.asterix.model.DataItemDescription;
import com.crocontrol.asterix.model.DataRecord;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class InputParser {
    private AsterixDefinition definitions;

    public InputParser(AsterixDefinition definitions) {
        this.definitions = definitions;
    }

    public List<DataBlock> parsePacket(byte[] buffer, double timestamp) {
        List<DataBlock> dataBlocks = new ArrayList<>();
        
        if (buffer == null || buffer.length < 2) {
            return dataBlocks;
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        
        int category = byteBuffer.get(0);
        
        Category cat = definitions.getCategory(category);
        
        if (cat == null) {
            return dataBlocks;
        }
        
        // Basic parsing - needs implementation of FSPEC logic
        parseCategoryData(byteBuffer, cat, timestamp, dataBlocks);
        
        return dataBlocks;
    }

    private void parseCategoryData(ByteBuffer buffer, Category cat, double timestamp, List<DataBlock> blocks) {
        DataBlock block = new DataBlock();
        block.setTimestamp(timestamp);
        
        DataRecord record = new DataRecord(cat, 0, buffer.remaining(), null, timestamp);
        
        // Simplified - parse FSPEC first
        int fspecLength = parseFSPEC(buffer);
        
        // Then parse items based on category
        // This is a placeholder for the complex parsing logic
        
        blocks.add(block);
    }

    private int parseFSPEC(ByteBuffer buffer) {
        int pos = 1; // Start after category byte
        int length = 0;
        
        while (pos < buffer.capacity()) {
            byte b = buffer.get(pos);
            length++;
            if ((b & 0x01) == 0) {
                break;
            }
            pos++;
        }
        
        return length;
    }

    public AsterixDefinition getDefinitions() {
        return definitions;
    }

    public String printDefinition() {
        return definitions.printDescriptors();
    }

    public boolean filterOutItem(int cat, String item, String name) {
        return definitions.filterOutItem(cat, item, name);
    }

    public boolean isFiltered(int cat, String item, String name) {
        return definitions.isFiltered(cat, item, name);
    }
}