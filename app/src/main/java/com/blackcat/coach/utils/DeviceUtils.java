/**
 * 
 */

package com.blackcat.coach.utils;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public final class DeviceUtils {
    public static String getDeviceSerial() {
        String serial = "";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception ignored) {
        }
        return serial;
    }

    /**
     * 获取社保屏幕信息
     * 
     * @param activity
     * @return
     */
    public static String getDisplayInfo(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.density + "," + dm.widthPixels + "," + dm.heightPixels;
    }

    // Suppress default constructor for noninstantiability
    private DeviceUtils() {
        throw new AssertionError();
    };

    public static int getScreenWidth(Context context) {
        if (context == null) {
            return 0;
        }
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int w = displayMetrics.widthPixels;
        if (w <= 0) {
            w = 720;
        }
        return w;
    }

    public static int getScreenHeight(Context context) {
        if (context == null) {
            return 0;
        }
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int w = displayMetrics.heightPixels;
        if (w <= 0) {
            w = 1280;
        }
        return w;
    }

    public static String getSubscriberId(Context ctx) {
        TelephonyManager telephonyManager = (TelephonyManager) ctx
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getSubscriberId();
    }
}
