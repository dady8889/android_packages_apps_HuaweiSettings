// HuaweiSettings - android_packages_apps_HuaweiSettings
// Simple Java app that provides preferences specific to the Huawei P9 Lite line of devices (AOSP)
// Copyright (C) 2017  Daniel 'dady8889' Múčka

package com.dady8889.huaweisettings;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.Context;
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
    private static final String PROPERTY_SOFT_VSYNC = "debug.sf.no_hw_vsync";
    private static final String PROPERTY_HAL_POWER = "persist.sys.stock_power_HAL";
    private static final String PROPERTY_HAL_LIGHTS = "persist.sys.stock_lights_HAL";
    private static final String PROPERTY_HAL_SENSORS = "persist.sys.sensorex";
    private static final String PROPERTY_MEDIA_GOOGLE_ENCODER = "persist.sys.google_avc_enc";
    private static final String PROPERTY_MEDIA_HIDEALBUMART = "persist.d.hidealbumart";
    private static final String PROPERTY_NOBLE = "persist.sys.noble";

    private static final String STRING_NULL = "null";

    private static final int MENU_REBOOT = Menu.FIRST;

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

        Log.d(TAG, "Activity opened!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // Add reboot button to menu
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

    public static class HuaweiFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        private static SharedPreferences preferenceManager;
        private static Context globalContext;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            globalContext = activity.getApplicationContext();
        }

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

            String actualString;
            boolean actualBool, functionAvailable;
            SwitchPreference switchPref;

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.layout.preferences);
            preferenceManager = PreferenceManager.getDefaultSharedPreferences(globalContext);

            // Set RIL/Dual SIM
            actualString = SystemPropertiesReflection.GetSystemString(PROPERTY_MULTISIM_CONFIG, STRING_NULL);
            switchPref = (SwitchPreference)findPreference("pref_huawei_simsetting");
            switchPref.setChecked(actualString.equals(SimConfig.DualStandby));

            // Set Gestures/Double tap to wake
            switchPref = (SwitchPreference)findPreference("pref_huawei_dt2w");
            functionAvailable = Functions.IsDT2WAvailable();
            if (functionAvailable) {
                boolean savedValue = preferenceManager.getBoolean("pref_huawei_dt2w", false);
                if (savedValue) {
                    switchPref.setChecked(savedValue);
                    Functions.SetDT2WValue(savedValue);
                }
            } else {
                switchPref.setEnabled(false);
            }

            // Set Workarounds/Software vsync
            actualBool = SystemPropertiesReflection.GetSystemBoolean(PROPERTY_SOFT_VSYNC, true);
            switchPref = (SwitchPreference)findPreference("pref_aospa_vsync");
            switchPref.setChecked(actualBool);

            // Set Workarounds/Bluetooth pairing
            actualBool = SystemPropertiesReflection.GetSystemBoolean(PROPERTY_NOBLE, true);
            switchPref = (SwitchPreference)findPreference("pref_noble");
            switchPref.setChecked(actualBool);

            // Set HAL/Power
            actualBool = SystemPropertiesReflection.GetSystemBoolean(PROPERTY_HAL_POWER, false);
            switchPref = (SwitchPreference)findPreference("pref_hal_power");
            switchPref.setChecked(actualBool);

            // Set HAL/Lights
            actualBool = SystemPropertiesReflection.GetSystemBoolean(PROPERTY_HAL_LIGHTS, false);
            switchPref = (SwitchPreference)findPreference("pref_hal_lights");
            switchPref.setChecked(actualBool);

            // Set HAL/Sensors
            actualBool = SystemPropertiesReflection.GetSystemBoolean(PROPERTY_HAL_SENSORS, false);
            switchPref = (SwitchPreference)findPreference("pref_hal_sensors");
            switchPref.setChecked(actualBool);

            // Set Media/Google AVC encoder
            actualBool = SystemPropertiesReflection.GetSystemBoolean(PROPERTY_MEDIA_GOOGLE_ENCODER, false);
            switchPref = (SwitchPreference)findPreference("pref_media_encoder");
            switchPref.setChecked(actualBool);

            // Set Media/Lockscreen album art
            actualBool = SystemPropertiesReflection.GetSystemBoolean(PROPERTY_MEDIA_HIDEALBUMART, false);
            switchPref = (SwitchPreference)findPreference("pref_media_albumart");
            switchPref.setChecked(actualBool);

            // Set Other/Gloves mode
            switchPref = (SwitchPreference)findPreference("pref_huawei_glovemode");
            functionAvailable = Functions.IsGloveModeAvailable();
            if (functionAvailable) {
                boolean savedValue = preferenceManager.getBoolean("pref_huawei_glovemode", false);
                if (savedValue) {
                    switchPref.setChecked(savedValue);
                    Functions.SetGloveModeValue(savedValue);
                }
            } else {
                switchPref.setEnabled(false);
            }

            // Set Other/USB Host mode
            switchPref = (SwitchPreference)findPreference("pref_huawei_usbhostmode");
            functionAvailable = Functions.IsUSBHostModeAvailable();
            if (functionAvailable) {
                boolean savedValue = preferenceManager.getBoolean("pref_huawei_usbhostmode", false);
                if (savedValue) {
                    switchPref.setChecked(savedValue);
                    Functions.SetUSBHostModeValue(savedValue);
                }
            } else {
                switchPref.setEnabled(false);
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
                case R.string.pref_aospa_vsync_key: {
                    SystemPropertiesReflection.SetSystemString(PROPERTY_SOFT_VSYNC, newValue ? "1" : "0");
                    editor.putBoolean("pref_aospa_vsync", newValue);
                    break;
                }
                case R.string.pref_noble_key: {
                    SystemPropertiesReflection.SetSystemString(PROPERTY_NOBLE, newValue ? "true" : "false");
                    break;
                }
                case R.string.pref_hal_power_key: {
                    SystemPropertiesReflection.SetSystemString(PROPERTY_HAL_POWER, newValue ? "true" : "false");
                    break;
                }
                case R.string.pref_hal_lights_key: {
                    SystemPropertiesReflection.SetSystemString(PROPERTY_HAL_LIGHTS, newValue ? "true" : "false");
                    break;
                }
                case R.string.pref_hal_sensors_key: {
                    SystemPropertiesReflection.SetSystemString(PROPERTY_HAL_SENSORS, newValue ? "true" : "false");
                    break;
                }
                case R.string.pref_media_encoder_key: {
                    SystemPropertiesReflection.SetSystemString(PROPERTY_MEDIA_GOOGLE_ENCODER, newValue ? "true" : "false");
                    break;
                }
                case R.string.pref_media_albumart_key: {
                    SystemPropertiesReflection.SetSystemString(PROPERTY_MEDIA_HIDEALBUMART, newValue ? "true" : "false");
                    break;
                }
                case R.string.pref_huawei_glovemode_key: {
                    Functions.SetGloveModeValue(newValue);
                    editor.putBoolean("pref_huawei_glovemode", newValue);
                    break;
                }
                case R.string.pref_huawei_usbhostmode_key: {
                    Functions.SetUSBHostModeValue(newValue);
                    editor.putBoolean("pref_huawei_usbhostmode", newValue);
                    break;
                }
            }
            editor.apply();
        }
    }
}
