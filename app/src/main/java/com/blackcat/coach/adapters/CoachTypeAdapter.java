package com.blackcat.coach.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.blackcat.coach.R;

/**
 * Created by pengdonghua on 2016/1/9.
 */
public class CoachTypeAdapter extends BaseAdapter{

    private LayoutInflater mInflater;

    private String[] list = new String[0];

    public CoachTypeAdapter(Context context,String[] list){
        mInflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public Object getItem(int i) {
        return list[0];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_pick_coach_type, null);
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvName.setText(list[i]);

        return convertView;
    }

    static class ViewHolder {
        TextView tvName;

        public ViewHolder(View view){
            tvName = (TextView) view.findViewById(R.id.row_pick_coach_type_tv);
        }

    }
}
