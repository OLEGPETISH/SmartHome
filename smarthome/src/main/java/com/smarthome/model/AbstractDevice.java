package com.smarthome.model;

import java.util.UUID;

public abstract class AbstractDevice implements Device {
    protected final String id;
    protected String name;
    protected String room;
    protected final DeviceType type;
    protected boolean on;
    protected DeviceSettings settings;
    protected String brand;

    protected AbstractDevice(String name, String room, DeviceType type, String brand) {
        this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.name = name;
        this.room = room;
        this.type = type;
        this.brand = brand;
        this.on = false;
        this.settings = new DeviceSettings();
    }

    @Override public String getId() { return id; }
    @Override public String getName() { return name; }
    @Override public String getRoom() { return room; }
    @Override public DeviceType getType() { return type; }
    @Override public boolean isOn() { return on; }
    @Override public void turnOn() { this.on = true; }
    @Override public void turnOff() { this.on = false; }
    @Override public DeviceSettings getSettings() { return settings; }
    @Override public void applySettings(DeviceSettings settings) { this.settings = settings.clone(); }

    public String getBrand() { return brand; }

    public void setName(String name) {
        this.name = name;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %s (%s) - %s - %s",
                id, type.getIcon(), name, brand, room, on ? "ON" : "OFF");
    }
}