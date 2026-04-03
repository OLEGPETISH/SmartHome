package com.smarthome.adapter;



class ZigbeeDevice {
    private final String deviceAddress;
    private boolean paired = false;

    public ZigbeeDevice(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    // Zigbee-специфичные методы — не совпадают с нашим Target
    public void pair()   { this.paired = true;  System.out.println("[Zigbee] Paired: "      + deviceAddress); }
    public void unpair() { this.paired = false; System.out.println("[Zigbee] Unpaired: "    + deviceAddress); }
    public void zigbeeSend(byte[] payload) {
        System.out.println("[Zigbee] Sending " + payload.length + " bytes to " + deviceAddress);
    }
    public String zigbeeRead() { return "ZIGBEE_STATUS{addr=" + deviceAddress + ",paired=" + paired + "}"; }
    public boolean isPaired()  { return paired; }
    public String getAddress() { return deviceAddress; }
}


class WifiDevice {
    private final String ipAddress;
    private boolean httpConnected = false;

    public WifiDevice(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void httpConnect()    { this.httpConnected = true;  System.out.println("[WiFi] HTTP connected to " + ipAddress); }
    public void httpDisconnect() { this.httpConnected = false; System.out.println("[WiFi] HTTP disconnected: " + ipAddress); }
    public String httpPost(String endpoint, String body) {
        System.out.println("[WiFi] POST http://" + ipAddress + endpoint + " body=" + body);
        return "{\"status\":\"ok\"}";
    }
    public String httpGet(String endpoint) {
        System.out.println("[WiFi] GET http://" + ipAddress + endpoint);
        return "{\"power\":\"on\",\"ip\":\"" + ipAddress + "\",\"connected\":" + httpConnected + "}";
    }
    public boolean isHttpConnected() { return httpConnected; }
    public String getIp() { return ipAddress; }
}


class BluetoothDevice {
    private final String macAddress;
    private boolean bleConnected = false;

    public BluetoothDevice(String macAddress) {
        this.macAddress = macAddress;
    }

    public void bleConnect()    { this.bleConnected = true;  System.out.println("[BT] BLE connected: "    + macAddress); }
    public void bleDisconnect() { this.bleConnected = false; System.out.println("[BT] BLE disconnected: " + macAddress); }
    public void writeGattCharacteristic(String uuid, String value) {
        System.out.println("[BT] GATT write UUID=" + uuid + " value=" + value + " mac=" + macAddress);
    }
    public String readGattCharacteristic(String uuid) {
        return "GATT{uuid=" + uuid + ",mac=" + macAddress + ",connected=" + bleConnected + "}";
    }
    public boolean isBleConnected() { return bleConnected; }
    public String getMac() { return macAddress; }
}
