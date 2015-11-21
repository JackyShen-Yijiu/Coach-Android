package com.blackcat.coach.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.blackcat.coach.events.SetDateEvent;

import java.util.Calendar;

import de.greenrobot.event.EventBus;

/**
 * Created by zou on 15/10/28.
 */

// http://developer.android.com/intl/zh-cn/guide/topics/ui/controls/pickers.html

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private int viewId;
    public static DatePickerFragment newInstance(int id) {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.viewId = id;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        SetDateEvent event = new SetDateEvent();
        event.viewId = viewId;
        event.year = year;
        event.month = month;
        event.day = day;
        EventBus.getDefault().post(event);
    }
}
