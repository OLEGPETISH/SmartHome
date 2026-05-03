package com.smarthome.state;

public class DisarmedState implements AlarmState {

    @Override
    public void arm(AlarmContext ctx) {
        System.out.println("[Alarm] Armed.");
        ctx.setState(AlarmContext.armedState());
    }

    @Override
    public void disarm(AlarmContext ctx) {
        System.out.println("[Alarm] Already disarmed.");
    }

    @Override
    public void trigger(AlarmContext ctx) {
        System.out.println("[Alarm] Not armed — trigger ignored.");
    }

    @Override public String getStateName() { return "DISARMED"; }
    @Override public String getStateIcon() { return "🔓"; }
}