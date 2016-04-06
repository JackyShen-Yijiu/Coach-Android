package com.blackcat.coach.adapters.rows;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.blackcat.coach.R;
import com.blackcat.coach.adapters.BaseViewHolder;
import com.blackcat.coach.imgs.UILHelper;
import com.blackcat.coach.models.AddStudentsVO;
import com.blackcat.coach.models.User;
import com.blackcat.coach.utils.BaseUtils;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.widgets.SelectableRoundedImageView;

/**
 * Created by pengdonghua on 2016/3/29.
 */
public class RowAddStudents {
    /**当前选中的项*/
    public static int index = -1;
    /**当前选中的学员Id*/
    public static AddStudentsVO selectUser=null;
    private static RadioButton rbLast = null;

    public static BaseViewHolder createViewHolder(ViewGroup parent, Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_add_student, parent, false);
        Holder holder = new Holder(view);
        holder.rootView = view.findViewById(R.id.rootView);
        holder.tvName = (TextView) view.findViewById(R.id.tv_name);
        holder.tvProgress = (TextView) view.findViewById(R.id.tv_progress);
        holder.ivAvatar = (SelectableRoundedImageView) view.findViewById(R.id.iv_avatar);
        holder.ivAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.ivAvatar.setImageResource(R.mipmap.ic_avatar_small);
        holder.ivAvatar.setOval(true);
        holder.imgIphone = (ImageView) view.findViewById(R.id.item_student_phone);
        holder.iv_check = (RadioButton) view.findViewById(R.id.iv_check);



        return holder;
    }

    public static <T> void bindViewHolder(final Activity activity,
                                          BaseViewHolder holder, final int position, final T info) {
        LogUtil.print("click--->" + index + "position-->" + position);
        final Holder viewHolder = (Holder) holder;
        final AddStudentsVO item = (AddStudentsVO) info;
        viewHolder.rootView.setOnClickListener(new MyOnClickListener(activity, item, position));
        viewHolder.imgIphone.setOnClickListener(new MyOnClickListener(activity, item, position));
        viewHolder.tvName.setText(item.name);
        //viewHolder.tvProgress.setText(item.subjectprocess);

        if (item.subject!=null){
             if (item.subject.subjectid==2){
                   viewHolder.tvProgress.setText(item.subjecttwo.progress);
              }else {
            viewHolder.tvProgress.setText(item.subjectthree.progress);
        }
        }


        viewHolder.iv_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(index != position){
                   index = position;
                   if(rbLast!=null){//上一次选中了，设置为不选中状态
                        rbLast.setChecked(false);
                   }
                   rbLast = viewHolder.iv_check;
               }
                selectUser = item;
                LogUtil.print(item.name+"click--->"+index);
                LogUtil.print("click--->" + index + "position--00000>" + position);
            }
        });

        if(index == position){
            viewHolder.iv_check.setChecked(true);
        }else{
            viewHolder.iv_check.setChecked(false);
        }

        if (item.headportrait != null && !TextUtils.isEmpty(item.headportrait.originalpic)) {
            //TODO
//            PicassoUtil.loadImage(activity, viewHolder.ivAvatar, item.headportrait.originalpic, R.dimen.avatar_size, R.dimen.avatar_size, false, R.mipmap.ic_avatar_small);
            UILHelper.loadImage(viewHolder.ivAvatar, item.headportrait.originalpic, false, R.mipmap.ic_avatar_small);

        } else {
            viewHolder.ivAvatar.setImageResource(R.mipmap.ic_avatar_small);
        }



    }

    /**
     * 选中某一项
     * @param user
     * @param pos
     * @param box
     */
    public static void select(AddStudentsVO user,int pos,RadioButton box){
        if(index != pos){
            index = pos;
            if(rbLast!=null){//上一次选中了，设置为不选中状态
                rbLast.setChecked(false);
            }
            rbLast = box;
        }
        selectUser = user;
    }

    static class Holder extends BaseViewHolder {

        private View rootView;
        private SelectableRoundedImageView ivAvatar;
        private TextView tvName;
        private TextView tvProgress;
        private ImageView imgIphone;
        private RadioButton iv_check;

        public Holder(View itemView) {
            super(itemView);
        }
    }

    static class MyOnClickListener implements View.OnClickListener {
        private Activity activity;
        private AddStudentsVO user;
        private int post;
        public MyOnClickListener(Activity act, AddStudentsVO item,int pos) {
            this.activity = act;
            this.user = item;
            this.post = pos;
        }
        @Override
        public void onClick(View v) {
            LogUtil.print("click--->view");
            switch(v.getId()){
                case R.id.item_student_phone:
                    if (!TextUtils.isEmpty(user.mobile)) {
                        BaseUtils.callSomebody(activity, user.mobile);
                    }else{
                    }
                    break;
                case R.id.iv_check:
//                    index = post;


                    break;
                case R.id.rootView:
                    break;
            }

        }
    }
}


