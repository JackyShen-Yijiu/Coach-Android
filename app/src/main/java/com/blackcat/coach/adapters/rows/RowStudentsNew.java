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
import com.blackcat.coach.activities.ChatActivity;
import com.blackcat.coach.activities.DetailStudentActivity;
import com.blackcat.coach.activities.NewDetailStudentAct;
import com.blackcat.coach.adapters.BaseViewHolder;
import com.blackcat.coach.easemob.BlackCatHXSDKHelper;
import com.blackcat.coach.easemob.Constant;
import com.blackcat.coach.imgs.UILHelper;
import com.blackcat.coach.models.User;
import com.blackcat.coach.utils.BaseUtils;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.widgets.SelectableRoundedImageView;

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
        holder.ivAvatar = (SelectableRoundedImageView) view.findViewById(R.id.iv_avatar);
        holder.ivAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.ivAvatar.setImageResource(R.mipmap.ic_avatar_small);
        holder.ivAvatar.setOval(true);


        holder.tvLast = (TextView) view.findViewById(R.id.tv_last);
        holder.tvMissing = (TextView) view.findViewById(R.id.tv_missing);
        holder.rl3 = (RelativeLayout) view.findViewById(R.id.item_student_rl3);
        holder.imgIphone = (ImageView) view.findViewById(R.id.item_student_phone);
        holder.imgChat = (ImageView) view.findViewById(R.id.item_student_msg);
        return holder;
    }

    public static <T> void bindViewHolder(final Activity activity,
                                          BaseViewHolder holder, final int position, final T info) {

        final Holder viewHolder = (Holder) holder;
        User item = (User) info;
        viewHolder.rootView.setOnClickListener(new MyOnClickListener(activity, item));
        viewHolder.imgIphone.setOnClickListener(new MyOnClickListener(activity, item));
        viewHolder.imgChat.setOnClickListener(new MyOnClickListener(activity, item));


        viewHolder.tvName.setText(item.name);
        viewHolder.tvProgress.setText(item.courseinfo.progress);
        LogUtil.print("bindViewHolder---->" + item.leavecoursecount);
        //sun
        if(item.subject.subjectid==2^item.subject.subjectid==3){
            viewHolder.tvLast.setText("剩余" + (item.courseinfo.totalcourse-item.courseinfo.finishcourse) + "课时");
            viewHolder.tvMissing.setText("漏"+item.courseinfo.missingcourse+"课时");
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
        private SelectableRoundedImageView ivAvatar;
        private TextView tvName;
        private TextView tvProgress;
        //剩余课程的数量  : 预约剩余课程20学时
        private TextView tvLast;
        private TextView tvMissing;
        private RelativeLayout rl3;
        private ImageView imgIphone;
        private ImageView imgChat;

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
                    Intent intent = new Intent(activity, NewDetailStudentAct.class);
                    intent.putExtra(Constants.DATA, user);
                    activity.startActivity(intent);
                    break;
                case R.id.item_student_phone:
                    if (!TextUtils.isEmpty(user.mobile)) {
                        BaseUtils.callSomebody(activity, user.mobile);
                    }else{

                    }
                    break;
                case R.id.item_student_msg://聊天
                    intoChat(activity,user);
                    break;
            }

        }
    }

    public static void intoChat(Activity activity,User mUser){
        if (!BlackCatHXSDKHelper.getInstance().isLogined()) {
            return;
        }
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(Constant.MESSAGE_USERID_ATR_KEY, mUser._id);
        intent.putExtra(Constant.MESSAGE_NAME_ATTR_KEY, mUser.name);
        if (mUser.headportrait != null &&
                !TextUtils.isEmpty( mUser.headportrait.originalpic)) {
            intent.putExtra(Constant.MESSAGE_AVATAR_ATTR_KEY, mUser.headportrait.originalpic);
        }
        intent.putExtra(Constant.CHAT_FROM_TYPE, Constant.CHAT_FROM_RESERVATION);
        activity.startActivity(intent);
    }
}


