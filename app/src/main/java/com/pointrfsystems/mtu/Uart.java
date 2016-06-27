package com.pointrfsystems.mtu;

import java.util.Random;

public class Uart {

    private UartCallback callback = null;

    public Uart(UartCallback callback) {
        this.callback = callback;
    }

    public void start() {

        new Thread() {

            public void run() {

                String bleId = "1234";
                byte rssi = 1;
                byte[] rawMsg = new byte[]{0x02, 0x01, 0x06, 0x06, (byte) 0xff, 0x34, 0x12, 0x0c, 0x09, (byte) 0xe0};

                byte[] macAddr = new byte[]{(byte) 0xbd, 0x50, (byte) 0xa7, (byte) 0xf8, (byte) 0xe6, (byte) 0xa0};

                while (true) {

                    Random rn = new Random();

                    rssi = -68;
                    int max = 21;
                    int min = 0;

                    rssi -= rn.nextInt((max - min) + 1) + min;

                    //rssi -= rn.ints(1, 0, 21).findFirst().getAsInt() ;

                    // Limit the RSSI to a range of [-93,-30]

                    if (rssi > -30)
                        rssi = -30;

                    if (rssi < -93)
                        rssi = -93;

                    if (callback != null)
                        callback.bleMsg(bleId, rssi, macAddr, rawMsg);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {

                    }

                }
            }

        }.start();
    }

} 
