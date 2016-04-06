package com.blackcat.coach.lib.calendar;


public abstract class OnDateClickListener {
	public static OnDateClickListener instance;

	public abstract void onDateClick(AppointmentDay day, boolean clickbale);
}
