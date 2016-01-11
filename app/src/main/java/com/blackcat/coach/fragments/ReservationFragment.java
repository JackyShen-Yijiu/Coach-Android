package com.blackcat.coach.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blackcat.coach.R;
import com.blackcat.coach.adapters.ReservationAdapter;
import com.blackcat.coach.lib.PagerSlidingTab;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReservationFragment extends BaseFragment {

    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;


//    //每个tab含的信息
//    class ChildTabInfo {
//        //tab的view
//        View tabView;
//        //tab对应的fragment
//        Fragment fragment;
//        //tab的索引，MainScreen.TAB_~
//        int type;
//    }
//
//    //所有tab
//    private List<ChildTabInfo> mTabs;
//    //当前tab
//    private ChildTabInfo mCurrentTab;
//
//    //上次tab
//    private ChildTabInfo mLastTab;
//
//    private int mContentId;
//
//    //tabs的父控件
//    private RadioGroup mRadioGroupReservation;
//
//    private FragmentManager mFragmentManager;
//    private Set<ChildTabInfo> mAddedTabs;//解决Fragment already added异常
//
//    public static final int TAB_CHILD_RESERVATION = 1;
//    public static final int TAB_CHILD_SCHEDULE = 2;

    View rootView;
    private PagerSlidingTab slidingTab;
    private ViewPager viewPager;
    FragmentActivity mContext;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReservationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReservationFragment newInstance(String param1, String param2) {
        ReservationFragment fragment = new ReservationFragment();
        return fragment;
    }

    public ReservationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_reservation, container, false);
        mContext = getActivity();
        //        mContentId = R.id.fragment_container;

//        setup(getChildFragmentManager());
        initView();
        initData();
//        mRadioGroupReservation = (RadioGroup) mActivity.findViewById(R.id.rg_reservation);
//        mRadioGroupReservation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                showTab(checkedId);
//            }
//        });

//        rootView.post(new Runnable() {
//            @Override
//            public void run() {
//                ReservationFragment.this.jumpTab(R.id.rbtn_reservation);
//            }
//        });
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                lazyInitTab(R.id.rbtn_schedule);
//            }
//        }, 500);
        return rootView;
    }

    private void initView() {
//        mTabs = new ArrayList<ChildTabInfo>();
//        mTabs.add(getTabInfo(R.id.rbtn_reservation, new ChildReservationFragment(), ReservationFragment.TAB_CHILD_RESERVATION));
//        mTabs.add(getTabInfo(R.id.rbtn_schedule, new ChildScheduleFragment(), ReservationFragment.TAB_CHILD_SCHEDULE));

        slidingTab = (PagerSlidingTab) rootView
                .findViewById(R.id.fragment_reservation_sliding_tab);
        viewPager = (ViewPager) rootView
                .findViewById(R.id.fragment_reservation_view_pager);
    }


    protected void initData() {

        // 初始化数据
        String[] titles = new String[] { "新订单", "待评价", "已取消", "已完成" };
        FragmentPagerAdapter adapter = new ReservationAdapter(mContext.getSupportFragmentManager(),titles);

        viewPager.setAdapter(adapter);
        slidingTab.setViewPager(viewPager);

    }

    @Override
    public void onDetach() {
        super.onDetach();
//        removeAllFragment();
//        mAddedTabs = null;
//        mTabs = null;
    }


//    private ChildTabInfo getTabInfo(int viewID, Fragment fragment, int type) {
//        ChildTabInfo tabInfo = new ChildTabInfo();
//        tabInfo.tabView = mActivity.findViewById(viewID);
//        tabInfo.tabView.setTag(type);
//        tabInfo.type = type;
//        tabInfo.fragment = fragment;
//        return tabInfo;
//    }

//    private void hideOneTab(ChildTabInfo tab) {
//        if (mAddedTabs.contains(tab)) {
//            FragmentTransaction ft = mFragmentManager.beginTransaction();
//            ft.hide(tab.fragment);
//            ft.commitAllowingStateLoss();
//        }
//    }
//
//
//    public void lazyInitTab(int tabId) {
//        if (mTabs == null) {
//            return;
//        }
//        ChildTabInfo tmpTab = null;
//        for (ChildTabInfo tab : mTabs) {
//            if (tab.tabView.getId() == tabId) {
//                tmpTab = tab;
//            }
//        }
//        if (tmpTab != null && !mAddedTabs.contains(tmpTab)) {
//            FragmentTransaction ft = mFragmentManager.beginTransaction();
//            ft.add(mContentId, tmpTab.fragment, tmpTab.type + "");
//            mAddedTabs.add(tmpTab);
//            ft.hide(tmpTab.fragment);
//            ft.commitAllowingStateLoss();
//        }
//    }
//
//    private void showOneTab(ChildTabInfo tab) {
//        if (mCurrentTab != tab) {
//            mCurrentTab = tab;
//            FragmentTransaction ft = mFragmentManager.beginTransaction();
//            if (mAddedTabs.contains(mCurrentTab)) {
//                ft.show(tab.fragment);
//            } else {
//                ft.add(mContentId, tab.fragment, tab.type + "");
////                ft.add(tab.fragment, tab.type + "");
//                mAddedTabs.add(mCurrentTab);
//            }
//            ft.commitAllowingStateLoss();
//        }
//    }
//
//    public void showTab(int viewId) {
//        if (mTabs == null) {
//            return;
//        }
//        for (ChildTabInfo tab : mTabs) {
//            if (tab.tabView.getId() == viewId) {
//                showOneTab(tab);
//            } else {
//                hideOneTab(tab);
//            }
//        }
//
//        mLastTab = mCurrentTab;
//    }
//
//    public void jumpTab(int viewId) {
//        showTab(viewId);
//    }
//
//    private void removeAllFragment() {
//        mAddedTabs = new HashSet<ChildTabInfo>();
//        int[] tags = new int[] { ReservationFragment.TAB_CHILD_RESERVATION, ReservationFragment.TAB_CHILD_SCHEDULE};
//
//        FragmentTransaction ft = null;
//        for (int tag : tags) {
//            Fragment fragment = mFragmentManager.findFragmentByTag(tag + "");
//            if (fragment != null) {
//                if (ft == null) {
//                    ft = mFragmentManager.beginTransaction();
//                }
//                ft.remove(fragment);
//            }
//        }
//
//        if (ft != null) {
//            ft.commitAllowingStateLoss();
//        }
//    }
//
//    public int getCurrentTabType() {
//        if (mCurrentTab != null) {
//            return mCurrentTab.type;
//        }
//        return ReservationFragment.TAB_CHILD_RESERVATION;
//    }
//
//    public Fragment getCurrentFragment() {
//        if (mCurrentTab != null) {
//            return mCurrentTab.fragment;
//        }
//        return null;
//    }
//
//    public void setup(FragmentManager fragmentManager) {
//        mFragmentManager = fragmentManager;
//        removeAllFragment();
//    }
}
