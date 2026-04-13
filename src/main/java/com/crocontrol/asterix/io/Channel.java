package com.crocontrol.asterix.io;

import java.io.Closeable;

public interface Channel extends Closeable {
    boolean open();
    boolean isOpen();
    byte[] read();
    void write(byte[] data);
}