package com.smarthome.mediator;

import com.smarthome.model.Thermostat;
import com.smarthome.singleton.DeviceManager;

public class MediatorThermostat extends SmartColleague {

    private final Thermostat thermostat;

    public MediatorThermostat(Thermostat thermostat, RoomMediator mediator) {
        super(mediator);
        this.thermostat = thermostat;
    }

    /** Симулируем высокую температуру — сообщаем посреднику */
    public void reportHighTemperature() {
        mediator.notify(this, "temp_high");
    }

    /** Симулируем низкую температуру */
    public void reportLowTemperature() {
        mediator.notify(this, "temp_low");
    }

    /** Термостат берёт управление (когда AC выключён) */
    public void takeover() {
        thermostat.turnOn();
        DeviceManager.getInstance().logEvent(
                "[Thermostat] " + thermostat.getName() + " now controlling temperature");
    }

    public void activateNightMode() {
        mediator.notify(this, "night_mode");
    }

    @Override public String getColleagueName() { return "Thermostat[" + thermostat.getName() + "]"; }
    public Thermostat getDevice() { return thermostat; }
}