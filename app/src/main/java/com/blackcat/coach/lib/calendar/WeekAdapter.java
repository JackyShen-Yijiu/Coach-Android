package com.blackcat.coach.lib.calendar;

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
import com.blackcat.coach.utils.CommonUtil;
import com.blackcat.coach.utils.LogUtil;


public class WeekAdapter extends ArrayAdapter<Integer> {

    private List<AppointmentDay> data;
    private Context mContext;

//    private int lastIitem;
    private boolean isFirst = true;

    public WeekAdapter(Context context, int resource, List<AppointmentDay> data) {
        super(context, resource);
        mContext = context;
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
            // holder.rest = (TextView) convertView.findViewById(R.id.rest);
            holder.solar = (TextView) convertView.findViewById(R.id.solar);
//			holder.week = (TextView) convertView.findViewById(R.id.week);
            // holder.hasOrder = (ImageView) convertView
            // .findViewById(R.id.has_order);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.solar.setBackgroundDrawable(null);

        holder.solar.setTextColor(CommonUtil.getColor(mContext,
                R.color.new_txt_blacks));

        // holder.hasOrder.setVisibility(View.INVISIBLE);
        // holder.rest.setVisibility(View.INVISIBLE);
        final AppointmentDay appointmentDay = data.get(position);
        // final Integer item = appointmentDay.day;
        // final Integer month = appointmentDay.month;
        Calendar calendar = Calendar.getInstance();
        final int today = calendar.get(Calendar.DAY_OF_MONTH);
//		final int thisMonth = calendar.get(Calendar.MONTH + 1);
        if (appointmentDay.day == today) {
//            holder.solar.setTextColor(CommonUtil.getColor(mContext,
//                    R.color.new_txt_blues));
            if(isFirst){
                holder.solar.setTextColor(CommonUtil.getColor(mContext,
                        R.color.white));
                holder.solar.setBackgroundResource(R.drawable.point);
                ChildScheduleFragment.lastClickView = holder.solar;
//                lastIitem = appointmentDay.day;
                isFirst = false;
            }

            // holder.hasOrder.setVisibility(View.VISIBLE);
            // LogUtil.print("今天");
        }

        else {
            holder.solar.setTextColor(CommonUtil.getColor(mContext,
                    R.color.new_txt_blacks));
        }
        if (selectedDate != null) {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(selectedDate);
//            if(lastClickView!=null){
//                if (lastIitem == today) {
//                    lastClickView.setBackgroundDrawable(null);
//                    lastClickView.setTextColor(CommonUtil.getColor(
//                            mContext, R.color.new_txt_blues));
//                } else {
//                    lastClickView.setBackgroundDrawable(null);
//                    lastClickView.setTextColor(CommonUtil.getColor(
//                            mContext, R.color.new_txt_blacks));
//                }
//            }

            if (appointmentDay.day == calendar1.get(Calendar.DATE)) {
                holder.solar.setTextColor(CommonUtil.getColor(mContext,
                        R.color.white));
                holder.solar.setBackgroundResource(R.drawable.point);
                ChildScheduleFragment.lastClickView = holder.solar;
//                lastIitem = appointmentDay.day;
            }
        }

//		holder.week.setText(appointmentDay.week);
        holder.solar.setText(appointmentDay.day + "");
        // LogUtil.print(appointmentDay.year + "-" + appointmentDay.month + "-"
        // + appointmentDay.day);
        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (OnDateClickListener.instance != null) {
//					if ((thisMonth == appointmentDay.month)
//							&& (appointmentDay.day < today)) {
//						OnDateClickListener.instance.onDateClick(
//								appointmentDay, false);
//					} else {
                    TextView textView = (TextView) ((RelativeLayout) v)
                            .getChildAt(0);
                    if ( ChildScheduleFragment.lastClickView != null) {
                        LogUtil.print( ChildScheduleFragment.lastClickView.getText().toString()
                                + "========");
                        // if (lastClickView == textView) {
                        // return;
                        // }
//                        if (lastIitem == today) {
////                            lastClickView.setBackgroundDrawable(null);
////                            lastClickView.setTextColor(CommonUtil.getColor(
////                                    mContext, R.color.new_txt_blues));
//                        } else {
                        ChildScheduleFragment.lastClickView.setBackgroundDrawable(null);
                        ChildScheduleFragment.lastClickView.setTextColor(CommonUtil.getColor(
                                    mContext, R.color.new_txt_blacks));
//                        }

                    }
                    textView.setBackgroundResource(R.drawable.point);

                    textView.setTextColor(CommonUtil.getColor(mContext,
                            R.color.white));
                    ChildScheduleFragment.lastClickView = textView;
//                    lastIitem = appointmentDay.day;
                    OnDateClickListener.instance.onDateClick(
                            appointmentDay, true);

//					}
                }

            }
        });
        return convertView;
    }

    private Date selectedDate;

    public void setSelectedDate(Date selectedDate,List<AppointmentDay> data) {
        this.selectedDate = selectedDate;
        this.data = data;
    }

    class ViewHolder {
        private TextView solar;
        // private ImageView hasOrder;
//		private TextView week;
    }

}