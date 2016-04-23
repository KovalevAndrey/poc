package com.pointrfsystems.poc;

public interface UartCallback {
	
    public void bleMsg(String bleId, byte rssi, byte[] macAddr, byte[] rawMsg);

}
