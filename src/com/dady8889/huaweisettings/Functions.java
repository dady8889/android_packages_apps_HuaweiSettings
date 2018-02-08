// HuaweiSettings - android_packages_apps_HuaweiSettings
// Simple Java app that provides preferences specific to the Huawei P9 Lite line of devices (AOSP)
// Copyright (C) 2017  Daniel 'dady8889' Múčka

package com.dady8889.huaweisettings;

import android.util.Log;

import java.io.*;

public class Functions {
    private static final String TAG = "HuaweiSettings_Funcs";

    private static final String FILE_EASY_WAKEUP_GESTURE = "/sys/devices/platform/huawei_touch/easy_wakeup_gesture";
    private static final String FILE_GLOVE_MODE = "/sys/devices/platform/huawei_touch/touch_glove";
    private static final String FILE_USB_HOST = "/sys/devices/platform/ff100000.hisi_usb/plugusb";

    //region Sys
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
    //endregion

    //region DoubleTap2Wake
    public static boolean IsDT2WAvailable() {
        return SysIsAvailable(FILE_EASY_WAKEUP_GESTURE);
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
    //endregion

    //region Glove Mode
    public static boolean IsGloveModeAvailable() {
        return SysIsAvailable(FILE_GLOVE_MODE);
    }

    public static void SetGloveModeValue(boolean on) {
        try {
            if (on) {
                Log.i(TAG, "Enabling Glove Mode");
                SysWrite(FILE_GLOVE_MODE, "1");
            } else {
                Log.i(TAG, "Disabling Glove Mode");
                SysWrite(FILE_GLOVE_MODE, "0");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
    //endregion

    //region USB OTG
    public static boolean IsUSBHostModeAvailable() {
        return SysIsAvailable(FILE_USB_HOST);
    }

    public static void SetUSBHostModeValue(boolean on) {
        try {
            if (on) {
                Log.i(TAG, "Enabling USB Host Mode");
                SysWrite(FILE_USB_HOST, "hoston");
            } else {
                Log.i(TAG, "Disabling USB Host Mode");
                SysWrite(FILE_USB_HOST, "hostoff");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
    //endregion
}
