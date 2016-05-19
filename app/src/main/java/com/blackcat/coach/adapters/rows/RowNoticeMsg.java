package com.blackcat.coach.adapters.rows;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackcat.coach.R;
import com.blackcat.coach.activities.WebViewMsg;
import com.blackcat.coach.adapters.BaseViewHolder;
import com.blackcat.coach.models.NoticeMsgVO;
import com.blackcat.coach.models.SystemMsg;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.DateUtil;

/**
 * 公告
 * Created by aa on 2016/1/14.
 */
public class RowNoticeMsg {
    public static BaseViewHolder createViewHolder(ViewGroup parent, Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_notice_msg, parent, false);
        Holder holder = new Holder(view);
        holder.tv_title = (TextView) view.findViewById(R.id.notice_title);
        holder.tv_content = (TextView) view.findViewById(R.id.notice_content);
        holder.tv_date = (TextView) view.findViewById(R.id.notice_time);
        holder.tv_sender = (TextView) view.findViewById(R.id.notice_sender);
        holder.rootView = view.findViewById(R.id.rootView);
        return holder;
    }

    public static <T> void bindViewHolder(final Activity activity,
                                          BaseViewHolder holder, final int position, final T info) {
        final Holder viewHolder = (Holder) holder;

        NoticeMsgVO item = (NoticeMsgVO) info;

//        viewHolder.rootView.setOnClickListener(new MyOnClickListener(activity, item));
        viewHolder.tv_sender.setText("发布者: "+item.name);
//        viewHolder.tv_date.setText(DateUtil.parseTimeFormat(item.createtime,"yyyy/MM/dd"));
        viewHolder.tv_content.setText(item.content);
        viewHolder.tv_title.setText(item.title);

    }

    static class Holder extends BaseViewHolder {

        private TextView tv_content;
        private TextView tv_sender;
        private TextView tv_title;
        private TextView tv_date;
        public View rootView;

        public Holder(View itemView) {
            super(itemView);
        }
    }

    static class MyOnClickListener implements View.OnClickListener {
        private Activity activity;
        private NoticeMsgVO item;
        public MyOnClickListener(Activity act, NoticeMsgVO item) {
            this.activity = act;
            this.item= item;
        }
        @Override
        public void onClick(View v) {
//            Intent intent = new Intent(activity, WebViewMsg.class);
//            intent.putExtra(Constants.DETAIL, item.contenturl);
//            activity.startActivity(intent);
        }
    }




}
