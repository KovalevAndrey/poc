package com.pointrfsystems.mtu;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.pointrfsystems.mtu.views.RegistrationFragment;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    public static final long DISCONNECT_TIMEOUT = 30000;
    private BroadcastReceiver mReceiver;
    private boolean isScreenOn = true;

    @Bind(R.id.action_bar_my)
    Toolbar toolbar;
    @Bind(R.id.title)
    TextView title;

    private Handler disconnectHandler = new Handler() {
        public void handleMessage(Message msg) {
        }
    };

    public class ScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                isScreenOn = false;
            }
        }
    }

    public void lockScreen() {
        if (!(getCurrentFragment() instanceof PasscodeFragment || getCurrentFragment() instanceof TrackingFragment)) {
            replace(new PasscodeFragment());
        }
    }

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            lockScreen();
        }
    };

    public void resetDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction() {
        resetDisconnectTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        resetDisconnectTimer();
        if (!isScreenOn) {
            lockScreen();
            isScreenOn = true;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }

    public void setToolbarName(String name) {
        toolbar.setTitle(name);
    }

    public void setToolbarVisibility(boolean isVisible) {
        if (isVisible) {
            toolbar.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        replace(new PasscodeFragment());
        setSupportActionBar(toolbar);


        toolbar.setVisibility(View.GONE);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
    }

    @TargetApi(21)
    public void setStatusBarColor(int color) {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(color));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }


    @Override
    public void onBackPressed() {
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof BleidFragment) {
            BleidFragment bleidFragment = (BleidFragment) fragment;
            if (bleidFragment.isCustomKeyboardVisible()) {
                bleidFragment.hideCustomKeyboard();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    public void replace(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction tx = fragmentManager.beginTransaction();
        tx.replace(R.id.container, fragment);
        tx.commitAllowingStateLoss();
    }

    public void addBackStack(Fragment fragment) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.container, fragment);
        tx.addToBackStack(fragment.getClass().getName());
        tx.commitAllowingStateLoss();
    }

    protected Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.container);
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void showNext() {
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof PasscodeFragment) {
            addBackStack(new BleidFragment());
        }

    }

    public void showRegistrationFragment() {
        addBackStack(RegistrationFragment.newInstance());
    }

    public void showTrackingFragment(String bleid) {
        addBackStack(TrackingFragment.newInstance(bleid));
    }

    public void showSettingsFragment() {
        addBackStack(new SettingsFragment());
    }
}
