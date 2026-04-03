package com.smarthome.composite;

import com.smarthome.model.Device;

import java.util.ArrayList;
import java.util.List;


public interface DeviceComponent {
    String getName();
    void turnOnAll();
    void turnOffAll();
    int getDeviceCount();
    long getActiveCount();
    void printTree(String indent); // для визуализации структуры

    // Операции управления потомками (необязательны для листьев)
    default void add(DeviceComponent component) {
        throw new UnsupportedOperationException(getName() + " is a leaf — cannot add children");
    }
    default void remove(DeviceComponent component) {
        throw new UnsupportedOperationException(getName() + " is a leaf — cannot remove children");
    }
    default List<DeviceComponent> getChildren() {
        return List.of();
    }
}



class DeviceLeaf implements DeviceComponent {

    private final Device device;

    public DeviceLeaf(Device device) {
        this.device = device;
    }

    @Override public String getName()     { return device.getType().getIcon() + " " + device.getName(); }
    @Override public void turnOnAll()     { device.turnOn(); }
    @Override public void turnOffAll()    { device.turnOff(); }
    @Override public int getDeviceCount() { return 1; }
    @Override public long getActiveCount(){ return device.isOn() ? 1 : 0; }

    @Override
    public void printTree(String indent) {
        System.out.printf("%s├─ %s [%s] (%s)%n",
                indent, getName(), device.isOn() ? "ON" : "OFF", device.getStatus());
    }

    public Device getDevice() { return device; }
}



class DeviceGroup implements DeviceComponent {

    private final String name;
    private final String icon;
    private final List<DeviceComponent> children = new ArrayList<>();

    public DeviceGroup(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    @Override
    public String getName() { return icon + " " + name; }

    @Override
    public void add(DeviceComponent component) {
        children.add(component);
        System.out.printf("[Composite] Added '%s' → '%s'%n", component.getName(), getName());
    }

    @Override
    public void remove(DeviceComponent component) {
        children.remove(component);
    }

    @Override
    public List<DeviceComponent> getChildren() { return List.copyOf(children); }

    @Override
    public void turnOnAll() {
        System.out.println("[Composite] Turn ON all in: " + getName());
        children.forEach(DeviceComponent::turnOnAll);
    }

    @Override
    public void turnOffAll() {
        System.out.println("[Composite] Turn OFF all in: " + getName());
        children.forEach(DeviceComponent::turnOffAll);
    }

    @Override
    public int getDeviceCount() {
        return children.stream().mapToInt(DeviceComponent::getDeviceCount).sum();
    }

    @Override
    public long getActiveCount() {
        return children.stream().mapToLong(DeviceComponent::getActiveCount).sum();
    }

    @Override
    public void printTree(String indent) {
        System.out.printf("%s┌─ %s [%d devices, %d active]%n",
                indent, getName(), getDeviceCount(), getActiveCount());
        for (DeviceComponent child : children) {
            child.printTree(indent + "│  ");
        }
    }
}



class RoomGroup extends DeviceGroup {
    public RoomGroup(String roomName) {
        super(roomName, "🚪");
    }
}

class FloorGroup extends DeviceGroup {
    public FloorGroup(String floorName) {
        super(floorName, "🏢");
    }
}

class HomeGroup extends DeviceGroup {
    public HomeGroup(String homeName) {
        super(homeName, "🏠");
    }
}



