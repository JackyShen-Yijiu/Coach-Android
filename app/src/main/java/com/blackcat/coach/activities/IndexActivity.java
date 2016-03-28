package com.blackcat.coach.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.blackcat.coach.R;
import com.blackcat.coach.adapters.ReservationAdapter;
import com.blackcat.coach.easemob.BlackCatHXSDKHelper;
import com.blackcat.coach.easemob.Constant;
import com.blackcat.coach.easemob.basefunction.HXSDKHelper;
import com.blackcat.coach.events.LogoutEvent;
import com.blackcat.coach.events.MonthApplyEvent;
import com.blackcat.coach.events.NetStateEvent;
import com.blackcat.coach.events.NewMessageReceiveEvent;
import com.blackcat.coach.fragments.ReservationFragment;
import com.blackcat.coach.i.IKillable;
import com.blackcat.coach.i.IResourceDischarger;
import com.blackcat.coach.models.CoachInfo;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.NetConstants;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.AppEnv;
import com.blackcat.coach.utils.CommonUtil;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.UIUtils;
import com.blackcat.coach.utils.Utils;
import com.blackcat.coach.utils.VolleyUtil;
import com.blackcat.coach.widgets.MainScreenContainer;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.util.NetUtils;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.greenrobot.event.EventBus;

