package com.blackcat.coach.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.blackcat.coach.R;
import com.blackcat.coach.utils.BaseUtils;
import com.blackcat.coach.utils.PrefConstants;
import com.blackcat.coach.utils.PrefUtils;


public class PullToRefreshView extends RelativeLayout {
	/**
	 * 当前状态
	 */
	private int state = NORMAL_STATE;
	// 正常状态
	public static final int NORMAL_STATE = -1;
	// 下拉刷新
	public static final int PULL_TO_FRESH = 0;
	// 松开刷新
	public static final int RELEASE_TO_FRESH = 1;
	// 加载中
	public static final int LOADING = 2;

	private int duration = 200;
	private Scroller mScroller;
	private View mPullRefreshHeader;
	private PullableListenner mPullRefreshView;
	private TextView mPullRefreshHeaderText;
	private TextView mPullRefreshHeaderTime;
	private ImageView mIvArrow;
	private ImageView mIvAnimView;
	
	private static long DEF_REFRESH_TIME = -1L; 
	private long mLastRefreshTime = DEF_REFRESH_TIME;
	private String mChannelId;

	/**
	 * 刷新栏高度
	 */
	private int mHeaderHeight;


	private OnRefreshListener mRefreshListener;

	/**
	 * 设置是否可以下拉
	 */
	private boolean mIsDownable = true;

	private boolean mScrolling;
	
	/**
	 * 0：下拉列表ListView 1：GridView 2：WebView
	 */
	private int mPullViewType;

	public PullToRefreshView(Context context) {
		super(context);
		initView(null, null);
	}

	public PullToRefreshView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context, attrs);
	}

	public PullToRefreshView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context, attrs);
		
	}
	
	public void setChannelId(String channelId) {
		this.mChannelId = channelId;
	}
	
	public String getChannelId() {
		return this.mChannelId;
	}

	private void initView(Context context, AttributeSet attrs) {
		if (attrs != null && context != null) {
			TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshView);
			mPullViewType = typedArray.getInt(R.styleable.PullToRefreshView_pullViewType, 0);
			typedArray.recycle();
		}
		Interpolator interpolator = new AccelerateInterpolator();
		mScroller = new Scroller(getContext(), interpolator);
	}

	@Override
	protected void onFinishInflate() {
		mPullRefreshHeader = findViewById(R.id.fresh_header);
		if (mPullRefreshHeader != null) {
			View text = mPullRefreshHeader.findViewById(R.id.pull_fresh_header_text);
			if (text instanceof TextView) {
				mPullRefreshHeaderText = (TextView) text;
			}
			View time = mPullRefreshHeader.findViewById(R.id.pull_fresh_header_time);
			if (time instanceof TextView) {
				mPullRefreshHeaderTime = (TextView) time;
			}
			View arrow = mPullRefreshHeader.findViewById(R.id.pull_fresh_header_arrow);
			if (arrow instanceof ImageView) {
				mIvArrow = (ImageView) arrow;
			}

			View anim = mPullRefreshHeader.findViewById(R.id.pull_fresh_header_anim);
			if (anim instanceof ImageView) {
				mIvAnimView = (ImageView) anim;
			}	
		} else {
			mIsDownable = false;
		}
		
		int res_id = R.id.inner_list;
		switch (mPullViewType) {
		case 2:
			// res_id = R.id.wcp_webview;
			break;
		default:
			// res_id = R.id.news_list;
			break;
		}
		mPullRefreshView = (PullableListenner) findViewById(res_id);

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (mPullRefreshHeader != null) {
			mHeaderHeight = mPullRefreshHeader.getMeasuredHeight();
			mPullRefreshHeader.layout(0, -mHeaderHeight, mPullRefreshHeader.getWidth(), 0);
		}
	}

	/**
	 * 当前的 Y滚动值
	 */
	private int mCurrentScrollY;

	/**
	 * 上次的event Y位置
	 */
	private float mLastY;
	private VelocityTracker mVelocityTracker;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean isHandled = super.onInterceptTouchEvent(ev);
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		float y = ev.getRawY();
		int action = ev.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			mLastY = y;
		}

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (state != LOADING) {
				if (mPullRefreshHeader != null) {
					mPullRefreshHeaderText.setText(getResources().getString(R.string.pull_fresh_header_down));
					mIvAnimView.setVisibility(View.INVISIBLE);
					mIvArrow.setVisibility(View.VISIBLE);
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000);
			float velocityY = velocityTracker.getYVelocity();
			float velocityX = velocityTracker.getXVelocity();
			float ratio = velocityX / velocityY;
			if (state == LOADING && mCurrentScrollY != 0
					&& Math.abs(velocityY) > 5) {
				isHandled = true;
				break;
			}
			if (velocityY > 20 && Math.abs(ratio) < 1) {
				// 网络是否连接
				if (mIsDownable) {
					isHandled = mPullRefreshView.isPullDownAble();
					// 如果正在加载中则不改变当前状态
					if (state != LOADING) {
						state = PULL_TO_FRESH;
						if(mLastRefreshTime == DEF_REFRESH_TIME) {
							mLastRefreshTime = PrefUtils.getLongData(PrefConstants.KEY_LAST_REFRESH_TIME, 0);
						}
						if (mLastRefreshTime != 0) {
							String time = getContext().getString(R.string.pull_fresh_update) + BaseUtils.formatTime(mLastRefreshTime);
							mPullRefreshHeaderTime.setText(time);
							mPullRefreshHeaderTime.setVisibility(View.VISIBLE);
						} else {
							mPullRefreshHeaderTime.setVisibility(View.GONE);
						}
					}
				}
			}

			mLastY = y;
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			break;
		}
		return isHandled;
	}

	private static final int MAX_DY = 50;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float y = event.getRawY();
		int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			mLastY = y;
		}
		// 如果在做动画则不控制view
		if (!mScroller.isFinished()) {
			// 防止自动滚动后 touch跳动
			mLastY = y;
			return true;
		}

		// 下拉
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mIvAnimView.clearAnimation();
			mIvArrow.clearAnimation();
			break;
		case MotionEvent.ACTION_MOVE:
			float dy = y - mLastY;
			if (Math.abs(mCurrentScrollY) > 0) {
				dy = (mHeaderHeight * 3 + mCurrentScrollY) * dy / (mHeaderHeight * 3);
			}
			if (Math.abs(dy) > MAX_DY) {
				dy = dy > 0 ? MAX_DY : -MAX_DY;
			}

			if (mCurrentScrollY > 0) {
				// 防止上拉
				mCurrentScrollY = 0;
			} else {
				// 防止滚动过远
				if (mCurrentScrollY < (-mHeaderHeight) * 3) {
					mCurrentScrollY = (-mHeaderHeight) * 3;
				}
				if (state == NORMAL_STATE) {
					state = PULL_TO_FRESH;
					mPullRefreshHeaderText.setText(getResources().getString(R.string.pull_fresh_header_down));
					mIvAnimView.setVisibility(View.INVISIBLE);
					mIvArrow.setVisibility(View.VISIBLE);
				}
				if (-mCurrentScrollY > mHeaderHeight && state == PULL_TO_FRESH) {
					mPullRefreshHeaderText.setText(getResources().getString(R.string.pull_fresh_header_up));
					state = RELEASE_TO_FRESH;
					mIvArrow.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.rotate_180_clockwise));
				}
