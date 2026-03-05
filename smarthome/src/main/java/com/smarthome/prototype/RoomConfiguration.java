package com.smarthome.prototype;

import com.smarthome.model.DeviceSettings;
import com.smarthome.model.DeviceType;

import java.util.HashMap;
import java.util.Map;


public class RoomConfiguration implements Cloneable {

    private String name;
    private String sourceRoom;
    private Map<DeviceType, DeviceSettings> settingsMap;

    public RoomConfiguration(String name, String sourceRoom) {
        this.name = name;
        this.sourceRoom = sourceRoom;
        this.settingsMap = new HashMap<>();
        System.out.printf("[Prototype] Created configuration '%s' for room '%s'%n", name, sourceRoom);
    }


    @Override
    public RoomConfiguration clone() {
        try {
            RoomConfiguration cloned = (RoomConfiguration) super.clone();
            cloned.settingsMap = new HashMap<>();
            for (Map.Entry<DeviceType, DeviceSettings> entry : this.settingsMap.entrySet()) {
                cloned.settingsMap.put(entry.getKey(), entry.getValue().clone()); // deep clone settings
            }
            System.out.printf("[Prototype] Cloned configuration '%s' → new copy%n", this.name);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }


    public RoomConfiguration cloneForRoom(String newRoom) {
        RoomConfiguration cloned = this.clone();
        cloned.sourceRoom = newRoom;
        System.out.printf("[Prototype] Adapted config '%s' → '%s'%n", this.name, newRoom);
        return cloned;
    }


    public RoomConfiguration setLampSettings(int brightness, String colorMode) {
        DeviceSettings s = new DeviceSettings();
        s.setBrightness(brightness);
        s.setColorMode(colorMode);
        settingsMap.put(DeviceType.LAMP, s);
        return this;
    }

    public RoomConfiguration setThermostatSettings(double temperature, int fanSpeed) {
        DeviceSettings s = new DeviceSettings();
        s.setTemperature(temperature);
        s.setFanSpeed(fanSpeed);
        settingsMap.put(DeviceType.THERMOSTAT, s);
        return this;
    }

    public RoomConfiguration setACSettings(double temperature, int fanSpeed) {
        DeviceSettings s = new DeviceSettings();
        s.setTemperature(temperature);
        s.setFanSpeed(fanSpeed);
        settingsMap.put(DeviceType.AIR_CONDITIONER, s);
        return this;
    }

    public RoomConfiguration setCameraSettings(boolean motionAlert) {
        DeviceSettings s = new DeviceSettings();
        s.setMotionAlert(motionAlert);
        settingsMap.put(DeviceType.CAMERA, s);
        return this;
    }


    public DeviceSettings getSettingsFor(DeviceType type) {
        return settingsMap.getOrDefault(type, new DeviceSettings());
    }

    public Map<DeviceType, DeviceSettings> getAllSettings() {
        return Map.copyOf(settingsMap);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSourceRoom() { return sourceRoom; }
    public void setSourceRoom(String room) { this.sourceRoom = room; }

    @Override
    public String toString() {
        return String.format("RoomConfig '%s' [room=%s, deviceTypes=%s]",
                name, sourceRoom, settingsMap.keySet());
    }
}
