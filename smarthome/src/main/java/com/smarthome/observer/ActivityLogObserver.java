package com.smarthome.observer;

import com.smarthome.singleton.DeviceManager;

/**
 * Наблюдатель: пишет события в DeviceManager лог (Activity Log в UI).
 */
public class ActivityLogObserver implements DeviceObserver {

    private static final ActivityLogObserver INSTANCE = new ActivityLogObserver();
    public static ActivityLogObserver getInstance() { return INSTANCE; }

    @Override
    public void onDeviceStateChanged(String deviceId, String deviceName,
                                     String event, String details) {
        String msg = String.format("[Observer] %s → %s: %s", deviceName, event, details);
        DeviceManager.getInstance().logEvent(msg);
    }
}