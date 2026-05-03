package com.smarthome.strategy;

import com.smarthome.model.*;
import java.util.List;

/**
 * Стратегия "Комфорт": оптимальные условия для жизни.
 */
public class ComfortStrategy implements AutomationStrategy {

    @Override
    public void apply(List<Device> devices) {
        for (Device d : devices) {
            switch (d.getType()) {
                case LAMP -> {
                    d.turnOn();
                    DeviceSettings s = d.getSettings().clone();
                    s.setBrightness(80);
                    s.setColorMode("daylight");
                    d.applySettings(s);
                }
                case AIR_CONDITIONER -> {
                    d.turnOn();
                    DeviceSettings s = d.getSettings().clone();
                    s.setTemperature(22.0);
                    s.setFanSpeed(2);
                    d.applySettings(s);
                }
                case THERMOSTAT -> {
                    DeviceSettings s = d.getSettings().clone();
                    s.setTemperature(21.0);
                    d.applySettings(s);
                }
                default -> { }
            }
        }
    }

    @Override public String getName()        { return "Comfort Mode"; }
    @Override public String getDescription() { return "Full brightness, AC 22°C, thermostat 21°C"; }
}