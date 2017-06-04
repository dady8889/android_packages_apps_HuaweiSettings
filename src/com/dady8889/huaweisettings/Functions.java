// HuaweiSettings - android_packages_apps_HuaweiSettings
// Simple Java app that provides preferences specific to the Huawei P9 Lite line of devices (AOSP)
// Copyright (C) 2017  Daniel 'dady8889' Múčka

package com.dady8889.huaweisettings;

import android.util.Log;

import java.io.*;
import java.io.FileInputStream;
import java.util.Random;
import java.io.BufferedWriter;
import java.io.BufferedReader;

public class Functions {
    private static final String TAG = "HuaweiSettings_Funcs";

    private static final String FILE_EASY_WAKEUP_GESTURE = "/sys/devices/platform/huawei_touch/easy_wakeup_gesture";

    public static String SysRead(String fileName) {
        String line = null;
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(fileName), 512);
            line = reader.readLine();
        } catch (FileNotFoundException e) {
            Log.w(TAG, "File " + fileName + " not found!", e);
        } catch (IOException e) {
            Log.e(TAG, "Cannot read" + fileName, e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                // Ignored, not much we can do anyway
            }
        }

        return line;
    }

    public static boolean SysIsAvailable(String fileName) {
        final File file = new File(fileName);
        return file.exists() && file.canWrite();
    }

    public static boolean SysWrite(String fileName, String value) {
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(value);
        } catch (FileNotFoundException e) {
            Log.w(TAG, "File " + fileName + " not found!", e);
            return false;
        } catch (IOException e) {
            Log.e(TAG, "Cannot write to " + fileName, e);
            return false;
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                // Ignored, not much we can do anyway
            }
        }

        return true;
    }

    public static boolean IsDT2WAvailable() {
        return SysIsAvailable(FILE_EASY_WAKEUP_GESTURE);
    }

    public static boolean IsDT2WEnabled() {
       String gestureTypeValue = "";
       try {
           gestureTypeValue = SysRead(FILE_EASY_WAKEUP_GESTURE);
       } catch (Exception e) { e.printStackTrace(); }
       Log.i(TAG,"DT2W type=" + gestureTypeValue);
       return gestureTypeValue.equals("0x0001");
    }

    public static void SetDT2WValue(boolean on) {
    try {
        if (on) {
            Log.i(TAG, "Enabling DT2W");
                SysWrite(FILE_EASY_WAKEUP_GESTURE, "1");
        } else {
            Log.i(TAG, "Disabling DT2W");
                SysWrite(FILE_EASY_WAKEUP_GESTURE, "0");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}
