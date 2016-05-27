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
    private static final String BLEID = "BLEID";
    private static final String IMAGE = "IMAGE";
    private static final String API = "IMAGE";

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

    public synchronized void storeImagePath(String path) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(IMAGE, path);
        editor.apply();
    }

    public synchronized String getImagePath() {
        return preferences.getString(IMAGE, "");
    }

    public synchronized void storeApiLink(String link) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(API, link);
        editor.apply();
    }

    public synchronized String getApiLink() {
        return preferences.getString(API, "");
    }
}


