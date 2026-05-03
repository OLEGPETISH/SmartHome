package com.smarthome.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Наблюдатель: отслеживает только ALERT-события (камеры, сигнализация).
 */
public class SecurityAlertObserver implements DeviceObserver {

    private final List<String> alerts = new ArrayList<>();

    @Override
    public void onDeviceStateChanged(String deviceId, String deviceName,
                                     String event, String details) {
        if (event.equals("ALERT")) {
            String msg = "🚨 SECURITY ALERT from " + deviceName + ": " + details;
            alerts.add(msg);
            System.out.println(msg);
        }
    }

    public List<String> getAlerts() { return List.copyOf(alerts); }
    public void clearAlerts() { alerts.clear(); }
}