package com.crocontrol.asterix.parser;

import com.crocontrol.asterix.model.AsterixDefinition;
import com.crocontrol.asterix.model.Category;
import com.crocontrol.asterix.model.DataBlock;
import com.crocontrol.asterix.model.DataItem;
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
        
        int category = byteBuffer.get(0) & 0xFF;
        System.out.println("Looking for category: " + category);
        
        Category cat = definitions.getCategory(category);
        System.out.println("Found category: " + (cat != null ? cat.getId() : "null"));
        
        if (cat == null) {
            // Try to find definition manually if not in map (debug)
            System.out.println("Definitions map: " + definitions);
            return dataBlocks;
        }
        
        parseCategoryData(byteBuffer, cat, timestamp, dataBlocks);
        
        return dataBlocks;
    }

    private void parseCategoryData(ByteBuffer buffer, Category cat, double timestamp, List<DataBlock> blocks) {
        System.out.println("Parsing category " + cat.getId() + " with " + cat.getDataItems().size() + " items defined.");
        
        DataBlock block = new DataBlock();
        block.setTimestamp(timestamp);
        
        int length = buffer.remaining();
        DataRecord record = new DataRecord(cat, 0, length, null, timestamp);
        
        int fspecLength = parseFSPEC(buffer);
        
        // Parse items defined in the category
        List<DataItemDescription> items = cat.getDataItems();
        
        if (items.isEmpty()) {
            System.out.println("No items defined for this category.");
            // Return empty block or add dummy
            blocks.add(block);
            return;
        }
        
        int dataOffset = 1 + fspecLength; // Start after cat byte and FSPEC
        
        // Simple loop - should check FSPEC bits to know what is present
        for (DataItemDescription itemDesc : items) {
            if (itemDesc.getRule() == DataItemDescription.Rule.DATAITEM_MANDATORY || 
                (dataOffset < buffer.capacity())) {
                
                DataItem item = new DataItem(itemDesc);
                
                // Set raw data for this item
                ByteBuffer itemData = buffer.duplicate();
                itemData.position(dataOffset);
                // Limit data length (simplified)
                item.setData(itemData);
                item.setLength(0); // unknown length for now
                
                record.addDataItem(item);
                
                // Advance offset (placeholder)
                dataOffset += 2; 
            }
        }
        
        block.addRecord(record);
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