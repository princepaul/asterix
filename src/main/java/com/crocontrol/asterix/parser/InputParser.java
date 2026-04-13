package com.crocontrol.asterix.parser;

import com.crocontrol.asterix.model.AsterixDefinition;
import com.crocontrol.asterix.model.Category;
import com.crocontrol.asterix.model.DataBlock;
import com.crocontrol.asterix.model.DataItem;
import com.crocontrol.asterix.model.DataItemDescription;
import com.crocontrol.asterix.model.DataItemFormat;
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
        int length = (byteBuffer.get(1) & 0xFF);
        
        // Check if length > 127, need to read second length byte
        if (length > 127) {
            length = ((length & 0x7F) << 8) | (byteBuffer.get(2) & 0xFF);
            byteBuffer.position(3); // Skip CAT + 2 LEN bytes
        } else {
            byteBuffer.position(2); // Skip CAT + 1 LEN byte
        }
        
        Category cat = definitions.getCategory(category);
        
        if (cat == null) {
            return dataBlocks;
        }
        
        parseCategoryData(byteBuffer, cat, timestamp, dataBlocks);
        
        return dataBlocks;
    }

    private void parseCategoryData(ByteBuffer buffer, Category cat, double timestamp, List<DataBlock> blocks) {
        DataBlock block = new DataBlock();
        block.setTimestamp(timestamp);
        
        int length = buffer.remaining();
        DataRecord record = new DataRecord(cat, 0, length, null, timestamp);
        
        int fspecLength = parseFSPEC(buffer);
        
        // Parse FSPEC bits to determine which items are present
        int[] fspecIndices = parseFSPECBits(buffer, fspecLength);
        
        List<DataItemDescription> items = cat.getDataItems();
        
        int dataOffset = 1 + fspecLength; // Start after cat byte and FSPEC
        
        int itemIndex = 0;
        // Map items to handle index gaps if needed, but assume sequential for now
        for (int i = 0; i < items.size(); i++) {
            DataItemDescription itemDesc = items.get(i);

            // Check if this item index is in the present list
            boolean present = false;
            if (fspecIndices != null) {
                for (int idx : fspecIndices) {
                    if (idx == i) {
                        present = true;
                        break;
                    }
                }
            }

            // Mandatory items are always present?
            if (!present && itemDesc.getRule() == DataItemDescription.Rule.DATAITEM_MANDATORY) {
                present = true;
            }

            if (present) {
                DataItem item = new DataItem(itemDesc);
                
// Calculate length based on format type
                DataItemFormat format = itemDesc.getFormatObj();
                long itemLen = 0;
                if (format != null) {
                    if (format.isFixed()) {
                        itemLen = format.getLength(buffer);
                    } else if (format.isVariable()) {
                        itemLen = buffer.capacity() - dataOffset;
                    }
                }
                
                if (itemLen < 0) itemLen = 0;
                
                // Limit length to remaining buffer space
                long remaining = buffer.capacity() - dataOffset;
                if (itemLen > remaining || itemLen < 0) {
                    itemLen = remaining;
                }
                
                if (itemLen <= 0) {
                    continue;
                }

                ByteBuffer itemData = buffer.duplicate();
                itemData.position(dataOffset);
                
                // Limit to item length or remaining
                int limit = (int)(dataOffset + itemLen);
                if (limit > buffer.capacity()) limit = buffer.capacity();
                itemData.limit(limit);
                
                item.setData(itemData);
                item.setLength(itemLen);
                
                record.addDataItem(item);
                
                dataOffset += itemLen;
            }
        }
        
        block.addRecord(record);
        blocks.add(block);
    }
    
    private int parseFSPEC(ByteBuffer buffer) {
        int start = buffer.position();
        int pos = start;
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
    
    private int[] parseFSPECBits(ByteBuffer buffer, int fspecLength) {
        List<Integer> itemIndexes = new ArrayList<>();
        
        // Start from byte 1 (after category)
        for (int i = 1; i < 1 + fspecLength; i++) {
            byte b = buffer.get(i);
            // Read bits 7 to 1
            for (int bit = 7; bit >= 1; bit--) {
                int itemIdx = (7 - bit) + ((i-1)*7);
                if ((b & (1 << bit)) != 0) {
                    itemIndexes.add(itemIdx);
                }
            }
        }
        
        int[] result = new int[itemIndexes.size()];
        for (int i = 0; i < itemIndexes.size(); i++) {
            result[i] = itemIndexes.get(i);
        }
        return result;
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