package com.smarthome.adapter;


public interface DeviceProtocol {
    void connect();
    void disconnect();
    void sendCommand(String command);
    String readStatus();
    String getProtocolName();
    boolean isConnected();
}
