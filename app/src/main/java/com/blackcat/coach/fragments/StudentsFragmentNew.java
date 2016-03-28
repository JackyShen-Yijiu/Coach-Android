package com.blackcat.coach.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.blackcat.coach.R;
import com.blackcat.coach.adapters.CommonAdapter;
import com.blackcat.coach.models.DicCode;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.User;
import com.blackcat.coach.net.URIUtil;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/** 新版 学员列表
 * Created by zou on 15/10/29.
 */
public class StudentsFragmentNew extends BaseListFragment<User> {
    public static StudentsFragmentNew newInstance(String param1, String param2) {
        StudentsFragmentNew fragment = new StudentsFragmentNew();
        return fragment;
    }

    public StudentsFragmentNew() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mType = new TypeToken<Result<List<User>>>(){}.getType();
        View rootView = inflater.inflate(R.layout.fragment_students, container, false);
        initViews(rootView, inflater, CommonAdapter.AdapterType.TYPE_ADAPTER_STUDENT);

        mPage = 1;
        if (!Session.isUserInfoEmpty()) {
            mURI = URIUtil.getStudentsList(Session.getSession().coachid, mPage);
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
        mPage = 1;
        mURI = URIUtil.getStudentsList(Session.getSession().coachid, mPage);
        refresh(DicCode.RefreshType.R_PULL_DOWN, mURI);
    }

    @Override
    public void onLoadMore() {
        mPage++;
        mURI = URIUtil.getStudentsList(Session.getSession().coachid, mPage);
        refresh(DicCode.RefreshType.R_PULL_UP, mURI);
    }

    @Override
    protected void onFeedsErrorResponse(VolleyError arg0, int refreshType) {
        super.onFeedsErrorResponse(arg0, refreshType);
        if (refreshType == DicCode.RefreshType.R_PULL_UP) {
            mPage--;
        }
    }
}