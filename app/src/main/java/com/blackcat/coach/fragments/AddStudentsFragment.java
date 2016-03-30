package com.blackcat.coach.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.blackcat.coach.R;
import com.blackcat.coach.adapters.CommonAdapter;
import com.blackcat.coach.models.AddStudentsVO;
import com.blackcat.coach.models.DicCode;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.LogUtil;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by aa on 2016/3/29.
 */
public class AddStudentsFragment extends BaseListFragment<AddStudentsVO> {
    private int type = 0;
    public static AddStudentsFragment newInstance(String param1, String param2) {
        AddStudentsFragment fragment = new AddStudentsFragment();
        return fragment;
    }

    public AddStudentsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mType = new TypeToken<Result<List<AddStudentsVO>>>(){}.getType();
        View rootView = inflater.inflate(R.layout.add_student, container, false);
        initViews(rootView, inflater, CommonAdapter.AdapterType.TYPE_ADD_STUDENTS);
        mPage = 1;
        if (!Session.isUserInfoEmpty()) {
            mURI = URIUtil.getAddStudentsList("-1", mPage);
            refresh(DicCode.RefreshType.R_INIT, mURI);
        }
        mListView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LogUtil.print("mListView--------------");
            }
        });
        return rootView;
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
        mURI = URIUtil.getAddStudentsList("-1", mPage);
        refresh(DicCode.RefreshType.R_PULL_DOWN, mURI);
    }

    @Override
    public void onLoadMore() {
        mPage++;
        mURI = URIUtil.getAddStudentsList("-1", mPage);
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
