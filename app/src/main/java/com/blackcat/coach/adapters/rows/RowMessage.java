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
import com.blackcat.coach.activities.ChatActivity;
import com.blackcat.coach.adapters.BaseViewHolder;
import com.blackcat.coach.imgs.UILHelper;
import com.blackcat.coach.models.Message;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.widgets.SelectableRoundedImageView;


public class RowMessage {

    public static BaseViewHolder createViewHolder(ViewGroup parent, Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_message, parent, false);
        Holder holder = new Holder(view);
        holder.rootView = view.findViewById(R.id.rootView);
        holder.ivAvatar = (SelectableRoundedImageView)view.findViewById(R.id.iv_avatar);
        holder.ivAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.ivAvatar.setImageResource(R.mipmap.ic_reservation_avatar);
        holder.ivAvatar.setOval(true);

        holder.tvName = (TextView)view.findViewById(R.id.tv_name);
        holder.tvDesc = (TextView)view.findViewById(R.id.tv_desc);
        holder.tvTime = (TextView)view.findViewById(R.id.tv_time);
        holder.tvCount = (TextView)view.findViewById(R.id.tv_unread_count);
        return holder;
    }

    public static <T> void bindViewHolder(final Activity activity,
                                          BaseViewHolder holder, final int position, final T info) {
        final Holder viewHolder = (Holder) holder;
        final Message message = (Message)info;
        LogUtil.print("tvName=show=>"+message.getShowName()+"tvName-->"+message.getUserName());
        if(message.getShowName()!=null)
            viewHolder.tvName.setText(message.getShowName());
        else if(message.getUserName()!=null)
            viewHolder.tvName.setText(message.getUserName());
        else
            viewHolder.tvName.setText("暂无用户名");
        viewHolder.tvDesc.setText(message.getMessageDesc());
        viewHolder.tvTime.setText(message.getShowTime());
        viewHolder.tvCount.setText(String.valueOf(message.getMsgCount()));
        viewHolder.tvCount.setVisibility(message.getMsgCount()>0?View.VISIBLE:View.GONE);
       // viewHolder.rootView.setOnClickListener(new MyOnClickListener(activity));
        try {
            String avatarUrl = message.getAvatarUrl();
            LogUtil.print("avatarUrl--->"+avatarUrl);
            if (avatarUrl != null && !TextUtils.isEmpty(avatarUrl)) {
                UILHelper.loadImage(viewHolder.ivAvatar, avatarUrl, false, R.mipmap.ic_avatar_small);
            }else{
                viewHolder.ivAvatar.setImageResource(R.mipmap.ic_avatar_small);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    static class Holder extends BaseViewHolder {

        private View rootView;
        private SelectableRoundedImageView ivAvatar;
        private TextView tvName;
        private TextView tvDesc;
        private TextView tvTime;
        private TextView tvCount;
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
            Intent intent = new Intent(activity, ChatActivity.class);
            activity.startActivity(intent);
        }
    }
}
