package com.smarthome.proxy;

import com.smarthome.model.Device;
import com.smarthome.model.DeviceSettings;
import com.smarthome.model.DeviceType;
import com.smarthome.singleton.DeviceManager;

import java.time.LocalTime;
import java.util.Set;


public class SecureDeviceProxy implements Device {

    public enum Role {
        ADMIN,  // полный доступ
        USER,   // стандартный доступ, нет доступа к сигнализации
        GUEST   // только чтение статуса, не может включать/выключать
    }

    private static final Set<DeviceType> ADMIN_ONLY = Set.of(DeviceType.ALARM, DeviceType.CAMERA);

    private final Device realDevice; // RealSubject — реальное устройство
    private final Role   userRole;
    private final String userName;
    private LocalTime    allowedFrom; // разрешённые часы
    private LocalTime    allowedTo;
    private boolean      locked;      // принудительная блокировка

    public SecureDeviceProxy(Device realDevice, String userName, Role role) {
        this.realDevice   = realDevice;
        this.userName     = userName;
        this.userRole     = role;
        this.allowedFrom  = LocalTime.of(6, 0);
        this.allowedTo    = LocalTime.of(23, 0);
        this.locked       = false;
        System.out.printf("[Proxy] Created proxy for '%s' | user=%s role=%s%n",
                realDevice.getName(), userName, role);
    }


    @Override
    public void turnOn() {
        if (!checkAccess("TURN_ON")) return;
        realDevice.turnOn(); // делегируем RealSubject
        log("turned ON");
    }

    @Override
    public void turnOff() {
        if (!checkAccess("TURN_OFF")) return;
        realDevice.turnOff();
        log("turned OFF");
    }

    @Override
    public void applySettings(DeviceSettings settings) {
        if (!checkAccess("APPLY_SETTINGS")) return;
        // Дополнительная проверка: GUEST не может менять настройки
        if (userRole == Role.GUEST) {
            deny("applySettings — GUEST cannot modify settings");
            return;
        }
        realDevice.applySettings(settings);
        log("settings applied");
    }


    @Override public String getId()              { return realDevice.getId(); }
    @Override public String getName()            { return realDevice.getName(); }
    @Override public String getRoom()            { return realDevice.getRoom(); }
    @Override public DeviceType getType()        { return realDevice.getType(); }
    @Override public boolean isOn()              { return realDevice.isOn(); }
    @Override public DeviceSettings getSettings(){ return realDevice.getSettings(); }

    @Override
    public String getStatus() {
        log("read status");
        return realDevice.getStatus() + " [via proxy: " + userName + "/" + userRole + "]";
    }


    private boolean checkAccess(String operation) {
        // 1. Принудительная блокировка
        if (locked) {
            deny(operation + " — device is locked by admin");
            return false;
        }

        if (ADMIN_ONLY.contains(realDevice.getType()) && userRole != Role.ADMIN) {
            deny(operation + " — " + realDevice.getType() + " requires ADMIN role");
            return false;
        }

        if (userRole == Role.GUEST && (operation.equals("TURN_ON") || operation.equals("TURN_OFF"))) {
            deny(operation + " — GUEST role is read-only");
            return false;
        }

        LocalTime now = LocalTime.now();
        if (now.isBefore(allowedFrom) || now.isAfter(allowedTo)) {
            deny(operation + String.format(" — outside allowed hours (%s–%s)",
                    allowedFrom, allowedTo));
            return false;
        }

        return true; // доступ разрешён
    }

    private void deny(String reason) {
        String msg = String.format("[Proxy] ⛔ ACCESS DENIED: user='%s' device='%s' reason: %s",
                userName, realDevice.getName(), reason);
        System.out.println(msg);
        DeviceManager.getInstance().logEvent(msg);
    }

    private void log(String action) {
        String msg = String.format("[Proxy] ✅ user='%s' %s '%s'", userName, action, realDevice.getName());
        System.out.println(msg);
        DeviceManager.getInstance().logEvent(msg);
    }


    public void lock()   { locked = true;  System.out.println("[Proxy] Device LOCKED: " + getName()); }
    public void unlock() { locked = false; System.out.println("[Proxy] Device UNLOCKED: " + getName()); }

    public void setAllowedHours(LocalTime from, LocalTime to) {
        this.allowedFrom = from;
        this.allowedTo   = to;
    }

    public Role getRole()     { return userRole; }
    public String getUser()   { return userName; }
    public boolean isLocked() { return locked; }

    public Device getRealDevice(Role requesterRole) {
        if (requesterRole != Role.ADMIN)
            throw new SecurityException("Only ADMIN can access real device directly");
        return realDevice;
    }
}
