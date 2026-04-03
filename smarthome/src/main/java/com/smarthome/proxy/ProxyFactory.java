package com.smarthome.proxy;

import com.smarthome.model.Device;


public class ProxyFactory {

    public static SecureDeviceProxy asAdmin(Device device, String userName) {
        return new SecureDeviceProxy(device, userName, SecureDeviceProxy.Role.ADMIN);
    }

    public static SecureDeviceProxy asUser(Device device, String userName) {
        return new SecureDeviceProxy(device, userName, SecureDeviceProxy.Role.USER);
    }

    public static SecureDeviceProxy asGuest(Device device, String userName) {
        return new SecureDeviceProxy(device, userName, SecureDeviceProxy.Role.GUEST);
    }


    public static SecureDeviceProxy create(Device device, String userName, String role) {
        SecureDeviceProxy.Role r = switch (role.toUpperCase()) {
            case "ADMIN" -> SecureDeviceProxy.Role.ADMIN;
            case "USER"  -> SecureDeviceProxy.Role.USER;
            default      -> SecureDeviceProxy.Role.GUEST;
        };
        return new SecureDeviceProxy(device, userName, r);
    }
}
