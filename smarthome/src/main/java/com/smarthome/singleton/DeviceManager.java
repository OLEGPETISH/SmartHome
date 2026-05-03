package com.smarthome.singleton;

import com.smarthome.model.AbstractDevice;
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
    private final Set<String> rooms;

    private String homeOwner;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private DeviceManager() {
        this.devices = new LinkedHashMap<>();
        this.eventLog = new ArrayList<>();
        this.rooms = new LinkedHashSet<>();
        this.homeOwner = "User";

        rooms.add("Living Room");
        rooms.add("Bedroom");
        rooms.add("Kitchen");

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
        rooms.add(device.getRoom());
        log(String.format("Added %s '%s' in %s", device.getType().getIcon(), device.getName(), device.getRoom()));
    }

    public void removeDevice(String deviceId) {
        Device d = devices.remove(deviceId);
        if (d != null) {
            log("Removed device: " + d.getName());
        }
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
        return rooms.stream().sorted().collect(Collectors.toList());
    }

    public void addRoom(String roomName) {
        if (roomName == null || roomName.isBlank()) return;
        rooms.add(roomName.trim());
        log("Room added: " + roomName.trim());
    }

    public void removeRoom(String roomName) {
        if (roomName == null || roomName.isBlank()) return;

        boolean hasDevices = devices.values().stream()
                .anyMatch(d -> d.getRoom().equalsIgnoreCase(roomName));

        if (!hasDevices) {
            rooms.removeIf(r -> r.equalsIgnoreCase(roomName));
            log("Room removed: " + roomName);
        } else {
            log("Cannot remove room '" + roomName + "' because it still contains devices");
        }
    }

    public void renameRoom(String oldName, String newName) {
        if (oldName == null || newName == null || oldName.isBlank() || newName.isBlank()) return;

        if (!rooms.removeIf(r -> r.equalsIgnoreCase(oldName))) return;
        rooms.add(newName.trim());

        for (Device d : devices.values()) {
            if (d.getRoom().equalsIgnoreCase(oldName) && d instanceof AbstractDevice ad) {
                ad.setRoom(newName.trim());
            }
        }

        log("Room renamed: " + oldName + " → " + newName);
    }

    public void updateDevice(String deviceId, String newName, String newRoom) {
        Device d = devices.get(deviceId);
        if (d instanceof AbstractDevice ad) {
            if (newName != null && !newName.isBlank()) {
                ad.setName(newName.trim());
            }
            if (newRoom != null && !newRoom.isBlank()) {
                rooms.add(newRoom.trim());
                ad.setRoom(newRoom.trim());
            }
            log("Updated device: " + ad.getName());
        }
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

    public int getTotalDevices() {
        return devices.size();
    }

    public long getActiveCount() {
        return devices.values().stream().filter(Device::isOn).count();
    }

    public long getActiveCountByRoom(String room) {
        return getDevicesByRoom(room).stream().filter(Device::isOn).count();
    }

    public Map<String, Long> getDeviceCountByRoom() {
        Map<String, Long> result = new LinkedHashMap<>();
        for (String room : rooms) {
            result.put(room, (long) getDevicesByRoom(room).size());
        }
        return result;
    }

    private void log(String event) {
        String entry = "[" + LocalDateTime.now().format(FMT) + "] " + event;
        eventLog.add(entry);
        if (eventLog.size() > 200) eventLog.remove(0);
    }

    public void logEvent(String event) {
        log(event);
    }

    public List<String> getEventLog() {
        return Collections.unmodifiableList(eventLog);
    }

    public List<String> getRecentEvents(int count) {
        int size = eventLog.size();
        return eventLog.subList(Math.max(0, size - count), size);
    }

    public String getHomeOwner() {
        return homeOwner;
    }

    public void setHomeOwner(String name) {
        this.homeOwner = name;
    }

    public void reset() {
        devices.clear();
        eventLog.clear();
        rooms.clear();
        rooms.add("Living Room");
        rooms.add("Bedroom");
        rooms.add("Kitchen");
        log("System reset");
    }
}