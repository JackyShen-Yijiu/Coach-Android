package com.blackcat.coach.widgets;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
<<<<<<< HEAD
import android.view.ViewGroup;
=======
>>>>>>> ebe9a74208b0108ecb7b719d55f4a08901b4a5c0
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.blackcat.coach.R;
import com.blackcat.coach.activities.IndexActivity;
import com.blackcat.coach.fragments.ChildScheduleFragment;
import com.blackcat.coach.fragments.MessageFragment;
import com.blackcat.coach.fragments.ProfileFragment;
import com.blackcat.coach.fragments.ReservationFragment;
import com.blackcat.coach.fragments.ScheduleTabFragment;
import com.blackcat.coach.fragments.StudentTabFragment;
import com.blackcat.coach.i.IIndicateMainTabNotification;
import com.blackcat.coach.i.INewIntent;
import com.blackcat.coach.i.IOnKeyDown;
import com.blackcat.coach.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 底部Tab
 */
public class MainScreenContainer extends RelativeLayout implements OnClickListener {

    private static final String TAG = "MainScreenContainer";
    public ScheduleTabFragment scheduleTabFragment;

    //每个tab含的信息
    class TabInfo {
        //tab的view
        MainScreenTab tabView;
        //tab对应的fragment
        Fragment fragment;
        //tab的索引，MainScreen.TAB_~
        int type;
    }

    //内容区域ID
    private int mContentId;

    //所有tab
    private List<TabInfo> mTabs;
    //当前tab
    private TabInfo mCurrentTab;
    
    //上次tab
    private TabInfo mLastTab;
    
    //tabs的父控件
    private View mTabContainer;

    private FragmentManager mFragmentManager;
    private Set<TabInfo> mAddedTabs;//解决Fragment already added异常

    //正常情况下不会被调用
    public MainScreenContainer(Context context) {
        super(context);
        initView();
    }

    public MainScreenContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.layout_main_screen, this);
        mContentId = R.id.fl_content;

        scheduleTabFragment = new ScheduleTabFragment();

        mTabContainer = findViewById(R.id.ll_tab_container);
        mTabs = new ArrayList<TabInfo>();
//        ChildScheduleFragment() 日程
        mTabs.add(getTabInfo(R.id.tab_schedule,scheduleTabFragment , IndexActivity.TAB_SCHEDULE,
                R.string.tab_indicator_title_schedule, R.drawable.sl_tab_icon_schedule));
