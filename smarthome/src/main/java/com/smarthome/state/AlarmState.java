package com.smarthome.state;


public interface AlarmState {
    void arm(AlarmContext ctx);
    void disarm(AlarmContext ctx);
    void trigger(AlarmContext ctx);
    String getStateName();
    String getStateIcon();
}