package com.blackcat.coach.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blackcat.coach.R;
import com.blackcat.coach.activities.IndexActivity;

/**
 * 日程 （tab）
 */
public class ScheduleTabFragment extends BaseFragment {

    private final String  SCHEDULE= "schedule";
    private final String  RESERVATION= "reservation";
    private ChildScheduleFragment scheduleFragment;
    private ReservationFragment reservationFragment;
    public int type;

    public static ScheduleTabFragment newInstance(String param1, String param2) {
        ScheduleTabFragment fragment = new ScheduleTabFragment();
        return fragment;
    }

    public ScheduleTabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_tab_schedule, container, false);
        scheduleFragment = new ChildScheduleFragment();
        reservationFragment = new ReservationFragment();
        showSchedule();
        return view;
    }


    private void showSchedule(){
        show(scheduleFragment, SCHEDULE);
        hide(reservationFragment, RESERVATION);
        type = 1;
        ((IndexActivity)getActivity()).showHideQianDao(true,type,0);
//        Toast.makeText(getActivity(),"showSchedule",Toast.LENGTH_SHORT).show();

    }

    private void showReservation(){
        show(reservationFragment,RESERVATION);
        hide(scheduleFragment, SCHEDULE);
        type =0;
        ((IndexActivity)getActivity()).showHideQianDao(true,-1,4);
//        Toast.makeText(getActivity(),"showReservation",Toast.LENGTH_SHORT).show();

    }

    public void switchFragment(){
        if(type==0){
            showSchedule();
        }else if(type == 1){
            showReservation();
        }
    }
    private void show(Fragment fragment,String tag){
        if(fragment==null){
            if(tag.equals(SCHEDULE)){
                fragment = new ChildScheduleFragment();
            }else{
                fragment = new ReservationFragment();
            }

        }
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if(fragment.isAdded()){
            ft.show(fragment);
        }else{
            ft.add(R.id.fragment_tab_schedule,fragment,tag);
        }
        ft.commitAllowingStateLoss();
    }

    private void hide(Fragment frag,String tag){
        if(frag==null){
            if(tag.equals(SCHEDULE)){
                frag = new ChildScheduleFragment();

            }else{
                frag = new ReservationFragment();
            }
        }
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if(frag.isAdded()){
            ft.hide(frag);
        }
        ft.commitAllowingStateLoss();
    }
}
