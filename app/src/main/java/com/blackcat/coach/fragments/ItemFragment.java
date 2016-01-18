package com.blackcat.coach.fragments;

import android.content.ClipData;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.blackcat.coach.R;
import com.blackcat.coach.adapters.CommonAdapter;
import com.blackcat.coach.events.ReservationOpOk;
import com.blackcat.coach.models.DicCode;
import com.blackcat.coach.models.Reservation;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.net.URIUtil;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by aa on 2016/1/9.
 */
public class ItemFragment extends BaseListFragment {

//    private final
    /***请求种类*/
    private int type = 0;

    public static  boolean REFRESH1 = false;

    public static  boolean REFRESH2 = false;

    public static  boolean REFRESH3 = false;

    public ItemFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View contextView = inflater.inflate(R.layout.fragment_item, container, false);
        //获取Activity传递过来的参数
        Bundle mBundle = getArguments();
        initView(contextView,inflater,mBundle.getInt("type"));

        initData();

        return contextView;
    }

    private void initData() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initView(View rootView,LayoutInflater inflater,int type){

        this.type = type;
        mType = new TypeToken<Result<List<Reservation>>>(){}.getType();

        initViews(rootView, inflater, CommonAdapter.AdapterType.TYPE_ADAPTER_RESERVATION);

        mPage = 1;
        if (!Session.isUserInfoEmpty()) {
            request(mPage);
        }
        EventBus.getDefault().register(this);
    }



    @Override
    protected void initViews(View rootView, LayoutInflater inflater,
                             int adapterType) {
        super.initViews(rootView, inflater, adapterType);
        mListView.setDividerHeight(0);
        mListView.setDivider(null);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onRefresh() {
        mPage = 1;
        request(mPage);
    }

    @Override
    public void onLoadMore() {
        mPage++;
        request(mPage);
    }

    private  void request(int page){
        int temp = 0;
        switch(type){
            case 0://预约中
                temp = 3;
                break;
            case 1://待评价
                temp = 6;
                break;
            case 2://学生取消
                temp = 2;
                break;
            case 3://已经完成
                temp = 7;
                break;
        }
        mURI = URIUtil.getAppointMent(Session.getSession().coachid,page,temp);
        refresh(DicCode.RefreshType.R_PULL_UP, mURI);
    }

    @Override
    protected void onFeedsErrorResponse(VolleyError arg0, int refreshType) {
        super.onFeedsErrorResponse(arg0, refreshType);
        if (refreshType == DicCode.RefreshType.R_PULL_UP) {
            mPage--;
        }
    }

    public void onEvent(ReservationOpOk event) {
//        mPage = 1;
//        mURI = URIUtil.getReservationList(Session.getSession().coachid, mPage);
//        refresh(DicCode.RefreshType.R_PULL_DOWN, mURI);
        List<Reservation>  list = mAdapter.getList();
        int pos = event.pos;
        if (list != null && list.size() > pos && pos >= 0) {
            Reservation item = list.get(pos);
            item.reservationstate = event.status;
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

}

