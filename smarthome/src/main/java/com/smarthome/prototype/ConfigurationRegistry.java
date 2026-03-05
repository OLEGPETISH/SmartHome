package com.smarthome.prototype;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class ConfigurationRegistry {

    private final Map<String, RoomConfiguration> registry = new HashMap<>();

    public void register(String key, RoomConfiguration config) {
        registry.put(key, config);
        System.out.printf("[Prototype Registry] Registered '%s'%n", key);
    }


    public RoomConfiguration getClone(String key) {
        RoomConfiguration proto = registry.get(key);
        if (proto == null) throw new IllegalArgumentException("No config for key: " + key);
        return proto.clone();
    }


    public RoomConfiguration getCloneForRoom(String key, String room) {
        RoomConfiguration proto = registry.get(key);
        if (proto == null) throw new IllegalArgumentException("No config for key: " + key);
        return proto.cloneForRoom(room);
    }

    public Set<String> getKeys() { return registry.keySet(); }

    public boolean hasConfig(String key) { return registry.containsKey(key); }
}
