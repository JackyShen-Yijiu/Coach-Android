package com.blackcat.coach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.events.BuyOkEvent;
import com.blackcat.coach.models.Product;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.BuyParams;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.NetConstants;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.GsonUtils;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class ProductOrderActivity extends BaseNoFragmentActivity {

    private Product mProduct;

    private EditText mEtName, mEtMobile, mEtAddr;
    private TextView mTvMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_order);
        configToolBar(R.mipmap.ic_back);
        mProduct = (Product) getIntent().getSerializableExtra(Constants.DETAIL);
        if (mProduct == null) {
            finish();
            return;
        }
        mTvMoney = (TextView) findViewById(R.id.product_order_currentcy_tv);
        mTvMoney.setText(String.valueOf(WalletActivity.money));
        mEtName = (EditText) findViewById(R.id.product_order_nameet);
        mEtMobile = (EditText) findViewById(R.id.product_order_phoneet);
        mEtAddr = (EditText) findViewById(R.id.product_order_addresset);

        findViewById(R.id.product_order_buy_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mEtName.getText())) {
                    ToastHelper.getInstance(ProductOrderActivity.this).toast("姓名不能为空");
                    return;
                }
                if (TextUtils.isEmpty(mEtMobile.getText())) {
                    ToastHelper.getInstance(ProductOrderActivity.this).toast("手机号码不能为空");
                    return;
                }
                if (TextUtils.isEmpty(mEtAddr.getText())) {
                    ToastHelper.getInstance(ProductOrderActivity.this).toast("订单地址不能为空");
                    return;
                }
                buyRequest(mEtName.getText().toString(), mEtMobile.getText().toString(), mEtAddr.getText().toString());
            }
        });
    }

    private Type mTokenType = new TypeToken<Result>() {}.getType();

    private void buyRequest(String name, String mobile, final String addr) {
        URI uri = URIUtil.getBuyProduct();
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }
        BuyParams param = new BuyParams();
        param.address = addr;
        param.name = name;
        param.mobile = mobile;
        param.usertype = Constants.USR_TYPE_COACH;
        param.productid = mProduct.productid;
        param.userid = Session.getSession().coachid;

        Map map = new HashMap<>();
        map.put(NetConstants.KEY_AUTHORIZATION, Session.getToken());

        GsonIgnoreCacheHeadersRequest<Result> request = new GsonIgnoreCacheHeadersRequest<Result>(
                Request.Method.POST, url, GsonUtils.toJson(param), mTokenType, map,
                new Response.Listener<Result>() {
                    @Override
                    public void onResponse(Result response) {
                        if (response != null && response.data != null && response.type == Result.RESULT_OK) {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.op_ok);
                            EventBus.getDefault().post(new BuyOkEvent());
                            startActivity(new Intent(ProductOrderActivity.this, ProductOrderSuccActivity.class));
                            finish();
                        } else {
                            if (!TextUtils.isEmpty(response.msg)) {
                                ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(response.msg);
                            } else {
                                ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.net_err);
                            }
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
}
