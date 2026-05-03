package com.smarthome.state;

import com.smarthome.observer.DeviceEventBus;
import com.smarthome.singleton.DeviceManager;

/**
 * Контекст сигнализации. Делегирует поведение текущему состоянию.
 */
public class AlarmContext {

    private AlarmState currentState;
    private final String deviceId;
    private final String deviceName;
    private final String room;

    // Singleton-состояния (нет внутреннего состояния — можно переиспользовать)
    private static final AlarmState DISARMED  = new DisarmedState();
    private static final AlarmState ARMED     = new ArmedState();
    private static final AlarmState TRIGGERED = new TriggeredState();

    public AlarmContext(String deviceId, String deviceName, String room) {
        this.deviceId   = deviceId;
        this.deviceName = deviceName;
        this.room       = room;
        this.currentState = DISARMED;
    }

    public void setState(AlarmState newState) {
        this.currentState = newState;
        String msg = String.format("[State] Alarm '%s' → %s", deviceName, newState.getStateName());
        DeviceManager.getInstance().logEvent(msg);
        DeviceEventBus.getInstance().publish(deviceId, deviceName, room,
                DeviceEventBus.EventType.STATUS_UPDATE, "State: " + newState.getStateName());
    }

    public void arm()     { currentState.arm(this); }
    public void disarm()  { currentState.disarm(this); }
    public void trigger() { currentState.trigger(this); }

    public AlarmState getState()       { return currentState; }
    public String     getStateName()   { return currentState.getStateName(); }
    public String     getStateIcon()   { return currentState.getStateIcon(); }
    public String     getDeviceName()  { return deviceName; }

    public static AlarmState disarmedState()  { return DISARMED; }
    public static AlarmState armedState()     { return ARMED; }
    public static AlarmState triggeredState() { return TRIGGERED; }
}