public class IndexActivity extends BaseActivity implements IKillable,
        EMEventListener,
        MainScreenContainer.OnTabLisener, View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private static final String TAG = "MainScreen";
    public static final int RESULT_CODE_EXIT_APP = 20;
    private static final int MSG_START_RECEIVE_TOUCH_DELAY = 500;
    private static final int MSG_RELOAD_RESOURCES = 8;
    private static final int MSG_RELOAD_RESOURCES_DELAY = 200;

    private static final int MSG_START_RECEIVE_TOUCH = 2; // it's time to
    // receive touch
    // event
    private static final String STATE_KEY_INT_SELECTED_TAB = "selected_tab";

    /**
     * Due to the activity switch is too fast to clean the touch event queue
     * completely, we will discard some touch event to avoid the touch problem
     * in splash screen
     */
    private boolean mIsClickable = false;
    private Context mContext;

    private boolean mIsActive = false;
    private boolean mShouldReloadResources = false;
    private final List<WeakReference<IResourceDischarger>> mFragDischargerList = new ArrayList<WeakReference<IResourceDischarger>>();
    private final List<WeakReference<IKillable>> mFragKillableList = new ArrayList<WeakReference<IKillable>>();

    private TextView mToolBarLeftTitle,mToolBarTitle, mVerificationWarning;
    private RadioGroup mRadioGroupReservation;

    // 是否是OnCreate之后的第一次onResume调用
    private boolean mFirstResumeAfterOnCreate = false;

    private boolean mIsConflict = false;

    private BCConnectionListener connectionListener;


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }


    // 防止内存泄漏
    static class StaticHandler extends Handler {
        final WeakReference<IndexActivity> outer;

        StaticHandler(WeakReference<IndexActivity> outer) {
            this.outer = outer;
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                IndexActivity outerObj = outer.get();
                if (outerObj != null) {
                    outerObj.handleMessage(msg);
                }
            } catch (Exception e) {
            }
        }
    }

    private Handler mHandler;

    public void handleMessage(Message msg) {
        IndexActivity mainScreen = (IndexActivity) msg.obj;
        if (mainScreen == null) {
            return;
        }

        switch (msg.what) {
            case MSG_START_RECEIVE_TOUCH:
                mIsClickable = true;
                break;
            case MSG_RELOAD_RESOURCES:
                reloadResources();
                break;
            default:
                break;
        }
    }

    //签到
    private LinearLayout llQianDao;
    private TextView tvQianDao;
    private ImageView imgQuery;

    private int currentPage = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        mHandler = new StaticHandler(new WeakReference<IndexActivity>(this));
        mShouldReloadResources = false;
        mContext = this;
        mFirstResumeAfterOnCreate = (savedInstanceState == null);

        // 刷新token
        // jpush alias和tag是通过接口请求的

        refreshCheck();

        init();
        setListener();

        //定位
        getLocation();
        EventBus.getDefault().register(this);

    }

    private void setListener(){
        mRadioGroupReservation.setOnCheckedChangeListener(this);
    }

    public LocationClient mLocationClient = null;
    public BDLocationListener mLocationListener = new BCLocationListener();

    private void getLocation() {
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(mLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 0;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.SetIgnoreCacheException(true);//可选，默认false，设置是否收集CRASH信息，默认收集
        mLocationClient.setLocOption(option);

        mLocationClient.start();
    }


    public class BCLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            LogUtil.print("location----0000>");
            //Receive Location
            if (location.getLocType() == BDLocation.TypeGpsLocation ||
                    location.getLocType() == BDLocation.TypeNetWorkLocation ||
                    location.getLocType() == BDLocation.TypeOffLineLocation ) {
                LogUtil.print("location---->"+location.getLatitude()+"Longitude:>>>"+location.getLongitude());

                Session.saveUserLocation(location.getLongitude()+"",location.getLatitude()+"");

//                LogUtil.print("getLongitude---" + Double.toString(location.getLongitude()));
//                LogUtil.print("getLatitude---"+Double.toString(location.getLatitude()));
            } else if (location.getLocType() == BDLocation.TypeServerError) {

            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {

            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {

            }
        }
    }


    public final static String DAILY_REFRESH_TAGS = "daily_refresh_tags";
    public final static String DAILY_REFRESH_TOKEN = "daily_refresh_token";

    private void refreshCheck() {
        fetchCoachInfo();
    }

    private Type mTokenType = new TypeToken<Result<CoachInfo>>() {
    }.getType();

    private void fetchCoachInfo() {
        URI uri = URIUtil.getCoachInfo(Session.getSession().coachid);
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }

        Map map = new HashMap<>();
        map.put(NetConstants.KEY_AUTHORIZATION, Session.getToken());
        GsonIgnoreCacheHeadersRequest<Result<CoachInfo>> request = new GsonIgnoreCacheHeadersRequest<Result<CoachInfo>>(
                Request.Method.GET, url, null, mTokenType, map,
                new Response.Listener<Result<CoachInfo>>() {
                    @Override
                    public void onResponse(Result<CoachInfo> response) {
                        if (response != null && response.data != null && response.type == Result.RESULT_OK) {
                            Session.save(response.data, true);
                            if (!Session.getSession().is_validation) {
                                mVerificationWarning.setVisibility(View.VISIBLE);
                            } else {
                                mVerificationWarning.setVisibility(View.GONE);
                            }
                        }
                        if (Constants.DEBUG) {
                            VolleyLog.v("Response:%n %s", response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                    }
                });
        // 请求加上Tag,用于取消请求
        request.setTag(this);
        request.setShouldCache(false);

        VolleyUtil.getQueue(this).add(request);
    }


    @Override
    protected boolean hasActionbarShadow() {
        return false;
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        IResourceDischarger fragDischarger = null;
        try {
            fragDischarger = (IResourceDischarger) fragment;
        } catch (ClassCastException e) {
            fragDischarger = null;
        }
        if (fragDischarger != null) {
            mFragDischargerList.add(new WeakReference<IResourceDischarger>(fragDischarger));
        }
        if (fragment instanceof IKillable) {
            if (AppEnv.bAppdebug) {
                Log.d(TAG, "killable fragment " + fragment.getTag());
            }
            mFragKillableList.add(new WeakReference<IKillable>((IKillable) fragment));
        }
    }

    private void reloadResources() {
        if (mShouldReloadResources) {
            for (WeakReference<IResourceDischarger> ref : mFragDischargerList) {
                IResourceDischarger f = ref.get();
                if (f != null && ((Fragment) f).isHidden()) {
                    f.reloadResource();
                }
            }
            mShouldReloadResources = false;
        }
    }

    private void releaseResources() {
        for (WeakReference<IResourceDischarger> ref : mFragDischargerList) {
            IResourceDischarger f = ref.get();
            if (f != null) {
                if (Utils.isActivityFinishing(this)
                        || ((Fragment) f).isHidden()) {
                    f.releaseResource();
                }
            }
        }
        mShouldReloadResources = true;
        // 系统进行内存回收
        System.gc();

        UIUtils.clearCavnasCaches();
    }

    @Override
    public void onStart() {
        super.onStart();

        mIsActive = true;
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Index Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app deep link URI is correct.
//                Uri.parse("android-app://com.blackcat.coach.activities/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirstResumeAfterOnCreate = false;

        Message msg = mHandler.obtainMessage(MSG_START_RECEIVE_TOUCH, IndexActivity.this);
        mHandler.sendMessageDelayed(msg, MSG_START_RECEIVE_TOUCH_DELAY);
        msg = mHandler.obtainMessage(MSG_RELOAD_RESOURCES, IndexActivity.this);
        mHandler.sendMessageDelayed(msg, MSG_RELOAD_RESOURCES_DELAY);
        refreshView();
        refreshUI();
        registerEnventListener();


    }

    private void registerEnventListener() {
        BlackCatHXSDKHelper hxsdkHelper = (BlackCatHXSDKHelper) BlackCatHXSDKHelper.getInstance();
        hxsdkHelper.pushActivity(this);

        EMChatManager.getInstance().registerEventListener(this,
                new EMNotifierEvent.Event[]{
                        EMNotifierEvent.Event.EventNewMessage,
                        EMNotifierEvent.Event.EventOfflineMessage,
                        EMNotifierEvent.Event.EventConversationListChanged
                });
    }

    @Override
    protected void onPause() {
        EMChatManager.getInstance().unregisterEventListener(this);
        BlackCatHXSDKHelper hxsdkHelper = (BlackCatHXSDKHelper) BlackCatHXSDKHelper.getInstance();
        hxsdkHelper.popActivity(this);

        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Index Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app deep link URI is correct.
//                Uri.parse("android-app://com.blackcat.coach.activities/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
        mIsActive = false;
        releaseResources();

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // discard the touch event
        if (mIsClickable == false) {
            return true;
        }
        // handle it
        return super.dispatchTouchEvent(ev);
    }


	/*
     * If anyone want to get the thumbnail after the resource release, we need
	 * to reload it and re-schedule the release time
	 */

    @Override
    public boolean onCreateThumbnail(Bitmap outBitmap, Canvas canvas) {
        if (!mIsActive) {
            reloadResources();
        }
        boolean ret = super.onCreateThumbnail(outBitmap, canvas);
        if (!mIsActive) {
            releaseResources();
        }
        return ret;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mMainScreenLocalReceiver);
        } catch (Exception e) {
            if (AppEnv.bAppdebug) {
                Log.e(TAG, "", e);
            }
        }

        // remove handler
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;

        // Release all UI related resource
        mIsActive = false;

        if (connectionListener != null) {
            EMChatManager.getInstance().removeConnectionListener(connectionListener);
        }

        EventBus.getDefault().unregister(this);
        // VolleyUtil.getQueue(mContext).add(new
        // ClearCacheRequest(VolleyUtil.getQueue(mContext).getCache(), null));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        selectTabByIntent(intent);
        refreshCheck();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CODE_EXIT_APP) {
            Utils.finishActivity(this);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage: {
                EMMessage message = (EMMessage) event.getData();
                HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
                refreshUI();
                break;
            }
            case EventOfflineMessage:
                refreshUI();
                break;
            case EventConversationListChanged:
                refreshUI();
                break;
            default:
                break;
        }
    }

    private void refreshUI() {
        //更新未读消息显示
        //refreshUnreadLabel();
        // 刷新当前页面
        ReservationAdapter.fragments[ReservationFragment.currentPage].reRusume();

        //更新fragment消息通知
        EventBus.getDefault().post(new NewMessageReceiveEvent());

    }

    public void refreshUnreadLabel() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int cpunt = getUnreadMsgCountTotal();
            }
        });
    }

    private int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal = 0;
        int chatroomUnreadMsgCount = 0;
        unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
        for (EMConversation conversation : EMChatManager.getInstance().getAllConversations().values()) {
            if (conversation.getType() == EMConversation.EMConversationType.ChatRoom)
                chatroomUnreadMsgCount = chatroomUnreadMsgCount + conversation.getUnreadMsgCount();
        }
        return unreadMsgCountTotal - chatroomUnreadMsgCount;
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////
    private MainScreenContainer mMainContainer;
    public static final String ACTION_REFRESH_REDPOINT = "ACTION_REFRESH_REDPOINT";// 刷新红点状态
    public static final String ACTION_SHOW_TAB = "ACTION_SHOW_TAB";// 显示tab
    public static final String ACTION_HIDE_TAB = "ACTION_HIDE_TAB";// 隐藏tab
    private final BroadcastReceiver mMainScreenLocalReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }

            String act = intent.getAction();
            if (ACTION_REFRESH_REDPOINT.equals(act)) {
                mMainContainer.refreshTab();
            } else if (ACTION_HIDE_TAB.equals(act)) {
                mMainContainer.hideTabContainer();
            } else if (ACTION_SHOW_TAB.equals(act)) {
                mMainContainer.showTabContainer();
            }
        }
    };

    private void init() {
        Utils.setContentView(this, R.layout.activity_index);
        mToolBarTitle = (TextView) findViewById(R.id.toolbar_title);
        mToolBarLeftTitle = (TextView) findViewById(R.id.toolbar_left_title);

        llQianDao = (LinearLayout) findViewById(R.id.toolbar_title_right);
        tvQianDao = (TextView) findViewById(R.id.toobar_title_right_tv);
        imgQuery = (ImageView) findViewById(R.id.toobar_title_right_img);
        mVerificationWarning = (TextView) findViewById(R.id.tv_verification_warning);
        if (!Session.getSession().is_validation) {
            mVerificationWarning.setVisibility(View.VISIBLE);
        }

        mRadioGroupReservation = (RadioGroup) findViewById(R.id.rg_reservation);
        mMainContainer = (MainScreenContainer) Utils.findViewById(this, R.id.main_screen_container);
        mMainContainer.setup(getSupportFragmentManager());
        mMainContainer.setOnTabListener(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_REFRESH_REDPOINT);
        intentFilter.addAction(ACTION_SHOW_TAB);
        intentFilter.addAction(ACTION_HIDE_TAB);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMainScreenLocalReceiver, intentFilter);

        mMainContainer.post(new Runnable() {

            @Override
            public void run() {
                selectTabByIntent(getIntent());
            }
        });

        connectionListener = new BCConnectionListener();
        EMChatManager.getInstance().addConnectionListener(connectionListener);
    }

    private void refreshView() {
        mMainContainer.refreshTab();
    }

    private void selectTabByIntent(Intent intent) {

        int tab = 0;
        if (intent != null) {
            tab = intent.getIntExtra(SELECT_TAB_NAME, 0);
        }
        mMainContainer.jumpTab(tab, intent);
    }

    public static final String SELECT_TAB_NAME = "SELECT_TAB_NAME";
    public static final int TAB_STUDENT = 1;//学员
    public static final int TAB_MESSAGE = 2;//消息
    public static final int TAB_PROFILE = 3;//我的
    public static final int TAB_SCHEDULE = 4;//日程

    /**
     * 向需要接收onKeyDown事件并且提供了onKeyDown方法的Fragment传递按键事件。
     * 需要接收按键事件的Fragment需要提供如下方法：<br>
     * public boolean onKeyDown(int keyCode, KeyEvent event) {return true or
     * false}
     */
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		return mMainContainer.onKeyDown(keyCode, event) ? true : super
//				.onKeyDown(keyCode, event);
//	}
//
    private long firstTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {// 如果两次按键时间间隔大于800毫秒，则不退出
                Toast.makeText(IndexActivity.this, "再按一次退出程序...",
                        Toast.LENGTH_SHORT).show();
                firstTime = secondTime;// 更新firstTime
                return true;
            } else {
                // System.exit(0);// 否则退出程序
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        // 加一个保护避免Fragment爆掉, like this:
        // 03-14 21:50:02.859 E/CrashHandler(16989):
        // java.lang.IllegalStateException: Can not perform this action after
        // onSaveInstanceState
        // 03-14 21:50:02.859 E/CrashHandler(16989): at
        // android.app.FragmentManagerImpl.checkStateLoss(FragmentManager.java:1280)
        // 03-14 21:50:02.859 E/CrashHandler(16989): at
        // android.app.FragmentManagerImpl.popBackStackImmediate(FragmentManager.java:451)
        // 03-14 21:50:02.859 E/CrashHandler(16989): at
        // android.app.Activity.onBackPressed(Activity.java:2228)
        // 03-14 21:50:02.859 E/CrashHandler(16989): at
        // android.app.Activity.onKeyUp(Activity.java:2206)
        // 03-14 21:50:02.859 E/CrashHandler(16989): at
        // android.view.KeyEvent.dispatch(KeyEvent.java:2633)
        // 03-14 21:50:02.859 E/CrashHandler(16989): at
        // android.app.Activity.dispatchKeyEvent(Activity.java:2436)
        try {
            super.onBackPressed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_KEY_INT_SELECTED_TAB,
                mMainContainer != null ? mMainContainer.getCurrentTabType()
                        : IndexActivity.TAB_STUDENT);
        outState.putBoolean(Constant.LOGIN_STATE_CONFLICT, isConflict());
    }

    @Override
    public boolean isKillable() {
        if (mFragKillableList != null) {
            for (WeakReference<IKillable> aKillableRef : mFragKillableList) {
                IKillable aKillable = aKillableRef.get();
                if (aKillable != null && !aKillable.isKillable()) {
                    return false;
                }
            }
        }
        return true;
    }



    @Override
    public void onTabSelected(int index, boolean reClicked) {
        mToolBarLeftTitle.setVisibility(View.GONE);
        switch (index) {
            case TAB_STUDENT:
                mToolBarTitle.setVisibility(View.VISIBLE);
                mRadioGroupReservation.setVisibility(View.GONE);
                mToolBarTitle.setText(R.string.title_reservation);
                showHideQianDao(true, ReservationFragment.currentPage, 0);
                mMainContainer.stttop(120);
                break;
            case TAB_SCHEDULE:
//                setRightTitleWithoutImg(CommonUtil.getString(mContext, R.string.student_appointment));
                setLeftTitle(CommonUtil.getString(mContext, R.string.toady));
                mToolBarTitle.setVisibility(View.VISIBLE);
                mRadioGroupReservation.setVisibility(View.GONE);
                mToolBarTitle.setText(R.string.title_schedule);
                showHideQianDao(false, ReservationFragment.currentPage, 1);
                mMainContainer.stttop(120);
                break;
            case TAB_MESSAGE:
                mToolBarTitle.setVisibility(View.VISIBLE);
                mRadioGroupReservation.setVisibility(View.GONE);
                mToolBarTitle.setText(R.string.title_message);
                showHideQianDao(false,-1,2);
                mMainContainer.stttop(120);
//			mMenuItemRight.setVisible(false);
                break;
            case TAB_PROFILE:
                mToolBarTitle.setVisibility(View.VISIBLE);
                mRadioGroupReservation.setVisibility(View.GONE);
                if (TextUtils.isEmpty(Session.getSession().name)){
                    mToolBarTitle.setText(Session.getSession().mobile);
                }else {
                    mToolBarTitle.setText(Session.getSession().name);
                }
                showHideQianDao(false, -1,3);
                mMainContainer.stttop(0);
//		    mMenuItemRight.setVisible(false);
                break;
            default:
                break;
        }
    }

    public void setmToolBarTitle(String title){
        mToolBarTitle.setText(title);
    }
    public void setLeftTitle(String title){
        mToolBarLeftTitle.setVisibility(View.VISIBLE);
        mToolBarLeftTitle.setText(title);
    }
