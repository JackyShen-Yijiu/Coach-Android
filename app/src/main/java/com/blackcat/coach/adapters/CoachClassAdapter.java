package com.blackcat.coach.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.blackcat.coach.R;
import com.blackcat.coach.models.CoachClass;

import java.util.List;

/**
 * Created by wangxiaoqing02 on 2015/10/28.
 */
public class CoachClassAdapter extends BaseAdapter {

    private List<CoachClass> mCoachClassList;
    private LayoutInflater mInflater;
    private Context mContext;
    public CoachClassAdapter(Context context, List<CoachClass> coachClassList) {
        mCoachClassList = coachClassList;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);

    }
    public int getCount() {

        return mCoachClassList == null ? 0 : mCoachClassList.size();
    }

    public Object getItem(int position) {

        if (mCoachClassList != null && position < mCoachClassList.size()) {
            return mCoachClassList.get(position);
        }
        return null;
    }

    public long getItemId(int position) {

        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        CoachClassHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_class, null);
            holder = new CoachClassHolder();
            holder.cbClass = (CheckBox)convertView.findViewById(R.id.cb_class);
            holder.cbClass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    CoachClass coachClass = mCoachClassList.get(position);
                    coachClass.SetChecked(isChecked);
                }
            });
            convertView.setTag(holder);
        }
        else {
           holder = (CoachClassHolder)convertView.getTag();
        }

        CoachClass coachClass = mCoachClassList.get(position);
        holder.cbClass.setText(coachClass.getClassName());
        holder.cbClass.setChecked(coachClass.isChecked());

        return convertView;
    }

    public static class CoachClassHolder {
        CheckBox cbClass;
    }
}
