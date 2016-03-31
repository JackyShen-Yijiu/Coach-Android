package com.blackcat.coach.fragments;

import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.adapters.CommonAdapter;
import com.blackcat.coach.adapters.rows.RowAddStudents;
import com.blackcat.coach.dialogs.ApplySuccessDialog;
import com.blackcat.coach.models.AddStudentsVO;
import com.blackcat.coach.models.DaytimelysReservation;
import com.blackcat.coach.models.DicCode;
import com.blackcat.coach.models.ExamInfo;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.CoachAppointParams;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.GsonUtils;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.UTC2LOC;
import com.blackcat.coach.utils.VolleyUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aa on 2016/3/29.
 */
public class ExamInfoFragment extends BaseListFragment<AddStudentsVO> {




    public static ExamInfoFragment newInstance(String param1, String param2) {
        ExamInfoFragment fragment = new ExamInfoFragment();
        return fragment;
    }

    public ExamInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mType = new TypeToken<Result<List<ExamInfo>>>() {
        }.getType();
        View rootView = inflater.inflate(R.layout.fragment_exam_info, container, false);
        initViews(rootView, inflater, CommonAdapter.AdapterType.TYPE_EXAM_INFO);
        initView(rootView);
        initData();
        mPage = 1;

        if (!Session.isUserInfoEmpty()) {
            mURI = URIUtil.getExamInfoList("-1", mPage);
            refresh(DicCode.RefreshType.R_INIT, mURI);
        }
        return rootView;
    }

    private void initView(View view) {

    }

    private void initData() {




    }

    @Override
    protected void initViews(View rootView, LayoutInflater inflater,
                             int adapterType) {
        super.initViews(rootView, inflater, adapterType);
        mListView.setDividerHeight(0);
        mListView.setDivider(null);
    }


    @Override
    public void onRefresh() {
        mPage = 1;
        mURI = URIUtil.getExamInfoList("-1", mPage);
        refresh(DicCode.RefreshType.R_PULL_DOWN, mURI);
    }

    @Override
    public void onLoadMore() {
        mPage++;
        mURI = URIUtil.getExamInfoList("-1", mPage);
        refresh(DicCode.RefreshType.R_PULL_UP, mURI);
    }

    @Override
    protected void onFeedsResponse(Result<List<AddStudentsVO>> response, int refreshType) {
        super.onFeedsResponse(response, refreshType);
        if (response != null && response.type == Result.RESULT_OK && response.data != null) {
            List<AddStudentsVO> list = response.data;
            Context c = getActivity();
//            if(c!=null && list.size()>0)
//                new SpHelper(c).set(NetConstants.KEY_MESSAGEID,list.get(0).);
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
