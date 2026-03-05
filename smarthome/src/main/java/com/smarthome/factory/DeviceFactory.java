package com.smarthome.factory;

import com.smarthome.model.*;


public abstract class DeviceFactory {


    public abstract Device createDevice(String name, String room, String brand);


    public Device createAndConfigure(String name, String room, String brand) {
        Device device = createDevice(name, room, brand);
        configureDefaults(device);
        System.out.printf("[Factory Method] Created %s '%s' (%s) in %s%n",
                device.getType().getLabel(), name, brand, room);
        return device;
    }

    protected void configureDefaults(Device device) {
    }


    public static class LampFactory extends DeviceFactory {
        @Override
        public Device createDevice(String name, String room, String brand) {
            return new Lamp(name, room, brand);
        }

        @Override
        protected void configureDefaults(Device device) {
            Lamp lamp = (Lamp) device;
            lamp.setBrightness(75);
            lamp.setColorMode("warm");
        }
    }

    public static class ThermostatFactory extends DeviceFactory {
        @Override
        public Device createDevice(String name, String room, String brand) {
            return new Thermostat(name, room, brand);
        }

        @Override
        protected void configureDefaults(Device device) {
            Thermostat t = (Thermostat) device;
            t.setTargetTemperature(22.0);
        }
    }

    public static class CameraFactory extends DeviceFactory {
        @Override
        public Device createDevice(String name, String room, String brand) {
            return new Camera(name, room, brand);
        }
    }

    public static class AlarmFactory extends DeviceFactory {
        @Override
        public Device createDevice(String name, String room, String brand) {
            return new Alarm(name, room, brand);
        }
    }

    public static class AirConditionerFactory extends DeviceFactory {
        @Override
        public Device createDevice(String name, String room, String brand) {
            return new AirConditioner(name, room, brand);
        }

        @Override
        protected void configureDefaults(Device device) {
            AirConditioner ac = (AirConditioner) device;
            ac.getSettings().setTemperature(24.0);
            ac.getSettings().setFanSpeed(2);
        }
    }

    public static class TVFactory extends DeviceFactory {
        @Override
        public Device createDevice(String name, String room, String brand) {
            return new TV(name, room, brand);
        }
    }
}
