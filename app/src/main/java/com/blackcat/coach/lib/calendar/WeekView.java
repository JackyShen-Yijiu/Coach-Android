package com.blackcat.coach.lib.calendar;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

import com.blackcat.coach.lib.calendar.adapter.WeekAdapter;
import com.blackcat.coach.lib.calendar.util.WeekDataUtil;
import com.blackcat.coach.utils.LogUtil;

public class WeekView extends GridView {

	private List<AppointmentDay> list;
	private WeekAdapter adapter;

	public WeekView(Context context) {
		super(context);
		this.setNumColumns(7);
		this.setCacheColorHint(0);
	}

	public WeekView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setNumColumns(7);
		this.setCacheColorHint(0);
	}

	public void initMonthAdapter(int pagePosition) {
//		if (pagePosition == 0) {
//			this.list = WeekDataUtil.getThisWeek2();
//		} else if (pagePosition == 1) {
//			this.list = WeekDataUtil.getNextWeek2();
//		}
		this.list = WeekDataUtil.getWeekData(pagePosition);

		adapter = new WeekAdapter(getContext(), 1, this.list);
		this.setAdapter(adapter);
	}

	@Override
	public void setSelection(int position) {
		//选中某一天
		super.setSelection(position);
	}

	public void setSelectedDate(Date seleDate,int selectPosition){
		LogUtil.print("---"+selectPosition);
		adapter.setSelectedDate(seleDate, WeekDataUtil.getWeekData(selectPosition));
		adapter.notifyDataSetChanged();

	}
}
