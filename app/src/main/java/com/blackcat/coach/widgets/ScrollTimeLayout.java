package com.blackcat.coach.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blackcat.coach.models.CoachCourseVO;
import com.blackcat.coach.utils.LogUtil;


@SuppressLint("NewApi")
public class ScrollTimeLayout extends LinearLayout implements
		AppointmentCarTimeLayout.TimeLayoutSelectedChangeListener, View.OnClickListener {

	private Context context;
	private int column = 4;
	private Comparator<Object> courseComp;
	// 用户选择时间段的课程
	private List<CoachCourseVO> selectCourseList = new ArrayList<CoachCourseVO>();
	private int screenWidth;
	private boolean isTimeBlockCon;
	/**0 单选，， 1 多选，用来预约 报名*/
	private int  type = 0;

	public ScrollTimeLayout(Context context) {
		super(context);
		init(context);
	}

	public ScrollTimeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ScrollTimeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	/**
	 * 设置 类型
	 * @param type
	 */
	public void setType(int type){
		this.type = type;
	}

	private void init(Context context) {
		this.context = context;
		screenWidth = getResources().getDisplayMetrics().widthPixels;
		setLayoutDirection(LinearLayout.VERTICAL);
		courseComp = new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				CoachCourseVO s1 = (CoachCourseVO) o1;
				CoachCourseVO s2 = (CoachCourseVO) o2;
				try {
					int c1 = Integer.parseInt(s1.getCoursetime().getTimeid());
					int c2 = Integer.parseInt(s2.getCoursetime().getTimeid());
					if (c1 < c2)
						return -1;
					else if (c1 == c2)
						return 0;
					else if (c1 > c2)
						return 1;
				} catch (Exception e) {

				}
				return 0;
			}
		};
	}

	public boolean isTimeBlockCon() {
		return isTimeBlockCon;
	}

	public List<CoachCourseVO> getSelectCourseList() {
		return selectCourseList;
	}

	public void setSelectCourseList(List<CoachCourseVO> selectCourseList) {
		this.selectCourseList = selectCourseList;
	}

	public void setColumn(int size) {
		this.column = size;
	}

	public void clearData() {
		removeAllViews();
		selectCourseList = new ArrayList<CoachCourseVO>();
	}

	public void setData(List<CoachCourseVO> list, float aspect) {

		if (list == null || list.size() == 0) {
			return;
		}

		Collections.sort(list, courseComp);
		// 最后一行的个数
		int lastConut = list.size() % column;
		// 行数
		int row = list.size() / column;
		if (lastConut > 0) {
			row++;
		}

		LayoutParams vertialDeviderParams = new LayoutParams(1,
				LayoutParams.MATCH_PARENT);
		LayoutParams horitalDeviderParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, 1);

		LayoutParams timeParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, (int) (screenWidth
						/ aspect / column), 1);

		LayoutParams rowParams = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		rowParams.setLayoutDirection(LinearLayout.HORIZONTAL);

		for (int i = 0; i < row - 1; i++) {
			// 此时每行有column个
			LinearLayout innerLayout = new LinearLayout(context);
			addView(innerLayout, rowParams);

			for (int j = 0; j < column; j++) {
				AppointmentCarTimeLayout timeLayout = new AppointmentCarTimeLayout(
						context);
				timeLayout.setSelectedChangeListener(this);
				timeLayout.setOnClickListener(this);
				timeLayout.setType(type);
				// 参数
				timeLayout.setVaule(list.get(i * column + j));

				if(type == 1){//预约报名，设置不可以点击
					notClick(timeLayout,list.get(i * column + j));
				}

				innerLayout.addView(timeLayout, timeParams);
				if (j < column - 1) {
					ImageView devider = new ImageView(context);
					devider.setBackgroundColor(Color.parseColor("#cccccc"));
					innerLayout.addView(devider, vertialDeviderParams);
				}
				LogUtil.print(i+"row---column"+j);
			}

			ImageView devider = new ImageView(context);
			devider.setBackgroundColor(Color.parseColor("#cccccc"));
			addView(devider, horitalDeviderParams);
			LogUtil.print(i+"row---");
		}

		LinearLayout innerLayout = new LinearLayout(context);
		addView(innerLayout, rowParams);

		lastConut = lastConut == 0 ? column : lastConut;
		for (int i = 0; i < lastConut; i++) {
			AppointmentCarTimeLayout timeLayout = new AppointmentCarTimeLayout(
					context);
			timeLayout.setSelectedChangeListener(this);

			// 参数
			timeLayout.setVaule(list.get((row - 1) * column + i));
			innerLayout.addView(timeLayout, timeParams);
			if (i != column - 1) {
				ImageView devider = new ImageView(context);
				devider.setBackgroundColor(Color.parseColor("#cccccc"));
				innerLayout.addView(devider, vertialDeviderParams);
			}
		}

		if (row == 1) {
			// 加横线
			ImageView devider = new ImageView(context);
			devider.setBackgroundColor(Color.parseColor("#cccccc"));
			addView(devider, horitalDeviderParams);
		}

	}

	/**
	 * 设置不可以点击
	 * @param timeLayout
	 * @param coach
	 */
	private void  notClick(AppointmentCarTimeLayout timeLayout,CoachCourseVO coach){
		if(coach.getSelectedstudentcount().equals(coach.getCoursestudentcount())){
			timeLayout.setEnabled(false);
		}else{
			timeLayout.setEnabled(true);
		}
	}

	private String currentId = "";
	/***上一次 选中的布局*/
	private AppointmentCarTimeLayout lastLayout;

	@Override
	public void onTimeLayoutSelectedChange(AppointmentCarTimeLayout layout,
			CoachCourseVO coachCourseVO, boolean selected) {
		if (coachCourseVO == null) {
			return;
		}
		if(type==1){
			doAppoint(layout,coachCourseVO,selected);
			return ;
		}

		LogUtil.print(selected + "layout--begintime->" + currentId + "id-->" + coachCourseVO.getCoursetime().getBegintime());

//		if(coa)
		//获取当前选择的项
		if(!currentId.equals(coachCourseVO.get_id()) && !currentId.equals("") ){//不一样的,不是第一次
			lastLayout.setCheckBoxState(false);
		}else{

		}
		currentId = coachCourseVO.get_id();
		lastLayout = layout;

//		if (selected) {
//			selectCourseList.add(coachCourseVO);
//		} else {
//			selectCourseList.remove(coachCourseVO);
//		}
		onTimeLayoutSelectedListener.TimeLayoutSelectedListener(coachCourseVO,selected);
//		isTimeBlockCon = checkTimeBlockCon();
//		if (!isTimeBlockCon) {
//			CustomDialog dialog = new CustomDialog(context,
//					CustomDialog.APPOINTMENT_TIME_ERROR);
//			dialog.show();
//			// 选择不连续，给出提示后，就取消该项选择
//			layout.setCheckBoxState(true);
//		}
	}

	/***
	 * 预约
	 */
	private void doAppoint(AppointmentCarTimeLayout layout,
						   CoachCourseVO coachCourseVO, boolean selected){
		if (selected) {
//			Toast.makeText(context, "时间不能超过", Toast.LENGTH_SHORT).show();
			selectCourseList.add(coachCourseVO);
		} else {
			selectCourseList.remove(coachCourseVO);
		}
			onTimeLayoutSelectedListener.TimeLayoutSelectedListener(coachCourseVO,selected);
		isTimeBlockCon = checkTimeBlockCon();
		LogUtil.print("time--conn"+isTimeBlockCon);
		if (!isTimeBlockCon) {
//			CustomDialog dialog = new CustomDialog(context,
//					CustomDialog.APPOINTMENT_TIME_ERROR);
//			dialog.show();
			Toast.makeText(context, "时间不连续,请选择连续的时间段", Toast.LENGTH_SHORT).show();
			// 选择不连续，给出提示后，就取消该项选择
			layout.setCheckBoxState(false);
			selectCourseList.remove(coachCourseVO);
		}
	}

	private boolean checkTimeBlockCon() {
		Collections.sort(selectCourseList, courseComp);
		int size = selectCourseList.size() - 1;
		for (int i = 0; i < size; i++) {
			if (Integer.parseInt(selectCourseList.get(i).getCoursetime()
					.getTimeid()) + 1 != Integer.parseInt(selectCourseList
					.get(i + 1).getCoursetime().getTimeid())) {
				return false;
			}
		}
		return true;
	}



	@Override
	public void onClick(View view) {
		LogUtil.print("click");
	}

	public interface OnTimeLayoutSelectedListener {
		void TimeLayoutSelectedListener(CoachCourseVO coachCourseVO,boolean selected);
	}

	private OnTimeLayoutSelectedListener onTimeLayoutSelectedListener;

	public void setOnTimeLayoutSelectedListener(
			OnTimeLayoutSelectedListener onTimeLayoutSelectedListener) {
		this.onTimeLayoutSelectedListener = onTimeLayoutSelectedListener;
	}
}
