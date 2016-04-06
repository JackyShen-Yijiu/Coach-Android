package com.blackcat.coach.lib.calendar.util;

import com.blackcat.coach.lib.calendar.AppointmentDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class WeekDataUtil {

	private static String[] weeks = { "周日", "周一", "周二", "周三", "周四", "周五", "周六", };

	/**当前日期*/
	public  static final int currentPosition=500;
	public static List<AppointmentDay> getCurrentWeek() {
		List<AppointmentDay> thisWeek = new ArrayList<AppointmentDay>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
		for (int i = weekDay; i >= 1; i--) {
			AppointmentDay day = new AppointmentDay();
			day.year = calendar.get(Calendar.YEAR);
			day.month = calendar.get(Calendar.MONTH) + 1;
			day.day = calendar.get(Calendar.DAY_OF_MONTH);
			thisWeek.add(0, day);
			// LogUtil.print(day.year + "-" + day.month + "-" + day.day);
			calendar.add(Calendar.DATE, -1);
		}
		calendar.setTime(new Date());
		for (int i = weekDay; i < 7; i++) {
			calendar.add(Calendar.DATE, 1);
			AppointmentDay day = new AppointmentDay();
			day.year = calendar.get(Calendar.YEAR);
			day.month = calendar.get(Calendar.MONTH) + 1;
			day.day = calendar.get(Calendar.DAY_OF_MONTH);
			thisWeek.add(day);
		}

		return thisWeek;
	}

	public static List<AppointmentDay> getNextWeek() {
		List<AppointmentDay> nextWeek = new ArrayList<AppointmentDay>();
		Calendar calendar = Calendar.getInstance();

		int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
		calendar.add(Calendar.DATE, 7 - weekDay + 1);
		for (int i = 0; i < 7; i++) {
			AppointmentDay day = new AppointmentDay();
			day.year = calendar.get(Calendar.YEAR);
			day.month = calendar.get(Calendar.MONTH) + 1;
			day.day = calendar.get(Calendar.DAY_OF_MONTH);
			nextWeek.add(day);
			calendar.add(Calendar.DATE, 1);
		}

		return nextWeek;
	}

	public static List<AppointmentDay> getThisWeek2() {
		List<AppointmentDay> thisWeek = new ArrayList<AppointmentDay>();
		Calendar calendar = Calendar.getInstance();
		for (int i = 0; i < 7; i++) {
			AppointmentDay day = new AppointmentDay();
			day.year = calendar.get(Calendar.YEAR);
			day.month = calendar.get(Calendar.MONTH) + 1;
			day.day = calendar.get(Calendar.DAY_OF_MONTH);
			if (i == 0) {
				day.week = "今天";
			} else {
				day.week = weeks[(calendar.get(Calendar.DAY_OF_WEEK) - 1)];
			}
			thisWeek.add(day);
			calendar.add(Calendar.DATE, 1);
		}
		return thisWeek;
	}

	public static List<AppointmentDay> getNextWeek2() {
		List<AppointmentDay> nextWeek = new ArrayList<AppointmentDay>();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 7);
		for (int i = 0; i < 7; i++) {
			AppointmentDay day = new AppointmentDay();
			day.year = calendar.get(Calendar.YEAR);
			day.month = calendar.get(Calendar.MONTH) + 1;
			day.day = calendar.get(Calendar.DAY_OF_MONTH);
			day.week = weeks[(calendar.get(Calendar.DAY_OF_WEEK) - 1)];
			nextWeek.add(day);
			calendar.add(Calendar.DATE, 1);
		}
		return nextWeek;
	}

	public static List<AppointmentDay> getWeekData(int position) {
		List<AppointmentDay> thisWeek = new ArrayList<AppointmentDay>();
		Calendar calendar = Calendar.getInstance();
		int distance = position - WeekDataUtil.currentPosition;
		calendar.add(Calendar.DATE, 7 * distance);
		// calendar.setTime(new Date());
		int index = 0;
		int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
		for (int i = weekDay; i >= 1; i--) {
			AppointmentDay day = new AppointmentDay();
			day.year = calendar.get(Calendar.YEAR);
			day.month = calendar.get(Calendar.MONTH) + 1;
			day.day = calendar.get(Calendar.DAY_OF_MONTH);
			thisWeek.add(0, day);
			// LogUtil.print(day.year + "-" + day.month + "-" + day.day);
			calendar.add(Calendar.DATE, -1);
			index++;
		}
		calendar.add(Calendar.DATE, index);
		for (int i = weekDay; i < 7; i++) {
			calendar.add(Calendar.DATE, 1);
			AppointmentDay day = new AppointmentDay();
			day.year = calendar.get(Calendar.YEAR);
			day.month = calendar.get(Calendar.MONTH) + 1;
			day.day = calendar.get(Calendar.DAY_OF_MONTH);
			thisWeek.add(day);
		}

		return thisWeek;
	}
}
