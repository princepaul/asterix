package com.crocontrol.asterix.engine;

import com.crocontrol.asterix.definition.INIFileLoader;
import com.crocontrol.asterix.definition.XMLDefinitionLoader;
import com.crocontrol.asterix.formatter.OutputFormatter;
import com.crocontrol.asterix.formatter.TextFormatter;
import com.crocontrol.asterix.io.Channel;
import com.crocontrol.asterix.io.ChannelFactory;
import com.crocontrol.asterix.model.AsterixDefinition;
import com.crocontrol.asterix.model.DataBlock;
import com.crocontrol.asterix.model.DataRecord;
import com.crocontrol.asterix.parser.InputParser;
import java.io.File;
import java.util.List;

public class ConverterEngine {
    private static ConverterEngine instance;

    private AsterixDefinition definitions;
    private InputParser parser;
    private ChannelFactory channelFactory;
    private OutputFormatter formatter;
    private boolean verbose;
    private boolean loop;
    private boolean synchronous;
    private String inputFormat;
    private String outputFormat;

    private ConverterEngine() {
        this.inputFormat = "ASTERIX_RAW";
        this.outputFormat = "ASTERIX_TXT";
    }

    public static ConverterEngine Instance() {
        if (instance == null) {
            instance = new ConverterEngine();
        }
        return instance;
    }

    public static void DeleteInstance() {
        instance = null;
    }

    public boolean initialize(String inputChannel, String[] outputChannels, int nOutput, int chFailover) {
        definitions = new AsterixDefinition();
        channelFactory = new ChannelFactory();
        
        // Parse input channel descriptor
        // format: "device;descriptor|format" (e.g. "std;0|ASTERIX_RAW" or "disk;filename|1|ASTERIX_PCAP")
        String[] parts = inputChannel.split(";");
        String deviceType = parts[0];
        String deviceDesc = parts.length > 1 ? parts[1] : "";
        String format = "";
        
        // Extract filename if it contains pipe
        if (deviceDesc.contains("|")) {
            String[] fileParts = deviceDesc.split("\\|");
            deviceDesc = fileParts[0];
            format = fileParts.length > 1 ? fileParts[1] : "";
        }

        if (!deviceType.isEmpty()) {
            channelFactory.createInputChannel(deviceType, deviceDesc, format, "");
        }

        // Create output channels
        for (int i = 0; i < nOutput; i++) {
            String outChannel = outputChannels[i];
            String[] outParts = outChannel.split(" ");
            if (outParts.length >= 2) {
                channelFactory.createOutputChannel(outParts[0], outParts[1], "", "");
            }
        }

        // Load definitions
        loadDefinitions("install/config/asterix.ini");

        // Setup formatter
        this.formatter = new TextFormatter(false);

        return true;
    }

    public boolean loadDefinitions(String iniFile) {
        try {
            INIFileLoader iniLoader = new INIFileLoader();
            if (!iniLoader.load(iniFile)) {
                System.err.println("Warning: Could not load INI file: " + iniFile);
                return false;
            }
            
            System.out.println("Loaded " + iniLoader.getDefinitionFiles().size() + " definition files.");
            
            // Resolve config directory
            File ini = new File(iniFile);
            String configDir = ini.getParent();
            
            XMLDefinitionLoader loader = new XMLDefinitionLoader();
            for (String xmlFile : iniLoader.getDefinitionFiles()) {
                // Prepend config dir
                String fullPath = configDir + File.separator + xmlFile;
                loader.load(fullPath);
            }
            
            // Merge loaded definitions
            AsterixDefinition loadedDefs = loader.getDefinitions();
            System.out.println("Loaded " + loadedDefs.categoryDefined(62) + " for CAT 62");
            
            this.definitions = loadedDefs;
            return true;
        } catch (Exception e) {
            System.err.println("Error loading definitions: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void start() {
        Channel input = channelFactory.getInputChannel();
        
        if (verbose) System.out.println("Engine started.");
        
        while (true) {
            byte[] data = input.read();
            if (data == null) {
                if (verbose) System.out.println("No more data.");
                break;
            }
            
            if (verbose) System.out.println("Read " + data.length + " bytes.");

            // Parse Packet
            if (parser == null) {
                if (verbose) System.out.println("Creating parser...");
                parser = new InputParser(definitions);
            }
            
            List<DataBlock> blocks = parser.parsePacket(data, System.currentTimeMillis() / 1000.0);
            
            if (verbose) System.out.println("Parsed " + blocks.size() + " blocks.");
            
            for (DataBlock block : blocks) {
                for (DataRecord record : block.getRecords()) {
                    String output = formatter.format(record);
                    if (!output.isEmpty()) {
                        System.out.print(output);
                    }
                }
            }
        }
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void setOutputFormat(String format) {
        this.outputFormat = format;
    }

    public AsterixDefinition getDefinitions() {
        return definitions;
    }

    public void setDefinitions(AsterixDefinition definitions) {
        this.definitions = definitions;
    }
}