//    public void setRightTitleWithoutImg(String title){
//        llQianDao.setVisibility(View.VISIBLE);
//        imgQuery.setVisibility(View.GONE);
//        tvQianDao.setText(title);
//    }


    /**
     * 是否显示  签到
     *
     * @param flag
     */
    public void showHideQianDao(boolean flag,int position,int type) {
        currentPage = type;
        if (flag){
            llQianDao.setVisibility(View.VISIBLE);
            if(position==0){//签到
                tvQianDao.setVisibility(View.VISIBLE);
                tvQianDao.setText("签到");
                imgQuery.setImageResource(R.drawable.iconfont_icon);
            }else {
                tvQianDao.setVisibility(View.GONE);
                imgQuery.setImageResource(R.drawable.iconfont_query);
            }
            imgQuery.setVisibility(View.VISIBLE);
        }else if(type == 2){
            llQianDao.setVisibility(View.VISIBLE);
            tvQianDao.setVisibility(View.VISIBLE);
            tvQianDao.setText("群发短信");
            imgQuery.setVisibility(View.GONE);

        }else if(type==1){
            llQianDao.setVisibility(View.VISIBLE);
            imgQuery.setVisibility(View.GONE);
            tvQianDao.setVisibility(View.VISIBLE);
            tvQianDao.setText("学员预约");
        }else if(type==3){
            llQianDao.setVisibility(View.VISIBLE);
            imgQuery.setVisibility(View.VISIBLE);
            tvQianDao.setVisibility(View.GONE);
            imgQuery.setImageResource(R.mipmap.modification);
        } else{
                llQianDao.setVisibility(View.GONE);
            }


    }



