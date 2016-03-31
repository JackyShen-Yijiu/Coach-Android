package com.blackcat.coach.adapters.rows;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.blackcat.coach.R;
import com.blackcat.coach.adapters.BaseViewHolder;
import com.blackcat.coach.adapters.ExamStudentListAdapter;
import com.blackcat.coach.imgs.UILHelper;
import com.blackcat.coach.models.AddStudentsVO;
import com.blackcat.coach.models.DicCode;
import com.blackcat.coach.models.ExamInfo;
import com.blackcat.coach.models.ExamStudentList;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.NetConstants;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.BaseUtils;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.VolleyUtil;
import com.blackcat.coach.widgets.SelectableRoundedImageView;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sun.bob.mcalendarview.adapters.CalendarExpAdapter;

/**
 * Created by pengdonghua on 2016/3/29.
 */
public class RowExamInfo {

    private static Context mContext;
//    private static
    public static BaseViewHolder createViewHolder(ViewGroup parent, Context context) {
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.row_exam_info, parent, false);
        Holder holder = new Holder(view);
        holder.rootView = view.findViewById(R.id.rootView);
        holder.timeTv = (TextView) view.findViewById(R.id.exam_info_time_tv);
        holder.passrateTv = (TextView) view.findViewById(R.id.exam_info_pass_rate_tv);
        holder.subjectTv = (TextView) view.findViewById(R.id.exam_info_subject_tv);
        holder.applyNumTv = (TextView) view.findViewById(R.id.exam_info_apply_num_tv);
        holder.missNumTv = (TextView) view.findViewById(R.id.exam_info_miss_exam_num_tv);
        holder.passNumTv = (TextView) view.findViewById(R.id.exam_info_pass_num_tv);
        holder.notPassNumTv = (TextView) view.findViewById(R.id.exam_info_not_pass_num_tv);
        holder.arrowIv = (ImageView) view.findViewById(R.id.exam_info_arrow_iv);
        holder.dividerIv = (ImageView) view.findViewById(R.id.item_divider);
        holder.examStudentListView = (ListView) view.findViewById(R.id.exam_info_listview);
        holder.passRl = (RelativeLayout) view.findViewById(R.id.exam_info_pass_rl);
        return holder;
    }

    public static <T> void bindViewHolder(final Activity activity,
                                          BaseViewHolder holder, final int position, final T info) {
        final Holder viewHolder = (Holder) holder;
        final ExamInfo item = (ExamInfo) info;
        viewHolder.rootView.setOnClickListener(new MyOnClickListener(activity, item, position));
//        viewHolder.passRl.setOnClickListener(new MyOnClickListener(activity, item, position));

        //
        viewHolder.timeTv.setText(item.examdate);
        viewHolder.passrateTv.setText(item.passrate + "%");

        if ("1".equals(item.subject)) {
            viewHolder.subjectTv.setText("科目一考试");
        } else if ("2".equals(item.subject)) {
            viewHolder.subjectTv.setText("科目二考试");

        } else if ("3".equals(item.subject)) {
            viewHolder.subjectTv.setText("科目三考试");

        } else if ("4".equals(item.subject)) {
            viewHolder.subjectTv.setText("科目四考试");
        }

        viewHolder.applyNumTv.setText(item.studentcount + "人");
        viewHolder.missNumTv.setText(item.missexamstudent + "人");
        viewHolder.passNumTv.setText(item.passstudent + "人");
        viewHolder.notPassNumTv.setText(item.nopassstudent + "人未通过");

        if (position == 0) {
            viewHolder.dividerIv.setVisibility(View.GONE);
        }
        //
        //获取学员列表

//                refresh(item.subject,item.examdate,"0");
        URI uri = URIUtil.getExamStudentList(item.subject, item.examdate, "0");
        Type mType = new TypeToken<Result<List<ExamStudentList>>>() {
        }.getType();
        String url = null;
        if (uri != null) {
            try {
                url = uri.toURL().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }

        Map map = new HashMap<>();
        map.put(NetConstants.KEY_AUTHORIZATION, Session.getToken());

        GsonIgnoreCacheHeadersRequest<Result<List<ExamStudentList>>> request = new GsonIgnoreCacheHeadersRequest<Result<List<ExamStudentList>>>(
                url, mType, map,
                new Response.Listener<Result<List<ExamStudentList>>>() {
                    @Override
                    public void onResponse(Result<List<ExamStudentList>> response) {
                        viewHolder.examStudentListView.setAdapter(new ExamStudentListAdapter(mContext, response.data));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                    }
                });
        // 请求加上Tag,用于取消请求
//        request.setTag(this);

        VolleyUtil.getQueue(mContext).add(request);

        viewHolder.passRl.setOnClickListener(new View.OnClickListener() {
            boolean isExpand = false;
            @Override
            public void onClick(View v) {
                //出现二级列表
                if (!isExpand) {
                    viewHolder.examStudentListView.setVisibility(View.VISIBLE);
                    viewHolder.arrowIv.setImageResource(R.mipmap.ic_arrow_up);

                }else{
                    viewHolder.examStudentListView.setVisibility(View.GONE);
                    viewHolder.arrowIv.setImageResource(R.mipmap.ic_arrow_down);
                }
                isExpand=!isExpand;


            }
        });
    }

    private static void refresh(String subjectid, String examdate, String examstate) {

    }

    static class Holder extends BaseViewHolder {

        private View rootView;
        private TextView timeTv, passrateTv, subjectTv;
        private TextView applyNumTv, missNumTv, passNumTv;
        private TextView notPassNumTv;

        private ImageView arrowIv, dividerIv;
        private ListView examStudentListView;
        private RelativeLayout passRl;

        public Holder(View itemView) {
            super(itemView);
        }
    }

    static class MyOnClickListener implements View.OnClickListener {
        private Activity activity;
        private ExamInfo examInfo;
        private int post;

        public MyOnClickListener(Activity act, ExamInfo item, int pos) {
            this.activity = act;
            this.examInfo = item;
            this.post = pos;
        }

        @Override
        public void onClick(View v) {
            LogUtil.print("click--->view");
            switch (v.getId()) {

                case R.id.exam_info_pass_rl:
                    //

                    break;
            }

        }
    }
}


