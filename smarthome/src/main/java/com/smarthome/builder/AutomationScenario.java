package com.smarthome.builder;

import com.smarthome.model.Device;
import com.smarthome.singleton.DeviceManager;

import java.util.ArrayList;
import java.util.List;


public class AutomationScenario {

    private final String name;
    private final String description;
    private final String triggerTime;
    private final String triggerEvent;
    private final List<DeviceAction> actions;
    private final int delayBetweenActionsMs;
    private final boolean active;

    private AutomationScenario(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.triggerTime = builder.triggerTime;
        this.triggerEvent = builder.triggerEvent;
        this.actions = List.copyOf(builder.actions);
        this.delayBetweenActionsMs = builder.delayBetweenActionsMs;
        this.active = builder.active;
    }


    public void execute() {
        DeviceManager dm = DeviceManager.getInstance();
        System.out.println("\n▶ Executing scenario: " + name);
        dm.logEvent("Scenario started: " + name);

        for (DeviceAction action : actions) {
            action.execute(dm);
            if (delayBetweenActionsMs > 0) {
                try { Thread.sleep(delayBetweenActionsMs); }
                catch (InterruptedException ignored) {}
            }
        }
        dm.logEvent("Scenario completed: " + name);
        System.out.println("✅ Scenario '" + name + "' complete.");
    }


    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getTriggerTime() { return triggerTime; }
    public String getTriggerEvent() { return triggerEvent; }
    public List<DeviceAction> getActions() { return actions; }
    public boolean isActive() { return active; }

    @Override
    public String toString() {
        return String.format("Scenario '%s' | Trigger: %s | Actions: %d | %s",
                name, triggerTime.isEmpty() ? triggerEvent : triggerTime,
                actions.size(), active ? "Active" : "Inactive");
    }


    public static class Builder {
        private final String name;

        // Optional with defaults
        private String description = "";
        private String triggerTime = "";
        private String triggerEvent = "";
        private final List<DeviceAction> actions = new ArrayList<>();
        private int delayBetweenActionsMs = 0;
        private boolean active = true;

        public Builder(String name) {
            if (name == null || name.isBlank())
                throw new IllegalArgumentException("Scenario name cannot be empty");
            this.name = name;
            System.out.println("[Builder] Creating scenario: " + name);
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder triggerAtTime(String time) {
            this.triggerTime = time;
            return this;
        }

        public Builder triggerOnEvent(String event) {
            this.triggerEvent = event;
            return this;
        }

        public Builder turnOn(String deviceId) {
            actions.add(new DeviceAction(deviceId, DeviceAction.Type.TURN_ON, null));
            return this;
        }

        public Builder turnOff(String deviceId) {
            actions.add(new DeviceAction(deviceId, DeviceAction.Type.TURN_OFF, null));
            return this;
        }

        public Builder turnOnRoom(String room) {
            actions.add(new DeviceAction(room, DeviceAction.Type.TURN_ON_ROOM, null));
            return this;
        }

        public Builder turnOffRoom(String room) {
            actions.add(new DeviceAction(room, DeviceAction.Type.TURN_OFF_ROOM, null));
            return this;
        }

        public Builder turnOffAll() {
            actions.add(new DeviceAction("ALL", DeviceAction.Type.TURN_OFF_ALL, null));
            return this;
        }

        public Builder setBrightness(String deviceId, int brightness) {
            actions.add(new DeviceAction(deviceId, DeviceAction.Type.SET_BRIGHTNESS,
                    String.valueOf(brightness)));
            return this;
        }

        public Builder setTemperature(String deviceId, double temp) {
            actions.add(new DeviceAction(deviceId, DeviceAction.Type.SET_TEMPERATURE,
                    String.valueOf(temp)));
            return this;
        }

        public Builder withDelay(int ms) {
            this.delayBetweenActionsMs = ms;
            return this;
        }

        public Builder inactive() {
            this.active = false;
            return this;
        }

        public AutomationScenario build() {
            System.out.printf("[Builder] Built scenario '%s' with %d actions%n", name, actions.size());
            return new AutomationScenario(this);
        }
    }


    public static class DeviceAction {
        public enum Type {
            TURN_ON, TURN_OFF, TURN_ON_ROOM, TURN_OFF_ROOM, TURN_OFF_ALL,
            SET_BRIGHTNESS, SET_TEMPERATURE
        }

        private final String target;
        private final Type type;
        private final String value;

        public DeviceAction(String target, Type type, String value) {
            this.target = target;
            this.type = type;
            this.value = value;
        }

        public void execute(DeviceManager dm) {
            switch (type) {
                case TURN_ON -> {
                    dm.turnOnDevice(target);
                    Device d = dm.getDevice(target);
                    System.out.println("  → ON: " + (d != null ? d.getName() : target));
                }
                case TURN_OFF -> {
                    dm.turnOffDevice(target);
                    Device d = dm.getDevice(target);
                    System.out.println("  → OFF: " + (d != null ? d.getName() : target));
                }
                case TURN_ON_ROOM -> {
                    dm.turnOnAllInRoom(target);
                    System.out.println("  → ALL ON in: " + target);
                }
                case TURN_OFF_ROOM -> {
                    dm.turnOffAllInRoom(target);
                    System.out.println("  → ALL OFF in: " + target);
                }
                case TURN_OFF_ALL -> {
                    dm.turnOffAll();
                    System.out.println("  → ALL DEVICES OFF");
                }
                case SET_BRIGHTNESS -> {
                    Device d = dm.getDevice(target);
                    if (d != null) {
                        d.getSettings().setBrightness(Integer.parseInt(value));
                        System.out.println("  → Brightness=" + value + "% on " + d.getName());
                    }
                }
                case SET_TEMPERATURE -> {
                    Device d = dm.getDevice(target);
                    if (d != null) {
                        d.getSettings().setTemperature(Double.parseDouble(value));
                        System.out.println("  → Temp=" + value + "°C on " + d.getName());
                    }
                }
            }
        }

        public String getTarget() { return target; }
        public Type getActionType() { return type; }
        public String getValue() { return value; }
    }
}
