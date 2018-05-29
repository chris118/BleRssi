package com.hh.blerssi;

/**
 * Created by chrisw on 2018/5/29.
 */

public class NSDevice {
    String name;
    String address;
    Integer rssi;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getRssi() {
        return rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }
}
