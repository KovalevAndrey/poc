package com.pointrfsystems.poc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addBackStack(new PasscodeFragment());
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

    public void showNext() {
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof PasscodeFragment) {
            replace(new BleidFragment());
        }
        if (currentFragment instanceof BleidFragment) {
            replace(new MainFragment());
        }

    }
}
