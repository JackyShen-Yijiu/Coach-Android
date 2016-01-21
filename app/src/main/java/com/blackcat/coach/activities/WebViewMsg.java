package com.blackcat.coach.activities;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.blackcat.coach.R;
import com.blackcat.coach.models.SystemMsg;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.LogUtil;

/**
 * Created by aa on 2016/1/21.
 */
public class WebViewMsg extends BaseActivity implements View.OnClickListener{
    private WebView webView;
    private SystemMsg mSystemMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);
        configToolBar(R.mipmap.ic_back);
       
        webView = (WebView) findViewById(R.id.system_messeage_webview);
        webView.setOnClickListener(this);
        initListener();
    }

    private void initListener() {
      String url=  getIntent().getStringExtra(Constants.DETAIL) ;
        LogUtil.print("111111111111"+getIntent().getStringExtra(Constants.DETAIL));
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

    @Override
    public void onClick(View view) {

    }
}
