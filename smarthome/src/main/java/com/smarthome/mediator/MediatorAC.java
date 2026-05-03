package com.smarthome.mediator;

import com.smarthome.model.AirConditioner;
import com.smarthome.model.DeviceSettings;

public class MediatorAC extends SmartColleague {

    private final AirConditioner ac;

    public MediatorAC(AirConditioner ac, RoomMediator mediator) {
        super(mediator);
        this.ac = ac;
    }

    public void turnOn()  {
        ac.turnOn();
        // не уведомляем — сам AC не является инициатором здесь
    }

    public void turnOff() {
        ac.turnOff();
        mediator.notify(this, "ac_off");
    }

    public void setFanHigh() {
        DeviceSettings s = ac.getSettings().clone();
        s.setFanSpeed(3);
        ac.applySettings(s);
    }

    public void setFanLow() {
        DeviceSettings s = ac.getSettings().clone();
        s.setFanSpeed(1);
        ac.applySettings(s);
    }

    /** Пользователь вручную выключил AC — сообщаем посреднику */
    public void userTurnOff() {
        ac.turnOff();
        mediator.notify(this, "ac_off");
    }

    @Override public String getColleagueName() { return "AC[" + ac.getName() + "]"; }
    public AirConditioner getDevice() { return ac; }
}