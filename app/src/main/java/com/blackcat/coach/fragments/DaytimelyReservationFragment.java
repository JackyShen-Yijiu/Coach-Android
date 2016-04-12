package com.blackcat.coach.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.R;
import com.blackcat.coach.adapters.CommonAdapter;
import com.blackcat.coach.events.ReservationOpOk;
import com.blackcat.coach.models.DaytimelysReservation;
import com.blackcat.coach.models.DicCode;
import com.blackcat.coach.models.Reservation;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.net.NetConstants;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.CommonUtil;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.UTC2LOC;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by aa on 2016/1/9.
 */
public class DaytimelyReservationFragment extends BaseListFragment<DaytimelysReservation> {


    private String date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View contextView = inflater.inflate(R.layout.fragment_item, container, false);
        //获取Activity传递过来的参数
        Bundle mBundle = getArguments();
        date = mBundle.getString("date");

        initView(contextView, inflater);

        initData();

        return contextView;
    }

    private void initData() {

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initView(View rootView, LayoutInflater inflater) {

        mType = new TypeToken<Result<List<DaytimelysReservation>>>() {
        }.getType();

        initViews(rootView, inflater, CommonAdapter.AdapterType.TYPE_ADAPTER_SCHEDULE);
        mListView.setOnLoadMoreListener(null);
        if (!Session.isUserInfoEmpty()) {
            request();
        }

        EventBus.getDefault().register(this);

    }


    public void setData(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        format.format(date.getDate());
        ChildScheduleFragment.selectedDate = date;
        LogUtil.print("DaytimelyReservationFragment--" + ChildScheduleFragment.selectedDate.toLocaleString());
        if (!Session.isUserInfoEmpty()) {
            this.date = format.format(date);
            request();
        }

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
        request();
    }

    @Override
    public void onLoadMore() {
    }

    @Override
    public void noData() {
        mListView.setVisibility(View.GONE);
        mNullIv.setImageResource(R.mipmap.no_data);
        mNullLayout.setVisibility(View.VISIBLE);
    }

    private void request() {

        mURI = URIUtil.getDaytimelyReservationList(Session.getSession().coachid, date);
        refresh(DicCode.RefreshType.R_INIT, mURI);
    }

    @Override
    protected void onFeedsErrorResponse(VolleyError arg0, int refreshType) {
        super.onFeedsErrorResponse(arg0, refreshType);
        if (refreshType == DicCode.RefreshType.R_PULL_UP) {
            mPage--;
        }
    }

    @Override
    public void onFeedsResponse(Result<List<DaytimelysReservation>> response, int refreshType) {
//        super.onFeedsResponse(response, refreshType);

        mPullToRefreshView.completeRefresh();
        if (Constants.DEBUG) {
            VolleyLog.v("Response:%n %s", response);
        }
        if (response != null && response.type == Result.RESULT_OK && response.data != null) {
            if (response.data.size() == 0) {
                noData();
                return;
            }
            mListView.setVisibility(View.VISIBLE);
            mNullLayout.setVisibility(View.GONE);
            List<DaytimelysReservation> list = new ArrayList<DaytimelysReservation>();
            //过滤掉过时的没有学员的项
            for (int i = 0; i < response.data.size(); i++) {

            }
            for (int i = 0; i < response.data.size(); i++) {
                if (UTC2LOC.instance.getDates(response.data.get(i).courseendtime,
                        "yyyy-MM-dd HH:mm:ss").before(Calendar.getInstance().getTime())) {
                    //过时的没有学员的从列表中移除
                    if (response.data.get(i).selectedstudentcount <= 0) {
//                        list.remove(response.data.get(i));
                    }else{
                        list.add(response.data.get(i));
                    }

                }else{
                    list.add(response.data.get(i));
                }
            }
            //
            if (list.size() == 0) {
                noData();
            }
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

            //设置到当天当前时间上
//            Date beginTime = null;
//            Date endTime = null;
//            mListView.setSelection(0);
//            if (list.size() > 0 && CommonUtil.isSameDate(Calendar.getInstance().getTime(),
//                    UTC2LOC.instance.getDates(list.get(0).coursedate, "yyyy-MM-dd"))) {
//                for (int i = 0; i < list.size(); i++) {
//                    beginTime = UTC2LOC.instance.getDates(list.get(i).coursebegintime, "yyyy-MM-dd HH:mm:ss");
//                    endTime = UTC2LOC.instance.getDates(list.get(i).courseendtime, "yyyy-MM-dd HH:mm:ss");
//                    LogUtil.print(beginTime.toLocaleString() + "----" + endTime.toLocaleString());
//                    Date now = Calendar.getInstance().getTime();
//                    if (now.after(beginTime) && now.before(endTime)) {
//                        mListView.setSelection(i);
//                        break;
//                    }
//                }
//
//        }


        }else if (response != null && !TextUtils.isEmpty(response.msg)) {
            noData();
//            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(response.msg);
        }
    }

    public void onEvent(ReservationOpOk event) {
////        mPage = 1;
////        mURI = URIUtil.getReservationList(Session.getSession().coachid, mPage);
////        refresh(DicCode.RefreshType.R_PULL_DOWN, mURI);
//        List<DaytimelysReservation>  list = mAdapter.getList();
//        int pos = event.pos;
//        if (list != null && list.size() > pos && pos >= 0) {
//            DaytimelysReservation item = list.get(pos);
////            item.reservationstate = event.status;
//            mAdapter.notifyDataSetChanged();
//        }
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

}

