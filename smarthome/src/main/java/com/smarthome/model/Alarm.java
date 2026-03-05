package com.smarthome.model;

/**
 * Smart alarm/security system device.
 */
public class Alarm extends AbstractDevice {
    private boolean triggered;
    private String securityCode;

    public Alarm(String name, String room, String brand) {
        super(name, room, DeviceType.ALARM, brand);
        this.triggered = false;
        this.securityCode = "1234";
        settings.setAlarmEnabled(false);
    }

    public void arm() {
        settings.setAlarmEnabled(true);
    }

    public void disarm(String code) {
        if (code.equals(securityCode)) {
            settings.setAlarmEnabled(false);
            triggered = false;
        }
    }

    public void trigger() {
        if (settings.isAlarmEnabled()) {
            this.triggered = true;
        }
    }

    public boolean isTriggered() { return triggered; }
    public boolean isArmed() { return settings.isAlarmEnabled(); }

    @Override
    public String getStatus() {
        if (!on) return "Off";
        if (triggered) return "🚨 ALARM TRIGGERED!";
        return isArmed() ? "Armed 🔒" : "Disarmed 🔓";
    }
}
