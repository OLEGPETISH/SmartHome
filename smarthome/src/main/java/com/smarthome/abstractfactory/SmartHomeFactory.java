package com.smarthome.abstractfactory;

import com.smarthome.model.*;


public interface SmartHomeFactory {

    String getBrand();

    Lamp createLamp(String name, String room);

    Thermostat createThermostat(String name, String room);

    Camera createCamera(String name, String room);

    Alarm createAlarm(String name, String room);

    AirConditioner createAirConditioner(String name, String room);

    TV createTV(String name, String room);

    default String factoryInfo() {
        return "[Abstract Factory] Brand: " + getBrand();
    }
}
