package com.smarthome.model;

/**
 * Smart thermostat device.
 */
public class Thermostat extends AbstractDevice {
    private double currentTemp;

    public Thermostat(String name, String room, String brand) {
        super(name, room, DeviceType.THERMOSTAT, brand);
        this.currentTemp = 20.0;
        settings.setTemperature(22.0);
        settings.setFanSpeed(2);
    }

    public void setTargetTemperature(double temp) {
        settings.setTemperature(temp);
    }

    public double getTargetTemperature() {
        return settings.getTemperature();
    }

    public double getCurrentTemperature() {
        return currentTemp;
    }

    public void setCurrentTemperature(double temp) {
        this.currentTemp = temp;
    }

    @Override
    public String getStatus() {
        if (!on) return "Off";
        return String.format("On | Current: %.1f°C | Target: %.1f°C | Fan: %d",
                currentTemp, settings.getTemperature(), settings.getFanSpeed());
    }
}
