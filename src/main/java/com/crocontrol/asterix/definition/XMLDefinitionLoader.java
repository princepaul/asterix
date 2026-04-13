package com.crocontrol.asterix.definition;

import com.crocontrol.asterix.model.Category;
import com.crocontrol.asterix.model.DataItemDescription;
import com.crocontrol.asterix.model.DataItemBits;
import com.crocontrol.asterix.model.DataItemFormatFixed;
import com.crocontrol.asterix.model.AsterixDefinition;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

public class XMLDefinitionLoader {
    private AsterixDefinition definitions;
    private String errorMessage;

    public XMLDefinitionLoader() {
        this.definitions = new AsterixDefinition();
    }

    public AsterixDefinition getDefinitions() {
        return definitions;
    }

    public boolean load(String fileName) {
        try {
            File inputFile = new File(fileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            parseDocument(doc);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
            return false;
        }
    }

    private void parseDocument(Document doc) {
        Element root = doc.getDocumentElement();
        
        // Check if root is Category
        if ("Category".equals(root.getTagName())) {
            parseCategory(root);
        } else {
            // Look for Category child
            NodeList children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element child = (Element) node;
                    if ("Category".equals(child.getTagName())) {
                        parseCategory(child);
                        break;
                    }
                }
            }
        }
    }

    private void parseCategory(Element catElement) {
        String idStr = catElement.getAttribute("id");
        int catId = 0;
        try {
            catId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            // Handle BDS as special category ID 256
            if ("BDS".equals(idStr)) {
                catId = 256;
            } else {
                System.out.println("Skipping non-numeric category: " + idStr);
                return; // Skip other non-numeric
            }
        }
        
        String catName = catElement.getAttribute("name");
        String catVer = catElement.getAttribute("ver");
        
        System.out.println("Parsed Category: " + catId);
        Category category = new Category(catId);
        category.setName(catName);
        category.setVersion(catVer);
        
        NodeList children = catElement.getChildNodes();
        
        // Parse DataItems
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element child = (Element) node;
                if ("DataItem".equals(child.getTagName())) {
                    String itemId = child.getAttribute("id");
                    String itemRuleStr = child.getAttribute("rule");

                    DataItemDescription desc = new DataItemDescription(itemId);
                    desc.setName(getElementText(child, "DataItemName"));
                    desc.setDefinition(getElementText(child, "DataItemDefinition"));
                    desc.setFormat(getElementText(child, "DataItemFormat"));
                    
                    if ("optional".equals(itemRuleStr)) {
                        desc.setRule(DataItemDescription.Rule.DATAITEM_OPTIONAL);
                    } else {
                        desc.setRule(DataItemDescription.Rule.DATAITEM_MANDATORY);
                    }
                    
                    // Parse Format
                    parseDataItemFormat(child, desc);
                    
                    category.addDataItem(desc);
                }
            }
        }
        
        definitions.setCategory(category);
    }

    private void parseDataItemFormat(Element dataItemElement, DataItemDescription desc) {
        NodeList children = dataItemElement.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element formatElement = (Element) node;
                if ("DataItemFormat".equals(formatElement.getTagName())) {
                    NodeList formatChildren = formatElement.getChildNodes();
                    for (int j = 0; j < formatChildren.getLength(); j++) {
                        Node formatNode = formatChildren.item(j);
                        if (formatNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element concreteFormatElement = (Element) formatNode;
                            String tagName = concreteFormatElement.getTagName();

                            if ("Fixed".equals(tagName)) {
                                String lengthStr = concreteFormatElement.getAttribute("length");
                                int length = 0;
                                try {
                                    length = Integer.parseInt(lengthStr);
                                } catch (NumberFormatException e) {
                                    System.err.println("Error parsing length: " + lengthStr);
                                }

                                DataItemFormatFixed format = new DataItemFormatFixed(0, length);
                                desc.setFormatObj(format);

                                // Parse Bits inside Fixed
                                NodeList fixedChildren = concreteFormatElement.getChildNodes();
                                for (int k = 0; k < fixedChildren.getLength(); k++) {
                                    Node bitNode = fixedChildren.item(k);
                                    if (bitNode.getNodeType() == Node.ELEMENT_NODE) {
                                        Element bitElement = (Element) bitNode;
                                        if ("Bits".equals(bitElement.getTagName())) {
                                            parseBits(bitElement, format);
                                        }
                                    }
                                }
                                return;
                            }
                        }
                    }
                }
            }
        }

        // Fallback if no Fixed found (should not happen based on C++ logic)
        DataItemFormatFixed format = new DataItemFormatFixed(0, 16);
        desc.setFormatObj(format);
    }

    private void parseBits(Element bitElement, DataItemFormatFixed parentFormat) {
        DataItemBits bit = new DataItemBits(0);
        
        // Attributes
        String fromStr = bitElement.getAttribute("from");
        String toStr = bitElement.getAttribute("to");
        String bitStr = bitElement.getAttribute("bit");
        String encodeStr = bitElement.getAttribute("encode");
        String fxStr = bitElement.getAttribute("fx");
        
        int from = 0;
        int to = 0;
        
        if (!fromStr.isEmpty()) {
            from = Integer.parseInt(fromStr);
            to = from;
        }
        if (!toStr.isEmpty()) {
            to = Integer.parseInt(toStr);
        }
        if (!bitStr.isEmpty()) {
            from = to = Integer.parseInt(bitStr);
        }
        
        bit.setFrom(from);
        bit.setTo(to);
        
        // Validate bit range
        int maxBit = parentFormat.getLengthValue() * 8;
        if (from > maxBit || to > maxBit) {
            System.err.println("Warning: Bit out of range (" + from + "-" + to + ") in Fixed length " + parentFormat.getLengthValue());
        }
        
        // Encoding
        if (!encodeStr.isEmpty()) {
            if ("signed".equals(encodeStr)) {
                bit.setEncoding(DataItemBits.Encoding.DATAITEM_ENCODING_SIGNED);
            } else if ("6bitschar".equals(encodeStr)) {
                bit.setEncoding(DataItemBits.Encoding.DATAITEM_ENCODING_SIX_BIT_CHAR);
            } else if ("hex".equals(encodeStr)) {
                bit.setEncoding(DataItemBits.Encoding.DATAITEM_ENCODING_HEX_BIT_CHAR);
            } else if ("octal".equals(encodeStr)) {
                bit.setEncoding(DataItemBits.Encoding.DATAITEM_ENCODING_OCTAL);
            } else if ("ascii".equals(encodeStr)) {
                bit.setEncoding(DataItemBits.Encoding.DATAITEM_ENCODING_ASCII);
            } else {
                bit.setEncoding(DataItemBits.Encoding.DATAITEM_ENCODING_UNSIGNED);
            }
        } else {
            bit.setEncoding(DataItemBits.Encoding.DATAITEM_ENCODING_UNSIGNED);
        }
        
        // FX (Extension)
        if (!fxStr.isEmpty()) {
            bit.setExtension("1".equals(fxStr));
        }
        
        // Children: BitsShortName, BitsName, BitsValue, BitsUnit, BitsConst
        NodeList bitChildren = bitElement.getChildNodes();
        for (int k = 0; k < bitChildren.getLength(); k++) {
            Node childNode = bitChildren.item(k);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childEl = (Element) childNode;
                String childTag = childEl.getTagName();
                
                if ("BitsShortName".equals(childTag)) {
                    bit.setShortName(getTextContent(childEl));
                } else if ("BitsName".equals(childTag)) {
                    bit.setName(getTextContent(childEl));
                } else if ("BitsValue".equals(childTag)) {
                    String valStr = childEl.getAttribute("val");
                    try {
                        int val = Integer.parseInt(valStr);
                        String desc = getTextContent(childEl);
                        bit.addValue(new DataItemBits.BitsValue(val, desc));
                    } catch (NumberFormatException e) {
                        // Ignore
                    }
                } else if ("BitsUnit".equals(childTag)) {
                    String scaleStr = childEl.getAttribute("scale");
                    if (!scaleStr.isEmpty()) {
                        try {
                            bit.setScale(Double.parseDouble(scaleStr));
                        } catch (NumberFormatException e) {
                            // Ignore
                        }
                    }
                    bit.setUnit(getTextContent(childEl));
                } else if ("BitsConst".equals(childTag)) {
                    String constStr = getTextContent(childEl);
                    if ("0".equals(constStr)) {
                        bit.setConst(true);
                        bit.setConstValue(0);
                    }
                }
            }
        }
        
        parentFormat.addBit(bit);
    }

    private String getTextContent(Element el) {
        Node child = el.getFirstChild();
        if (child != null) {
            return child.getNodeValue().trim();
        }
        return "";
    }

    private String getElementText(Element parent, String tagName) {
        NodeList nl = parent.getElementsByTagName(tagName);
        if (nl.getLength() > 0) {
            Element child = (Element) nl.item(0);
            Node textNode = child.getFirstChild();
            if (textNode != null && textNode.getNodeValue() != null) {
                return textNode.getNodeValue().trim();
            }
        }
        return "";
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
}