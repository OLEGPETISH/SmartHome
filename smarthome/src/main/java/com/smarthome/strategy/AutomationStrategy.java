package com.smarthome.strategy;

import com.smarthome.model.Device;
import java.util.List;


public interface AutomationStrategy {
    /** Применить стратегию к списку устройств комнаты. */
    void apply(List<Device> devices);
    String getName();
    String getDescription();
}