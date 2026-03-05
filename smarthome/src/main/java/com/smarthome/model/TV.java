package com.smarthome.model;

/**
 * Smart TV device.
 */
public class TV extends AbstractDevice {
    private int channel;
    private int volume;

    public TV(String name, String room, String brand) {
        super(name, room, DeviceType.TV, brand);
        this.channel = 1;
        this.volume = 30;
    }

    public int getChannel() { return channel; }
    public void setChannel(int channel) { this.channel = channel; }
    public int getVolume() { return volume; }
    public void setVolume(int volume) { this.volume = Math.min(100, Math.max(0, volume)); }

    @Override
    public String getStatus() {
        if (!on) return "Off";
        return String.format("On | Ch: %d | Vol: %d%%", channel, volume);
    }
}
