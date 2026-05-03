package com.smarthome.mediator;


public interface RoomMediator {
    void notify(SmartColleague sender, String event);
    String getRoomName();
}