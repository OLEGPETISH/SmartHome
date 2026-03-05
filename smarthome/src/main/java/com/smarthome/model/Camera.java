package com.smarthome.model;

/**
 * Smart security camera device.
 */
public class Camera extends AbstractDevice {
    private boolean recording;
    private boolean motionDetected;

    public Camera(String name, String room, String brand) {
        super(name, room, DeviceType.CAMERA, brand);
        this.recording = false;
        this.motionDetected = false;
        settings.setMotionAlert(true);
    }

    @Override
    public void turnOn() {
        super.turnOn();
        this.recording = true;
    }

    @Override
    public void turnOff() {
        super.turnOff();
        this.recording = false;
        this.motionDetected = false;
    }

    public void detectMotion() {
        if (on) {
            this.motionDetected = true;
        }
    }

    public void clearMotion() {
        this.motionDetected = false;
    }

    public boolean isRecording() { return recording; }
    public boolean isMotionDetected() { return motionDetected; }

    @Override
    public String getStatus() {
        if (!on) return "Off";
        return String.format("Recording | Motion: %s | Alerts: %s",
                motionDetected ? "DETECTED ⚠️" : "clear",
                settings.isMotionAlert() ? "enabled" : "disabled");
    }
}
