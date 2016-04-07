package com.blackcat.coach.lib.calendar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.fragments.ChildScheduleFragment;
import com.blackcat.coach.utils.LogUtil;

import java.util.Date;

public class AppointmentWeekFragment extends Fragment{

	public LinearLayout rootview;
	private WeekView weekView;

	public static AppointmentWeekFragment newInstance(int pagePosition){
//		this.pagePosition = pagePosition;
		return new AppointmentWeekFragment();
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		int pagePosition =getArguments ().getInt("position");
		LogUtil.print("pagePosition="+pagePosition);
		rootview = new LinearLayout(CarCoachApplication.getInstance());
		rootview.setBackgroundColor(Color.parseColor("#ffffff"));
		LayoutParams params = new LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		rootview.setLayoutParams(params);
		weekView = new WeekView(CarCoachApplication.getInstance());
		weekView.initMonthAdapter(pagePosition);
		rootview.addView(weekView, params);

		return rootview;
	}

	public void setSelectedDate(Date seleDate,int selectPosition){

		weekView.setSelectedDate(seleDate,selectPosition);
	}
}
