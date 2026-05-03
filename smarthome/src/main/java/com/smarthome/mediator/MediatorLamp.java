package com.smarthome.mediator;

import com.smarthome.model.DeviceSettings;
import com.smarthome.model.Lamp;

public class MediatorLamp extends SmartColleague {

    private final Lamp lamp;

    public MediatorLamp(Lamp lamp, RoomMediator mediator) {
        super(mediator);
        this.lamp = lamp;
    }

    public void dimToNight() {
        lamp.turnOn();
        DeviceSettings s = lamp.getSettings().clone();
        s.setBrightness(15);
        s.setColorMode("warm");
        lamp.applySettings(s);
    }

    @Override public String getColleagueName() { return "Lamp[" + lamp.getName() + "]"; }
    public Lamp getDevice() { return lamp; }
}