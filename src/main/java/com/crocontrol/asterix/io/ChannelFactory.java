package com.crocontrol.asterix.io;

import java.io.IOException;

public class ChannelFactory {
    public static final int MAX_OUTPUT_CHANNELS = 10;

    private Channel inputChannel;
    private Channel[] outputChannels;
    private int nOutputChannels;

    public ChannelFactory() {
        this.outputChannels = new Channel[MAX_OUTPUT_CHANNELS];
    }

    public boolean createInputChannel(String type, String descriptor, String format, String formatDescriptor) {
        System.out.println("ChannelFactory: type=" + type + ", desc=" + descriptor + ", fmt=" + format);
        if ("disk".equals(type)) {
            // Simple file input, loop not implemented yet
            this.inputChannel = new FileChannel(descriptor);
        } else if ("udp".equals(type)) {
            String[] parts = descriptor.split(":");
            String mcast = parts[0];
            String iface = parts.length > 1 ? parts[1] : null;
            int port = Integer.parseInt(parts[parts.length - 1]);
            this.inputChannel = new UdpMulticastChannel(mcast, iface, port);
        } else {
            // std input (not implemented)
            System.err.println("Unknown input type: " + type);
            return false;
        }
        boolean opened = inputChannel.open();
        System.out.println("ChannelFactory: opened=" + opened);
        return opened;
    }

    public boolean createOutputChannel(String type, String descriptor, String format, String formatDescriptor) {
        if (nOutputChannels >= MAX_OUTPUT_CHANNELS) {
            return false;
        }
        Channel channel = null;
        if ("disk".equals(type)) {
            channel = new FileChannel(descriptor);
        }
        // std output not implemented yet
        
        if (channel != null && channel.open()) {
            outputChannels[nOutputChannels++] = channel;
            return true;
        }
        return false;
    }

    public Channel getInputChannel() {
        return inputChannel;
    }

    public int getNOutputChannels() {
        return nOutputChannels;
    }

    public Channel getOutputChannel(int index) {
        if (index >= 0 && index < nOutputChannels) {
            return outputChannels[index];
        }
        return null;
    }

    public void close() {
        if (inputChannel != null) {
            try {
                inputChannel.close();
            } catch (IOException e) {
                // Ignore
            }
        }
        for (int i = 0; i < nOutputChannels; i++) {
            if (outputChannels[i] != null) {
                try {
                    outputChannels[i].close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }
}