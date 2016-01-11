package com.blackcat.coach.adapters;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.blackcat.coach.fragments.ItemFragment;


/**
 * Created by aa on 2016/1/9.
 */
public class ReservationAdapter extends FragmentPagerAdapter {

    private final String[] titles;

    public ReservationAdapter(FragmentManager fm,String[] titles) {
        super(fm);
        this.titles = titles;
    }




    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putString("arg", titles[position]);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position % titles.length];
    }
}
