package com.blackcat.coach.activities;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.blackcat.coach.R;

public class TermsActivity extends BaseActivity implements View.OnClickListener {

	private WebView webView;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question);
		configToolBar(R.mipmap.ic_back);

		webView = (WebView) findViewById(R.id.question_webview);
		webView.setOnClickListener(this);
		initListener();
	}


	protected void initListener() {
		String url = "http://www.yibuxueche.com/jzjfcoachgreement.htm";
		WebSettings webSettings = webView.getSettings();
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}
		});

		webSettings.setJavaScriptEnabled(true);
		webView.loadUrl(url);
	}



	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onPause() {
		webView.onPause();
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View view) {

	}
}
