package com.smarthome.observer;

import com.smarthome.model.Device;
import com.smarthome.model.DeviceSettings;
import com.smarthome.model.DeviceType;

/**
 * Обёртка над Device, публикующая события в DeviceEventBus.
 * Позволяет не трогать AbstractDevice (принцип Open/Closed).
 */
public class ObservableDevice implements Device {

    private final Device wrapped;
    private final DeviceEventBus bus = DeviceEventBus.getInstance();

    public ObservableDevice(Device device) { this.wrapped = device; }

    public Device getWrapped() { return wrapped; }

    @Override
    public void turnOn() {
        wrapped.turnOn();
        bus.publish(wrapped.getId(), wrapped.getName(), wrapped.getRoom(),
                DeviceEventBus.EventType.TURNED_ON, "Device turned ON");
    }

    @Override
    public void turnOff() {
        wrapped.turnOff();
        bus.publish(wrapped.getId(), wrapped.getName(), wrapped.getRoom(),
                DeviceEventBus.EventType.TURNED_OFF, "Device turned OFF");
    }

    @Override public String getId()                         { return wrapped.getId(); }
    @Override public String getName()                       { return wrapped.getName(); }
    @Override public String getRoom()                       { return wrapped.getRoom(); }
    @Override public DeviceType getType()                   { return wrapped.getType(); }
    @Override public boolean isOn()                         { return wrapped.isOn(); }
    @Override public String getStatus()                     { return wrapped.getStatus(); }
    @Override public DeviceSettings getSettings()           { return wrapped.getSettings(); }
    @Override public void applySettings(DeviceSettings s)   { wrapped.applySettings(s); }
}