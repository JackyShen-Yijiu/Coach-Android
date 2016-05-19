package com.blackcat.coach.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.blackcat.coach.R;
import com.blackcat.coach.adapters.CommonAdapter;
import com.blackcat.coach.models.DicCode;
import com.blackcat.coach.models.NoticeMsgVO;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.SystemMsg;
import com.blackcat.coach.net.NetConstants;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.SpHelper;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * 公告
 * Created by zou on 15/10/29.
 */
public class NoticeMsgFragment extends BaseListFragment<NoticeMsgVO> {
    public static NoticeMsgFragment newInstance(String param1, String param2) {
        NoticeMsgFragment fragment = new NoticeMsgFragment();
        return fragment;
    }

    public NoticeMsgFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mType = new TypeToken<Result<List<NoticeMsgVO>>>(){}.getType();
        View rootView = inflater.inflate(R.layout.fragment_systemmsg, container, false);
        initViews(rootView, inflater, CommonAdapter.AdapterType.TYPE_ADAPTER_NOTICE);

        mPage = 0;
        if (!Session.isUserInfoEmpty()) {
            mURI = URIUtil.getNoticeMsgList(Session.getSession().coachid, mPage, Session.getSession().driveschoolinfo.id);
            refresh(DicCode.RefreshType.R_INIT, mURI);
        }
        return rootView;
    }


    @Override
    protected void initViews(View rootView, LayoutInflater inflater,
                             int adapterType) {
        super.initViews(rootView, inflater, adapterType);
    }

    @Override
    public void onRefresh() {
        mPage = 0;
        mURI = URIUtil.getNoticeMsgList(Session.getSession().coachid, mPage, Session.getSession().driveschoolinfo.id);
        refresh(DicCode.RefreshType.R_PULL_DOWN, mURI);
    }

    @Override
    public void onLoadMore() {
        mPage++;
        mURI = URIUtil.getNoticeMsgList(Session.getSession().coachid, mPage, Session.getSession().driveschoolinfo.id);
        refresh(DicCode.RefreshType.R_PULL_UP, mURI);
    }

    @Override
    protected void onFeedsResponse(Result<List<NoticeMsgVO>> response, int refreshType) {
            super.onFeedsResponse(response, refreshType);
        if (response != null && response.type == Result.RESULT_OK && response.data != null) {
            List<NoticeMsgVO> list = response.data;
            Context c = getActivity();
            if(c!=null && list.size()>0)
                new SpHelper(c).set(NetConstants.KEY_NEWSID,list.get(0).seqindex);
        }

    }

    @Override
    protected void onFeedsErrorResponse(VolleyError arg0, int refreshType) {
        super.onFeedsErrorResponse(arg0, refreshType);
        if (refreshType == DicCode.RefreshType.R_PULL_UP) {
            mPage--;
        }
    }
}
