package com.example.sarthak.remindme;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sarthak on 11/5/16.
 * To check for first time launch
 */
public class PrefManagerWelcomeScreen {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public PrefManagerWelcomeScreen(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(Config.prefName, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(Config.firstTimeLaunch, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(Config.firstTimeLaunch, true);
    }
}
