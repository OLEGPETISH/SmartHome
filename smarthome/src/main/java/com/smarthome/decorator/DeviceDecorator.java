package com.smarthome.decorator;

import com.smarthome.model.Device;
import com.smarthome.model.DeviceSettings;
import com.smarthome.model.DeviceType;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;



public abstract class DeviceDecorator implements Device {


    protected final Device wrapped;

    protected DeviceDecorator(Device device) {
        this.wrapped = device;
    }

    @Override public String getId()                               { return wrapped.getId(); }
    @Override public String getName()                             { return wrapped.getName(); }
    @Override public String getRoom()                             { return wrapped.getRoom(); }
    @Override public DeviceType getType()                         { return wrapped.getType(); }
    @Override public boolean isOn()                               { return wrapped.isOn(); }
    @Override public void turnOn()                                { wrapped.turnOn(); }
    @Override public void turnOff()                               { wrapped.turnOff(); }
    @Override public DeviceSettings getSettings()                 { return wrapped.getSettings(); }
    @Override public void applySettings(DeviceSettings settings)  { wrapped.applySettings(settings); }
    @Override public String getStatus()                           { return wrapped.getStatus(); }

    public Device getWrapped() { return wrapped; }
}




class ScheduledLampDecorator extends DeviceDecorator {

    private final LocalTime onTime;
    private final LocalTime offTime;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm");

    public ScheduledLampDecorator(Device device, LocalTime onTime, LocalTime offTime) {
        super(device);
        this.onTime = onTime;
        this.offTime = offTime;
        System.out.printf("[Decorator] ScheduledLamp wraps '%s' | ON:%s OFF:%s%n",
                device.getName(), onTime.format(FMT), offTime.format(FMT));
    }

    @Override
    public void turnOn() {
        LocalTime now = LocalTime.now();
        if (now.isAfter(offTime)) {
            System.out.printf("[ScheduledLamp] '%s' — schedule block, off time %s reached%n",
                    getName(), offTime.format(FMT));
            return; // не включаем — время уже прошло
        }
        super.turnOn(); // делегируем оригинальному устройству
        System.out.printf("[ScheduledLamp] '%s' turned ON by schedule%n", getName());
    }

    @Override
    public String getStatus() {
        return wrapped.getStatus() + String.format(" | Schedule: ON@%s OFF@%s",
                onTime.format(FMT), offTime.format(FMT));
    }

    @Override
    public String getName() {
        return wrapped.getName() + " ⏰";
    }

    public void checkSchedule() {
        LocalTime now = LocalTime.now();
        if (now.isAfter(onTime) && now.isBefore(offTime) && !isOn()) {
            System.out.printf("[ScheduledLamp] Auto-turning ON '%s'%n", wrapped.getName());
            wrapped.turnOn();
        } else if ((now.isBefore(onTime) || now.isAfter(offTime)) && isOn()) {
            System.out.printf("[ScheduledLamp] Auto-turning OFF '%s'%n", wrapped.getName());
            wrapped.turnOff();
        }
    }
}




class AutoClimateDecorator extends DeviceDecorator {

    private double currentRoomTemp;
    private final double hysteresis = 0.5; // порог срабатывания

    public AutoClimateDecorator(Device device, double initialRoomTemp) {
        super(device);
        this.currentRoomTemp = initialRoomTemp;
        System.out.printf("[Decorator] AutoClimate wraps '%s' | roomTemp=%.1f°C%n",
                device.getName(), initialRoomTemp);
    }


    public void updateRoomTemperature(double roomTemp) {
        this.currentRoomTemp = roomTemp;
        double target = getSettings().getTemperature();

        if (roomTemp > target + hysteresis && !isOn()) {
            System.out.printf("[AutoClimate] Room %.1f°C > target %.1f°C — turning ON '%s'%n",
                    roomTemp, target, getName());
            wrapped.turnOn();
        } else if (roomTemp < target - hysteresis && isOn()) {
            System.out.printf("[AutoClimate] Room %.1f°C < target %.1f°C — turning OFF '%s'%n",
                    roomTemp, target, getName());
            wrapped.turnOff();
        }
    }

    @Override
    public String getStatus() {
        return wrapped.getStatus() + String.format(" | RoomTemp: %.1f°C [auto]", currentRoomTemp);
    }

    @Override
    public String getName() {
        return wrapped.getName() + " 🌡️";
    }
}




class EnergyMonitorDecorator extends DeviceDecorator {

    private double totalEnergyKwh = 0.0;
    private long turnOnTimestamp = -1;
    private final double wattsPerHour; // потребление устройства

    public EnergyMonitorDecorator(Device device, double wattsPerHour) {
        super(device);
        this.wattsPerHour = wattsPerHour;
        System.out.printf("[Decorator] EnergyMonitor wraps '%s' | %.0fW%n",
                device.getName(), wattsPerHour);
    }

    @Override
    public void turnOn() {
        wrapped.turnOn();
        turnOnTimestamp = System.currentTimeMillis();
        System.out.printf("[EnergyMonitor] '%s' ON — tracking energy (%.0fW)%n",
                getName(), wattsPerHour);
    }

    @Override
    public void turnOff() {
        if (turnOnTimestamp > 0) {
            double hoursOn = (System.currentTimeMillis() - turnOnTimestamp) / 3_600_000.0;
            totalEnergyKwh += (wattsPerHour / 1000.0) * hoursOn;
        }
        wrapped.turnOff();
        turnOnTimestamp = -1;
    }

    @Override
    public String getStatus() {
        return wrapped.getStatus() + String.format(" | Energy: %.4f kWh", totalEnergyKwh);
    }

    @Override
    public String getName() {
        return wrapped.getName() + " ⚡";
    }

    public double getTotalEnergyKwh() { return totalEnergyKwh; }
    public double getWattsPerHour()   { return wattsPerHour; }
}




class MotionAlertDecorator extends DeviceDecorator {

    private int motionEventCount = 0;
    private String lastAlertTime = "";

    public MotionAlertDecorator(Device device) {
        super(device);
        System.out.printf("[Decorator] MotionAlert wraps '%s'%n", device.getName());
    }


    public void onMotionDetected() {
        if (!isOn()) return;
        motionEventCount++;
        lastAlertTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.printf("[MotionAlert] 🚨 MOTION on '%s' at %s (event #%d)%n",
                getName(), lastAlertTime, motionEventCount);
    }

    @Override
    public String getStatus() {
        return wrapped.getStatus() + String.format(" | Alerts: %d | Last: %s",
                motionEventCount,
                lastAlertTime.isEmpty() ? "none" : lastAlertTime);
    }

    @Override
    public String getName() {
        return wrapped.getName() + " 📡";
    }

    public int getMotionEventCount() { return motionEventCount; }
}



class DeviceDecoratorFactory {

    public static Device withSchedule(Device device, LocalTime on, LocalTime off) {
        return new ScheduledLampDecorator(device, on, off);
    }

    public static Device withAutoClimate(Device device, double roomTemp) {
        return new AutoClimateDecorator(device, roomTemp);
    }

    public static Device withEnergyMonitor(Device device, double watts) {
        return new EnergyMonitorDecorator(device, watts);
    }

    public static Device withMotionAlert(Device device) {
        return new MotionAlertDecorator(device);
    }


    public static Device withScheduleAndEnergy(Device device,
                                               LocalTime on, LocalTime off,
                                               double watts) {
        Device scheduled = new ScheduledLampDecorator(device, on, off);
        return new EnergyMonitorDecorator(scheduled, watts);
    }
}
