package com.blackcat.coach.adapters.rows;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackcat.coach.R;
import com.blackcat.coach.activities.DetailReservationActivity;
import com.blackcat.coach.adapters.BaseViewHolder;
import com.blackcat.coach.imgs.UILHelper;
import com.blackcat.coach.models.Reservation;
import com.blackcat.coach.utils.Constants;


public class RowSchedule {

    public static BaseViewHolder createViewHolder(ViewGroup parent, Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_schedule, parent, false);
        Holder holder = new Holder(view);
        holder.rootView = view.findViewById(R.id.rootView);
        holder.ivAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
        holder.tvBegin = (TextView) view.findViewById(R.id.tv_start_time);
        holder.tvEnd = (TextView) view.findViewById(R.id.tv_end_time);
        holder.tvName = (TextView) view.findViewById(R.id.tv_name);
        holder.tvProgress = (TextView) view.findViewById(R.id.tv_learning_item);
        return holder;
    }

    public static <T> void bindViewHolder(final Activity activity,
                                          BaseViewHolder holder, final int position, final T info) {
        final Holder viewHolder = (Holder) holder;
        Reservation item = (Reservation) info;
        viewHolder.rootView.setOnClickListener(new MyOnClickListener(activity, item));
        if (item.userid != null) {
            viewHolder.tvName.setText(item.userid.name);
            if (item.userid.headportrait != null && !TextUtils.isEmpty(item.userid.headportrait.originalpic)) {
                //TODO
//                PicassoUtil.loadImage(activity, viewHolder.ivAvatar, item.userid.headportrait.originalpic, R.dimen.avatar_size, R.dimen.avatar_size, false, R.mipmap.ic_avatar_small);
                UILHelper.loadImage(viewHolder.ivAvatar, item.userid.headportrait.originalpic, false, R.mipmap.ic_avatar_small);
            } else {
                viewHolder.ivAvatar.setImageResource(R.mipmap.ic_avatar_small);
            }
        } else {
            viewHolder.tvName.setText(null);
            viewHolder.ivAvatar.setImageResource(R.mipmap.ic_avatar_small);
        }
        viewHolder.tvProgress.setText(item.courseprocessdesc);
        viewHolder.tvBegin.setText(item.begintime);
        viewHolder.tvEnd.setText(item.endtime);
    }

    static class Holder extends BaseViewHolder {

        private View rootView;
        private TextView tvBegin, tvEnd;
        private TextView tvName, tvProgress;
        private ImageView ivAvatar;
        public Holder(View itemView) {
            super(itemView);
        }
    }

    static class MyOnClickListener implements View.OnClickListener {
        private Activity activity;
        private Reservation item;
        public MyOnClickListener(Activity act, Reservation item) {
            this.activity = act;
            this.item = item;
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(activity, DetailReservationActivity.class);
            intent.putExtra(Constants.DETAIL, item);
            activity.startActivity(intent);
        }
    }
}
