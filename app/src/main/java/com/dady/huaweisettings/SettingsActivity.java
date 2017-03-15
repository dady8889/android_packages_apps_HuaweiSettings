package com.dady.huaweisettings;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "HuaweiSettings";
    private static final String PROPERTY_MULTISIM_CONFIG = "persist.radio.multisim.config";
    private static final String STRING_NULL = "null";

    private static final class SimConfig {
        private SimConfig () {}
        static final String Single = "single";
        static final String DualStandby = "dsds";
        static final String DualActive = "dsda";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content, new HuaweiFragment()).commit();

        Log.d(TAG, "Activity opened!");
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_huawei, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home: {
                finish();
                break;
            }
            case R.id.action_reboot: {
                try {
                    //Process proc = Runtime.getRuntime().exec(new String[]{"sh", "-c", "killall system_server"});
                    Process proc = Runtime.getRuntime().exec(new String[]{"sh", "-c", "reboot"});
                    proc.waitFor();
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
                break;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public static class HuaweiFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

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
            addPreferencesFromResource(R.xml.pref_huawei);
            //PreferenceManager.setDefaultValues(getContext(), R.xml.pref_huawei, false);

            String actualSimConfigValue = SystemPropertiesReflection.GetSystemString(PROPERTY_MULTISIM_CONFIG, STRING_NULL);
            SwitchPreference switchpref = (SwitchPreference)findPreference("pref_huawei_simsetting");
            if (actualSimConfigValue.equals(SimConfig.DualStandby)) {
                switchpref.setChecked(true);
            } else {
                switchpref.setChecked(false);
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            int resourceIndex = getResources().getIdentifier(key + "_key", "string", getContext().getPackageName());
            switch (resourceIndex) {
                case R.string.pref_huawei_simsetting_key: {
                    Boolean newValue = sharedPreferences.getBoolean(key, false);
                    if (newValue)
                        SystemPropertiesReflection.SetSystemString(PROPERTY_MULTISIM_CONFIG, SimConfig.DualStandby);
                    else
                        SystemPropertiesReflection.SetSystemString(PROPERTY_MULTISIM_CONFIG, SimConfig.Single);
                    break;
                }
            }
        }
    }
}
