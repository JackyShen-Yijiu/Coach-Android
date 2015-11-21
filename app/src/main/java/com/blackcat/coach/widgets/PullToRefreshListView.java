package com.blackcat.coach.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;

import com.blackcat.coach.R;


public class PullToRefreshListView extends LoadMoreListView implements
		PullToRefreshView.PullableListenner {

	private int mPinnedViewId;
	private View mPinnedView;
	public PullToRefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initViews(context, attrs);
	}

	public PullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews(context, attrs);
	}

	public PullToRefreshListView(Context context) {
		super(context);
	}
	
	private void initViews(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshListView);
		mPinnedViewId = a.getResourceId(R.styleable.PullToRefreshListView_pinnedViewId, 0);
		a.recycle();
		
		mPinnedLocation = new int[2];
	}
	
	public void addHeaderView(View v) {
		super.addHeaderView(v);
		if (mPinnedViewId != 0) {
			mPinnedView = v.findViewById(mPinnedViewId);
		}
	};
	
	/**
	 * 是否可以下啦
	 * 
	 * @return
	 */
	@Override
	public boolean isPullDownAble() {
		boolean isPullDownAble = false;
		int firstP = getFirstVisiblePosition();
		// 判断是否可以下啦
		if (firstP == 0) {
			View child0 = getChildAt(0);
			if (child0 != null) {
				int firstTop = child0.getTop();
				if (firstTop >= 0) {
					isPullDownAble = true;
				}
			} else {
				isPullDownAble = true;
			}
		}
		return isPullDownAble;
	}
	
	private int[] mPinnedLocation;
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		
		if (mListener != null && mPinnedView != null && mPinnedLocation != null) {
			mPinnedView.getLocationOnScreen(mPinnedLocation);
			mListener.onPinnedViewScroll(mPinnedLocation, view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
//		System.out.println("mListView.getFirstVisiblePosition() = " + getFirstVisiblePosition());
//		System.out.println("mListView.getChildCount() = " + getChildCount());
	}
	
	private OnPinnedViewScrollListener mListener;
	
	public void setOnPinnedViewScrollListener(OnPinnedViewScrollListener listener) {
		this.mListener = listener;
	}
	
	public interface OnPinnedViewScrollListener {
		public void onPinnedViewScroll(int[] position, AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
	}
}
