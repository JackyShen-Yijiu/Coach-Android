package com.blackcat.coach;

import android.app.Application;
import android.content.Context;

import com.blackcat.coach.easemob.BlackCatHXSDKHelper;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.once.Once;
import com.blackcat.coach.utils.Constants;

import cn.jpush.android.api.JPushInterface;

import java.util.List;
import java.util.Iterator;
import android.app.ActivityManager;
import android.content.pm.PackageManager;

import com.easemob.chat.EMChat;
/**
 * Created by zou on 15/9/17.
 */
public class CarCoachApplication2 extends Application {

    /**
     * YYDoctorApplication的静态实例，为对外暴露的初始化方法的调用提供便利
     */
    private static CarCoachApplication sInstance;

    public final String PREF_USERNAME = "username";
    public static String currentUserNick = "";
    public static BlackCatHXSDKHelper hxSDKHelper = new BlackCatHXSDKHelper();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sInstance = this;
    }

    public static CarCoachApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Session.restore(this);
        Once.initialise(this);

        hxSDKHelper.onInit(this);
    }


}
