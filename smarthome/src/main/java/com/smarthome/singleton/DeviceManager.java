package com.smarthome.singleton;

import com.smarthome.model.Device;
import com.smarthome.model.DeviceType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


public class DeviceManager {

    private static volatile DeviceManager instance;

    private final Map<String, Device> devices;
    private final List<String> eventLog;
    private String homeOwner;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private DeviceManager() {
        this.devices = new LinkedHashMap<>();
        this.eventLog = new ArrayList<>();
        this.homeOwner = "User";
        log("🏠 Smart Home System initialized");
    }

    public static DeviceManager getInstance() {
        if (instance == null) {
            synchronized (DeviceManager.class) {
                if (instance == null) {
                    instance = new DeviceManager();
                    System.out.println("[Singleton] DeviceManager instance created.");
                }
            }
        }
        return instance;
    }


    public void addDevice(Device device) {
        devices.put(device.getId(), device);
        log(String.format("Added %s '%s' in %s", device.getType().getIcon(), device.getName(), device.getRoom()));
    }

    public void removeDevice(String deviceId) {
        Device d = devices.remove(deviceId);
        if (d != null) log("Removed device: " + d.getName());
    }

    public Device getDevice(String deviceId) {
        return devices.get(deviceId);
    }

    public List<Device> getAllDevices() {
        return new ArrayList<>(devices.values());
    }

    public List<Device> getDevicesByRoom(String room) {
        return devices.values().stream()
                .filter(d -> d.getRoom().equalsIgnoreCase(room))
                .collect(Collectors.toList());
    }

    public List<Device> getDevicesByType(DeviceType type) {
        return devices.values().stream()
                .filter(d -> d.getType() == type)
                .collect(Collectors.toList());
    }

    public List<String> getRooms() {
        return devices.values().stream()
                .map(Device::getRoom)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }


    public void turnOnDevice(String deviceId) {
        Device d = devices.get(deviceId);
        if (d != null) {
            d.turnOn();
            log(String.format("Turned ON %s '%s'", d.getType().getIcon(), d.getName()));
        }
    }

    public void turnOffDevice(String deviceId) {
        Device d = devices.get(deviceId);
        if (d != null) {
            d.turnOff();
            log(String.format("Turned OFF %s '%s'", d.getType().getIcon(), d.getName()));
        }
    }

    public void turnOnAllInRoom(String room) {
        getDevicesByRoom(room).forEach(d -> {
            d.turnOn();
            log(String.format("Turned ON %s '%s' (room action)", d.getType().getIcon(), d.getName()));
        });
    }

    public void turnOffAllInRoom(String room) {
        getDevicesByRoom(room).forEach(d -> {
            d.turnOff();
            log(String.format("Turned OFF %s '%s' (room action)", d.getType().getIcon(), d.getName()));
        });
    }

    public void turnOffAll() {
        devices.values().forEach(Device::turnOff);
        log("All devices turned off");
    }


    public int getTotalDevices() { return devices.size(); }

    public long getActiveCount() {
        return devices.values().stream().filter(Device::isOn).count();
    }

    public Map<String, Long> getDeviceCountByRoom() {
        return devices.values().stream()
                .collect(Collectors.groupingBy(Device::getRoom, Collectors.counting()));
    }


    private void log(String event) {
        String entry = "[" + LocalDateTime.now().format(FMT) + "] " + event;
        eventLog.add(entry);
        if (eventLog.size() > 100) eventLog.remove(0); // keep last 100
    }

    public void logEvent(String event) { log(event); }

    public List<String> getEventLog() { return Collections.unmodifiableList(eventLog); }

    public List<String> getRecentEvents(int count) {
        int size = eventLog.size();
        return eventLog.subList(Math.max(0, size - count), size);
    }


    public String getHomeOwner() { return homeOwner; }
    public void setHomeOwner(String name) { this.homeOwner = name; }


    public void reset() {
        devices.clear();
        eventLog.clear();
        log("System reset");
    }
}
