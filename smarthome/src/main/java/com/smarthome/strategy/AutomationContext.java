package com.smarthome.strategy;

import com.smarthome.model.Device;
import com.smarthome.singleton.DeviceManager;
import java.util.List;

/**
 * Контекст, хранящий текущую стратегию и применяющий её.
 */
public class AutomationContext {

    private AutomationStrategy strategy;

    public AutomationContext(AutomationStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(AutomationStrategy strategy) {
        this.strategy = strategy;
        DeviceManager.getInstance().logEvent(
                "[Strategy] Changed to: " + strategy.getName());
    }

    public AutomationStrategy getStrategy() { return strategy; }

    public void applyToRoom(String room) {
        DeviceManager dm = DeviceManager.getInstance();
        List<Device> devices = room.equals("All Rooms")
                ? dm.getAllDevices()
                : dm.getDevicesByRoom(room);
        strategy.apply(devices);
        dm.logEvent("[Strategy] Applied '" + strategy.getName() + "' to: " + room);
    }

    public void applyToDevices(List<Device> devices) {
        strategy.apply(devices);
    }
}