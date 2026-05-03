package com.smarthome.mediator;

import com.smarthome.model.AirConditioner;
import com.smarthome.model.Device;
import com.smarthome.model.DeviceSettings;
import com.smarthome.model.Thermostat;
import com.smarthome.singleton.DeviceManager;

import java.util.ArrayList;
import java.util.List;


public class ClimateMediator implements RoomMediator {

    private final String roomName;
    private final List<SmartColleague> colleagues = new ArrayList<>();

    // Ссылки на устройства для прямого доступа
    private MediatorAC    ac;
    private MediatorThermostat thermostat;
    private final List<MediatorLamp> lamps = new ArrayList<>();

    public ClimateMediator(String roomName) { this.roomName = roomName; }

    public void register(SmartColleague colleague) {
        colleagues.add(colleague);
        if (colleague instanceof MediatorAC        m) ac = m;
        if (colleague instanceof MediatorThermostat m) thermostat = m;
        if (colleague instanceof MediatorLamp       m) lamps.add(m);
    }

    @Override
    public void notify(SmartColleague sender, String event) {
        String log = String.format("[Mediator:%s] %s → event='%s'",
                roomName, sender.getColleagueName(), event);
        DeviceManager.getInstance().logEvent(log);
        System.out.println(log);

        switch (event) {
            case "temp_high" -> {
                if (ac != null) { ac.turnOn(); ac.setFanHigh(); }
                DeviceManager.getInstance().logEvent(
                        "[Mediator] Temp high → AC turned ON, fan MAX");
            }
            case "temp_low" -> {
                if (ac != null) ac.turnOff();
                DeviceManager.getInstance().logEvent(
                        "[Mediator] Temp low → AC turned OFF");
            }
            case "ac_off" -> {
                if (thermostat != null) thermostat.takeover();
                DeviceManager.getInstance().logEvent(
                        "[Mediator] AC off → Thermostat takes over");
            }
            case "night_mode" -> {
                lamps.forEach(MediatorLamp::dimToNight);
                if (ac != null) ac.setFanLow();
                DeviceManager.getInstance().logEvent(
                        "[Mediator] Night mode → lamps dimmed, AC fan LOW");
            }
        }
    }

    @Override public String getRoomName() { return roomName; }
    public List<SmartColleague> getColleagues() { return List.copyOf(colleagues); }
}