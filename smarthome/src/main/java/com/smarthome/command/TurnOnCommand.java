package com.smarthome.command;

import com.smarthome.model.Device;

public class TurnOnCommand implements SmartCommand {
    private final Device device;
    private boolean wasOn;

    public TurnOnCommand(Device device) { this.device = device; }

    @Override
    public void execute() {
        wasOn = device.isOn();
        device.turnOn();
    }

    @Override
    public void undo() {
        if (!wasOn) device.turnOff();
    }

    @Override
    public String getDescription() {
        return "Turn ON: " + device.getName();
    }
}