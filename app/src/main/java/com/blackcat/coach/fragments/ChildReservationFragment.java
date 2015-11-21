package com.blackcat.coach.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChildReservationFragment extends BaseListFragment {

    public static ChildScheduleFragment newInstance(String param1, String param2) {
        ChildScheduleFragment fragment = new ChildScheduleFragment();
        return fragment;
    }

    public ChildReservationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mType = new TypeToken<Result<List<Reservation>>>(){}.getType();
        View rootView = inflater.inflate(R.layout.fragment_child_reservation, container, false);
        initViews(rootView, inflater, CommonAdapter.AdapterType.TYPE_ADAPTER_RESERVATION);

        mPage = 1;
        if (!Session.isUserInfoEmpty()) {
            mURI = URIUtil.getReservationList(Session.getSession().coachid, mPage);
            refresh(DicCode.RefreshType.R_INIT, mURI);
        }
        EventBus.getDefault().register(this);
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
        mURI = URIUtil.getReservationList(Session.getSession().coachid, mPage);
        refresh(DicCode.RefreshType.R_PULL_DOWN, mURI);
    }

    @Override
    public void onLoadMore() {
        mPage++;
        mURI = URIUtil.getReservationList(Session.getSession().coachid, mPage);
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