//	private MenuItem mMenuItemRight;
//	@Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_grab, menu);
//        mMenuItemRight = menu.findItem(R.id.menu_item_grab_tips);
//        MenuItemCompat.getActionView(mMenuItemRight).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(IndexActivity.this, GrabTipsActivity.class);
//                intent.putExtra(DetailWebActivity.URL, Constants.GRAB_TIPS_URL);
//                startActivity(intent);
//            }
//        });
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//        case R.id.menu_item_grab_tips:
//            return true;
//        default:
//            return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//		case R.id.toolbar_title:
//			break;
            case R.id.toolbar_title_right://签到
            case R.id.toobar_title_right_tv:

//                LogUtil.print("current---page" + mMainContainer.getCurrentTabType());
                if(currentPage == 0){
                    if(ReservationFragment.currentPage == 0){//签到
                        Intent intent = new Intent(this, CaptureActivity.class);
                        startActivity(intent);
                    }else{//搜索
                        Intent intent1 = new Intent(this, QueryAct.class);
                        startActivity(intent1);
                    }
                }else if(currentPage == 2){
//                    Intent intent1 = new Intent(this, StudentAppointmentAct.class);
                    Intent intent1 = new Intent(this, StudentsActivity1.class);
                    intent1.setFlags(1);
                    startActivity(intent1);
                }else if(currentPage == 1){
                    Intent intent = new Intent(this, StudentAppointmentAct.class);
                    startActivity(intent);
                }else if(currentPage == 3){
                    Intent intent = new Intent(this, PersonalInfoActivity.class);
                    startActivity(intent);
            }

                break;
            case R.id.toolbar_left_title:
                //点击今天按钮
                LogUtil.print("点击今天---");
                EventBus.getDefault().post(new MonthApplyEvent());
                break;
            default:
                break;
        }
    }


    class BCConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
            boolean groupSynced = HXSDKHelper.getInstance().isGroupsSyncedWithServer();
            boolean contactSynced = HXSDKHelper.getInstance().isContactsSyncedWithServer();

            // in case group and contact were already synced, we supposed to notify sdk we are ready to receive the events
            if (contactSynced) {
                new Thread() {
                    @Override
                    public void run() {
                        HXSDKHelper.getInstance().notifyForRecevingEvents();
                    }
                }.start();
            } else {

                if (!contactSynced) {
                }

            }

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    NetStateEvent event = new NetStateEvent();
                    event.mIsNetOk = true;
                    EventBus.getDefault().post(event);
                }

            });
        }


        @Override
        public void onDisconnected(final int error) {
            final String st1 = getResources().getString(R.string.can_not_connect_chat_server_connection);
            final String st2 = getResources().getString(R.string.the_current_network);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                    } else if (error == EMError.CONNECTION_CONFLICT) {
                        // 显示帐号在其他设备登陆dialog
                      //  showConflictDialog();
                    } else {
                        NetStateEvent event = new NetStateEvent();
                        event.mIsNetOk = false;
                        if (NetUtils.hasNetwork(IndexActivity.this)) {
                            event.mErrorMsg = st1;
                        } else {
                            event.mErrorMsg = st2;
                        }
                        EventBus.getDefault().post(event);
                    }
                }

            });
        }
    }

    public boolean isConflict() {
        return mIsConflict;
    }

    private AlertDialog.Builder conflictBuilder;
    private boolean isConflictDialogShow;

    private void showConflictDialog() {
        isConflictDialogShow = true;
        BlackCatHXSDKHelper.getInstance().logout(false, null);
        String st = getResources().getString(R.string.Logoff_notification);
        if (!IndexActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (conflictBuilder == null)
                    conflictBuilder = new AlertDialog.Builder(IndexActivity.this);
                conflictBuilder.setTitle(st);
                conflictBuilder.setMessage(R.string.connect_conflict);
                conflictBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        conflictBuilder = null;
                        Session.save(null, true);
                        finish();
                        startActivity(new Intent(IndexActivity.this, LoginActivity.class));
                    }
                });
                conflictBuilder.setCancelable(false);
                conflictBuilder.create().show();
                mIsConflict = true;
            } catch (Exception e) {
            }
        }
    }

    public void onEvent(LogoutEvent event) {
        finish();
        JPushInterface.setAlias(getApplicationContext(), "", mAliasCallback);
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    // 延迟 60 秒来调用 Handler 设置别名
                    // mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS,
                    // alias), 1000 * 60);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }
        }
    };
}
