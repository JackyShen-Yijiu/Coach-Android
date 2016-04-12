package com.blackcat.coach.fragments;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.adapters.CommonAdapter;
import com.blackcat.coach.models.DicCode;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.NetConstants;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.blackcat.coach.widgets.LoadMoreListView;
import com.blackcat.coach.widgets.PullToRefreshListView;
import com.blackcat.coach.widgets.PullToRefreshView;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zou on 15/10/25.
 */
public abstract class BaseListFragment<T> extends BaseFragment
        implements PullToRefreshView.OnRefreshListener, LoadMoreListView.OnLoadMoreListener {
    protected PullToRefreshListView mListView;
    protected PullToRefreshView mPullToRefreshView;
    protected CommonAdapter<T> mAdapter;
    protected View mHeaderView;
    protected Type mType = null;
    protected URI mURI = null;
    protected int mPage = 1;

    protected int mDataSourceType;
    protected RelativeLayout mNullLayout;
    protected ImageView mNullIv;

    // Called in onCreateView of subclass
    protected void initViews(View rootView, LayoutInflater inflater, int adapterType) {
        //空白页
        mNullLayout = (RelativeLayout) rootView.findViewById(R.id.layout_null);
        mNullIv = (ImageView) rootView.findViewById(R.id.null_iv);

        mPullToRefreshView = (PullToRefreshView) rootView.findViewById(R.id.pull_refresh_view);
        mListView = (PullToRefreshListView) rootView.findViewById(R.id.inner_list);
        mAdapter = new CommonAdapter<>(mActivity, null, adapterType);
        mListView.setAdapter(mAdapter);
        mPullToRefreshView.setRefreshListener(this);
        mListView.setOnLoadMoreListener(this);
        mListView.setVisibility(View.VISIBLE);
        mNullLayout.setVisibility(View.GONE);

    }

    protected void initViews(View rootView, LayoutInflater inflater, int adapterType, int headerLayoutRes) {
        //空白页
        mNullLayout = (RelativeLayout) rootView.findViewById(R.id.layout_null);
        mNullIv = (ImageView) rootView.findViewById(R.id.null_iv);
        mPullToRefreshView = (PullToRefreshView) rootView.findViewById(R.id.pull_refresh_view);
        mListView = (PullToRefreshListView) rootView.findViewById(R.id.inner_list);
        mHeaderView = inflater.inflate(headerLayoutRes, mListView, false);
        mListView.addHeaderView(mHeaderView);
        mAdapter = new CommonAdapter<>(mActivity, null, adapterType);
        mListView.setAdapter(mAdapter);
        mPullToRefreshView.setRefreshListener(this);
        mListView.setOnLoadMoreListener(this);
        mListView.setVisibility(View.VISIBLE);
        mNullLayout.setVisibility(View.GONE);
    }

    protected void refresh(final int refreshType, URI uri) {
        String url = null;
        if (uri != null) {
            try {
                url = uri.toURL().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }

        Map map = new HashMap<>();
        map.put(NetConstants.KEY_AUTHORIZATION, Session.getToken());
//        map.put("authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiI1NjE2MzUyNzIxZWMyOTA0MWE5YWY4ODkiLCJ0aW1lc3RhbXAiOiIyMDE1LTEwLTA4VDA5OjIzOjQ4LjY5NloiLCJhdWQiOiJibGFja2NhdGUiLCJpYXQiOjE0NDQyOTYyMjh9.-iOZ5fIQjdmdHBthsCP7VQWRYYM68zWWHWWnIUxRSEg");

        GsonIgnoreCacheHeadersRequest<Result<List<T>>> request = new GsonIgnoreCacheHeadersRequest<Result<List<T>>>(
                url, mType, map,
                new Response.Listener<Result<List<T>>>() {
                    @Override
                    public void onResponse(Result<List<T>> response) {
                        onFeedsResponse(response, refreshType);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        onFeedsErrorResponse(arg0, refreshType);
                    }
                });
        // 请求加上Tag,用于取消请求
        request.setTag(this);
        if (refreshType == DicCode.RefreshType.R_PULL_DOWN) {
            request.setManuallyRefresh(true);
        } else if (refreshType == DicCode.RefreshType.R_PULL_UP) {
            request.setShouldCache(false);
        }

        VolleyUtil.getQueue(getActivity()).add(request);
    }

    protected void onFeedsErrorResponse(VolleyError arg0, int refreshType) {
        if(mPullToRefreshView == null)
            return ;
        mPullToRefreshView.completeRefresh();
        if (refreshType != DicCode.RefreshType.R_PULL_UP) {
            mListView.setVisibility(View.GONE);
            mNullIv.setImageResource(R.mipmap.no_network);
            mNullLayout.setVisibility(View.VISIBLE);
//            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.net_err);
        } else {
            mListView.setLoadMoreFailed();
        }
        if (Constants.DEBUG) {
            VolleyLog.e("Error: ", arg0.getMessage());
        }
    }

    protected void onFeedsResponse(Result<List<T>> response, int refreshType) {
        mPullToRefreshView.completeRefresh();
        if (Constants.DEBUG) {
            VolleyLog.v("Response:%n %s", response);
        }
        if (response != null && response.type == Result.RESULT_OK && response.data != null) {
            if(response.data.size() ==0){
                noData();
                return;
            }
            mListView.setVisibility(View.VISIBLE);
            mNullLayout.setVisibility(View.GONE);
            List<T> list = response.data;
//            if(list.size()>0 && list.get(0) instanceof User){
//                for(int i=0;i<list.size();i++){//
//                    Log.d("tag","user-->"+((User)list.get(i)).leavecoursecount);
//                }
//            }
            LogUtil.print("listsize-00-->"+list.size());
            if (refreshType == DicCode.RefreshType.R_PULL_UP) {
                mListView.setLoadMoreComplete();
                if (list.size() < NetConstants.REQ_LEN) {
                    mListView.setNoMoreData(true);
                } else {
                    mListView.setNoMoreData(false);
                }
                mAdapter.appendList(list);
            } else {
                mListView.setNoMoreData(false);
                mAdapter.setList(list);
            }
            mAdapter.notifyDataSetChanged();
        } else if (response != null && !TextUtils.isEmpty(response.msg)) {
            noData();
//            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(response.msg);
        }
    }

    public CommonAdapter<T> getAdapter(){
        return mAdapter;
    }



    @Override
    public void onDestroyView() {
        VolleyUtil.getQueue(mActivity).cancelAll(this);
        super.onDestroyView();
    }

    @Override
    abstract public void onRefresh();

    @Override
    abstract public void onLoadMore();
    //返回的数据为空时
    public void noData(){}
}
