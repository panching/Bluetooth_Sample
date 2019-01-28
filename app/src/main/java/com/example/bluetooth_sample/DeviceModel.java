package com.example.bluetooth_sample;


import java.io.Serializable;

public class DeviceModel implements Serializable {
    private String name;
    private String mac;
    private String UUID;

    public DeviceModel() {
    }

    public DeviceModel(String name, String mac, String UUID) {
        this.name = name;
        this.mac = mac;
        this.UUID = UUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    @Override
    public String toString() {
        return "DeviceModel{" +
                "name='" + name + '\'' +
                ", mac='" + mac + '\'' +
                ", UUID='" + UUID + '\'' +
                '}';
    }
}
