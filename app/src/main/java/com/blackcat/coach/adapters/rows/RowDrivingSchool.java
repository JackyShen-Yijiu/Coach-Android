package com.blackcat.coach.adapters.rows;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blackcat.coach.R;
import com.blackcat.coach.adapters.BaseViewHolder;
import com.blackcat.coach.models.DrivingSchool;

/**
 * Created by zou on 15/10/17.
 */
public class RowDrivingSchool {
    public static BaseViewHolder createViewHolder(ViewGroup parent, Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_school, parent, false);
        Holder holder = new Holder(view);
        holder.tvName = (TextView)view.findViewById(R.id.tv_school);
        return holder;
    }

    public static <T> void bindViewHolder(final Activity mActivity,
                                          BaseViewHolder holder, final int position, final T info) {
        final Holder viewHolder = (Holder) holder;
        DrivingSchool school = (DrivingSchool) info;
        viewHolder.tvName.setText(school.name);
    }

    static class Holder extends BaseViewHolder {
        public TextView tvName;
        public Holder(View itemView) {
            super(itemView);
        }
    }
}