//        new ReservationFragment()  预约
        mTabs.add(getTabInfo(R.id.tab_grab_order, new StudentTabFragment(), IndexActivity.TAB_STUDENT,
                R.string.tab_indicator_title_student, R.drawable.sl_tab_icon_reservation));

        mTabs.add(getTabInfo(R.id.tab_my_orders, new MessageFragment(), IndexActivity.TAB_MESSAGE,
                R.string.tab_indicator_title_message, R.drawable.sl_tab_icon_message));
        mTabs.add(getTabInfo(R.id.tab_profile, new ProfileFragment(), IndexActivity.TAB_PROFILE,
                R.string.tab_indicator_title_profile, R.drawable.sl_tab_icon_profile));
    }

    /**
     * IndexActivity
     * @param top
     */
    public void setMarginTop(int top){
        FrameLayout contaner = (FrameLayout)  findViewById(mContentId);
        LayoutParams p =  new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        p.setMargins(0,top,0,0);
//        p.
        contaner.setLayoutParams(p);
//        contaner.be
    }


    private TabInfo getTabInfo(int viewID, Fragment fragment, int type, int txtID, int imgID) {
        TabInfo tabInfo = new TabInfo();
        tabInfo.tabView = (MainScreenTab) findViewById(viewID);
        tabInfo.tabView.mTextView.setText(txtID);
        tabInfo.tabView.mTextView.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(imgID),
                null, null);
        tabInfo.tabView.setTag(type);
        tabInfo.tabView.setOnClickListener(this);
        tabInfo.type = type;
        tabInfo.fragment = fragment;
        return tabInfo;
    }

    @Override
    public void onClick(View v) {
        LogUtil.print("jumpTabaaaaaaaaaaa");
        int type = (Integer) v.getTag();
        showTab(type);
    }

    private void hideOneTab(TabInfo tab) {
        if (mAddedTabs.contains(tab)) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.hide(tab.fragment);
            ft.commitAllowingStateLoss();
        }
    }


    public void stttop(int top){
        FrameLayout fragment=(FrameLayout)findViewById(mContentId);
        LayoutParams l=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        l.setMargins(0,top,0,0);
        fragment.setLayoutParams(l);

    }

    private void showOneTab(TabInfo tab) {
        if (mCurrentTab != tab) {
            mCurrentTab = tab;
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            if (mAddedTabs.contains(mCurrentTab)) {
                ft.show(tab.fragment);
            } else {
                ft.add(mContentId, tab.fragment, tab.type + "");
                    mAddedTabs.add(mCurrentTab);
            }
            ft.commitAllowingStateLoss();
        }
        refreshTab(tab);
    }

    public void showTab(int type) {
        for (TabInfo tab : mTabs) {
            if (tab.type == type) {
                tab.tabView.setSelected(true);
                showOneTab(tab);
            } else {
                tab.tabView.setSelected(false);
                hideOneTab(tab);
            }
        }
        if(mOnTabLisener != null) {
            boolean reClicked = false;
            if (mLastTab == mCurrentTab) {
                reClicked = true;
            }
        	mOnTabLisener.onTabSelected(type, reClicked);
        }
        
        mLastTab = mCurrentTab;
    }

    public void jumpTab(int type, Intent intent) {

        if (type <= IndexActivity.TAB_SCHEDULE && type >= IndexActivity.TAB_STUDENT) {
            for (int i = 0; i < mTabs.size(); i++) {
                TabInfo tb = mTabs.get(i);
                if (tb.type == type) {
                    if (tb.fragment instanceof INewIntent) {
                        ((INewIntent) tb.fragment).onNewIntent(intent);
                    }
                    break;
                }
            }
        } else {
            type = IndexActivity.TAB_SCHEDULE;
        }
        showTab(type);
    }

    public void refreshTab() {
//        new SafeAsyncTask<Void, Void, ArrayList<View>>() {
//            @Override
//            protected ArrayList<View> doInBackground(Void... params) {
//                ArrayList<View> resultList = null;
//                for (TabInfo tab : mTabs) {
//                    Fragment f = tab.fragment;
//                    if (f != null && f instanceof IIndicateMainTabNotification) {
//                        IIndicateMainTabNotification in = (IIndicateMainTabNotification) f;
//                        final boolean show = in.isNeedIndicateMainScreenTabNotification(getContext());
//                        final View redPointView = tab.tabView.mRedPointView;
//                        if (show != (redPointView.getVisibility() == View.VISIBLE)) {
//                            if (resultList == null) {
//                                resultList = new ArrayList<View>();
//                            }
//
//                            redPointView.setTag(show);
//                            resultList.add(redPointView);
//                        }
//                    }
//                }
//                return resultList;
//            }
//
//            @Override
//            protected void onPostExecute(ArrayList<View> resultList) {
//                if (resultList != null) {
//                    for (View redPointView : resultList) {
//                        redPointView.setVisibility((Boolean) redPointView.getTag() ? View.VISIBLE : View.GONE);
//                    }
//                }
//            };
//        }.execute();
    }

    public void refreshTab(TabInfo tab) {
        Fragment f = tab.fragment;
        if (f != null && f instanceof IIndicateMainTabNotification) {
            IIndicateMainTabNotification in = (IIndicateMainTabNotification) f;
            boolean hasNew = in.isNeedIndicateMainScreenTabNotification(getContext());
            boolean isRemove = in.removeRedPointhOnSelected(getContext());
            View redPoint = tab.tabView.mRedPointView;
            if (hasNew) {
                if (isRemove) {
                    redPoint.setVisibility(View.GONE);
                } else {
                    redPoint.setVisibility(View.VISIBLE);
                }
            } else {
                redPoint.setVisibility(View.GONE);
            }
        }
    }

    private void removeAllFragment() {
        mAddedTabs = new HashSet<TabInfo>();
        int[] tags = new int[] { IndexActivity.TAB_STUDENT, IndexActivity.TAB_MESSAGE, IndexActivity.TAB_PROFILE ,IndexActivity.TAB_SCHEDULE};

        FragmentTransaction ft = null;
        for (int tag : tags) {
            Fragment fragment = mFragmentManager.findFragmentByTag(tag + "");
            if (fragment != null) {
                if (ft == null) {
                    ft = mFragmentManager.beginTransaction();
                }
                ft.remove(fragment);
            }
        }

        if (ft != null) {
            ft.commitAllowingStateLoss();
        }
    }

    public void hideTabContainer() {
        mTabContainer.setVisibility(View.GONE);
    }

    public void showTabContainer() {
        mTabContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mCurrentTab != null) {
            Fragment currentFragment = mCurrentTab.fragment;
            if (currentFragment != null && currentFragment instanceof IOnKeyDown && currentFragment.isResumed()) {
                if (((IOnKeyDown) currentFragment).onKeyDown(keyCode, event)) {
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public int getCurrentTabType() {
        if (mCurrentTab != null) {
            return mCurrentTab.type;
        }
        return IndexActivity.TAB_STUDENT;
    }
    
    public Fragment getCurrentFragment() {
    	if (mCurrentTab != null) {
    		return mCurrentTab.fragment;
    	}
    	return null;
    }

    public void setup(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
        removeAllFragment();
    }
    
    private OnTabLisener mOnTabLisener;
    public interface OnTabLisener {
    	public void onTabSelected(int index, boolean reClicked);
    }
    
    public void setOnTabListener(OnTabLisener listener) {
    	this.mOnTabLisener = listener;
    }
}
