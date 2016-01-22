package com.blackcat.coach.lib.calendar.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.LinearLayout;

/**
 * 
 * TODO<用于生成日历展示的GridView布局>
 * 
 * @author ZhuZiQiang
 * @data: 2014-3-29 下午1:57:53
 * @version: V1.0
 */
public class CalendarGridView extends GridView {

	/**
	 * 当前操作的上下文对象
	 */
	private Context mContext;

	/**
	 * CalendarGridView 构造器
	 * 
	 * @param context
	 *            当前操作的上下文对象
	 */
	public CalendarGridView(Context context) {
		super(context);
		mContext = context;

		setGirdView();
	}

	/**
	 * 初始化gridView 控件的布局
	 */
	private void setGirdView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

		setLayoutParams(params);
		setNumColumns(7);// 设置每行列数
		setGravity(Gravity.CENTER_VERTICAL);// 位置居中
		setVerticalSpacing(0);// 垂直间隔
		setHorizontalSpacing(0);// 水平间隔
		setSelector(android.R.color.transparent);
		// setBackgroundColor(getResources().getColor(R.color.calendar_background));

		WindowManager windowManager = ((Activity) mContext).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int i = display.getWidth() / 7;
		int j = display.getWidth() - (i * 7);
		int x = j / 2;
		setPadding(x, 0, 0, 0);// 居中
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
