package com.blackcat.coach.adapters.rows;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blackcat.coach.R;
import com.blackcat.coach.activities.DetailReservationActivity;
import com.blackcat.coach.adapters.BaseViewHolder;
import com.blackcat.coach.imgs.UILHelper;
import com.blackcat.coach.models.DaytimelysReservation;
import com.blackcat.coach.models.MessageCount;
import com.blackcat.coach.models.Reservation;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.UTC2LOC;
import com.blackcat.coach.widgets.SelectableRoundedImageView;

import org.jivesoftware.smack.util.collections.IterableMap;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class RowSchedule {

    private static Context mContext;

    public static BaseViewHolder createViewHolder(ViewGroup parent, Context context) {
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.row_schedule, parent, false);
        Holder holder = new Holder(view);
        holder.rootView = view.findViewById(R.id.rootView);
        holder.timeIv = (ImageView) view.findViewById(R.id.schedule_time_iv);
        holder.beginTimeTv = (TextView) view.findViewById(R.id.schedule_begintime_tv);
        holder.endTimeTv = (TextView) view.findViewById(R.id.schedule_endtime_tv);
        holder.orderedStudentTv = (TextView) view.findViewById(R.id.schedule_ordered_student_tv);
        holder.remainStudnetTv = (TextView) view.findViewById(R.id.schedule_remain_student_tv);
        holder.studentGv = (GridView) view.findViewById(R.id.schedule_student_gv);
        holder.lineIv = (ImageView) view.findViewById(R.id.schedule_line_iv);

        holder.timeLl = (LinearLayout) view.findViewById(R.id.schedule_time_ll);
        return holder;
    }

    public static <T> void bindViewHolder(final Activity activity,
                                          BaseViewHolder holder, final int position, final T info) {
        final Holder viewHolder = (Holder) holder;
        final DaytimelysReservation item = (DaytimelysReservation) info;
        viewHolder.rootView.setOnClickListener(new MyOnClickListener(activity, item));
        if (item != null) {
            viewHolder.beginTimeTv.setText(item.coursetime.getBegintime().substring(0,item.coursetime.getBegintime().lastIndexOf(":")));
            viewHolder.endTimeTv.setText(item.coursetime.getEndtime().substring(0, item.coursetime.getEndtime().lastIndexOf(":")));
            Date now = Calendar.getInstance().getTime();
            if (UTC2LOC.instance.getDates(item.coursebegintime, "yyyy-MM-dd HH:mm:ss").after(now)) {
                //未来的
                viewHolder.timeIv.setBackgroundResource(R.mipmap.schedule_item_node_future);
                viewHolder.lineIv.setBackgroundColor(mContext.getResources().getColor(R.color.new_txt_blues));
                viewHolder.orderedStudentTv.setTextColor(mContext.getResources().getColor(R.color.new_txt_lights));
                viewHolder.remainStudnetTv.setTextColor(mContext.getResources().getColor(R.color.new_txt_lights));
                viewHolder.beginTimeTv.setTextColor(mContext.getResources().getColor(R.color.new_txt_blacks));
                viewHolder.endTimeTv.setTextColor(mContext.getResources().getColor(R.color.new_txt_lights));
                viewHolder.orderedStudentTv.setText("已约" + item.selectedstudentcount + "人");
                viewHolder.remainStudnetTv.setText("剩余名额" + (item.coursestudentcount - item.selectedstudentcount) + "人");
                viewHolder.rootView.setBackgroundColor(mContext.getResources().getColor(R.color.white_pure));
                viewHolder.rootView.setClickable(true);
                viewHolder.timeLl.setBackgroundColor(mContext.getResources().getColor(R.color.white_pure));

            } else if (UTC2LOC.instance.getDates(item.courseendtime, "yyyy-MM-dd HH:mm:ss").before(now)) {
                //过时
                viewHolder.timeIv.setBackgroundResource(R.mipmap.schedule_item_node_past);
                viewHolder.lineIv.setBackgroundColor(mContext.getResources().getColor(R.color.new_txt_lights));
                viewHolder.rootView.setBackgroundColor(mContext.getResources().getColor(R.color.item_bg));
                viewHolder.beginTimeTv.setTextColor(mContext.getResources().getColor(R.color.new_txt_lights));
                viewHolder.endTimeTv.setTextColor(mContext.getResources().getColor(R.color.new_txt_lights_p));
                viewHolder.rootView.setClickable(false);
                viewHolder.orderedStudentTv.setTextColor(mContext.getResources().getColor(R.color.new_txt_lights));
                viewHolder.remainStudnetTv.setTextColor(mContext.getResources().getColor(R.color.new_txt_lights));
                viewHolder.orderedStudentTv.setText("已学" + item.selectedstudentcount + "人");
                viewHolder.remainStudnetTv.setText("漏课" + (item.selectedstudentcount - item.signinstudentcount) + "人");
                viewHolder.timeLl.setBackgroundColor(mContext.getResources().getColor(R.color.item_bg));
            } else {
                //现在
                viewHolder.timeIv.setBackgroundResource(R.mipmap.schedule_item_node_now);
                viewHolder.lineIv.setBackgroundColor(mContext.getResources().getColor(R.color.new_txt_blues));
                viewHolder.orderedStudentTv.setTextColor(mContext.getResources().getColor(R.color.new_txt_blues));
                viewHolder.remainStudnetTv.setTextColor(mContext.getResources().getColor(R.color.new_txt_blues));
                viewHolder.beginTimeTv.setTextColor(mContext.getResources().getColor(R.color.new_txt_blacks));
                viewHolder.endTimeTv.setTextColor(mContext.getResources().getColor(R.color.new_txt_lights));
                viewHolder.orderedStudentTv.setText("已约" + item.selectedstudentcount + "人");
                viewHolder.remainStudnetTv.setText("剩余名额" + (item.coursestudentcount - item.selectedstudentcount) + "人");
                viewHolder.rootView.setBackgroundColor(mContext.getResources().getColor(R.color.white_pure));
                viewHolder.rootView.setClickable(true);
                viewHolder.timeLl.setBackgroundColor(mContext.getResources().getColor(R.color.white_pure));
            }


            //
            viewHolder.studentGv.setAdapter(new StudentAdapter(mContext, item));
            viewHolder.studentGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   if(item.coursereservationdetial!=null &&item.coursereservationdetial.size()>position){
                       //已预约的学员
                       LogUtil.print("已预约的学员onItemClick");
                   }else{
                       //添加学员
                       LogUtil.print("添加学员onItemClick");
                   }
                }
            });

        }

    }

    static class StudentAdapter extends BaseAdapter {

        private Context mContext;
        private DaytimelysReservation reservation;

        public StudentAdapter(Context mContext, DaytimelysReservation reservation) {
            this.mContext = mContext;
            this.reservation = reservation;
        }

        @Override
        public int getCount() {
            return reservation.coursestudentcount;
        }


        @Override
        public Object getItem(int position) {
            return reservation.coursereservationdetial.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.schedule_student_info_item, parent, false);
            SelectableRoundedImageView studentPic = (SelectableRoundedImageView) convertView.findViewById(R.id.student_pic_iv);
            TextView studentNameTv = (TextView) convertView.findViewById(R.id.student_name_tv);

            studentPic.setScaleType(ImageView.ScaleType.CENTER_CROP);
            studentPic.setImageResource(R.mipmap.schedule_item_no_student);
            studentPic.setOval(true);
            if(reservation.coursereservationdetial!=null&&reservation.coursereservationdetial.size()>position){
                DaytimelysReservation.Coursereservationdetial detail =reservation.coursereservationdetial.get(position);
                if (detail.userid != null && detail.userid.headportrait != null && !TextUtils.isEmpty(detail.userid.headportrait.originalpic)) {
                    UILHelper.loadImage(studentPic, detail.userid.headportrait.originalpic, false, R.mipmap.schedule_item_no_student);
                }
                studentNameTv.setText(detail.userid.name);
            }else{
                studentPic.setImageResource(R.mipmap.schedule_item_add_student);
                studentNameTv.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    static class Holder extends BaseViewHolder {

        private View rootView;
        private TextView beginTimeTv, endTimeTv;
        private ImageView timeIv;
        private ImageView lineIv;
        private TextView orderedStudentTv, remainStudnetTv;
        private GridView studentGv;//
        private LinearLayout timeLl;

        public Holder(View itemView) {
            super(itemView);
        }
    }

    static class MyOnClickListener implements View.OnClickListener {
        private Activity activity;
        private DaytimelysReservation item;

        public MyOnClickListener(Activity act, DaytimelysReservation item) {
            this.activity = act;
            this.item = item;
        }

        @Override
        public void onClick(View v) {
//            Intent intent = new Intent(activity, DetailReservationActivity.class);
//            intent.putExtra(Constants.DETAIL, item);
//            activity.startActivity(intent);
        }
    }
}
