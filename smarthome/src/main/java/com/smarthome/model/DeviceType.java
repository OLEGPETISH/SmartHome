package com.smarthome.model;

public enum DeviceType {
    LAMP("💡", "Lamp"),
    THERMOSTAT("🌡️", "Thermostat"),
    CAMERA("📷", "Camera"),
    ALARM("🔔", "Alarm"),
    AIR_CONDITIONER("❄️", "Air Conditioner"),
    REFRIGERATOR("🧊", "Refrigerator"),
    TV("📺", "Television"),
    WASHER("🫧", "Washer");

    private final String icon;
    private final String label;

    DeviceType(String icon, String label) {
        this.icon = icon;
        this.label = label;
    }

    public String getIcon() { return icon; }
    public String getLabel() { return label; }
}
