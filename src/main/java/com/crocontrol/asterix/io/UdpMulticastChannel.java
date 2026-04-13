package com.crocontrol.asterix.io;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class UdpMulticastChannel implements Channel {
    private String multicastAddress;
    private String interfaceAddress;
    private int port;
    private MulticastSocket socket;
    private InetAddress multicastGroup;
    private boolean isOpen;

    public UdpMulticastChannel(String multicastAddress, String interfaceAddress, int port) {
        this.multicastAddress = multicastAddress;
        this.interfaceAddress = interfaceAddress;
        this.port = port;
    }

    public UdpMulticastChannel(String multicastAddress, int port) {
        this(multicastAddress, null, port);
    }

    @Override
    public boolean open() {
        try {
            socket = new MulticastSocket(port);
            multicastGroup = InetAddress.getByName(multicastAddress);
            
            if (interfaceAddress != null) {
                NetworkInterface networkInterface = NetworkInterface.getByName(interfaceAddress);
                if (networkInterface != null) {
                    socket.setNetworkInterface(networkInterface);
                }
            }
            
            socket.joinGroup(multicastGroup);
            isOpen = true;
            return true;
        } catch (IOException e) {
            isOpen = false;
            return false;
        }
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public byte[] read() {
        if (!isOpen()) {
            return null;
        }
        
        try {
            byte[] buffer = new byte[2048];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            
            byte[] data = new byte[packet.getLength()];
            System.arraycopy(packet.getData(), 0, data, 0, packet.getLength());
            return data;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void write(byte[] data) {
        if (!isOpen()) {
            return;
        }
        
        try {
            DatagramPacket packet = new DatagramPacket(data, data.length, multicastGroup, port);
            socket.send(packet);
        } catch (IOException e) {
            // Log error
        }
    }

    @Override
    public void close() {
        if (socket != null && isOpen) {
            try {
                socket.leaveGroup(multicastGroup);
                socket.close();
            } catch (IOException e) {
                // Ignore
            }
        }
        isOpen = false;
    }
}