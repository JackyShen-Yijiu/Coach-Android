package com.blackcat.coach.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import com.blackcat.coach.events.SetTimeEvent;

import java.util.Calendar;

import de.greenrobot.event.EventBus;

/**
 * Created by zou on 15/10/28.
 */

// http://developer.android.com/intl/zh-cn/guide/topics/ui/controls/pickers.html
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private int viewId;

    public static TimePickerFragment newInstance(int id) {
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.viewId = id;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute, true);
//                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        SetTimeEvent event = new SetTimeEvent();
        event.viewId = viewId;
        event.hourOfDay = hourOfDay;
        event.minutes = minute;
        EventBus.getDefault().post(event);
    }
}