package com.crocontrol.asterix.definition;

import com.crocontrol.asterix.model.Category;
import com.crocontrol.asterix.model.DataItemDescription;
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
            System.out.println("Skipping non-numeric category: " + idStr);
            return; // Skip BDS or other non-numeric
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
                    
                    // Create format
                    DataItemFormatFixed format = new DataItemFormatFixed(0);
                    format.setLengthValue(16); 
                    desc.setFormatObj(format);
                    
                    category.addDataItem(desc);
                }
            }
        }
        
        definitions.setCategory(category);
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