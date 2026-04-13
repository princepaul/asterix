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
        String format = parts.length > 2 ? parts[2] : "";

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

        // Setup formatter
        this.formatter = new TextFormatter(false);

        return true;
    }

    public void start() {
        Channel input = channelFactory.getInputChannel();
        
        while (true) {
            byte[] data = input.read();
            if (data == null) {
                break;
            }

            // Parse Packet
            if (parser == null) {
                parser = new InputParser(definitions);
            }
            
            List<DataBlock> blocks = parser.parsePacket(data, System.currentTimeMillis() / 1000.0);
            
            for (DataBlock block : blocks) {
                for (DataRecord record : block.getRecords()) {
                    String output = formatter.format(record);
                    System.out.print(output);
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