package com.blackcat.coach.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.blackcat.coach.fragments.StudentFragment1;

/**
 * Created by pengdonghua on 2016/2/3.
 */
public class StudentsAdapter  extends FragmentPagerAdapter {

    private final String[] titles ;

//    public static ItemFragment[] fragments = {new ItemFragment(),new ItemFragment(),new ItemFragment()};

    private StudentFragment1[] students = null;

    public StudentsAdapter(FragmentManager fm,String[] titles ,StudentFragment1[] students) {
        super(fm);
        this.titles = titles;
        this.students = students;
    }


    @Override
    public Fragment getItem(int position) {
        if(students[position].isAdded()){
            return students[position];
        }
//        Bundle args = new Bundle();
//        args.putInt("type", position);
////        args.putString("type", titles[position]);
//        students[position].setArguments(args);
        return students[position];
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
