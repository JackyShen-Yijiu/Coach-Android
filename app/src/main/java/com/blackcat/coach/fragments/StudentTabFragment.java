package com.blackcat.coach.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blackcat.coach.R;
import com.blackcat.coach.activities.IndexActivity;
import com.blackcat.coach.adapters.ReservationAdapter;
import com.blackcat.coach.lib.PagerSlidingTab;

/**
 * 学员
 */
public class StudentTabFragment extends BaseFragment {

    private View rootView;
    private FragmentActivity mContext;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReservationFragment.
     */
    public static StudentTabFragment newInstance(String param1, String param2) {
        StudentTabFragment fragment = new StudentTabFragment();
        return fragment;
    }

    public StudentTabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_reservation, container, false);
        mContext = getActivity();
        initView();
        initData();
        return rootView;
    }

    private void initView() {
    }



    protected void initData() {
    }

}
