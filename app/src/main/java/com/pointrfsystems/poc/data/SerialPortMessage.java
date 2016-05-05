package com.pointrfsystems.poc.data;

/**
 * Created by an.kovalev on 04.05.2016.
 */
public class SerialPortMessage {

    private String bleId;
    private int rssi;

    public int getMaxRssi() {
        return maxRssi;
    }

    public int getRssi() {
        return rssi;
    }

    private int maxRssi;

    public SerialPortMessage(String bleId, int rssi, int maxRssi) {
        this.bleId = bleId;
        this.rssi = rssi;
        this.maxRssi = maxRssi;

    }

    public String getBleId() {
        return bleId;
    }

}
