package com.blackcat.coach.activities;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.events.SetDateEvent;
import com.blackcat.coach.events.SetTimeEvent;
import com.blackcat.coach.fragments.DatePickerFragment;
import com.blackcat.coach.fragments.TimePickerFragment;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.VacationParams;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.NetConstants;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.BaseUtils;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.GsonUtils;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class VacationActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTvStartDate, mTvStartTime, mTvEndDate, mTvEndTime;
    private SetTimeEvent mStartTime, mEndTime;
    private SetDateEvent mStartDate, mEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation);
        configToolBar(R.mipmap.ic_back);
        initViews();
        EventBus.getDefault().register(this);
    }

    private void initViews() {
        mTvStartDate = (TextView) findViewById(R.id.tv_start_date);
        mTvStartTime = (TextView) findViewById(R.id.tv_start_time);
        mTvEndDate = (TextView) findViewById(R.id.tv_end_date);
        mTvEndTime = (TextView) findViewById(R.id.tv_end_time);

        final Calendar c = Calendar.getInstance();
        int year;
        int month;
        int day ;
        int hour = c.get(Calendar.HOUR_OF_DAY);

        mStartDate = new SetDateEvent();
        mEndDate = new SetDateEvent();
        if (hour < 12) {
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            mStartDate.year = year;
            mStartDate.month = month;
            mStartDate.day = day;

            c.add(Calendar.DATE, 1);
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            mEndDate.year = year;
            mEndDate.month = month;
            mEndDate.day = day;
        } else {
            c.add(Calendar.DATE, 1);
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            mStartDate.year = year;
            mStartDate.month = month;
            mStartDate.day = day;

            c.add(Calendar.DATE, 1);
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            mEndDate.year = year;
            mEndDate.month = month;
            mEndDate.day = day;
        }

        mTvStartDate.setText(BaseUtils.getDate(mStartDate));
        mTvEndDate.setText(BaseUtils.getDate(mEndDate));
        mTvEndTime.setOnClickListener(this);
        mTvEndDate.setOnClickListener(this);
        mTvStartTime.setOnClickListener(this);
        mTvStartDate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_start_date:
                showDatePickerDialog(v);
                break;
            case R.id.tv_start_time:
                showTimePickerDialog(v);
                break;
            case R.id.tv_end_date:
                showDatePickerDialog(v);
                break;
            case R.id.tv_end_time:
                showTimePickerDialog(v);
                break;
        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = TimePickerFragment.newInstance(v.getId());
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = DatePickerFragment.newInstance(v.getId());
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vacation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_finish) {
            if (mStartDate == null || mEndDate == null) {
                ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.date_empty);
            } else {
                editVacationRequest();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onEvent(SetDateEvent e) {
        int id = e.viewId;
        switch (id) {
            case R.id.tv_start_date:
                mStartDate = e;
                mTvStartDate.setText(BaseUtils.getDate(e));
                break;
            case R.id.tv_end_date:
                mEndDate = e;
                mTvEndDate.setText(BaseUtils.getDate(e));
                break;
        }
    }

    public void onEvent(SetTimeEvent e) {
        int id = e.viewId;
        switch (id) {
            case R.id.tv_start_time:
                mStartTime = e;
                mTvStartTime.setText(BaseUtils.getTime(e));
                break;
            case R.id.tv_end_time:
                mEndTime = e;
                mTvEndTime.setText(BaseUtils.getTime(e));
                break;
        }
    }

    protected Type mType = new TypeToken<Result>() {}.getType();
    private void editVacationRequest() {
        URI uri = URIUtil.getSetVacation();
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }
        VacationParams param = new VacationParams();
        param.coachid = Session.getSession().coachid;
        long start, end;
        start = BaseUtils.getTimestamp(mStartDate, mStartTime);
        end = BaseUtils.getTimestamp(mEndDate, mEndTime);
        if (start >= end) {
            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.str_vacation_invalid);
        }
        param.begintime = start + "";
        param.endtime = end + "";

        Map map = new HashMap();
        map.put(NetConstants.KEY_AUTHORIZATION, Session.getToken());
        GsonIgnoreCacheHeadersRequest<Result> request = new GsonIgnoreCacheHeadersRequest<Result>(
                Request.Method.POST, url, GsonUtils.toJson(param), mType, map,
                new Response.Listener<Result>() {
                    @Override
                    public void onResponse(Result response) {
                        if (response != null && response.type == Result.RESULT_OK) {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.op_ok);
                            finish();
                        } else if (!TextUtils.isEmpty(response.msg)) {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(response.msg);
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
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
