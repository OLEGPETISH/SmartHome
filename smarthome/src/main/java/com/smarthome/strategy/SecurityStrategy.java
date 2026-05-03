package com.smarthome.strategy;

import com.smarthome.model.*;
import com.smarthome.observer.DeviceEventBus;
import java.util.List;

/**
 * Стратегия "Безопасность": включить все камеры и сигнализацию.
 */
public class SecurityStrategy implements AutomationStrategy {

    @Override
    public void apply(List<Device> devices) {
        for (Device d : devices) {
            switch (d.getType()) {
                case CAMERA, ALARM -> {
                    d.turnOn();
                    DeviceEventBus.getInstance().publish(
                            d.getId(), d.getName(), d.getRoom(),
                            DeviceEventBus.EventType.STATUS_UPDATE,
                            "Activated by Security Strategy"
                    );
                }
                case LAMP -> {
                    // Оставить свет включённым снаружи
                    DeviceSettings s = d.getSettings().clone();
                    s.setBrightness(100);
                    d.applySettings(s);
                }
                default -> { }
            }
        }
    }

    @Override public String getName()        { return "Security Mode"; }
    @Override public String getDescription() { return "All cameras ON, alarms armed, max brightness"; }
}