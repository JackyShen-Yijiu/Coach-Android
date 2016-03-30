package com.blackcat.coach.adapters.rows;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blackcat.coach.R;
import com.blackcat.coach.activities.DetailStudentActivity;
import com.blackcat.coach.adapters.BaseViewHolder;
import com.blackcat.coach.imgs.UILHelper;
import com.blackcat.coach.models.User;
import com.blackcat.coach.utils.BaseUtils;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.LogUtil;

/**
 * Created by pengdonghua on 2016/3/25.
 */
public class RowStudentsNew {

    public static BaseViewHolder createViewHolder(ViewGroup parent, Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_student, parent, false);
        Holder holder = new Holder(view);
        holder.rootView = view.findViewById(R.id.rootView);
        holder.tvName = (TextView) view.findViewById(R.id.tv_name);
        holder.tvProgress = (TextView) view.findViewById(R.id.tv_progress);
        holder.ivAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
        holder.tvLast = (TextView) view.findViewById(R.id.tv_last);
        holder.tvMissing = (TextView) view.findViewById(R.id.tv_missing);
        holder.rl3 = (RelativeLayout) view.findViewById(R.id.item_student_rl3);
        holder.imgIphone = (ImageView) view.findViewById(R.id.item_student_phone);
        return holder;
    }

    public static <T> void bindViewHolder(final Activity activity,
                                          BaseViewHolder holder, final int position, final T info) {

        final Holder viewHolder = (Holder) holder;
        User item = (User) info;
        viewHolder.rootView.setOnClickListener(new MyOnClickListener(activity, item));
        viewHolder.imgIphone.setOnClickListener(new MyOnClickListener(activity, item));
        viewHolder.tvName.setText(item.name);
        viewHolder.tvProgress.setText(item.subjectprocess);
        LogUtil.print("bindViewHolder---->" + item.name);
        //sun
        if(item.subject.subjectid==2^item.subject.subjectid==3){
            viewHolder.tvLast.setText("剩余" + item.leavecoursecount + "课时");
            viewHolder.tvMissing.setText("漏"+item.missingcoursecount+"课时");
        }
        else{
            viewHolder.rl3.setVisibility(View.GONE);
            viewHolder.tvLast.setVisibility(View.GONE);
            viewHolder.tvProgress.setText(item.subject.name);
        }

        if (item.headportrait != null && !TextUtils.isEmpty(item.headportrait.originalpic)) {
            //TODO
//            PicassoUtil.loadImage(activity, viewHolder.ivAvatar, item.headportrait.originalpic, R.dimen.avatar_size, R.dimen.avatar_size, false, R.mipmap.ic_avatar_small);
            UILHelper.loadImage(viewHolder.ivAvatar, item.headportrait.originalpic, false, R.mipmap.ic_avatar_small);

        } else {
            viewHolder.ivAvatar.setImageResource(R.mipmap.ic_avatar_small);
        }



    }

    static class Holder extends BaseViewHolder {

        private View rootView;
        private ImageView ivAvatar;
        private TextView tvName;
        private TextView tvProgress;
        //剩余课程的数量  : 预约剩余课程20学时
        private TextView tvLast;
        private TextView tvMissing;
        private RelativeLayout rl3;
        private ImageView imgIphone;

        public Holder(View itemView) {
            super(itemView);
        }
    }

    static class MyOnClickListener implements View.OnClickListener {
        private Activity activity;
        private User user;
        public MyOnClickListener(Activity act, User item) {
            this.activity = act;
            this.user = item;
        }
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.rootView:
                    Intent intent = new Intent(activity, DetailStudentActivity.class);
                    intent.putExtra(Constants.DATA, user);
                    activity.startActivity(intent);
                    break;
                case R.id.item_student_phone:
                    if (!TextUtils.isEmpty(user.mobile)) {
                        BaseUtils.callSomebody(activity, user.mobile);
                    }else{

                    }
                    break;
            }

        }
    }
}


