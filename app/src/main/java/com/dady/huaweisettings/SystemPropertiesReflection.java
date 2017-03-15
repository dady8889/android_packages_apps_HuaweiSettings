package com.dady.huaweisettings;

import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by dady on 14.3.2017.
 */

final class SystemPropertiesReflection {

    private static final String TAG = "HuaweiSettings_Reflect";

    static String GetSystemString(String name, String def)
    {
        Class systemProperties = null;
        try {
            systemProperties = Class.forName("android.os.SystemProperties");
            Method method = systemProperties.getDeclaredMethod("get", String.class, String.class);
            String propertyValue = (String)method.invoke(systemProperties, name, def);
            Log.d(TAG, "GetSystemString: Prop [" + name + "] Value [" + propertyValue + "]");
            return propertyValue;
        } catch (Exception ex) {
            Log.e(TAG, "GetSystemString: " + ex.getMessage());
        }
        return def;
    }

    static void SetSystemString(String name, String val)
    {
        Class systemProperties = null;
        try {
            systemProperties = Class.forName("android.os.SystemProperties");
            Method method = systemProperties.getDeclaredMethod("set", String.class, String.class);
            method.invoke(systemProperties, name, val);
            Log.d(TAG, "SetSystemString: Prop [" + name + "] Value [" + val + "]");
        } catch (Exception ex) {
            Log.e(TAG, "SetSystemString: " + ex.getMessage());
        }
    }
}
