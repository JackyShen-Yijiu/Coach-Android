package com.blackcat.coach.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebIconDatabase;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.blackcat.coach.R;
import com.blackcat.coach.events.BuyOkEvent;
import com.blackcat.coach.models.Product;
import com.blackcat.coach.utils.BaseUtils;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.SDKUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import de.greenrobot.event.EventBus;

public class DetailProductActivity extends BaseNoFragmentActivity {

    private Product mProduct;
    public static final String URL = "url";
    private static final String BLANK_URL = "about:blank";

    private ViewGroup mViewGroup;
    private WebView mWebView;
    private TextView mTvMoney;
    private String mBackUrl;
    private boolean mLoadError;
    private String mLastUrl;
    private boolean mUrlLoading;

    private Button mBtnBuy;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        setContentView(R.layout.activity_detail_product);
        configToolBar(R.mipmap.ic_back);
        mProduct = (Product) getIntent().getSerializableExtra(Constants.DETAIL);
        if (mProduct == null) {
            finish();
            return;
        }

        mTvMoney = (TextView) findViewById(R.id.product_detail_currentcy_tv);
        mTvMoney.setText(String.valueOf(WalletActivity.money));

        mViewGroup = (ViewGroup) findViewById(R.id.container);
        // mWebNetError = findViewById(R.id.web_net_error);
        mWebView = new WebView(this);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mViewGroup.addView(mWebView, 0, params);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());

        try {
            /**
             * 解决诸如漏洞
             */
            if (SDKUtils.hasHoneycomb()) {
                mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
                mWebView.removeJavascriptInterface("accessibility");
                mWebView.removeJavascriptInterface("accessibilityTraversal");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = mProduct.detailsimg;
        mWebView.loadUrl(url);

        WebSettings setting = mWebView.getSettings();
        String ua = getPackageName();
        setting.setUserAgentString(setting.getUserAgentString() + " " + ua);
        setting.setJavaScriptEnabled(true);
        setting.setBuiltInZoomControls(true);
        if (SDKUtils.hasHoneycomb()) {
            setting.setDisplayZoomControls(false);
        }
        setting.setSupportZoom(false);

        /**
         * webview Fatal signal 11 4.1.X crash bug
         */
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.JELLY_BEAN) {
            if (SDKUtils.hasHoneycomb()) {
                mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
            setting.setLoadWithOverviewMode(true);
            setting.setUseWideViewPort(true);
        } else {
            setting.setLoadWithOverviewMode(false);
            setting.setUseWideViewPort(false);
        }

        setting.setDomStorageEnabled(true);

        // setting.setLoadWithOverviewMode(true);
        // setting.setUseWideViewPort(true);
        // setting.setCacheMode(WebSettings.LOAD_NO_CACHE);// 解决缓存问题
        try {
            WebIconDatabase.getInstance().open(getDir("icons", MODE_PRIVATE).getPath());
        } catch (Exception e2) {
            e2.printStackTrace();
        }

        mWebView.setDownloadListener(new DownloadListener() {

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                        long contentLength) {
                if (!SDKUtils.hasHoneycomb()) {
                    return;
                }
                try {
                    DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    try {
                        url = URLDecoder.decode(url, "utf-8");
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }
                    Uri uri = Uri.parse(url);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setMimeType(mimetype);
                    request.allowScanningByMediaScanner();
                    String fileType = "";
                    try {
                        fileType = url.substring(url.lastIndexOf("."));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String dir = BaseUtils.getAppSdRootPath() + "download/";
                    File fileDir = new File(dir);
                    fileDir.mkdirs();

                    String fileName = BaseUtils.getMD5code(url) + fileType;
                    String path = dir + fileName;
                    File file = new File(path);
                    request.setDestinationUri(Uri.fromFile(file));
                    manager.enqueue(request);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mBtnBuy = (Button) findViewById(R.id.product_detail_buy_btn);
        if (WalletActivity.money < mProduct.productprice) {
            mBtnBuy.setEnabled(false);
        }
        mBtnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailProductActivity.this, ProductOrderActivity.class);
                intent.putExtra(Constants.DETAIL, mProduct);
                startActivity(intent);
            }
        });
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.resumeTimers();
    }

    @Override
    protected void onPause() {
        mWebView.pauseTimers();
        super.onPause();
    }

    public void onEvent(BuyOkEvent event) {
        finish();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        if (mWebView != null) {
            try {
                WebIconDatabase.getInstance().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mViewGroup.removeAllViews();
            mWebView.stopLoading();
            mWebView.clearCache(false);
            mWebView.destroyDrawingCache();
            mWebView.setWebChromeClient(null);
            mWebView.setWebViewClient(null);
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
    }

    @Override
    public void onBackPressed() {
        // if (mWebNetError.getVisibility() == View.VISIBLE) {
        // mWebNetError.setVisibility(View.GONE);
        // }
        if (mWebView.canGoBack()) {
            mBackUrl = mWebView.getUrl();
            if (BLANK_URL.equals(mBackUrl)) {
                super.onBackPressed();
                return;
            }
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    mBackUrl = null;
                }
            }, 1 * 1000);
            mWebView.goBackOrForward(-1);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(10);
        if (runningTasks != null && runningTasks.size() > 0) {
            ActivityManager.RunningTaskInfo cinfo = runningTasks.get(0);
            int num = cinfo.numRunning;
            if (num <= 1) {
                // 用于从闪屏广告页跳转
                Intent intent = new Intent(DetailProductActivity.this, IndexActivity.class);
                startActivity(intent);
            }
            super.finish();
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            // if (mWebNetError.getVisibility() == View.VISIBLE && !mLoadError)
            // {
            // mWebNetError.setVisibility(View.GONE);
            // }
        }

        @SuppressLint("DefaultLocale")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.toLowerCase().startsWith("http") || url.toLowerCase().startsWith("https")
                    || url.toLowerCase().startsWith("file")) {
                if (mBackUrl != null && mBackUrl.equals(url)) {
                    onBackPressed();
                    return true;
                }
                mWebView.loadUrl(url);
                mLastUrl = null;
            } else {
                try {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (mBackUrl != null && mBackUrl.equals(url)) {
                view.goBackOrForward(-1);
                return;
            }
            super.onPageFinished(view, url);
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            if (TextUtils.isEmpty(failingUrl)) {
                return;
            }
            if (failingUrl.toLowerCase().startsWith("http") || failingUrl.toLowerCase().startsWith("https")
                    || failingUrl.toLowerCase().startsWith("file")) {
                mLoadError = true;
                super.onReceivedError(view, errorCode, description, failingUrl);
                view.stopLoading();
                view.clearView();
                mLastUrl = view.getUrl();
                view.loadUrl(BLANK_URL);
                // if (mWebNetError.getVisibility() == View.GONE) {
                // mWebNetError.setVisibility(View.VISIBLE);
                // }
            }
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed(); // Ignore SSL certificate errors
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }
    }

    private void onNewNetAction() {
        mLoadError = false;
        // if (mWebNetError.getVisibility() == View.VISIBLE) {
        // mWebNetError.setVisibility(View.GONE);
        // }
    }
}
