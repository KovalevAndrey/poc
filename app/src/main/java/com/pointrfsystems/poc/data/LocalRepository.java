package com.pointrfsystems.poc.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by a.kovalev on 25.05.16.
 */
public class LocalRepository {

    private static LocalRepository instance;
    private SharedPreferences preferences;
    private static final String POC_PREF = "POC_PREF";
    private static final String PASSWORD = "PASSWORD";
    private static final String IS_REGISTRED = "IS_REGISTRED";
    private static final String BLEID = "BLEID";
    private static final String POINTRF = "POINTRF";
    private static final String NOWANDER = "NOWANDER";
    private static final String API = "API";
    private static final String VOLUME = "VOLUME";
    public static final int VIBRO = 0;
    public static final int MUTE = 1;
    public static final int NORMAL = 2;

    public synchronized static LocalRepository getInstance(Context context) {
        if (instance == null) {
            instance = new LocalRepository(context);
            return instance;
        } else {
            return instance;
        }
    }

    private LocalRepository(Context context) {
        preferences = context.getSharedPreferences(POC_PREF, Context.MODE_PRIVATE);
    }

    public synchronized void storeBleid(String bleid) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(BLEID, bleid);
        editor.apply();
    }

    public synchronized String getBleid() {
        return preferences.getString(BLEID, "");
    }

    public synchronized void storePointrfPath(String path) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(POINTRF, path);
        editor.apply();
    }

    public synchronized String getPointRfPath() {
        return preferences.getString(POINTRF, "");
    }

    public synchronized void storeNowanderPath(String path) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(NOWANDER, path);
        editor.apply();
    }

    public synchronized String getNowanderPath() {
        return preferences.getString(NOWANDER, "");
    }

    public synchronized void storeApiLink(String link) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(API, link);
        editor.apply();
    }

    public synchronized String getApiLink() {
        return preferences.getString(API, "");
    }


    public synchronized void storeVolumeSettings(int volume) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(VOLUME, volume);
        editor.apply();
    }

    public synchronized int getVolumeSettings() {
        return preferences.getInt(VOLUME, 2);
    }


    public synchronized void storePassoword(String password) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PASSWORD, password);
        editor.apply();
    }

    public synchronized String getPassword() {
        return "1234A";
        //return preferences.getString(PASSWORD, "");
    }


    public synchronized void setAppRegistred(boolean isRegisted) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_REGISTRED, isRegisted);
        editor.apply();
    }

    public synchronized boolean isAppRegistred() {
        return preferences.getBoolean(IS_REGISTRED, false);
    }
}


