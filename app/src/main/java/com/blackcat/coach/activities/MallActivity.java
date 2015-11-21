package com.blackcat.coach.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.adapters.CommonAdapter;
import com.blackcat.coach.fragments.ImageFragment;
import com.blackcat.coach.models.Product;
import com.blackcat.coach.models.ProductList;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.google.gson.reflect.TypeToken;
import com.viewpagerindicator.LinePageIndicator;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

public class MallActivity extends BaseActivity {

    private ListView mListView;
    private CommonAdapter<Product> mAdapter;
    private View mPinnedView;
    private ViewPager mPager;
    private ViewPagerAdapter mBannerAdapter;
    private LinePageIndicator mIndicator;
    private Type mType = new TypeToken<Result<ProductList>>(){}.getType();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mall);
        configToolBar(R.mipmap.ic_back);
        initViews();
        // 发起请求
        refresh();
    }

    private void initViews() {
        mListView = (ListView) findViewById(R.id.mall_listview);
        mPager = (ViewPager) findViewById(R.id.pager);
        mBannerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), null);
        mPager.setAdapter(mBannerAdapter);
        mIndicator = (LinePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        mAdapter = new CommonAdapter<Product>(this, null, CommonAdapter.AdapterType.TYPE_ADAPTER_PRODUCT);
        mListView.setAdapter(mAdapter);
    }

    private void refresh() {
        String url = null;
        URI uri = URIUtil.getMallProduct();
        try {
            url = uri.toURL().toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }
        GsonIgnoreCacheHeadersRequest<Result<ProductList>> request = new GsonIgnoreCacheHeadersRequest<Result<ProductList>>(
                url, mType, null,
                new Response.Listener<Result<ProductList>>() {
                    @Override
                    public void onResponse(Result<ProductList> response) {
                        if (response != null && response.type == Result.RESULT_OK && response.data != null) {
                            mAdapter.setList(response.data.mainlist);
                            mBannerAdapter.setList(response.data.toplist);
                            mBannerAdapter.notifyDataSetChanged();
                            mIndicator.notifyDataSetChanged();
                            mAdapter.notifyDataSetChanged();
                        } else if (response != null && !TextUtils.isEmpty(response.msg)) {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(response.msg);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.net_err);
                    }
                });
        // 请求加上Tag,用于取消请求
        request.setTag(this);
        request.setManuallyRefresh(true);
        request.setShouldCache(true);

        VolleyUtil.getQueue(this).add(request);
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private List<Product> banners;

        public ViewPagerAdapter(FragmentManager fm, List<Product> urls) {
            super(fm);
            banners = urls;
        }

        public void setList(List<Product> urls) {
            banners = urls;
        }

        @Override
        public Fragment getItem(int index) {
            Fragment fragment = null;
            fragment = ImageFragment.newInstance(banners.get(index));
            return fragment;
        }

        @Override
        public int getCount() {
            if (banners != null) {
                return banners.size();
            }
            return 0;
        }
    }
}
