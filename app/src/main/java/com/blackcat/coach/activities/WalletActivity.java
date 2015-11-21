package com.blackcat.coach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.adapters.CommonAdapter;
import com.blackcat.coach.dialogs.BonusDialog;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.Wallet;
import com.blackcat.coach.models.WalletRecord;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.NetConstants;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.blackcat.coach.widgets.LoadMoreListView;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class WalletActivity extends BaseNoFragmentActivity
        implements View.OnClickListener, LoadMoreListView.OnLoadMoreListener {

    private LoadMoreListView mListView;
    private TextView mTvAmount;
    private ImageView mIvToggleShow;
    private TextView mTvInviteCode;
    private CommonAdapter<WalletRecord> mAdapter;

    public static int money = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        configToolBar(R.mipmap.ic_back);
        initView();
        getWalletRequest();
    }

    private void initView() {
        mTvAmount = (TextView) findViewById(R.id.my_wallet_change_tv);
        findViewById(R.id.my_wallet_invite_btn).setOnClickListener(this);
        findViewById(R.id.my_wallet_change_btn).setOnClickListener(this);

        mListView = (LoadMoreListView) findViewById(R.id.my_wallet_listview);
        mListView.setOnLoadMoreListener(this);

        mTvInviteCode = (TextView) findViewById(R.id.my_wallet_invit_code_tv);
        mTvInviteCode.setText(Session.getSession().invitationcode);

        findViewById(R.id.my_wallet_show_tv).setOnClickListener(this);

        mIvToggleShow = (ImageView) findViewById(R.id.my_wallet_show_im);

        mAdapter = new CommonAdapter<>(this, null, CommonAdapter.AdapterType.TYPE_ADAPTER_WALLET);
        mListView.setAdapter(mAdapter);
    }

    private void bindViewData(Wallet wallet) {
        mTvAmount.setText(String.valueOf(wallet.wallet));
        mAdapter.setList(wallet.list);
        mAdapter.notifyDataSetChanged();
        money = wallet.wallet;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getWalletRequest();
    }

    private int clickNum = 1;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_wallet_show_tv:
                if (++clickNum % 2 == 0) {
                    mListView.setVisibility(View.INVISIBLE);
                    mIvToggleShow.setBackgroundResource(R.mipmap.ic_arrow_down);
                } else {
                    mListView.setVisibility(View.VISIBLE);
                    mIvToggleShow.setBackgroundResource(R.mipmap.ic_arrow_up);
                }
                break;
            case R.id.my_wallet_change_btn:
                Intent intent = new Intent(this, MallActivity.class);
                startActivity(intent);
                break;
            case R.id.my_wallet_invite_btn:
                BonusDialog dialog = new BonusDialog(this);
                dialog.show();
                break;
        }
    }


    private int mListSeq = 0;
    private final int mReqCount = 10;
    private Type mWalletType = new TypeToken<Result<Wallet>>(){}.getType();
    private void getWalletRequest() {
        URI uri = URIUtil.getMyWallet(Session.getSession().coachid, mListSeq, mReqCount);
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }

        Map map = new HashMap<>();
        map.put(NetConstants.KEY_AUTHORIZATION, Session.getToken());
        GsonIgnoreCacheHeadersRequest<Result<Wallet>> request = new GsonIgnoreCacheHeadersRequest<Result<Wallet>>(
                Request.Method.GET, url, null, mWalletType, map,
                new Response.Listener<Result<Wallet>>() {
                    @Override
                    public void onResponse(Result<Wallet> response) {
                        if (response != null) {
                            if (response.type == Result.RESULT_OK && response.data != null) {
                                bindViewData(response.data);
                            } else if (!TextUtils.isEmpty(response.msg)) {
                                ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(response.msg);
                            }
                        } else {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.net_err);
                        }
                        if (Constants.DEBUG) {
                            VolleyLog.v("Response:%n %s", response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.net_err);
                    }
                });
        // 请求加上Tag,用于取消请求
        request.setTag(this);
        request.setShouldCache(false);

        VolleyUtil.getQueue(this).add(request);
    }

    @Override
    public void onLoadMore() {
        if (mAdapter != null) {
            mListSeq = mAdapter.getItem(mAdapter.getCount() - 1).seqindex;
        }
        getWalletRequest();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        money = 0;
    }
}
