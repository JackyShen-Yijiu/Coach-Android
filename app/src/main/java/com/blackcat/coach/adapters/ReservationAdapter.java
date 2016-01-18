package com.blackcat.coach.adapters;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.blackcat.coach.fragments.ItemFragment;
import com.blackcat.coach.utils.LogUtil;


/**
 * Created by aa on 2016/1/9.
 */
public class ReservationAdapter extends FragmentPagerAdapter {

    private final String[] titles ;

    public static Fragment[] fragments = {new ItemFragment(),new ItemFragment(),new ItemFragment(),new ItemFragment()};

    public ReservationAdapter(FragmentManager fm,String[] titles) {
        super(fm);
        this.titles = titles;
    }




    @Override
    public Fragment getItem(int position) {
        if(fragments[position].isAdded()){
            return fragments[position];
        }
        Bundle args = new Bundle();
        args.putInt("type", position);
//        args.putString("type", titles[position]);
        fragments[position].setArguments(args);
        return fragments[position];
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
