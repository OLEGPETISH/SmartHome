package com.smarthome.observer;

public interface DeviceObserver {
    void onDeviceStateChanged(String deviceId, String deviceName, String event, String details);
}