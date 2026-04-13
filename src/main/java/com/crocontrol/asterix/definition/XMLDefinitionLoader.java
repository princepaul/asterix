package com.crocontrol.asterix.definition;

import com.crocontrol.asterix.model.Category;
import com.crocontrol.asterix.model.DataItemBits;
import com.crocontrol.asterix.model.DataItemDescription;
import com.crocontrol.asterix.model.DataItemFormat;
import com.crocontrol.asterix.model.DataItemFormatFixed;
import com.crocontrol.asterix.model.DataItemFormatCompound;
import com.crocontrol.asterix.model.DataItemFormatVariable;
import com.crocontrol.asterix.model.AsterixDefinition;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class XMLDefinitionLoader {
    private AsterixDefinition definitions;
    private Category currentCategory;
    private DataItemDescription currentDataItem;
    private DataItemFormat currentFormat;
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
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            parseDocument(doc);
            return true;
        } catch (Exception e) {
            errorMessage = e.getMessage();
            return false;
        }
    }

    private void parseDocument(Document doc) {
        Element root = doc.getDocumentElement();
        NodeList categoryNodes = root.getElementsByTagName("Category");
        
        for (int i = 0; i < categoryNodes.getLength(); i++) {
            Node catNode = categoryNodes.item(i);
            if (catNode.getNodeType() == Node.ELEMENT_NODE) {
                Element catElement = (Element) catNode;
                int catId = Integer.parseInt(catElement.getAttribute("id"));
                String catName = catElement.getAttribute("name");
                String catVer = catElement.getAttribute("ver");
                
                Category category = new Category(catId);
                category.setName(catName);
                category.setVersion(catVer);
                
                NodeList itemNodes = catElement.getElementsByTagName("DataItem");
                for (int j = 0; j < itemNodes.getLength(); j++) {
                    Node itemNode = itemNodes.item(j);
                    if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element itemElement = (Element) itemNode;
                        String itemId = itemElement.getAttribute("id");
                        String itemRuleStr = itemElement.getAttribute("rule");
                        
                        DataItemDescription desc = new DataItemDescription(itemId);
                        desc.setName(getChildText(itemElement, "DataItemName"));
                        desc.setDefinition(getChildText(itemElement, "DataItemDefinition"));
                        desc.setFormat(getChildText(itemElement, "DataItemFormat"));
                        desc.setNote(getChildText(itemElement, "DataItemNote"));
                        
                        if ("mandatory".equals(itemRuleStr)) {
                            desc.setRule(DataItemDescription.Rule.DATAITEM_MANDATORY);
                        } else if ("optional".equals(itemRuleStr)) {
                            desc.setRule(DataItemDescription.Rule.DATAITEM_OPTIONAL);
                        }
                        
                        // Parse the format (simplified - just assuming Fixed for example)
                        // In a real implementation, you would iterate the <Bit> elements here
                        DataItemFormatFixed format = new DataItemFormatFixed(0);
                        format.setLengthValue(16); // Placeholder
                        desc.setFormatObj(format);
                        
                        category.addDataItem(desc);
                    }
                }
                
                definitions.setCategory(category);
            }
        }
    }

    private String getChildText(Element parent, String tagName) {
        NodeList nl = parent.getElementsByTagName(tagName);
        if (nl.getLength() > 0) {
            Element child = (Element) nl.item(0);
            Node textNode = child.getFirstChild();
            if (textNode != null) {
                return textNode.getNodeValue().trim();
            }
        }
        return "";
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
}