package com.blackcat.coach.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.blackcat.coach.R;
import com.blackcat.coach.utils.UIUtils;
import com.blackcat.coach.utils.Utils;
import com.blackcat.coach.utils.VolleyUtil;
import com.umeng.analytics.MobclickAgent;

import cn.jpush.android.api.JPushInterface;

public class BaseActivity extends AppCompatActivity {
	protected static final int ACTION_GROUP_ID = 0;
	protected static final int ACTION_MENUITEM_ID0 = 0;
	protected static final int ACTION_MENUITEM_ID1 = 1;

	protected Toolbar mToolBar;
	protected TextView mToolBarTitle;
	protected View mShadow;
	protected int mActionbarSize;
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);


		View v = findViewById(R.id.toolbar);

		if (v != null) {
			mToolBar = (Toolbar) v;
		    setSupportActionBar(mToolBar);
		    mToolBarTitle = (TextView) v.findViewById(R.id.toolbar_title);
		    if (mToolBarTitle != null) {
		        getSupportActionBar().setDisplayShowTitleEnabled(false);
		    }
		}
	}

	public Toolbar configToolBar(int navigationIconId) {
		mToolBar.setNavigationIcon(navigationIconId);
		mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		return mToolBar;
	}
	
	protected void setNavigationIcon(int res) {
		mToolBar.setNavigationIcon(res);
	}

	/*
	 * 已包含ToolBar，并且可以通过Manifest文件设置Title的Activity
	 */
	@Override
	protected void onTitleChanged(CharSequence title, int color) {
	    super.onTitleChanged(title, color);
	    if (mToolBarTitle != null) {
	    	mToolBarTitle.setText(title);
	    }
	}

	protected boolean hasActionbarShadow(){
		return true;
	}
	
	public void showActionbarShadow() {
		if (mShadow != null) {
			mShadow.setVisibility(View.VISIBLE);
		}
	}

	public void hideActionbarShadow() {
		if (mShadow != null) {
			mShadow.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mActionbarSize = getResources().getDimensionPixelSize(R.dimen.actionBarSize);
		if (hasActionbarShadow()) {
			mShadow = new View(this);
			mShadow.setBackgroundResource(R.color.toolbar_shadow);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.WRAP_CONTENT);
			params.topMargin = mActionbarSize + Utils.getStatusBarHeight(this);
			params.height = UIUtils.dip2px(getBaseContext(), (float) 0.5);
			mShadow.setLayoutParams(params);
			((ViewGroup) getWindow().getDecorView()).addView(mShadow);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// 友盟session统计
		MobclickAgent.onResume(this);

		//JPush统计
		JPushInterface.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// 友盟session统计
		MobclickAgent.onPause(this);

		//JPush统计
		JPushInterface.onPause(this);
	}

	@Override
	protected void onDestroy() {
		VolleyUtil.getQueue(this).cancelAll(this);;
		super.onDestroy();
	}
}
