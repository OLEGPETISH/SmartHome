package com.smarthome.adapter;


class ZigbeeAdapter implements DeviceProtocol {

    private final ZigbeeDevice zigbee; // Adaptee — хранится как поле (Object Adapter)

    public ZigbeeAdapter(String deviceAddress) {
        this.zigbee = new ZigbeeDevice(deviceAddress);
    }

    @Override
    public void connect() {
        zigbee.pair(); // pair() → connect()
    }

    @Override
    public void disconnect() {
        zigbee.unpair(); // unpair() → disconnect()
    }

    @Override
    public void sendCommand(String command) {
        // Конвертируем строку-команду в байты — Zigbee работает с байтами
        zigbee.zigbeeSend(command.getBytes());
    }

    @Override
    public String readStatus() {
        return zigbee.zigbeeRead(); // адаптируем ответ Zigbee
    }

    @Override
    public String getProtocolName() { return "Zigbee"; }

    @Override
    public boolean isConnected() { return zigbee.isPaired(); }
}


class WifiAdapter implements DeviceProtocol {

    private final WifiDevice wifi; // Adaptee

    public WifiAdapter(String ipAddress) {
        this.wifi = new WifiDevice(ipAddress);
    }

    @Override
    public void connect() {
        wifi.httpConnect(); // httpConnect() → connect()
    }

    @Override
    public void disconnect() {
        wifi.httpDisconnect();
    }

    @Override
    public void sendCommand(String command) {
        // Команды отправляем как HTTP POST на /api/command
        wifi.httpPost("/api/command", "{\"cmd\":\"" + command + "\"}");
    }

    @Override
    public String readStatus() {
        return wifi.httpGet("/api/status"); // GET → readStatus()
    }

    @Override
    public String getProtocolName() { return "Wi-Fi"; }

    @Override
    public boolean isConnected() { return wifi.isHttpConnected(); }
}



class BluetoothAdapter implements DeviceProtocol {

    private final BluetoothDevice bluetooth; // Adaptee
    private static final String COMMAND_UUID = "0000FF01-0000-1000-8000-00805F9B34FB";
    private static final String STATUS_UUID  = "0000FF02-0000-1000-8000-00805F9B34FB";

    public BluetoothAdapter(String macAddress) {
        this.bluetooth = new BluetoothDevice(macAddress);
    }

    @Override
    public void connect() {
        bluetooth.bleConnect(); // bleConnect() → connect()
    }

    @Override
    public void disconnect() {
        bluetooth.bleDisconnect();
    }

    @Override
    public void sendCommand(String command) {
        // Команду пишем в GATT характеристику
        bluetooth.writeGattCharacteristic(COMMAND_UUID, command);
    }

    @Override
    public String readStatus() {
        return bluetooth.readGattCharacteristic(STATUS_UUID);
    }

    @Override
    public String getProtocolName() { return "Bluetooth"; }

    @Override
    public boolean isConnected() { return bluetooth.isBleConnected(); }
}


public class ProtocolAdapterFactory {

    public static DeviceProtocol create(String protocol, String address) {
        DeviceProtocol adapter = switch (protocol.toLowerCase()) {
            case "zigbee"    -> new ZigbeeAdapter(address);
            case "wifi"      -> new WifiAdapter(address);
            case "bluetooth" -> new BluetoothAdapter(address);
            default -> throw new IllegalArgumentException("Unknown protocol: " + protocol);
        };
        System.out.printf("[Adapter] Created %s adapter for address: %s%n",
                adapter.getProtocolName(), address);
        return adapter;
    }

    public static String[] availableProtocols() {
        return new String[]{"Zigbee", "WiFi", "Bluetooth"};
    }
}