//				int base_level = mHeaderHeight / 5;
//				int level = 0;
//				if (base_level != 0) {
//					level = Math.abs(mCurrentScrollY / base_level);
//				}
//				if (level > 7) {
//					level = 7;
//				}
//				mIvArrow.setImageLevel(level);
				if (-mCurrentScrollY <= mHeaderHeight && state == RELEASE_TO_FRESH) {
					mPullRefreshHeaderText.setText(getResources().getString(R.string.pull_fresh_header_down));
					state = PULL_TO_FRESH;
					mIvArrow.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.rotate_180_anti_clockwise));
				}
			}
			if (mCurrentScrollY < 0) {
				final ViewParent parent = getParent();
				if (parent != null) {
					parent.requestDisallowInterceptTouchEvent(true);
				}
			}
			mCurrentScrollY = (int) -dy + mCurrentScrollY;
			if (mCurrentScrollY > 0) {
				// 防止上滚
				mCurrentScrollY = 0;
			}
			scrollTo(0, mCurrentScrollY);
			mLastY = y;
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			ViewParent parent = getParent();
			if (parent != null) {
				parent.requestDisallowInterceptTouchEvent(false);
			}
			switch (state) {
			case RELEASE_TO_FRESH:
				// 松开刷新
				refresh();
				break;
			case LOADING:
				// 如果正在加载中 下拉后滚动回来
				if (-mCurrentScrollY > mHeaderHeight) {
					startScroll(0, -mCurrentScrollY, 0,
							mHeaderHeight + mCurrentScrollY, duration);
					mCurrentScrollY = -mHeaderHeight;
				}
				break;
			case PULL_TO_FRESH:
				// 松开返回初始状态
				startScroll(0, -mCurrentScrollY, 0, mCurrentScrollY, duration);
				mCurrentScrollY = 0;
				break;
			}
		}

		return true;
	}

	private void startScroll(int startX, int startY, int dx, int dy,
			int duration) {
		mScroller.startScroll(startX, startY, dx, dy, duration);
		toInvalidate();
		mScrolling = true;
	}

	/**
	 * 运行下拉刷新接口
	 */
	public void refresh() {
		// 避免多次刷新
		if (state == LOADING) {
			return;
		}

		mPullRefreshHeaderText.setText(R.string.pull_fresh_state_refresh);
		if (mIvAnimView != null) {
		    mIvAnimView.setVisibility(View.VISIBLE);
//			mIvAnimView.setImageResource(R.anim.pull_view_loading_anim);
//			AnimationDrawable animationDrawable = (AnimationDrawable) mIvAnimView.getDrawable();
//			animationDrawable.start();
			mIvAnimView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.rotate_loading_anim));
		}
		mIvArrow.clearAnimation();
		mIvArrow.setVisibility(View.INVISIBLE);
		startScroll(0, -mCurrentScrollY, 0, mHeaderHeight + mCurrentScrollY, duration);
		mCurrentScrollY = -mHeaderHeight;
		state = LOADING;

		if (mRefreshListener != null) {
			mRefreshListener.onRefresh();
		}
		
		if(mLastRefreshTime == DEF_REFRESH_TIME) {
			mLastRefreshTime = PrefUtils.getLongData(PrefConstants.KEY_LAST_REFRESH_TIME, 0);
		}
		if (mLastRefreshTime != 0) {
			String time = getContext().getString(R.string.pull_fresh_update) + BaseUtils.formatTime(mLastRefreshTime);
			mPullRefreshHeaderTime.setText(time);
			mPullRefreshHeaderTime.setVisibility(View.VISIBLE);
		} else {
			mPullRefreshHeaderTime.setVisibility(View.GONE);
		}
	}

	/**
	 * 下拉刷新加载完成
	 */
	public void completeRefresh() {
		state = NORMAL_STATE;
		mLastRefreshTime = DEF_REFRESH_TIME;
		if (mCurrentScrollY == 0) {
			return;
		}
		mScroller.startScroll(0, -mCurrentScrollY, 0, mCurrentScrollY, duration);
		toInvalidate();
		mCurrentScrollY = 0;
		mIvAnimView.clearAnimation();
		mIvArrow.clearAnimation();
	}

	/**
	 * 下拉刷新监听
	 * 
	 * @author Administrator
	 * 
	 */
	public interface OnRefreshListener {
		/**
		 * 下拉刷新监听方法
		 */
		public void onRefresh();

	}

	/**
	 * 设舒心监听器
	 * 
	 * @param refreshListener
	 */
	public void setRefreshListener(OnRefreshListener refreshListener) {
		this.mRefreshListener = refreshListener;
	}

	@Override
	public void computeScroll() {
		if (!mScroller.isFinished()) {
			if (mScroller.computeScrollOffset()) {
				int currX = mScroller.getCurrX();
				int currY = mScroller.getCurrY();
				scrollTo(currX, -currY);
				toInvalidate();
				return;
			}
		}
		// Done with scroll, clean up state.
		completeScroll();
	}

	private void completeScroll() {
		boolean needPopulate = mScrolling;
		if (needPopulate) {
			// Done with scroll, no longer want to cache view drawing.
			// setScrollingCacheEnabled(false);
			mScroller.abortAnimation();
			int oldX = getScrollX();
			int oldY = getScrollY();
			int currX = mScroller.getFinalX();
			int currY = mScroller.getFinalY();
			if (oldX != currX || oldY != currY) {
				scrollTo(currX, -currY);
			}
		}
		mScrolling = false;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void toInvalidate() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			postInvalidateOnAnimation();
		} else {
			postInvalidate();
		}
	}
	
	public int getPullToRefreshViewState() {
		return this.state;
	}

	public PullableListenner getPullToRefreshView() {
		return mPullRefreshView;
	}

	/**
	 * 设置是否可以下拉刷新
	 * 
	 * @param isDownable
	 */
	public void setDownable(boolean isDownable) {
		// 存在下拉头才可以设置
		if (mPullRefreshHeader != null) {
			this.mIsDownable = isDownable;
		}
	}

	public interface PullableListenner {
		/**
		 * @return 是否可以下拉
		 */
		public boolean isPullDownAble();
	}

}
