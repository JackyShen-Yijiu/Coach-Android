package com.blackcat.coach.activities;

import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;

public class BaseNoFragmentActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// 友盟页面的统计
		MobclickAgent.onPageStart(this.getClass().getSimpleName());
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		// 友盟页面的统计
		MobclickAgent.onPageEnd(this.getClass().getSimpleName());
	}
}
