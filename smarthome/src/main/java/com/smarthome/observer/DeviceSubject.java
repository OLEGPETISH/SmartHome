package com.smarthome.observer;

public interface DeviceSubject {
    void addObserver(DeviceObserver observer);
    void removeObserver(DeviceObserver observer);
    void notifyObservers(String event, String details);
}