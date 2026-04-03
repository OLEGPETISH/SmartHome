package com.smarthome.decorator;

import com.smarthome.model.Device;
import java.time.LocalTime;


public class Decorators {

    public static Device scheduled(Device device, LocalTime on, LocalTime off) {
        return DeviceDecoratorFactory.withSchedule(device, on, off);
    }

    public static Device autoClimate(Device device, double roomTemp) {
        return DeviceDecoratorFactory.withAutoClimate(device, roomTemp);
    }

    public static Device energyMonitor(Device device, double watts) {
        return DeviceDecoratorFactory.withEnergyMonitor(device, watts);
    }

    public static Device motionAlert(Device device) {
        return DeviceDecoratorFactory.withMotionAlert(device);
    }

    public static Device scheduledWithEnergy(Device device, LocalTime on, LocalTime off, double watts) {
        return DeviceDecoratorFactory.withScheduleAndEnergy(device, on, off, watts);
    }

    public static Device unwrap(Device device) {
        while (device instanceof DeviceDecorator d) {
            device = d.getWrapped();
        }
        return device;
    }

    public static boolean hasDecorator(Device device) {
        return device instanceof DeviceDecorator;
    }

    public static String decoratorChain(Device device) {
        StringBuilder chain = new StringBuilder();
        Device current = device;
        while (current instanceof DeviceDecorator d) {
            chain.append(current.getClass().getSimpleName()).append(" → ");
            current = d.getWrapped();
        }
        chain.append(current.getClass().getSimpleName());
        return chain.toString();
    }
}
