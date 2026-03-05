package com.smarthome.model;

/**
 * Smart lamp device.
 */
public class Lamp extends AbstractDevice {

    public Lamp(String name, String room, String brand) {
        super(name, room, DeviceType.LAMP, brand);
        settings.setBrightness(80);
        settings.setColorMode("warm");
    }

    public void setBrightness(int brightness) {
        settings.setBrightness(brightness);
    }

    public int getBrightness() {
        return settings.getBrightness();
    }

    public void setColorMode(String mode) {
        settings.setColorMode(mode);
    }

    @Override
    public String getStatus() {
        if (!on) return "Off";
        return String.format("On | %d%% brightness | %s light", settings.getBrightness(), settings.getColorMode());
    }
}
