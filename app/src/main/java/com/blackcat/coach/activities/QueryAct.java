package com.blackcat.coach.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.blackcat.coach.R;
import com.blackcat.coach.adapters.CommonAdapter;
import com.blackcat.coach.models.DicCode;
import com.blackcat.coach.models.Reservation;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.NetConstants;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.VolleyUtil;
import com.blackcat.coach.widgets.LoadMoreListView;
import com.blackcat.coach.widgets.PullToRefreshListView;
import com.blackcat.coach.widgets.PullToRefreshView;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.reflect.*;

public class QueryAct extends BaseActivity implements PullToRefreshView.OnRefreshListener, LoadMoreListView.OnLoadMoreListener {

    PullToRefreshListView listview;

    CommonAdapter mAdapter;

    PullToRefreshView mPullToRefreshView;

    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        configToolBar(R.mipmap.ic_back);
        initView();
    }

    private void initView() {
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_refresh_view);

        listview = (PullToRefreshListView) findViewById(R.id.inner_list);

        et = (EditText) findViewById(R.id.act_query_et);
        mAdapter = new CommonAdapter<>(this, null, CommonAdapter.AdapterType.TYPE_ADAPTER_RESERVATION);
        listview.setAdapter(mAdapter);
        mPullToRefreshView.setRefreshListener(this);
        listview.setOnLoadMoreListener(this);

        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))

                {
                //do something;
                request(et.getText().toString());
                    return true;
                }
                return false;

            }

        });

    }

    public void request(String key){
        Type  mType = new TypeToken<Result<List<Reservation>>>(){}.getType();

        String url = null;
        //所有的数据
        URI uri = URIUtil.getReservationListQuery(Session.getSession().coachid, 0,key);
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
        map.put(NetConstants.KEY_AUTHORIZATION, Session.getSession().token);
        GsonIgnoreCacheHeadersRequest<Result<List<Reservation>>> request = new GsonIgnoreCacheHeadersRequest<Result<List<Reservation>>>(
                url, mType, map,
                new Response.Listener<Result<List<Reservation>>>() {
                    @Override
                    public void onResponse(Result<List<Reservation>> response) {
                        List<Reservation>  list = response.data;
                        mAdapter.setList(list);
                        mAdapter.notifyDataSetChanged();
//                        onFeedsResponse(response, refreshType);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
//                        onFeedsErrorResponse(arg0, refres/hType);
                    }
                });
        // 请求加上Tag,用于取消请求
        request.setTag(this);

        VolleyUtil.getQueue(this).add(request);
    }


    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }
}
