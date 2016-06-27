package com.pointrfsystems.mtu.data;

import android.media.AudioManager;
import android.media.ToneGenerator;

/**
 * Created by a.kovalev on 02.06.16.
 */
public class SoundPlayer {

    private ToneGenerator toneGenerator;
    private boolean isReleased;

    public void setFrequency(int rssi) {
        if (!isReleased) {
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 150);
        }
    }

    public SoundPlayer() {
        toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
    }

    private int transformRssiToFrequency(int rssi) {
        if (rssi < -80) {
            return ToneGenerator.TONE_SUP_DIAL;
        } else {
            return ToneGenerator.TONE_SUP_ERROR;
        }
    }

    public void release() {
        isReleased = true;
        toneGenerator.stopTone();
        toneGenerator.release();
    }

}
