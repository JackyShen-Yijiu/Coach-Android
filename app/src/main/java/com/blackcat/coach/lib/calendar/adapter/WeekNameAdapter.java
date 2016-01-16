package com.blackcat.coach.lib.calendar.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.blackcat.coach.R;


public class WeekNameAdapter extends BaseAdapter {

	private Context mContext;

	private List<String> mData;

	public WeekNameAdapter(Context context, List<String> data) {
		this.mContext = context;
		this.mData = data;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		TextView weekName = new TextView(mContext);
		weekName.setTextColor(mContext.getResources().getColor(
				R.color.caldroid_333));
		weekName.setTextSize(12);
		weekName.setText(mData.get(position));
		return weekName;
	}
}
