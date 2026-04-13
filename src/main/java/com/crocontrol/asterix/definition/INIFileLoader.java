package com.crocontrol.asterix.definition;

import com.crocontrol.asterix.model.AsterixDefinition;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class INIFileLoader {
    private AsterixDefinition definitions;
    private List<String> definitionFiles;

    public INIFileLoader() {
        this.definitionFiles = new ArrayList<>();
    }

    public boolean load(String fileName) {
        definitionFiles.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }
                if (line.endsWith(".xml")) {
                    definitionFiles.add(line);
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public List<String> getDefinitionFiles() {
        return definitionFiles;
    }
}