package com.blackcat.coach.adapters;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.blackcat.coach.fragments.ItemFragment;


/**
 * Created by aa on 2016/1/9.
 */
public class MainStudentPagerAdapter extends FragmentPagerAdapter {

    private final String[] titles ;

    public Fragment[] fragments = null;

    public MainStudentPagerAdapter(FragmentManager fm, String[] titles,Fragment[] fragments) {
        super(fm);
        this.titles = titles;
        this.fragments = fragments;
    }

//    public MainStudentPagerAdapter(FragmentManager fm, String[] titles,Fragment[] fragments) {
//        super(fm);
//        this.titles = titles;
//        this.fragments = fragments;
//    }


    @Override
    public Fragment getItem(int position) {
        if(fragments[position].isAdded()){
            return fragments[position];
        }
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
