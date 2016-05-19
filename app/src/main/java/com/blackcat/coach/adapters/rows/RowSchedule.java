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
import com.blackcat.coach.activities.AddStudentsActivity;
import com.blackcat.coach.activities.NewDetailStudentAct;
import com.blackcat.coach.adapters.BaseViewHolder;
import com.blackcat.coach.imgs.UILHelper;
import com.blackcat.coach.models.DaytimelysReservation;
import com.blackcat.coach.models.User;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.UTC2LOC;
import com.blackcat.coach.widgets.SelectableRoundedImageView;


import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class RowSchedule {

    private static Context mContext;

    private static int blue;
    private static int blue_light;
    private static int txt_light;
    private static int txt_black;
    private static int white;
    private static int item_bg;

    //当前选择项
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
        holder.adapter = new StudentAdapter(context);
        holder.studentGv.setAdapter(holder.adapter);
        blue = mContext.getResources().getColor(R.color.new_txt_blues);
        txt_light = mContext.getResources().getColor(R.color.new_txt_lights);
        txt_black = mContext.getResources().getColor(R.color.new_txt_blacks);
        blue_light = mContext.getResources().getColor(R.color.new_txt_light_blues);
        white = mContext.getResources().getColor(R.color.white_pure);
        item_bg = mContext.getResources().getColor(R.color.item_bg);
        return holder;
    }
    private User user;
    public static <T> void bindViewHolder(final Activity activity,
                                          BaseViewHolder holder, final int position, final List<T> info) {
        long start = System.currentTimeMillis();
        LogUtil.print("start--time->"+start);
        final Holder viewHolder = (Holder) holder;
        final DaytimelysReservation item = (DaytimelysReservation) info.get(position);
        final int index = position;
        viewHolder.rootView.setOnClickListener(new MyOnClickListener(activity, item));
        if (item != null) {
            LogUtil.print("start--777->" + (System.currentTimeMillis() - start));
            viewHolder.beginTimeTv.setText(UTC2LOC.instance.getDate(item.coursebegintime, "HH:mm"));
            viewHolder.endTimeTv.setText(UTC2LOC.instance.getDate(item.courseendtime, "HH:mm"));
            LogUtil.print("start--777-2222>" + (System.currentTimeMillis() - start));
            Date now = Calendar.getInstance().getTime();
            LogUtil.print("start--666->" + (System.currentTimeMillis() - start));
            if (UTC2LOC.instance.getDates(item.coursebegintime, "yyyy-MM-dd HH:mm:ss").after(now)) {
                //未来的
                viewHolder.timeIv.setBackgroundResource(R.mipmap.schedule_item_node_future);

                viewHolder.lineIv.setBackgroundColor(blue);
                viewHolder.orderedStudentTv.setTextColor(txt_light);
                viewHolder.remainStudnetTv.setTextColor(txt_light);
                viewHolder.beginTimeTv.setTextColor(txt_black);
                viewHolder.endTimeTv.setTextColor(txt_light);
                LogUtil.print("start--888->" + (System.currentTimeMillis() - start));
                viewHolder.orderedStudentTv.setText("已约" + item.selectedstudentcount + "人");
                viewHolder.remainStudnetTv.setText("剩余名额" + (item.coursestudentcount - item.selectedstudentcount) + "人");
                viewHolder.rootView.setBackgroundColor(white);
                viewHolder.rootView.setClickable(true);
                viewHolder.timeLl.setBackgroundColor(white);
                LogUtil.print("start--999->" + (System.currentTimeMillis() - start));
            } else if (UTC2LOC.instance.getDates(item.courseendtime, "yyyy-MM-dd HH:mm:ss").before(now)) {
                //过时
                viewHolder.timeIv.setBackgroundResource(R.mipmap.schedule_item_node_past);
                viewHolder.lineIv.setBackgroundColor(txt_light);
                viewHolder.rootView.setBackgroundColor(item_bg);
                viewHolder.beginTimeTv.setTextColor(txt_light);
                viewHolder.endTimeTv.setTextColor(mContext.getResources().getColor(R.color.new_txt_lights_p));
                viewHolder.rootView.setClickable(false);
                viewHolder.orderedStudentTv.setTextColor(txt_light);
                viewHolder.remainStudnetTv.setTextColor(txt_light);
                viewHolder.orderedStudentTv.setText("已学" + item.selectedstudentcount + "人");
                viewHolder.remainStudnetTv.setText("漏课" + (item.selectedstudentcount - item.signinstudentcount) + "人");
                viewHolder.timeLl.setBackgroundColor(item_bg);
            } else {
                //现在
                viewHolder.timeIv.setBackgroundResource(R.mipmap.schedule_item_node_now);
                viewHolder.lineIv.setBackgroundColor(blue);
                viewHolder.orderedStudentTv.setTextColor(blue);
                viewHolder.remainStudnetTv.setTextColor(blue);
                viewHolder.beginTimeTv.setTextColor(blue);
                viewHolder.endTimeTv.setTextColor(blue_light);
                viewHolder.orderedStudentTv.setText("已约" + item.selectedstudentcount + "人");
                viewHolder.remainStudnetTv.setText("剩余名额" + (item.coursestudentcount - item.selectedstudentcount) + "人");
                viewHolder.rootView.setBackgroundColor(white);
                viewHolder.rootView.setClickable(true);
                viewHolder.timeLl.setBackgroundColor(white);
            }

            LogUtil.print("start--222->"+(System.currentTimeMillis() - start));
            viewHolder.lineIv.setVisibility(View.VISIBLE);
            viewHolder.adapter.setData(item);
            LogUtil.print("start--333->" + (System.currentTimeMillis() - start));
//            viewHolder.studentGv.setAdapter(viewHolder.adapter);//new StudentAdapter(mContext, item)
            viewHolder.studentGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Date now = Calendar.getInstance().getTime();
                    //过时的不能点击
                    if (UTC2LOC.instance.getDates(item.coursebegintime, "yyyy-MM-dd HH:mm:ss").before(now)) {

                    } else {


                        if (item.coursereservationdetial != null && item.coursereservationdetial.size() > position) {
                            //已预约的学员
                            LogUtil.print("已预约的学员onItemClick");
                            Intent intent = new Intent(mContext, NewDetailStudentAct.class);
                            intent.putExtra(Constants.DATA,item.coursereservationdetial.get(position).userid);
                            mContext.startActivity(intent);

                        } else {
                            //添加学员
                            LogUtil.print("添加学员onItemClick" + info.size() + "---" + index);
                            Intent intent = new Intent(mContext, AddStudentsActivity.class);
                            intent.putExtra("student1", item);
                            if (info.size() > (index + 1)) {
                                intent.putExtra("student2", (DaytimelysReservation) info.get(index + 1));
                            }
                            if (info.size() > (index + 2)) {
                                intent.putExtra("student3", (DaytimelysReservation) info.get(index + 2));
                            }
                            if (info.size() > (index + 3)) {
                                intent.putExtra("student4", (DaytimelysReservation) info.get(index + 3));
                            }

                            mContext.startActivity(intent);
                        }
                    }

                }
            });
            LogUtil.print("start--444->" + (System.currentTimeMillis() - start));
        }

    }

    static class StudentAdapter extends BaseAdapter {

        private Context mContext;
        private DaytimelysReservation reservation = null;

        public StudentAdapter(Context mContext, DaytimelysReservation reservation) {
            this.mContext = mContext;
            this.reservation = reservation;
        }

        public StudentAdapter(Context mContext){
            this.mContext = mContext;
        }

        public void setData(DaytimelysReservation r){
            reservation = r;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            int count=0;
            if(reservation == null){
                count = 0;
            }else if (UTC2LOC.instance.getDates(reservation.coursebegintime, "yyyy-MM-dd HH:mm:ss")
                    .before(Calendar.getInstance().getTime())) {

                //当前时间的不显示“+”
                    count = reservation.selectedstudentcount;
                }else{
                    count=reservation.coursestudentcount;
                }
            if(count ==0){
                count =1;
            }
            return count;
        }


        @Override
        public Object getItem(int position) {
            //
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
            ImageView studyStatusIv = (ImageView) convertView.findViewById(R.id.student_study_status_iv);
            SelectableRoundedImageView missClassTv = (SelectableRoundedImageView) convertView.findViewById(R.id.student_miss_class_tv);
            missClassTv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            missClassTv.setImageResource(R.mipmap.schedule_item_miss_class);
            missClassTv.setOval(true);
            missClassTv.setVisibility(View.INVISIBLE);
            studyStatusIv.setVisibility(View.INVISIBLE);
            studentPic.setScaleType(ImageView.ScaleType.CENTER_CROP);
            studentPic.setImageResource(R.mipmap.schedule_item_no_student);
            studentPic.setOval(true);
            if(reservation == null){

            }else  if (reservation.coursereservationdetial != null && reservation.coursereservationdetial.size() > position) {
                DaytimelysReservation.Coursereservationdetial detail = reservation.coursereservationdetial.get(position);
                if (detail.userid != null && detail.userid.headportrait != null && !TextUtils.isEmpty(detail.userid.headportrait.originalpic)) {
                    UILHelper.loadImage(studentPic, detail.userid.headportrait.originalpic, false, R.mipmap.schedule_item_no_student);
                }
                studentNameTv.setText(detail.userid.name);
                if (detail.reservationstate == 9) {
                    //已签到
                    studyStatusIv.setVisibility(View.VISIBLE);
                    studyStatusIv.setImageResource(R.mipmap.schedule_item_register);
                } else if (detail.reservationstate == 10) {
                    //漏课
                    missClassTv.setVisibility(View.VISIBLE);
                } else if (detail.reservationstate == 7||detail.reservationstate == 6) {
                    //评价完成
                    studyStatusIv.setVisibility(View.VISIBLE);
                    studyStatusIv.setImageResource(R.mipmap.schedule_item_complete);

                } else {
                    studyStatusIv.setVisibility(View.INVISIBLE);
                }


            } else {
                Date now = Calendar.getInstance().getTime();
                //过时
                if (UTC2LOC.instance.getDates(reservation.courseendtime, "yyyy-MM-dd HH:mm:ss").before(now)) {
                    //过时的不出现“+”
                    studentPic.setVisibility(View.INVISIBLE);
                    studentNameTv.setVisibility(View.INVISIBLE);
                } else {
                    if (UTC2LOC.instance.getDates(reservation.coursebegintime, "yyyy-MM-dd HH:mm:ss").before(Calendar.getInstance().getTime())
                            &&UTC2LOC.instance.getDates(reservation.courseendtime, "yyyy-MM-dd HH:mm:ss").after(Calendar.getInstance().getTime())) {
                        //当前时间的不出现“+”
                        studentPic.setVisibility(View.GONE);
                        studentNameTv.setVisibility(View.GONE);
                    }else{
                        studentPic.setImageResource(R.mipmap.schedule_item_add_student);
                        studentNameTv.setVisibility(View.INVISIBLE);
                    }

                }
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
        StudentAdapter adapter;
        int blue;
        int blue_light;
        int txt_light;
        int txt_black;

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
