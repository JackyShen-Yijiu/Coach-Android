package com.blackcat.coach.adapters;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.blackcat.coach.fragments.DaytimelyReservationFragment;
import com.blackcat.coach.fragments.ItemFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Created by aa on 2016/1/9.
 */
public class ScheduleReservationAdapter extends FragmentStatePagerAdapter {

    private List<Date> dates;
    private DaytimelyReservationFragment fragment;
    private FragmentManager fm;

    public ScheduleReservationAdapter(FragmentManager fm, List<Date> dates) {
        super(fm);
        this.dates = dates;
        this.fm = fm;
    }


    @Override
    public Fragment getItem(int position) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


        fragment = new DaytimelyReservationFragment();
        Bundle args = new Bundle();
        args.putString("date", format.format(dates.get(position)));
        fragment.setArguments(args);
//        }else{
//            fragment.getArguments().putString("date", format.format(dates.get(position)));
//        }

//        if(fragment.isAdded()){
//
//        }


        return fragment;
    }


    @Override
    public int getCount() {
        return dates.size();
    }

}
