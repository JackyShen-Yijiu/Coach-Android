package com.blackcat.coach.models;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.utils.GsonUtils;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * A utility class for storing and retrieving session data.
 */
public class Session implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final String KEY = "session";
    private static final String KEY_USR = "usr";
    private static CoachInfo mUsrInfo;

    private static UserSetting mUserSetting;

    public static boolean isUserInfoEmpty() {
        return Session.mUsrInfo == null;
    }

    public static boolean loginIfNeeded(Activity act) {
        boolean res = isUserInfoEmpty();
        if (res) {
//			ToastHelper.getInstance(YYDoctorApplication.getInstance()).toast(R.string.no_login);
//			act.startActivity(new Intent(act, LoginActivity.class));
        }
        return res;
    }

    public static UserSetting getUserSetting() {
        return Session.mUserSetting;
    }

    public static void setUserSetting(UserSetting userSetting) {
        Session.mUserSetting = userSetting;
    }

    public static CoachInfo getSession() {
        return Session.mUsrInfo;
    }

    public static String getToken() {
        if (Session.mUsrInfo != null) {
            return Session.mUsrInfo.token;
        }
        return "";
    }

    /**
     * Stores the session data on disk.
     */
    public static void save(final String usrString) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Editor editor = CarCoachApplication.getInstance().getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
                editor.putString(KEY_USR, usrString);
                editor.commit();
            }
        }).start();
    }

    public static void save(CoachInfo wrapper, boolean needFlush) {
        Session.mUsrInfo = wrapper;
        if (wrapper != null) {
            Session.mUserSetting = wrapper.usersetting;
        }
        if (needFlush) {
            if (wrapper == null) {
                clearSavedSession();
            } else {
                save(GsonUtils.toJson(wrapper));
            }
        }
    }

    /**
     * Loads the session data from disk.
     */
    public static CoachInfo restore(Context context) {
        CoachInfo wrapper = null;
        SharedPreferences prefs = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        String usrString = prefs.getString(KEY_USR, null);
        if (!TextUtils.isEmpty(usrString)) {
            Type type = new TypeToken<CoachInfo>() {
            }.getType();
            try {
                wrapper = GsonUtils.fromJson(usrString, type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mUsrInfo = wrapper;
        return wrapper;
    }

    /**
     * Clears the saved session data.
     */
    public static void clearSavedSession() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Editor editor = CarCoachApplication.getInstance().getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
                editor.clear().commit();
            }
        }).start();
    }
}