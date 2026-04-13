package com.crocontrol.asterix;

import com.crocontrol.asterix.engine.ConverterEngine;
import java.io.File;

public class Main {
    private static final String VERSION = "1.0.0";

    public static void main(String[] args) {
        System.out.println("Asterix Java Implementation v" + VERSION);

        String definitionsFile = "config/asterix.ini";
        String inputFile = "";
        String inputFormat = "ASTERIX_RAW";
        String outputFormat = "ASTERIX_TXT";
        boolean verbose = false;
        boolean bListDefinitions = false;

        // Simple arguments parsing
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-h") || arg.equals("--help")) {
                showUsage();
                return;
            } else if (arg.equals("-V") || arg.equals("--version")) {
                System.out.println("Version: " + VERSION);
                return;
            } else if (arg.equals("-v") || arg.equals("--verbose")) {
                verbose = true;
            } else if (arg.equals("-d") || arg.equals("--def")) {
                if (i + 1 < args.length) {
                    definitionsFile = args[++i];
                }
            } else if (arg.equals("-L") || arg.equals("--list")) {
                bListDefinitions = true;
            } else if (arg.equals("-f")) {
                if (i + 1 < args.length) {
                    inputFile = args[++i];
                }
            } else if (arg.equals("-P") || arg.equals("--pcap")) {
                inputFormat = "ASTERIX_PCAP";
            } else if (arg.equals("-l") || arg.equals("--line")) {
                outputFormat = "ASTERIX_OUT";
            } else if (arg.equals("-x") || arg.equals("--xml")) {
                outputFormat = "ASTERIX_XML";
            } else if (arg.equals("-j") || arg.equals("--json")) {
                outputFormat = "ASTERIX_JSON";
            }
        }

        // Setup Engine
        ConverterEngine engine = ConverterEngine.Instance();
        engine.setVerbose(verbose);

        // Create input channel description
        String inputChannelStr;
        if (!inputFile.isEmpty()) {
            inputChannelStr = "disk;" + inputFile + "|1|" + inputFormat;
        } else {
            inputChannelStr = "std;0|" + inputFormat;
        }

        // Output channel (stdout)
        String[] outputChannels = new String[1];
        outputChannels[0] = "std 0 " + outputFormat;

        // Initialize
        boolean success = engine.initialize(inputChannelStr, outputChannels, 1, 0);
        if (!success) {
            System.err.println("Failed to initialize engine");
            return;
        }

        // Check definitions
        File defFile = new File(definitionsFile);
        if (!defFile.exists()) {
            System.err.println("Definitions file not found: " + definitionsFile);
            return;
        }

        // In a real app, we would load definitions here using XMLDefinitionLoader

        if (bListDefinitions) {
            System.out.println("Listing definitions...");
            // engine.printDefinitions();
        } else {
            engine.start();
        }

        ConverterEngine.DeleteInstance();
    }

    private static void showUsage() {
        System.out.println("Usage: java -jar asterix.jar [options]");
        System.out.println("Options:");
        System.out.println("  -h, --help             Show this help message and exit.");
        System.out.println("  -V, --version          Show version information and exit.");
        System.out.println("  -v, --verbose         Show more information during program execution.");
        System.out.println("  -d, --def <file>      XML protocol definitions filename.");
        System.out.println("  -L, --list           List all configured ASTERIX items.");
        System.out.println("  -f         Input file (pcap, final, or raw).");
        System.out.println("  -P, --pcap           Input is from PCAP file.");
        System.out.println("  -l, --line           Output as one line per item.");
        System.out.println("  -x, --xml           Output as XML.");
        System.out.println("  -j, --json          Output as JSON.");
    }
}