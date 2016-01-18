package com.blackcat.coach.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.blackcat.coach.R;
import com.blackcat.coach.adapters.CommonAdapter;
import com.blackcat.coach.fragments.ReservationFragment;
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

    private SearchView search_view;

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
        search_view = (SearchView) findViewById(R.id.search_view);
        mAdapter = new CommonAdapter<>(this, null, CommonAdapter.AdapterType.TYPE_ADAPTER_RESERVATION);
        listview.setAdapter(mAdapter);
        mPullToRefreshView.setRefreshListener(this);
        listview.setOnLoadMoreListener(this);

//        int id = search_view.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
//        TextView textView = (TextView) search_view.findViewById(id);
//        textView.setTextColor(Color.parseColor("#333333"));


        final int editViewId = getResources().getIdentifier("search_src_text", "id", getPackageName());
        SearchView.SearchAutoComplete mEdit = (SearchView.SearchAutoComplete) search_view.findViewById(editViewId);
        if (mEdit != null) {
            mEdit.setHintTextColor(getResources().getColor(R.color.text_333));
            mEdit.setTextColor(getResources().getColor(R.color.text_333));
            mEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mEdit.setHint("请输入学员姓名");//String.format(getResources().getString(R.string.search_hint_tip), MemoryData.departmentList.get(mPosition).getMembers().size()));
        }

        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                request(et.getText().toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

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
        int temp = 0;
        switch(ReservationFragment.currentPage){
            case 0://预约中
                temp = 1;
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
        //所有的数据
        URI uri = URIUtil.getReservationListQuery(Session.getSession().coachid, temp,key);
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
        map.put(NetConstants.KEY_SEARCH_NAME, key);
//        params.add(new BasicNameValuePair(NetConstants.KEY_SEARCH_NAME, key));
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
