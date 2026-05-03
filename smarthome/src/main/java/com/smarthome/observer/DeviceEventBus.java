package com.smarthome.observer;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Singleton-шина событий. Устройства публикуют события,
 * наблюдатели (лог, dashboard, уведомления) их получают.
 * ПАТТЕРН: Observer
 */
public class DeviceEventBus {

    public enum EventType { TURNED_ON, TURNED_OFF, SETTINGS_CHANGED, ALERT, STATUS_UPDATE }

    public record DeviceEvent(String deviceId, String deviceName,
                              String room, EventType type, String details,
                              String timestamp) {}

    private static DeviceEventBus instance;
    private final List<DeviceObserver> observers = new ArrayList<>();
    private final List<DeviceEvent> eventHistory = new ArrayList<>();

    private DeviceEventBus() {}

    public static DeviceEventBus getInstance() {
        if (instance == null) instance = new DeviceEventBus();
        return instance;
    }

    public void subscribe(DeviceObserver observer) {
        if (!observers.contains(observer)) observers.add(observer);
    }

    public void unsubscribe(DeviceObserver observer) {
        observers.remove(observer);
    }

    public void publish(String deviceId, String deviceName, String room,
                        EventType type, String details) {
        String ts = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        DeviceEvent event = new DeviceEvent(deviceId, deviceName, room, type, details, ts);
        eventHistory.add(event);
        for (DeviceObserver o : new ArrayList<>(observers)) {
            o.onDeviceStateChanged(deviceId, deviceName, type.name(), details);
        }
    }

    public List<DeviceEvent> getHistory() { return Collections.unmodifiableList(eventHistory); }
    public List<DeviceEvent> getLastN(int n) {
        int from = Math.max(0, eventHistory.size() - n);
        return eventHistory.subList(from, eventHistory.size());
    }
}