package com.blackcat.coach.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.blackcat.coach.R;
import com.blackcat.coach.easemob.BlackCatHXSDKHelper;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.utils.SharedPreferencesUtil;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;

import java.lang.ref.WeakReference;
import java.util.List;

public class SplashActivity extends BaseNoFragmentActivity{

    private static final int sleepTime = 2000;

    public static String IS_APP_FIRST_OPEN = "is_app_first_open";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initViews();
        initData();
    }

    private void initData() {
//        Toast.makeText(this,"data-->"+this.getPackageName(),Toast.LENGTH_SHORT).show();

    }




    private final static int mSplashTime = 1000;

    private void initViews() {
        // 如果正在运行的除了本页面还有其他界面就不需要执行闪屏页动画
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(10);
        if (runningTasks != null && runningTasks.size() > 0) {
            ActivityManager.RunningTaskInfo cinfo = runningTasks.get(0);
            int num = cinfo.numRunning;
            if (num > 1) {
                finish();
                return;
            }
        }
        mHandler = new StaticHandler(new WeakReference<>(this));
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(0, mSplashTime);
        }
    }

    private Handler mHandler;
    // 防止内存泄漏
    static class StaticHandler extends Handler {
        final WeakReference<SplashActivity> outer;

        StaticHandler(WeakReference<SplashActivity> outer) {
            this.outer = outer;
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                SplashActivity outerObj = outer.get();
                if (outerObj != null) {
                    outerObj.go2IndexActivity();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static final String SHOW_GUIDE_TOUR = "ShowGuideTour";
    private void go2IndexActivity() {

        Intent intent = null;
        boolean isFirstOpen = SharedPreferencesUtil.getBoolean(
                getApplicationContext(), IS_APP_FIRST_OPEN, true);
        if (isFirstOpen) {
            intent = new Intent(this,GuideActivity.class);
        }else{
            if (Session.isUserInfoEmpty()) {
                intent = new Intent(this, NewLoginActivity.class);
            } else {
                intent = new Intent(this, IndexActivity.class);
            }
        }

        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }

    @Override
    protected void onStart(){
        super.onStart();
        if (Session.isUserInfoEmpty()) {
            return;
        }
        new Thread(new Runnable() {
            public void run() {
                if (BlackCatHXSDKHelper.getInstance().isLogined()) {
                    // ** 免登陆情况 加载所有本地群和会话
                    //不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
                    //加上的话保证进了主页面会话和群组都已经load完毕
                    long start = System.currentTimeMillis();
                    EMGroupManager.getInstance().loadAllGroups();
                    EMChatManager.getInstance().loadAllConversations();
                    long costTime = System.currentTimeMillis() - start;
                    //等待sleeptime时长
                    if (sleepTime - costTime > 0) {
                        try {
                            Thread.sleep(sleepTime - costTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
                    //进入主页面
//                    startActivity(new Intent(SplashActivity.this, IndexActivity.class));
//                    finish();
//                } else {
//                    try {
//                        Thread.sleep(sleepTime);
//                    } catch (InterruptedException e) {
//                    }
//                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//                    finish();
//                }
//            }
        }).start();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected boolean hasActionbarShadow() {
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //remove handler
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }
}
