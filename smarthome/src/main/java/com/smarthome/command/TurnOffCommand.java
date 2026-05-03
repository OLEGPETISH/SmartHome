package com.smarthome.command;

import com.smarthome.model.Device;

public class TurnOffCommand implements SmartCommand {
    private final Device device;
    private boolean wasOn;

    public TurnOffCommand(Device device) { this.device = device; }

    @Override
    public void execute() {
        wasOn = device.isOn();
        device.turnOff();
    }

    @Override
    public void undo() {
        if (wasOn) device.turnOn();
    }

    @Override
    public String getDescription() {
        return "Turn OFF: " + device.getName();
    }
}