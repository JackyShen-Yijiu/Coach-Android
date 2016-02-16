package com.blackcat.coach.widgets;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.blackcat.coach.R;
import com.blackcat.coach.models.CoachCourseVO;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.UTC2LOC;


@SuppressLint({ "InflateParams", "SimpleDateFormat" })
public class AppointmentCarTimeLayout extends LinearLayout implements
		OnCheckedChangeListener {

	public static final int selected = 1;
	public static final int noSelected = 2;
	public static final int NOT_ENABLE = 3;
	public static final int over = 0;
	// over 对应多种
	public static final int timeout = 0;
	public static final int has = 1;
	public static final int other = 2;

	public TextView startTimeTv, endTimeTv, countTv, endTv;

	public CheckBox ck;

	public RadioButton rb;

	private TimeLayoutSelectedChangeListener selectedListener;
	private CoachCourseVO coachCourseVO;

	private int type = 0;

	public interface TimeLayoutSelectedChangeListener {
		public void onTimeLayoutSelectedChange(AppointmentCarTimeLayout layout,
											   CoachCourseVO coachCourseVO, boolean selected);
	}

	public void setSelectedChangeListener(
			TimeLayoutSelectedChangeListener selectedListener) {
		this.selectedListener = selectedListener;
	}

	public AppointmentCarTimeLayout(Context context) {
		super(context);
		init(context);
	}

	public AppointmentCarTimeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AppointmentCarTimeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public void setType(int type){
		LogUtil.print(type +"coach----begin>--type::"+type);
		this.type = type;
	}

	public void setOver(boolean isOver, int type) {
		LogUtil.print("Enable--->"+ "type-->setOver" + type);
		ck.setEnabled(!isOver);
		if (isOver) {
			setTextColor(over, type);
		} else {
			setTextColor(noSelected, type);
		}
	}

	public void setVaule(CoachCourseVO coachCourseVO) {
		this.coachCourseVO = coachCourseVO;
		if (coachCourseVO == null) {
			ck.setOnCheckedChangeListener(null);
			ck.setChecked(false);
			ck.setOnCheckedChangeListener(this);
			startTimeTv.setText("暂无");
			endTimeTv.setText("可约0人");
			endTv.setText("已约0人");
			countTv.setText("签到0人");

//			if(type == 1){
//				startTimeTv.setText("暂无");
//				endTimeTv.setText("可约0人");
//				endTv.setText("");
//				countTv.setText("签到0人");
//			}
			setOver(true, other);
			return;
		}
		String beginTime = coachCourseVO.getCoursetime().getBegintime();
		beginTime = beginTime.substring(0, beginTime.lastIndexOf(":"));
		startTimeTv.setText(beginTime);

		if(type == 1){
			String endTime = coachCourseVO.getCoursetime().getEndtime();
			endTime = endTime.substring(0, endTime.lastIndexOf(":"));
			endTimeTv.setText(endTime+"结束");
			endTv.setText("");
			int temp = Integer.parseInt(coachCourseVO.getCoursestudentcount())  - Integer.parseInt(coachCourseVO.getSelectedstudentcount());
			countTv.setText("剩余"+temp+"个名额");
		}else{
			endTimeTv.setText("可约"+coachCourseVO.getCoursestudentcount()+"人");
			endTv.setText("已约"+coachCourseVO.getSelectedstudentcount()+"人");
			countTv.setText("签到"+coachCourseVO.signinstudentcount+"人");
		}
		LogUtil.print(type +"coach----begin>"+coachCourseVO.getCoursetime().getBegintime()+"Sign--000>"+coachCourseVO.getCoursestudentcount()+"111-->"+coachCourseVO.getSelectedstudentcount());


//		String endTime = coachCourseVO.getCoursetime().getEndtime();
//		endTime = endTime.substring(0, endTime.lastIndexOf(":"));
//		endTimeTv.setText(endTime);

//		if (Arrays.asList(coachCourseVO.getCourseuser()).contains(
//				BlackCatApplication.getInstance().userVO.getUserid())) {
//			// 此预约是否已经预约
//			countTv.setText("您已预约");
//			setOver(true, has);
//		} else {
		if(type == 1){
			try {
				if (beginTime.length() == 4)
					beginTime = "0" + beginTime;
				Date date = new Date();
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm");
//				format.parse(UTC2LOC.instance.)
				long courseTime = format.parse(
						UTC2LOC.instance.getDate(
								coachCourseVO.getCoursebegintime(),
								"yyyy-MM-dd") + " " + beginTime).getTime();
				long curTime = date.getTime();
				if (curTime > courseTime) {
					// 此预约是否已过当前时间
					endTimeTv.setText("已过时");
					LogUtil.print("Enable--->" +beginTime+ "type-->" + type);
					setEnableM(false);
				}else{
					setEnableM(true);
				}
				LogUtil.print("Enable--->" +beginTime+ "type--end>" + type);

			}
			catch(Exception e){

			}
		}

//				else {
//					int totalCount = Integer.parseInt(coachCourseVO
//							.getCoursestudentcount());
//					int selectCount = Integer.parseInt(coachCourseVO
//							.getSelectedstudentcount());
//					int sub = totalCount - selectCount;
//					countTv.setText("剩余" + sub + "个名额");
//					setOver(sub == 0 ? true : false, other);
//				}
//
//			} catch (Exception e) {
//				// 没有数据的时候 课时列表显示
//				startTimeTv.setText("暂无");
//				endTimeTv.setText("暂无(请联系其他教练)");
//				countTv.setText("剩余0个名额");
//				setOver(true, other);
//				e.printStackTrace();
//			}
//		}

	}

	private void init(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.appointmentcar_time_layout, null);

		startTimeTv = (TextView) view
				.findViewById(R.id.appointment_car_starttime_tv);
		//可约1人
		endTimeTv = (TextView) view
				.findViewById(R.id.appointment_car_endtime_tv);
		//签到0 人
		countTv = (TextView) view.findViewById(R.id.appointment_car_count_tv);
		//已约 4 人
		endTv = (TextView) view.findViewById(R.id.appointment_car_end_tv);
		ck = (CheckBox) view.findViewById(R.id.appointment_car_ck);

		rb = (RadioButton) view.findViewById(R.id.appointment_car_rb);

		rb.setOnCheckedChangeListener(this);

		ck.setOnCheckedChangeListener(this);

		addView(view);
	}

	private void setTextColor(int style, int reason) {
		LogUtil.print("Enable--->"+ "type-->" + style);
		if (style == over) {
			if (reason == has) {
				startTimeTv.setTextColor(Color.parseColor("#ff6633"));
				endTimeTv.setTextColor(Color.parseColor("#ff6633"));
				endTv.setTextColor(Color.parseColor("#ff6633"));
				countTv.setTextColor(getResources().getColor(
						R.color.app_bg));
			} else {
				startTimeTv.setTextColor(Color.parseColor("#cccccc"));
				endTimeTv.setTextColor(Color.parseColor("#cccccc"));
				countTv.setTextColor(Color.parseColor("#cccccc"));
				endTv.setTextColor(Color.parseColor("#cccccc"));
			}
		} else if (style == selected) {
			startTimeTv.setTextColor(getResources().getColorStateList(
					R.color.blue));
			endTimeTv.setTextColor(getResources().getColorStateList(
					R.color.blue));
			endTv.setTextColor(getResources().getColorStateList(
					R.color.blue));
			countTv.setTextColor(getResources().getColorStateList(
					R.color.blue));
		} else if (style == noSelected) {
//			startTimeTv.setTextColor(Color.parseColor("#333333"));
//			endTimeTv.setTextColor(Color.parseColor("#333333"));
//			endTv.setTextColor(Color.parseColor("#333333"));
//			countTv.setTextColor(Color.parseColor("#999999"));
			startTimeTv.setTextColor(getResources().getColorStateList(
					R.color.text_333));
			endTimeTv.setTextColor(getResources().getColorStateList(
					R.color.text_333));
			endTv.setTextColor(getResources().getColorStateList(
					R.color.text_333));
			countTv.setTextColor(getResources().getColorStateList(
					R.color.text_999));
		}else if(style == NOT_ENABLE){
			startTimeTv.setTextColor(getResources().getColorStateList(
					R.color.text_999));
			endTimeTv.setTextColor(getResources().getColorStateList(
					R.color.text_999));
			endTv.setTextColor(getResources().getColorStateList(
					R.color.text_999));
			countTv.setTextColor(getResources().getColorStateList(
					R.color.text_999));
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		if (isChecked &&type == 1) {
			setTextColor(selected, other);
		} else if(type == 1){
			setTextColor(noSelected, other);
		}
		selectedListener.onTimeLayoutSelectedChange(this, coachCourseVO,
				isChecked);
	}

	public void setCheckBoxState(boolean isChecked) {
		LogUtil.print("Enable--->"+ "type-->--setCheckBoxState" );
		ck.setChecked(isChecked);
		if(type == 1 && isChecked){
			setTextColor(selected, other);
		}else if(type == 1 && !isChecked){
//			setTextColor(noSelected, other);
		}
		setTextColor(noSelected, other);

	}

	public void setEnableM(boolean isEnable) {

		ck.setClickable(isEnable);
		if(type == 1 && isEnable){
			setTextColor(noSelected, other);
		}else if(type == 1 && !isEnable){
			setTextColor(NOT_ENABLE, other);
		}

	}
}
