package com.smarthome.model;

/**
 * Smart air conditioner device.
 */
public class AirConditioner extends AbstractDevice {
    public enum Mode { COOL, HEAT, FAN, AUTO }
    private Mode mode;

    public AirConditioner(String name, String room, String brand) {
        super(name, room, DeviceType.AIR_CONDITIONER, brand);
        this.mode = Mode.COOL;
        settings.setTemperature(24.0);
        settings.setFanSpeed(2);
    }

    public Mode getMode() { return mode; }
    public void setMode(Mode mode) { this.mode = mode; }

    @Override
    public String getStatus() {
        if (!on) return "Off";
        return String.format("On | Mode: %s | Temp: %.0f°C | Fan: %d",
                mode.name(), settings.getTemperature(), settings.getFanSpeed());
    }
}
