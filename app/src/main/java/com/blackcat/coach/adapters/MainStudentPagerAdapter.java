package com.blackcat.coach.adapters;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.blackcat.coach.fragments.ItemFragment;
import com.blackcat.coach.lib.PagerSlidingTab;


/**
 * Created by aa on 2016/1/9.
 */
public class MainStudentPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTab.IconTextTabProvider {

    private final String[] titles ;

    private final int[] res;

    private final int[] resDefault;


    public Fragment[] fragments = null;

    public MainStudentPagerAdapter(FragmentManager fm, String[] titles,int[] resDefault,int[] res,Fragment[] fragments) {
        super(fm);
        this.titles = titles;
        this.fragments = fragments;
        this.res = res;
        this.resDefault = resDefault;
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

    @Override
    public int getPageIconResId(int position) {
        return res[position];
    }

    @Override
    public int getPageIconResDefaultId(int position) {
        return resDefault[position];
    }

    @Override
    public String getPageText(int position) {
        return titles[position];
    }
}
