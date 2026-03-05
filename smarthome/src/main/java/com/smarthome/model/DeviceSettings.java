package com.smarthome.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds all configurable settings for a device.
 * Used by the Prototype pattern for cloning room configurations.
 */
public class DeviceSettings implements Cloneable {
    private int brightness;        // 0-100 for lamps
    private double temperature;    // target temp for thermostats/AC
    private int fanSpeed;          // 1-3
    private boolean motionAlert;   // for cameras
    private boolean alarmEnabled;  // for alarm
    private String colorMode;      // warm/cool/daylight
    private Map<String, String> extras;

    public DeviceSettings() {
        this.brightness = 80;
        this.temperature = 22.0;
        this.fanSpeed = 2;
        this.motionAlert = true;
        this.alarmEnabled = false;
        this.colorMode = "warm";
        this.extras = new HashMap<>();
    }

    public DeviceSettings(int brightness, double temperature, int fanSpeed,
                          boolean motionAlert, boolean alarmEnabled, String colorMode) {
        this.brightness = brightness;
        this.temperature = temperature;
        this.fanSpeed = fanSpeed;
        this.motionAlert = motionAlert;
        this.alarmEnabled = alarmEnabled;
        this.colorMode = colorMode;
        this.extras = new HashMap<>();
    }

    @Override
    public DeviceSettings clone() {
        try {
            DeviceSettings cloned = (DeviceSettings) super.clone();
            cloned.extras = new HashMap<>(this.extras);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone failed", e);
        }
    }

    // Getters and setters
    public int getBrightness() { return brightness; }
    public void setBrightness(int brightness) { this.brightness = Math.min(100, Math.max(0, brightness)); }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public int getFanSpeed() { return fanSpeed; }
    public void setFanSpeed(int fanSpeed) { this.fanSpeed = Math.min(3, Math.max(1, fanSpeed)); }

    public boolean isMotionAlert() { return motionAlert; }
    public void setMotionAlert(boolean motionAlert) { this.motionAlert = motionAlert; }

    public boolean isAlarmEnabled() { return alarmEnabled; }
    public void setAlarmEnabled(boolean alarmEnabled) { this.alarmEnabled = alarmEnabled; }

    public String getColorMode() { return colorMode; }
    public void setColorMode(String colorMode) { this.colorMode = colorMode; }

    public Map<String, String> getExtras() { return extras; }
    public void setExtra(String key, String value) { extras.put(key, value); }
    public String getExtra(String key) { return extras.getOrDefault(key, ""); }

    @Override
    public String toString() {
        return String.format("Settings{brightness=%d%%, temp=%.1f°C, fanSpeed=%d, colorMode=%s}",
                brightness, temperature, fanSpeed, colorMode);
    }
}
