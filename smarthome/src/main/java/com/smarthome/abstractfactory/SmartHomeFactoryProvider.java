package com.smarthome.abstractfactory;

import com.smarthome.model.*;


class XiaomiFactory implements SmartHomeFactory {

    @Override public String getBrand() { return "Xiaomi"; }

    @Override
    public Lamp createLamp(String name, String room) {
        Lamp lamp = new Lamp(name, room, "Xiaomi");
        lamp.setBrightness(70);
        lamp.setColorMode("cool");
        return lamp;
    }

    @Override
    public Thermostat createThermostat(String name, String room) {
        Thermostat t = new Thermostat(name, room, "Xiaomi");
        t.setTargetTemperature(21.0);
        return t;
    }

    @Override
    public Camera createCamera(String name, String room) {
        Camera c = new Camera(name, room, "Xiaomi");
        c.getSettings().setMotionAlert(true);
        return c;
    }

    @Override
    public Alarm createAlarm(String name, String room) {
        return new Alarm(name, room, "Xiaomi");
    }

    @Override
    public AirConditioner createAirConditioner(String name, String room) {
        AirConditioner ac = new AirConditioner(name, room, "Xiaomi");
        ac.getSettings().setTemperature(26.0);
        ac.getSettings().setFanSpeed(1); // energy-saving default
        return ac;
    }

    @Override
    public TV createTV(String name, String room) {
        return new TV(name, room, "Xiaomi");
    }
}


class PhilipsFactory implements SmartHomeFactory {

    @Override public String getBrand() { return "Philips"; }

    @Override
    public Lamp createLamp(String name, String room) {
        Lamp lamp = new Lamp(name, room, "Philips");
        lamp.setBrightness(90);
        lamp.setColorMode("warm");
        return lamp;
    }

    @Override
    public Thermostat createThermostat(String name, String room) {
        Thermostat t = new Thermostat(name, room, "Philips");
        t.setTargetTemperature(22.5);
        return t;
    }

    @Override
    public Camera createCamera(String name, String room) {
        Camera c = new Camera(name, room, "Philips");
        c.getSettings().setMotionAlert(false); // silent by default
        return c;
    }

    @Override
    public Alarm createAlarm(String name, String room) {
        return new Alarm(name, room, "Philips");
    }

    @Override
    public AirConditioner createAirConditioner(String name, String room) {
        AirConditioner ac = new AirConditioner(name, room, "Philips");
        ac.getSettings().setTemperature(23.0);
        ac.getSettings().setFanSpeed(2);
        return ac;
    }

    @Override
    public TV createTV(String name, String room) {
        return new TV(name, room, "Philips");
    }
}


class SamsungFactory implements SmartHomeFactory {

    @Override public String getBrand() { return "Samsung"; }

    @Override
    public Lamp createLamp(String name, String room) {
        Lamp lamp = new Lamp(name, room, "Samsung");
        lamp.setBrightness(100);
        lamp.setColorMode("daylight");
        return lamp;
    }

    @Override
    public Thermostat createThermostat(String name, String room) {
        Thermostat t = new Thermostat(name, room, "Samsung");
        t.setTargetTemperature(20.0);
        return t;
    }

    @Override
    public Camera createCamera(String name, String room) {
        Camera c = new Camera(name, room, "Samsung");
        c.getSettings().setMotionAlert(true);
        return c;
    }

    @Override
    public Alarm createAlarm(String name, String room) {
        return new Alarm(name, room, "Samsung");
    }

    @Override
    public AirConditioner createAirConditioner(String name, String room) {
        AirConditioner ac = new AirConditioner(name, room, "Samsung");
        ac.getSettings().setTemperature(22.0);
        ac.getSettings().setFanSpeed(3);
        return ac;
    }

    @Override
    public TV createTV(String name, String room) {
        return new TV(name, room, "Samsung");
    }
}


public class SmartHomeFactoryProvider {

    public static SmartHomeFactory getFactory(String brand) {
        return switch (brand.toLowerCase()) {
            case "xiaomi" -> new XiaomiFactory();
            case "philips" -> new PhilipsFactory();
            case "samsung" -> new SamsungFactory();
            default -> throw new IllegalArgumentException("Unknown brand: " + brand);
        };
    }

    public static String[] availableBrands() {
        return new String[]{"Xiaomi", "Philips", "Samsung"};
    }
}
