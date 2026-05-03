package com.smarthome.mediator;

/**
 * Базовый коллега — знает о своём посреднике.
 */
public abstract class SmartColleague {
    protected RoomMediator mediator;

    public SmartColleague(RoomMediator mediator) {
        this.mediator = mediator;
    }

    public abstract String getColleagueName();
}