package com.smarthome.composite;

import com.smarthome.model.Device;

import java.util.List;

public class DeviceHierarchyBuilder {


    public static DeviceComponent buildRoom(String roomName, List<Device> devices) {
        RoomGroup room = new RoomGroup(roomName);
        devices.forEach(d -> room.add(new DeviceLeaf(d)));
        return room;
    }


    public static DeviceComponent buildFloor(String floorName, List<DeviceComponent> rooms) {
        FloorGroup floor = new FloorGroup(floorName);
        rooms.forEach(floor::add);
        return floor;
    }


    public static DeviceComponent buildHome(String homeName, List<DeviceComponent> floors) {
        HomeGroup home = new HomeGroup(homeName);
        floors.forEach(home::add);
        return home;
    }
}
