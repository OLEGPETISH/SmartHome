package com.smarthome.model;

/**
 * Base interface for all smart home devices.
 */
public interface Device {
    String getId();
    String getName();
    String getRoom();
    DeviceType getType();
    boolean isOn();
    void turnOn();
    void turnOff();
    String getStatus();
    DeviceSettings getSettings();
    void applySettings(DeviceSettings settings);
}
