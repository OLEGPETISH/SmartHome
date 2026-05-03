package com.smarthome.strategy;

import com.smarthome.model.*;
import java.util.List;

/**
 * Стратегия "Экономия": минимальное потребление энергии.
 */
public class EcoStrategy implements AutomationStrategy {

    @Override
    public void apply(List<Device> devices) {
        for (Device d : devices) {
            switch (d.getType()) {
                case LAMP -> {
                    if (d.isOn()) {
                        DeviceSettings s = d.getSettings().clone();
                        s.setBrightness(30);
                        s.setColorMode("warm");
                        d.applySettings(s);
                    }
                }
                case AIR_CONDITIONER -> {
                    DeviceSettings s = d.getSettings().clone();
                    s.setTemperature(26.0);
                    s.setFanSpeed(1);
                    d.applySettings(s);
                }
                case TV -> d.turnOff();
                default -> { /* не трогаем */ }
            }
        }
    }

    @Override public String getName()        { return "Eco Mode"; }
    @Override public String getDescription() { return "Min energy: dim lights, raise AC temp, turn off TV"; }
}