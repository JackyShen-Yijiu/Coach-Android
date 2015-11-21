package com.blackcat.coach.adapters.rows;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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


public class RowReservation {

	public static BaseViewHolder createViewHolder(ViewGroup parent, Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.row_reservation, parent, false);
		Holder holder = new Holder(view);
		holder.rootView = view.findViewById(R.id.rootView);
		holder.tvName = (TextView) view.findViewById(R.id.tv_name);
		holder.tvSchoolName = (TextView) view.findViewById(R.id.tv_school_name);
		holder.tvTime = (TextView) view.findViewById(R.id.tv_date);
		holder.tvStatus = (TextView) view.findViewById(R.id.tv_status);
		holder.tvPickPlace = (TextView) view.findViewById(R.id.tv_place);
		holder.tvTrainField = (TextView) view.findViewById(R.id.tv_train_field);
		holder.ivAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
		holder.statusLine = view.findViewById(R.id.status_line);
		return holder;
	}

	public static  <T> void bindViewHolder(final Activity activity,
			BaseViewHolder holder, final int position, final T info) {
		final Holder viewHolder = (Holder) holder;
		Reservation item = (Reservation) info;
		item.pos = position;
		viewHolder.rootView.setOnClickListener(new MyOnClickListener(activity, item));
		if (!TextUtils.isEmpty(item.courseprocessdesc)) {
			viewHolder.tvSchoolName.setText(item.courseprocessdesc);
		} else {
			viewHolder.tvSchoolName.setText("");
		}
		if (item.userid != null) {
			viewHolder.tvName.setText(item.userid.name);
//			if (item.userid.applyschoolinfo != null) {
//				viewHolder.tvSchoolName.setText(item.userid.applyschoolinfo.name);
//			} else {
//				viewHolder.tvSchoolName.setText("");
//			}
			if (item.userid.headportrait != null && !TextUtils.isEmpty(item.userid.headportrait.originalpic)) {
				//TODO
//				PicassoUtil.loadImage(activity, viewHolder.ivAvatar, item.userid.headportrait.originalpic, R.dimen.avatar_size, R.dimen.avatar_size, false, R.mipmap.ic_avatar_small);
				UILHelper.loadImage(viewHolder.ivAvatar, item.userid.headportrait.originalpic, false, R.mipmap.ic_avatar_small);
			} else {
				viewHolder.ivAvatar.setImageResource(R.mipmap.ic_avatar_small);
			}
		} else {
			viewHolder.tvName.setText("");
			viewHolder.tvSchoolName.setText("");
			viewHolder.tvSchoolName.setText("");
			viewHolder.ivAvatar.setImageResource(R.mipmap.ic_avatar_small);
		}
		Resources res = activity.getResources();
		if (item.trainfieldlinfo != null) {
			viewHolder.tvTrainField.setText(res.getString(R.string.str_train_field, item.trainfieldlinfo.name));
		} else {
			viewHolder.tvTrainField.setText("");
		}
		viewHolder.tvPickPlace.setText(res.getString(R.string.str_pick_place, item.shuttleaddress));
		viewHolder.tvTime.setText(item.classdatetimedesc);
		viewHolder.tvStatus.setText(item.getReservationStatusDescId());

		int id = R.color.text_999;
		switch (item.getReservationstate()) {
			case APPLYING:
				id = R.color.reservation_status_applying;
				break;

			case APPLYCANCEL:
			case APPLYREFUSE:
				id = R.color.reservation_status_canceled;
				//已取消
				break;
			case FINISH:
				//已完成
				id = R.color.reservation_status_finish;
				break;

			case APPLYCONFIRM:
				id = R.color.reservation_status_comfirmed;
				break;

			case UNCONFIRMFINISH:
				id = R.color.reservation_status_uncomform;
				break;

			case UNCOMMENTS:
				id = R.color.reservation_status_uncomment;
				break;
		}
		int color = activity.getResources().getColor(id);
		viewHolder.statusLine.setBackgroundColor(color);
		viewHolder.tvStatus.setTextColor(color);
	}

	static class Holder extends BaseViewHolder {

		public TextView tvTime, tvStatus;
		public TextView tvPickPlace, tvTrainField;
		public TextView tvName;
		public TextView tvSchoolName;
		public ImageView ivAvatar;
		public View statusLine;
		public View rootView;

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
