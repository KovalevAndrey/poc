package com.pointrfsystems.poc;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
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

import com.pointrfsystems.poc.data.SerialPortMessage;
import com.pointrfsystems.poc.utils.Parser;

import java.lang.ref.WeakReference;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by an.kovalev on 04.05.2016.
 */
public class MainFragment extends Fragment {

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
    private DiagramAnimator diagramAnimator;
    private UsbService usbService;
    @Bind(R.id.result)
    TextView display;
    private MyHandler mHandler;

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    display.append("USB ready");
                    usbService.write("AT+SCAN3".getBytes());
                    byte lf[] = new byte[2];
                    lf[0] = 0xd;
                    lf[1] = 0x0a;
                    usbService.write(lf);
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    display.append("USB no perm");
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    display.append("USB no conn");
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    display.append("USB disconn");
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    display.append("USB no support");
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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        diagramAnimator = new DiagramAnimator(bar, max_bar, 400, curr_value, max_value);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diagramAnimator.clearMaxValue();
            }
        });
        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new MyHandler(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mUsbReceiver);
        getActivity().unbindService(usbConnection);
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

        private WeakReference<MainFragment> mFragment = null;

        public void setFragment(WeakReference<MainFragment> mFragment) {
            this.mFragment = mFragment;
        }

        @Override
        public void bleMsg(String bleId, byte rssi, byte[] macAddr, byte[] rawMsg) {

            if (mFragment != null) {
                String s = bleId + " " + rssi;
                mFragment.get().display.append(s);
            }
        }

    }

    /*
     * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
     */
    private static class MyHandler extends Handler {
        private final WeakReference<MainFragment> mFragment;

        static private byte[] buf = new byte[4096];
        static private byte[] pkt = new byte[1024];
        static private int bufPtr = 0;
        static private int bufStart = -1;
        static private boolean sync = false;
        static private byte rssi = 0;
        static private int pktLen = 0;

        static private UsbCallback usbCallback = new UsbCallback();

        static private Parser parser = new Parser(MyHandler.usbCallback);

        public MyHandler(MainFragment fragment) {
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

                    SerialPortMessage data = (SerialPortMessage) msg.obj;
                    //parser.newData(data.getBytes(), data.length());

                    //mFragment.get().display.append(data);
                    mFragment.get().display.setText(Integer.toString(data.getRssi()));
                    mFragment.get().diagramAnimator.animateView(data.getRssi());


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
