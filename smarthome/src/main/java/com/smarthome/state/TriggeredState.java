package com.smarthome.state;

import com.smarthome.observer.DeviceEventBus;

public class TriggeredState implements AlarmState {

    @Override
    public void arm(AlarmContext ctx) {
        System.out.println("[Alarm] Cannot arm while triggered. Disarm first.");
    }

    @Override
    public void disarm(AlarmContext ctx) {
        System.out.println("[Alarm] Alert cleared. Disarmed.");
        ctx.setState(AlarmContext.disarmedState());
    }

    @Override
    public void trigger(AlarmContext ctx) {
        System.out.println("[Alarm] Already triggered!");
        DeviceEventBus.getInstance().publish(
                ctx.getDeviceName(), ctx.getDeviceName(), "",
                DeviceEventBus.EventType.ALERT, "Repeated trigger detected!"
        );
    }

    @Override public String getStateName() { return "TRIGGERED 🚨"; }
    @Override public String getStateIcon() { return "🚨"; }
}