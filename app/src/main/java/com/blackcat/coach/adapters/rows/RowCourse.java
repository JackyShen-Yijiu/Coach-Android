package com.blackcat.coach.adapters.rows;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blackcat.coach.R;
import com.blackcat.coach.adapters.BaseViewHolder;
import com.blackcat.coach.models.Course;
import com.blackcat.coach.models.Vip;
import com.blackcat.coach.widgets.CheckableRelativeLayout;

/**
 * Created by zou on 15/10/17.
 */
public class RowCourse {
    public static BaseViewHolder createViewHolder(ViewGroup parent, Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_course, parent, false);
        Holder holder = new Holder(view);
        holder.rootView = (CheckableRelativeLayout) view.findViewById(R.id.rootView);
        holder.tvName = (TextView) view.findViewById(R.id.tv_course_name);
        holder.tvPrice = (TextView) view.findViewById(R.id.tv_course_price);
        holder.tvPlace = (TextView) view.findViewById(R.id.tv_course_place);
        holder.tvVip = (TextView) view.findViewById(R.id.tv_course_vip);
        return holder;
    }

    public static <T> void bindViewHolder(final Activity mActivity,
                                          BaseViewHolder holder, final int position, final T info) {
        final Holder viewHolder = (Holder) holder;
        Course item = (Course) info;
        viewHolder.tvName.setText(item.classname);
        viewHolder.tvPrice.setText(item.price);
        viewHolder.tvPlace.setText(item.address);
        if (item.is_choose) {
            viewHolder.rootView.setChecked(true);
        } else {
            viewHolder.rootView.setChecked(false);
        }

        if (item.vipserverlist != null && item.vipserverlist.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (Vip vip : item.vipserverlist) {
                sb.append(vip.name).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            viewHolder.tvVip.setText(mActivity.getResources().getString(R.string.str_brackets, sb.toString()));
        } else {
            viewHolder.tvVip.setText(null);
        }
    }

    static class Holder extends BaseViewHolder {

        private CheckableRelativeLayout rootView;
        private TextView tvName, tvPrice, tvPlace, tvVip;
        public Holder(View itemView) {
            super(itemView);
        }
    }
}
