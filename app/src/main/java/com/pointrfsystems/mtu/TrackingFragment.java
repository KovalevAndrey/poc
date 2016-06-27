package com.pointrfsystems.mtu;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pointrfsystems.mtu.data.LocalRepository;
import com.pointrfsystems.mtu.data.SerialPortMessage;
import com.pointrfsystems.mtu.data.SoundPlayer;
import com.pointrfsystems.mtu.utils.Parser;

import java.lang.ref.WeakReference;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by an.kovalev on 04.05.2016.
 */
public class TrackingFragment extends Fragment {

    private static final String BLEID = "bleid";
    private static final String BLEID_VALUE = "value";

    @Bind(R.id.bar_curent_rssi)
    ImageView bar;
    @Bind(R.id.bar_max_rssi)
    ImageView max_bar;
    @Bind(R.id.clear)
    Button clear;
    @Bind(R.id.curr_value)
    TextView curr_value;
    @Bind(R.id.max_value)
    TextView max_value;
    @Bind(R.id.sound_image)
    ImageView sound_image;

    private MyHandler mHandler;
    private String bleid;
    private boolean shouldCompare;
    private DiagramAnimator diagramAnimator;
    LocalRepository localRepository;
    private UsbService usbService;
    private SoundPlayer soundPlayer;

    private int currentRssi;
    private Thread soundThread;

    private boolean isSilent;

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    usbService.write("AT+SCAN3".getBytes());
                    byte lf[] = new byte[2];
                    lf[0] = 0xd;
                    lf[1] = 0x0a;
                    usbService.write(lf);
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };


    public static TrackingFragment newInstance(String bleid) {
        TrackingFragment trackingFragment = new TrackingFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BLEID, bleid);
        trackingFragment.setArguments(bundle);
        return trackingFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracking, container, false);
        ButterKnife.bind(this, view);

        localRepository = LocalRepository.getInstance(getContext());
        bleid = getArguments().getString(BLEID);

        ((MainActivity) getActivity()).setStatusBarColor(R.color.toolbar_background);
        ((MainActivity) getActivity()).setToolbarVisibility(true);
        ((MainActivity) getActivity()).setToolbarName("BLE ID: " + bleid);

        if (bleid == null || bleid.isEmpty()) {
            shouldCompare = false;
        } else {
            shouldCompare = true;
        }

        diagramAnimator = new DiagramAnimator(bar, max_bar, 400, curr_value, max_value);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diagramAnimator.clearMaxValue();
            }
        });

        if (localRepository.getIsSilentChecked()) {
            isSilent = true;
        } else {
            isSilent = false;
        }

        if (isSilent) {
            sound_image.setBackground(getResources().getDrawable(R.drawable.mute_image));
        } else {
            sound_image.setBackground(getResources().getDrawable(R.drawable.unmute_image));
        }


        sound_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSilent) {
                    sound_image.setBackground(getResources().getDrawable(R.drawable.unmute_image));
                    isSilent = false;
                } else {
                    sound_image.setBackground(getResources().getDrawable(R.drawable.mute_image));
                    isSilent = true;
                }
            }
        });


        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        soundPlayer = new SoundPlayer();
        soundThread = new Thread() {

            public void run() {

                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                int minFreq = 20;
                int maxFreq = 1000 + minFreq;
                int delta = maxFreq - minFreq;
                long counter = 0;


// MainActivity.currRssiPrecentage is a precentage value (0-100) representing the precentage of cuurnet RSSI reading with relation to the min-max tracking bar

                while (true) {

                    try {

                        Thread.sleep(minFreq);

                        if (getCurrentRssiPersantage() < 0)
                            continue;
                        counter += minFreq;

                        long s = (long) (delta * (float) ((100 - getCurrentRssiPersantage()) / 100.0));

                        if (counter >= s) {
                            if (!isSilent) {
                                toneGen1.startTone(ToneGenerator.TONE_PROP_BEEP, 100);
                            }
                            counter = 0;
                        }

                    } catch (InterruptedException e) {
                        return;
                    }

                }
            }
        };

        soundThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        soundPlayer.release();
        soundThread.interrupt();
    }

    private int getCurrentRssiPersantage() {
        return (currentRssi + 93) / 60 * 100;
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler = new MyHandler(this);
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
        localRepository.storeIsSilentChecked(isSilent);
        getActivity().unregisterReceiver(mUsbReceiver);
        getActivity().unbindService(usbConnection);
        Intent stopService = new Intent(getContext(), UsbService.class);
        getActivity().stopService(stopService);
    }


    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(getContext(), service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            getActivity().startService(startService);
        }
        Intent bindingIntent = new Intent(getContext(), service);
        getActivity().bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        getActivity().registerReceiver(mUsbReceiver, filter);
    }

    private static class UsbCallback implements UartCallback {

        private WeakReference<TrackingFragment> mFragment = null;

        public void setFragment(WeakReference<TrackingFragment> mFragment) {
            this.mFragment = mFragment;
        }

        @Override
        public void bleMsg(String bleId, byte rssi, byte[] macAddr, byte[] rawMsg) {

            if (mFragment != null) {
                String s = bleId + " " + rssi;
            }
        }

    }

    /*
     * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
     */
    private static class MyHandler extends Handler {
        private final WeakReference<TrackingFragment> mFragment;

        static private byte[] buf = new byte[4096];
        static private byte[] pkt = new byte[1024];
        static private int bufPtr = 0;
        static private int bufStart = -1;
        static private boolean sync = false;
        static private byte rssi = 0;
        static private int pktLen = 0;

        static private UsbCallback usbCallback = new UsbCallback();

        static private Parser parser = new Parser(MyHandler.usbCallback);

        public MyHandler(TrackingFragment fragment) {
            mFragment = new WeakReference<>(fragment);
            MyHandler.usbCallback.setFragment(mFragment);

            // Uart uartSim = new Uart(usbCallback);
            // uartSim.start();
        }


        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    int i;
                    //String data = (String) msg.obj;

                    if (mFragment.get() == null) {
                        return;
                    }
                    SerialPortMessage data = (SerialPortMessage) msg.obj;
                    //parser.newData(data.getBytes(), data.length());

                    //mFragment.get().display.append(data);

                    mFragment.get().currentRssi = data.getRssi();


                    if (mFragment.get().shouldCompare) {
                        String blied = mFragment.get().bleid;
                        if (data.getBleId().equalsIgnoreCase(blied)) {
                            mFragment.get().diagramAnimator.animateView(data.getRssi());
//                            if (mFragment.get().soundPlayer != null && mFragment.get().isSilent) {
//                                mFragment.get().soundPlayer.setFrequency(data.getRssi());
//                            }
                        }
                    } else {
                        mFragment.get().diagramAnimator.animateView(data.getRssi());
//                        if (mFragment.get().soundPlayer != null && mFragment.get().isSilent) {
//                            mFragment.get().soundPlayer.setFrequency(data.getRssi());
//                        }
                    }

                    break;
                case UsbService.CTS_CHANGE:
                    Toast.makeText(mFragment.get().getActivity(), "CTS_CHANGE", Toast.LENGTH_LONG).show();
                    break;
                case UsbService.DSR_CHANGE:
                    Toast.makeText(mFragment.get().getActivity(), "DSR_CHANGE", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
}
