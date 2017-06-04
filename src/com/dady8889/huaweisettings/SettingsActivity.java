// HuaweiSettings - android_packages_apps_HuaweiSettings
// Simple Java app that provides preferences specific to the Huawei P9 Lite line of devices (AOSP)
// Copyright (C) 2017  Daniel 'dady8889' Múčka

package com.dady8889.huaweisettings;

import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.SwitchPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import com.android.settingslib.drawer.SettingsDrawerActivity;

import com.dady8889.huaweisettings.Functions;

public class SettingsActivity extends SettingsDrawerActivity {

    private static final String TAG = "HuaweiSettings";

    private static final String PROPERTY_MULTISIM_CONFIG = "persist.radio.multisim.config";

    private static final String STRING_NULL = "null";

    private static final int MENU_REBOOT = Menu.FIRST;

    SharedPreferences preferenceManager;

    private static final class SimConfig {
        private SimConfig () {}
        static final String Single = "single";
        static final String DualStandby = "dsds";
		static final String DualActive = "dsda";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(R.id.content_frame, new HuaweiFragment()).commit();

        // Get default preferences file
        preferenceManager = PreferenceManager.getDefaultSharedPreferences(this);

        Log.d(TAG, "Activity opened!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // Ad reboot button to menu
        menu.add(Menu.NONE, MENU_REBOOT, Menu.NONE, R.string.action_reboot_title).setIcon(R.drawable.ic_replay_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case MENU_REBOOT: {

                // Show confirmation dialog
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setMessage(R.string.action_reboot_description);
                alertBuilder.setCancelable(true);
                alertBuilder.setPositiveButton(
                    R.string.alert_yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                Process proc = Runtime.getRuntime().exec(new String[]{"sh", "-c", "svc power reboot"});
                                proc.waitFor();
                            } catch (Exception ex) {
                                Log.e(TAG, ex.getMessage());
                            }
                        }
                    });
                alertBuilder.setNegativeButton(
                    R.string.alert_no,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                AlertDialog alertReboot = alertBuilder.create();
                alertReboot.show();
                break;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public class HuaweiFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.layout.preferences);

            // Get saved preferences

            // Set RIL/Dual SIM value
            String actualSimSettingValue = SystemPropertiesReflection.GetSystemString(PROPERTY_MULTISIM_CONFIG, STRING_NULL);
            SwitchPreference switchpref = (SwitchPreference)findPreference("pref_huawei_simsetting");
            switchpref.setChecked(actualSimSettingValue.equals(SimConfig.DualStandby) ? true : false);
            switchpref.setEnabled(false); // Disable Dual SIM on AOSPA N

            // Set Gestures/Double tap to wake value
            switchpref = (SwitchPreference)findPreference("pref_huawei_dt2w");
            boolean dt2wAvailable = Functions.IsDT2WAvailable();
            if (dt2wAvailable) {
                switchpref.setChecked(Functions.IsDT2WEnabled() || preferenceManager.getBoolean("pref_huawei_dt2w", false));
            } else {
                switchpref.setEnabled(false);
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            int resourceIndex = getResources().getIdentifier(key + "_key", "string", getContext().getPackageName());
            boolean newValue = sharedPreferences.getBoolean(key, false);
            SharedPreferences.Editor editor = preferenceManager.edit();

            switch (resourceIndex) {
                case R.string.pref_huawei_simsetting_key: {
                    if (newValue)
                        SystemPropertiesReflection.SetSystemString(PROPERTY_MULTISIM_CONFIG, SimConfig.DualStandby);
                    else
                        SystemPropertiesReflection.SetSystemString(PROPERTY_MULTISIM_CONFIG, SimConfig.Single);
                    break;
                }
                case R.string.pref_huawei_dt2w_key: {
                    Functions.SetDT2WValue(newValue);
                    editor.putBoolean("pref_huawei_dt2w", newValue);
                    break;
                }
            }
            editor.apply();
        }
    }
}
