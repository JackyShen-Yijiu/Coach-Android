package com.blackcat.coach.lib.calendar.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blackcat.coach.R;
import com.blackcat.coach.lib.calendar.util.CalendarUtil;
import com.blackcat.coach.utils.LogUtil;


/**
 * 
 * TODO<GridViewAdapter>
 * 
 * @author ZhuZiQiang
 * @data: 2014-3-29 上午7:58:22
 * @version: V1.0
 */
public class CalendarGridViewAdapter extends BaseAdapter {

	private Calendar calStartDate = Calendar.getInstance();// 当前显示的日历
	private Calendar calSelected = Calendar.getInstance(); // 选择的日历

	public void setSelectedDate(Calendar cal) {
		calSelected = cal;
	}

	private Calendar calToday = Calendar.getInstance(); // 今日
	private int iMonthViewCurrentMonth = 0; // 当前视图月

	// 根据改变的日期更新日历
	// 填充日历控件用
	private void UpdateStartDateForMonth() {
		calStartDate.set(Calendar.DATE, 1); // 设置成当月第一天
		iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);// 得到当前日历显示的月

		// 星期一是2 星期天是1 填充剩余天数
		int iDay = 0;
		int iFirstDayOfWeek = Calendar.MONDAY;
		int iStartDay = iFirstDayOfWeek;
		if (iStartDay == Calendar.MONDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
			if (iDay < 0)
				iDay = 6;
		}
		if (iStartDay == Calendar.SUNDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
			if (iDay < 0)
				iDay = 6;
		}
		calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);

		calStartDate.add(Calendar.DAY_OF_MONTH, -1);// 周日第一位

	}

	ArrayList<Date> titles;

	private ArrayList<Date> getDates() {

		UpdateStartDateForMonth();

		ArrayList<Date> alArrayList = new ArrayList<Date>();

		for (int i = 1; i <= 42; i++) {
			alArrayList.add(calStartDate.getTime());
			calStartDate.add(Calendar.DAY_OF_MONTH, 1);
		}

		return alArrayList;
	}

	private Activity activity;
	Resources resources;
	/**
	 * 请假的日期
	 */
	private int[] leaveoff;
	/**
	 * 有订单的日期
	 */
	private int[] reservationapply;
	// construct

	public CalendarGridViewAdapter(Activity a, Calendar cal, int[] leaveoff,int[] reservationapply) {
		calStartDate = cal;
		activity = a;
		resources = activity.getResources();
		titles = getDates();

		this.leaveoff = leaveoff;
		this.reservationapply=reservationapply;
	}

	public void setData(Calendar cal, int[] leaveoff,int[] reservationapply){
		calStartDate = cal;
		titles = getDates();

		this.leaveoff = leaveoff;
		this.reservationapply=reservationapply;
	}


	@Override
	public int getCount() {
		return titles.size();
	}

	@Override
	public Object getItem(int position) {
		return titles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(activity, R.layout.calendar_day_list, null);
			holder.restView = (TextView) convertView.findViewById(R.id.rest);
			holder.solarDay = (TextView) convertView.findViewById(R.id.solar);
			holder.lunarDay = (TextView) convertView.findViewById(R.id.lunar);
			holder.hasOrderView = (ImageView) convertView
					.findViewById(R.id.has_order);
			holder.day_rl = (RelativeLayout) convertView
					.findViewById(R.id.day_rl);
			holder.calender_ll = (LinearLayout) convertView
					.findViewById(R.id.calender_ll);
			holder.select = (ImageView) convertView.findViewById(R.id.day_select);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// LinearLayout iv = new LinearLayout(activity);
		// iv.setId(position + 5000);

		// RelativeLayout holder.day_rl = new RelativeLayout(activity);
		// RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
		// ViewGroup.LayoutParams.WRAP_CONTENT,
		// ViewGroup.LayoutParams.WRAP_CONTENT);
		// lp1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		// lp1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		// lp1.rightMargin = 10;
		// // lp1.topMargin = 10;
		// TextView restDay = new TextView(activity);
		// restDay.setText("休");
		// restDay.setTextSize(9);
		// restDay.setTextColor(resources.getColor(R.color.title_text_6));
		//
		// iv.setGravity(Gravity.CENTER);
		// iv.setOrientation(1);
//		holder.day_rl.setBackgroundColor(resources.getColor(R.color.white));
		holder.select.setVisibility(View.GONE);
		holder.calender_ll.setId(position + 5000);
		Date myDate = (Date) getItem(position);
		Calendar calCalendar = Calendar.getInstance();
		calCalendar.setTime(myDate);

		final int iMonth = calCalendar.get(Calendar.MONTH);
		final int iDay = calCalendar.get(Calendar.DAY_OF_WEEK);

		// 判断周六周日
		// //
		// holder.day_rl.setBackgroundColor(resources.getColor(R.color.white));
		// // if (iDay == 7) {
		// // // 周六
		// // //
		// //
		// holder.day_rl.setBackgroundColor(resources.getColor(R.color.text_6));
		// // } else if (iDay == 1) {
		// // // 周日
		// // //
		// //
		// holder.day_rl.setBackgroundColor(resources.getColor(R.color.text_7));
		// // }
		// // 判断周六周日结束
		//
		// TextView holder.lunarDay = new TextView(activity);// 日本老黄历
		// holder.lunarDay.setGravity(Gravity.CENTER_HORIZONTAL);
		// holder.lunarDay.setTextSize(9);
		CalendarUtil calendarUtil = new CalendarUtil(calCalendar);
		if (equalsDate(calToday.getTime(), myDate)) {
			// 当前日期
//			holder.day_rl.setBackgroundResource(R.drawable.calendar_current_date_bg);
			holder.select.setVisibility(View.VISIBLE);
			holder.lunarDay.setText(calendarUtil.toString());
		} else {
			holder.lunarDay.setText(calendarUtil.toString());
		}

		// 这里用于比对是不是比当前日期小，如果比当前日期小就高亮
		// if (!CalendarUtil.compare(myDate, calToday.getTime())) {
		// //
		// holder.day_rl.setBackgroundColor(resources.getColor(R.color.frame));
		// } else {
		// 设置背景颜色
		if (equalsDate(calSelected.getTime(), myDate)) {
			// 选择的
//			holder.day_rl.setBackgroundResource(R.drawable.calendar_current_date_bg);;
			holder.select.setVisibility(View.VISIBLE);
		} else {
			if (equalsDate(calToday.getTime(), myDate)) {
				// 当前日期
//				holder.day_rl.setBackgroundColor(resources
//						.getColor(R.color.calendar_zhe_day));
//				holder.day_rl.setBackgroundResource(R.drawable.calendar_current_date_bg);
				holder.select.setVisibility(View.VISIBLE);
			}
			// }
		}
		// 设置背景颜色结束

		// // 日期开始
		// TextView holder.solarDay = new TextView(activity);// 日期
		// holder.solarDay.setGravity(Gravity.CENTER_HORIZONTAL);

		int day = myDate.getDate(); // 日期
		holder.solarDay.setText(String.valueOf(day));
		holder.solarDay.setId(position + 500);
		holder.calender_ll.setTag(myDate);
		holder.restView.setVisibility(View.INVISIBLE);
		holder.hasOrderView.setVisibility(View.INVISIBLE);
		// 判断是否是当前月
		if (iMonth == iMonthViewCurrentMonth) {
			holder.lunarDay.setTextColor(resources.getColor(R.color.ToDayText));
			holder.solarDay.setTextColor(resources.getColor(R.color.ToDayText));

			if(leaveoff != null){

			for (int i = 0; i < leaveoff.length; i++) {
				if(leaveoff[i] == day){
					holder.restView.setVisibility(View.VISIBLE);
				}
			}
			}

			if(reservationapply != null){

			for (int i = 0; i < reservationapply.length; i++) {
				if(reservationapply[i] == day){
					holder.hasOrderView.setVisibility(View.VISIBLE);
				}
			}
			}
		} else {
			holder.solarDay.setTextColor(resources.getColor(R.color.noMonth));
			holder.lunarDay.setTextColor(resources.getColor(R.color.noMonth));
		}




		// LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
		// LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		// iv.addView(holder.solarDay, lp);
		//
		// LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
		// LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		// iv.addView(holder.lunarDay, lp2);

		// lp2.gravity = Gravity.CENTER_HORIZONTAL;
		// lp2.bottomMargin = 5;
		// ImageView point = new ImageView(activity);
		// point.setBackgroundResource(R.drawable.point);
		// iv.addView(point, lp2);
		// RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(
		// ViewGroup.LayoutParams.WRAP_CONTENT,
		// ViewGroup.LayoutParams.WRAP_CONTENT);
		// lp3.addRule(RelativeLayout.CENTER_IN_PARENT);
		// holder.day_rl.addView(iv, lp3);
		// holder.day_rl.addView(restDay, lp1);
		return convertView;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	private Boolean equalsDate(Date date1, Date date2) {

		if (date1.getYear() == date2.getYear()
				&& date1.getMonth() == date2.getMonth()
				&& date1.getDate() == date2.getDate()) {
			return true;
		} else {
			return false;
		}

	}

	static class ViewHolder {
		TextView lunarDay; // 农历
		TextView solarDay;// 阳历
		ImageView hasOrderView;
		TextView restView;
		RelativeLayout day_rl;
		LinearLayout calender_ll;
		ImageView select;
	}
}
