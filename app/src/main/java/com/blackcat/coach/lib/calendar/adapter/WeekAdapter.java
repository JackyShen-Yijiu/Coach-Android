package com.blackcat.coach.lib.calendar.adapter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blackcat.coach.R;
import com.blackcat.coach.fragments.ChildScheduleFragment;
import com.blackcat.coach.lib.calendar.AppointmentDay;
import com.blackcat.coach.lib.calendar.OnDateClickListener;
import com.blackcat.coach.utils.CommonUtil;
import com.blackcat.coach.utils.LogUtil;


public class WeekAdapter extends ArrayAdapter<Integer> {

    private List<AppointmentDay> data;
    private Context mContext;

    private boolean isFirst = true;

    public WeekAdapter(Context context, int resource, List<AppointmentDay> data) {
        super(context, resource);
        mContext = context;
        LogUtil.print("lastClickView==data===" + data.size());
        this.data = data;

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.calendar_date, null);
            holder.solar = (TextView) convertView.findViewById(R.id.solar);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.solar.setBackgroundDrawable(null);

        holder.solar.setTextColor(CommonUtil.getColor(mContext,
                R.color.new_txt_blacks));
        final AppointmentDay appointmentDay = data.get(position);
        Calendar calendar = Calendar.getInstance();
        if (appointmentDay.year == calendar.get(Calendar.YEAR)&&
                appointmentDay.month ==(calendar.get(Calendar.MONTH)+1) &&
                appointmentDay.day == calendar.get(Calendar.DATE)) {
            //今天
            if (isFirst) {
                holder.solar.setTextColor(CommonUtil.getColor(mContext,
                        R.color.white));
                holder.solar.setBackgroundResource(R.drawable.point);
                ChildScheduleFragment.lastClickView = holder.solar;
                isFirst = false;
            }

        } else {
            holder.solar.setTextColor(CommonUtil.getColor(mContext,
                    R.color.new_txt_blacks));
        }
        if (selectedDate != null) {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(selectedDate);
            if (appointmentDay.year == calendar.get(Calendar.YEAR)&&
                    appointmentDay.month == (calendar1.get(Calendar.MONTH) + 1) &&
                    appointmentDay.day == calendar1.get(Calendar.DATE)) {
                LogUtil.print("ChildScheduleFragment.lastClickView--3-" + ChildScheduleFragment.lastClickView);
                LogUtil.print("ChildScheduleFragment.lastClickView--3-" + ChildScheduleFragment.lastClickView.getText().toString());
                holder.solar.setTextColor(CommonUtil.getColor(mContext,
                        R.color.white));
                holder.solar.setBackgroundResource(R.drawable.point);
                if (ChildScheduleFragment.lastClickView != null) {

                    if(!ChildScheduleFragment.lastClickView.getText().toString().
                            equals(holder.solar.getText().toString())){
                        ChildScheduleFragment.lastClickView.setBackgroundDrawable(null);
                        ChildScheduleFragment.lastClickView.setTextColor(CommonUtil.getColor(
                                mContext, R.color.new_txt_blacks));
                    }


                }

                ChildScheduleFragment.lastClickView = holder.solar;
            }
        }

        holder.solar.setText(appointmentDay.day + "");
        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (OnDateClickListener.instance != null) {
                    TextView textView = (TextView) ((RelativeLayout) v)
                            .getChildAt(0);
                    LogUtil.print("ChildScheduleFragment.lastClickView---" + ChildScheduleFragment.lastClickView);
                    if (ChildScheduleFragment.lastClickView != null) {
                        LogUtil.print(ChildScheduleFragment.lastClickView.getText().toString()
                                + "=====lastClickView===");

                        ChildScheduleFragment.lastClickView.setBackgroundDrawable(null);
                        ChildScheduleFragment.lastClickView.setTextColor(CommonUtil.getColor(
                                mContext, R.color.new_txt_blacks));
//                        }

                    }
                    textView.setBackgroundResource(R.drawable.point);

                    textView.setTextColor(CommonUtil.getColor(mContext,
                            R.color.white));
                    ChildScheduleFragment.lastClickView = textView;
                    OnDateClickListener.instance.onDateClick(
                            appointmentDay, true);

//					}
                }

            }
        });
        return convertView;
    }

    private Date selectedDate;

    public void setSelectedDate(Date selectedDate, List<AppointmentDay> data) {
        this.selectedDate = selectedDate;
        this.data = data;
    }

    class ViewHolder {
        private TextView solar;
    }

}
