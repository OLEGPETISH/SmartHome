package com.smarthome.state;

public class ArmedState implements AlarmState {

    @Override
    public void arm(AlarmContext ctx) {
        System.out.println("[Alarm] Already armed.");
    }

    @Override
    public void disarm(AlarmContext ctx) {
        System.out.println("[Alarm] Disarmed.");
        ctx.setState(AlarmContext.disarmedState());
    }

    @Override
    public void trigger(AlarmContext ctx) {
        System.out.println("[Alarm] TRIGGERED! Sending alert!");
        ctx.setState(AlarmContext.triggeredState());
    }

    @Override public String getStateName() { return "ARMED"; }
    @Override public String getStateIcon() { return "🔒"; }
}