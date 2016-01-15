package com.blackcat.coach.adapters.rows;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blackcat.coach.R;
import com.blackcat.coach.adapters.BaseViewHolder;
import com.blackcat.coach.models.OrderMsg;
import com.blackcat.coach.models.SystemMsg;

/**
 * Created by aa on 2016/1/14.
 */
public class RowOrderMsg {
    public static BaseViewHolder createViewHolder(ViewGroup parent, Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_msg, parent, false);
        Holder holder = new Holder(view);
        holder.tv_time =(TextView) view.findViewById(R.id.order_tv_time);
        holder.tv_order_data =(TextView) view.findViewById(R.id.tv_order_data);
        holder.tv_title = (TextView) view.findViewById(R.id.tv_title_money);
        holder.tv_content = (TextView) view.findViewById(R.id.tv_content);
        return holder;
    }

    public static <T> void bindViewHolder(final Activity activity,
                                          BaseViewHolder holder, final int position, final T info) {
        final Holder viewHolder = (Holder) holder;
        OrderMsg item = (OrderMsg) info;
        viewHolder.tv_time.setText(item.createtime);
        viewHolder.tv_order_data.setText(item.createtime);
        viewHolder.tv_content.setText(item.description);
        viewHolder.tv_title.setText(item.title);

    }

    static class Holder extends BaseViewHolder {

        private TextView tv_time;
        private TextView tv_content;
        private TextView tv_order_data;
        private TextView tv_title;

        public Holder(View itemView) {
            super(itemView);
        }
    }
}
