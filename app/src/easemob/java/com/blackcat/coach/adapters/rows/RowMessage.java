package com.blackcat.coach.adapters.rows;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackcat.coach.R;
import com.blackcat.coach.activities.ChatActivity;
import com.blackcat.coach.adapters.BaseViewHolder;
import com.blackcat.coach.models.Message;


public class RowMessage {

    public static BaseViewHolder createViewHolder(ViewGroup parent, Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_message, parent, false);
        Holder holder = new Holder(view);
        holder.rootView = view.findViewById(R.id.rootView);
        holder.ivAvatar = (ImageView)view.findViewById(R.id.iv_avatar);
        holder.tvName = (TextView)view.findViewById(R.id.tv_name);
        holder.tvDesc = (TextView)view.findViewById(R.id.tv_desc);
        holder.tvTime = (TextView)view.findViewById(R.id.tv_time);

        return holder;
    }

    public static <T> void bindViewHolder(final Activity activity,
                                          BaseViewHolder holder, final int position, final T info) {
        final Holder viewHolder = (Holder) holder;
        final Message message = (Message)info;
        viewHolder.tvName.setText(message.getShowName());
        viewHolder.tvDesc.setText(message.getMessageDesc());
        viewHolder.tvTime.setText(message.getShowTime());
//        viewHolder.rootView.setOnClickListener(new MyOnClickListener(activity));
    }

    static class Holder extends BaseViewHolder {

        private View rootView;
        private ImageView ivAvatar;
        private TextView tvName;
        private TextView tvDesc;
        private TextView tvTime;

        public Holder(View itemView) {
            super(itemView);
        }
    }

    static class MyOnClickListener implements View.OnClickListener {
        private Activity activity;
        public MyOnClickListener(Activity act) {
            this.activity = act;
        }
        @Override
        public void onClick(View v) {
//            Intent intent = new Intent(activity, ChatActivity.class);
//            activity.startActivity(intent);
        }
    }
}
