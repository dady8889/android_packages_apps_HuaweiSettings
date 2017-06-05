// HuaweiSettings - android_packages_apps_HuaweiSettings
// Simple Java app that provides preferences specific to the Huawei P9 Lite line of devices (AOSP)
// Copyright (C) 2017  Daniel 'dady8889' Múčka

package com.dady8889.huaweisettings;

import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.preference.PreferenceManager;

import com.dady8889.huaweisettings.Functions;

public class Bootreceiver extends BroadcastReceiver  {

    private static final String TAG = "HuaweiSettings_Boot";

    SharedPreferences preferenceManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "Received on boot finish event");

        preferenceManager = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        // Enable DT2W if user enabled it
        if (Functions.IsDT2WAvailable() && preferenceManager.getBoolean("pref_huawei_dt2w", false)) {
            Functions.SetDT2WValue(true);
        }

        // Enable gloves mode if user enabled it
        if (Functions.IsGloveModeAvailable() && preferenceManager.getBoolean("pref_huawei_glovemode", false)) {
            Functions.SetGloveModeValue(true);
        }

        // Enable VSYNC workaround
        boolean vsyncEnabled = preferenceManager.getBoolean("pref_aospa_vsync", true);
        SystemPropertiesReflection.SetSystemString("debug.sf.no_hw_vsync", vsyncEnabled ? "1" : "0");
    }
}
