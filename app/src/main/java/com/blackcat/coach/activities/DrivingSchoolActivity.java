package com.blackcat.coach.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.adapters.CommonAdapter;
import com.blackcat.coach.models.DrivingSchool;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.UpdateCoachParams;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;

import de.greenrobot.event.EventBus;

public class DrivingSchoolActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ListView mListView;
    private EditText mEtSearch;
    private CommonAdapter<DrivingSchool> mAdapter;
    private List<DrivingSchool> mDrivingSchoolList;
    protected URI mURI = null;
    protected Type mType = new TypeToken<Result<List<DrivingSchool>>>() {}.getType();

    private String mLatitude;
    private String mLongitude;
    private String mRadius = "1000";
    private String mCity = "北京";
    private String mSearchText;

    public LocationClient mLocationClient = null;
    public BDLocationListener mLocationListener = new BCLocationListener();

    private MenuItem mCityItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving_school);
        configToolBar(R.mipmap.ic_back);
        initViews();
        getLocation();
    }

    private void initViews() {
        mListView = (ListView) findViewById(R.id.inner_list);
		mListView.setOnItemClickListener(this);
        mListView.setDividerHeight(0);
        mListView.setDivider(null);
        mEtSearch = (EditText) findViewById(R.id.et_search);
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mSearchText = mEtSearch.getText().toString().trim();
                getSchoolbyName();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    private void refresh() {
        if (mAdapter == null) {
            mAdapter = new CommonAdapter<DrivingSchool>(this, null, CommonAdapter.AdapterType.TYPE_ADAPTER_DRIVING);
            mListView.setAdapter(mAdapter);
        }
        mAdapter.setList(mDrivingSchoolList);
        mAdapter.notifyDataSetChanged();
    }

    private void getLocation() {
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(mLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1500;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.SetIgnoreCacheException(true);//可选，默认false，设置是否收集CRASH信息，默认收集
        mLocationClient.setLocOption(option);

        mLocationClient.start();
    }

    @Override
    protected void onDestroy() {
        mLocationClient.unRegisterLocationListener(mLocationListener);
        super.onDestroy();
    }

    public void getNearbyDriverSchool() {

        if (Session.isUserInfoEmpty()) {
            return;
        }
        mURI = URIUtil.getNearbySchoolList(mLatitude, mLongitude, mRadius);
        String url = null;
        if (mURI != null) {
            try {
                url = mURI.toURL().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }
        GsonIgnoreCacheHeadersRequest<Result<List<DrivingSchool>>> request = new GsonIgnoreCacheHeadersRequest<Result<List<DrivingSchool>>>(
                url, mType, null,
                new Response.Listener<Result<List<DrivingSchool>>>() {
                    @Override
                    public void onResponse(Result<List<DrivingSchool>> response) {
                        if(response != null && response.type == Result.RESULT_OK && response.data != null) {
                            mDrivingSchoolList = response.data;
                            if (mDrivingSchoolList != null) {
                                refresh();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.net_err + arg0.getMessage());
                    }
                });
        // 请求加上Tag,用于取消请求
        request.setTag(this);
        request.setManuallyRefresh(true);
        VolleyUtil.getQueue(this).add(request);
    }

    private void getSchoolbyName() {
        if (Session.isUserInfoEmpty() || TextUtils.isEmpty(mSearchText)) {
            return;
        }
        mURI = URIUtil.getSchoolbyNameList(mSearchText);
        String url = null;
        if (mURI != null) {
            try {
                url = mURI.toURL().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }
        GsonIgnoreCacheHeadersRequest<Result<List<DrivingSchool>>> request = new GsonIgnoreCacheHeadersRequest<Result<List<DrivingSchool>>>(
                url, mType, null,
                new Response.Listener<Result<List<DrivingSchool>>>() {
                    @Override
                    public void onResponse(Result<List<DrivingSchool>> response) {
                        if(response != null && response.type == Result.RESULT_OK && response.data != null) {
                            mDrivingSchoolList = response.data;
                            if (mDrivingSchoolList != null) {
                                refresh();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.net_err + arg0.getMessage());
                    }
                });
        // 请求加上Tag,用于取消请求
        request.setTag(this);
        request.setShouldCache(false);
        VolleyUtil.getQueue(this).add(request);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DrivingSchool ds = mAdapter.getItem(position);
        if (!Session.isUserInfoEmpty()) {EventBus.getDefault().post(ds);
//            Session.getSession().driveschoolinfo = ds;
//            Session.save(Session.getSession(), true);
//            finish();

            updateRequest(ds);
        }
    }

    protected Type mUpdateType = new TypeToken<Result>() {}.getType();
    private void updateRequest(final DrivingSchool ds) {
        UpdateCoachParams params = new UpdateCoachParams(Session.getSession());
        params.driveschoolinfo = ds;
        params.driveschoolid = ds.id;
        params.coachid = Session.getSession().coachid;
        Session.getSession().updateRequest(this, params);
    }

    public class BCLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            //Receive Location
            if (location.getLocType() == BDLocation.TypeGpsLocation ||
                    location.getLocType() == BDLocation.TypeNetWorkLocation ||
                    location.getLocType() == BDLocation.TypeOffLineLocation ) {
                mLatitude =  Double.toString(location.getLatitude());
                mLongitude = Double.toString(location.getLongitude());
                if( !TextUtils.isEmpty(location.getCity()) ) {
                    mCity = location.getCity();
                    if (mCityItem != null) {
                        mCityItem.setTitle(mCity);
                    }
                }

                getNearbyDriverSchool();

            } else if (location.getLocType() == BDLocation.TypeServerError) {

            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {

            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_driving_school, menu);
        mCityItem = menu.findItem(R.id.action_city);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }
}
