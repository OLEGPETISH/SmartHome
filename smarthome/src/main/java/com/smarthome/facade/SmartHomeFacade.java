package com.smarthome.facade;

import com.smarthome.abstractfactory.SmartHomeFactory;
import com.smarthome.abstractfactory.SmartHomeFactoryProvider;
import com.smarthome.adapter.DeviceProtocol;
import com.smarthome.adapter.ProtocolAdapterFactory;
import com.smarthome.builder.AutomationScenario;
import com.smarthome.builder.ScenarioDirector;
import com.smarthome.model.*;
import com.smarthome.prototype.ConfigurationRegistry;
import com.smarthome.prototype.RoomConfiguration;
import com.smarthome.singleton.DeviceManager;

import java.util.List;


public class SmartHomeFacade {

    // Фасад хранит ссылки на все подсистемы
    private final DeviceManager deviceManager;
    private final ConfigurationRegistry registry;

    public SmartHomeFacade(ConfigurationRegistry registry) {
        this.deviceManager = DeviceManager.getInstance();
        this.registry = registry;
        System.out.println("[Facade] SmartHomeFacade initialized — unified interface ready.");
    }


    public Device addDevice(String type, String brand, String room,
                            String protocol, String address) {
        // Подсистема 1: Abstract Factory
        SmartHomeFactory factory = SmartHomeFactoryProvider.getFactory(brand);

        Device device = switch (type.toLowerCase()) {
            case "lamp"           -> factory.createLamp(type + " " + room, room);
            case "thermostat"     -> factory.createThermostat(type + " " + room, room);
            case "camera"         -> factory.createCamera(type + " " + room, room);
            case "airconditioner" -> factory.createAirConditioner(type + " " + room, room);
            case "tv"             -> factory.createTV(type + " " + room, room);
            default               -> factory.createLamp(type + " " + room, room);
        };

        deviceManager.addDevice(device);

        if (protocol != null && !protocol.isBlank()) {
            DeviceProtocol proto = ProtocolAdapterFactory.create(protocol, address);
            proto.connect();
            deviceManager.logEvent(String.format("Device '%s' connected via %s (%s)",
                    device.getName(), proto.getProtocolName(), address));
        }

        System.out.printf("[Facade] addDevice: '%s' brand=%s room=%s protocol=%s%n",
                type, brand, room, protocol);
        return device;
    }


    public DeviceProtocol connectDeviceProtocol(String protocol, String address) {
        DeviceProtocol proto = ProtocolAdapterFactory.create(protocol, address);
        proto.connect();
        deviceManager.logEvent("Protocol connected: " + proto.getProtocolName() + " @ " + address);
        System.out.printf("[Facade] connectDeviceProtocol: %s @ %s%n", protocol, address);
        return proto;
    }


    public void setDeviceOn(String deviceId, boolean on) {
        if (on) deviceManager.turnOnDevice(deviceId);
        else    deviceManager.turnOffDevice(deviceId);
    }


    public void turnOnRoom(String room)  { deviceManager.turnOnAllInRoom(room); }
    public void turnOffRoom(String room) { deviceManager.turnOffAllInRoom(room); }
    public void turnOffAll()             { deviceManager.turnOffAll(); }


    public void activateNightMode(String room) {
        applyConfigAndRun("night_mode", room, "Night Mode");
    }

    public void activateMorningMode(String room) {
        applyConfigAndRun("morning_mode", room, "Morning Mode");
    }

    public void activateMovieMode(String room) {
        applyConfigAndRun("movie_mode", room, "Movie Mode");
    }

    public void activateAwayMode() {
        AutomationScenario scenario = new ScenarioDirector(
                new AutomationScenario.Builder("Away Mode")
        ).buildAwayMode();
        scenario.execute();
        System.out.println("[Facade] activateAwayMode executed.");
    }


    public List<Device> getDevices()               { return deviceManager.getAllDevices(); }
    public List<Device> getDevicesInRoom(String r)  { return deviceManager.getDevicesByRoom(r); }
    public List<String> getRooms()                  { return deviceManager.getRooms(); }
    public long getActiveCount()                    { return deviceManager.getActiveCount(); }
    public int  getTotalDevices()                   { return deviceManager.getTotalDevices(); }
    public List<String> getRecentLog(int n)         { return deviceManager.getRecentEvents(n); }


    private void applyConfigAndRun(String configKey, String room, String label) {
        // Подсистема 1: Prototype
        if (registry.hasConfig(configKey)) {
            RoomConfiguration config = registry.getCloneForRoom(configKey, room);
            deviceManager.getDevicesByRoom(room)
                    .forEach(d -> d.applySettings(config.getSettingsFor(d.getType())));
        }
        AutomationScenario scenario = new ScenarioDirector(
                new AutomationScenario.Builder(label)
        ).buildNightMode(room);
        scenario.execute();
        System.out.printf("[Facade] %s activated for room: %s%n", label, room);
    }
}
