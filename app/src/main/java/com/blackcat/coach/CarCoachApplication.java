package com.blackcat.coach;

import android.app.Application;
import android.content.Context;

import com.blackcat.coach.models.Session;
import com.blackcat.coach.once.Once;
import com.blackcat.coach.utils.Constants;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.blackcat.coach.easemob.BlackCatHXSDKHelper;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by zou on 15/9/17.
 */
public class CarCoachApplication extends Application {

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
        initImageLoader(getApplicationContext());
        initJpush();
        hxSDKHelper.onInit(this);

    }

    private void initJpush() {
        // 设置开启日志,发布时请关闭日志
        if (Constants.DEBUG) {
            JPushInterface.setDebugMode(true);
        }
        // 初始化 JPush
        JPushInterface.init(this);
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
//        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        if (Constants.DEBUG) {
            config.writeDebugLogs(); // Remove for release app
        }

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    public void setUsername(String username) {
        hxSDKHelper.setHXId(username);
    }
    public String getUsername() {
        return hxSDKHelper.getHXId();
    }

    public void setPassword(String pwd) {
        hxSDKHelper.setPassword(pwd);
    }
    public String getPassword() {
        return hxSDKHelper.getPassword();
    }
}
