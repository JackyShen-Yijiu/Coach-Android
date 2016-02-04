package com.blackcat.coach.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.blackcat.coach.R;
import com.blackcat.coach.activities.StudentsActivity1;
import com.blackcat.coach.adapters.CommonAdapter;
import com.blackcat.coach.models.DicCode;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.User;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.NetConstants;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.VolleyUtil;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by pengdonghua on 2016/2/3.
 */
public class StudentFragment1 extends BaseListFragment<User> {

//    StudentFragment1 fragment;

    private int type = 0;
//
//    public static StudentFragment1 getInstance(int t){
//        if(fragment == null)
//            fragment = new StudentFragment1();
//        type = t;
//        return fragment;
//    }

    public StudentFragment1(){};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View contextView = inflater.inflate(R.layout.fragment_item, container, false);
        //获取Activity传递过来的参数
        Bundle mBundle = getArguments();
        type = mBundle.getInt("type");
        initView(contextView, inflater, type);

        initData();

        return contextView;
    }

    private void initData() {
        mPage = 1;
        request(mPage);
    }

    private void initView(View rootView,LayoutInflater inflater,int type){

        this.type = type;
        mType = new TypeToken<Result<List<User>>>(){}.getType();

//
        if(StudentsActivity1.flag  == 0){//普通学员0
            initViews(rootView, inflater, CommonAdapter.AdapterType.TYPE_ADAPTER_STUDENT);
        }else{//群发短信
            initViews(rootView, inflater, CommonAdapter.AdapterType.TYPE_ADAPTER_STUDENT_SMS);
        }



//        EventBus.getDefault().register(this);
        //初始化
    }



    @Override
    protected void initViews(View rootView, LayoutInflater inflater,
                             int adapterType) {
        super.initViews(rootView, inflater, adapterType);
        mListView.setDividerHeight(0);
        mListView.setDivider(null);
    }

    private  void request(int page){

        mURI = URIUtil.getStudentsList1(Session.getSession().coachid, page, type);
        if(page==1)
            refresh(DicCode.RefreshType.R_PULL_DOWN, mURI,type);
        else{
            refresh(DicCode.RefreshType.R_PULL_UP, mURI,type);
        }
    }

    protected void refresh(final int refreshType, URI uri,int type) {
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
//        map.put(NetConstants.STUDENT_TYPE, type+"");

        GsonIgnoreCacheHeadersRequest<Result<List<User>>> request = new GsonIgnoreCacheHeadersRequest<Result<List<User>>>(
                url, mType, map,
                new Response.Listener<Result<List<User>>>() {
                    @Override
                    public void onResponse(Result<List<User>> response) {
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



    @Override
    public void onRefresh() {
        mPage = 1;

        request(mPage);
//        mURI = URIUtil.getStudentsList(Session.getSession().coachid, mPage);
//        refresh(DicCode.RefreshType.R_PULL_DOWN, mURI);
    }

    @Override
    public void onLoadMore() {
        mPage++;
        request(mPage);
//        mURI = URIUtil.getStudentsList(Session.getSession().coachid, mPage);
//        refresh(DicCode.RefreshType.R_PULL_UP, mURI);
    }

    @Override
    protected void onFeedsErrorResponse(VolleyError arg0, int refreshType) {
        super.onFeedsErrorResponse(arg0, refreshType);
        if (refreshType == DicCode.RefreshType.R_PULL_UP) {
            mPage--;
        }
    }
}
