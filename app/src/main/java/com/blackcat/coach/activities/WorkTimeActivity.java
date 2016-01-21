package com.blackcat.coach.activities;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.events.SetTimeEvent;
import com.blackcat.coach.fragments.TimePickerFragment;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.Worktimespace;
import com.blackcat.coach.models.params.WorktimeParams;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.BaseUtils;
import com.blackcat.coach.utils.BaseUtils.TimeOfDay;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.GsonUtils;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class WorkTimeActivity extends BaseActivity implements View.OnClickListener {

    private ListView mListView;
    private TextView mTvStartTime, mTvEndTime;
    private ArrayAdapter mAdapter;
    private String[] mWorktime;
    protected URI mURI = null;
    protected Type mType = new TypeToken<Result>() {}.getType();
    private TimeOfDay mStartTime, mEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_time);
        configToolBar(R.mipmap.ic_back);
        initViews();
        EventBus.getDefault().register(this);
    }

    private void initViews() {
        mListView = (ListView) findViewById(R.id.lv_list);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mWorktime = getResources().getStringArray(R.array.worktime);
        mAdapter = new ArrayAdapter(this, R.layout.row_select_item, mWorktime);
        mListView.setAdapter(mAdapter);
        if (Session.getSession().workweek != null) {
            int[] workweek = Session.getSession().workweek;
            int len = workweek.length;
            for (int i = 0; i < len; i++) {
                if (workweek[i] >= 1 && workweek[i] <= 7) {
                    mListView.setItemChecked(workweek[i] - 1, true);
                }
            }
        }
        findViewById(R.id.rl_start_time).setOnClickListener(this);
        findViewById(R.id.rl_end_time).setOnClickListener(this);
        mTvStartTime = (TextView) findViewById(R.id.tv_start_time);
        mTvEndTime = (TextView) findViewById(R.id.tv_end_time);

        mStartTime = new TimeOfDay();
        mStartTime.hourOfDay = 9;
        mEndTime = new TimeOfDay();
        mEndTime.hourOfDay = 17;
        if (Session.getSession().worktimespace != null) {
            mStartTime.hourOfDay = Session.getSession().worktimespace.begintimeint;
            mEndTime.hourOfDay = Session.getSession().worktimespace.endtimeint;
        }
        mTvStartTime.setText(BaseUtils.getTime(mStartTime));
        mTvEndTime.setText(BaseUtils.getTime(mEndTime));
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.rl_start_time:
                showTimePickerDialog(v);
                break;
            case R.id.rl_end_time:
                showTimePickerDialog(v);
                break;
        }

    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = TimePickerFragment.newInstance(v.getId());
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void onEvent(SetTimeEvent e) {
        int id = e.viewId;
        switch (id) {
            case R.id.rl_start_time:
                mStartTime.hourOfDay = e.hourOfDay;
                mStartTime.minutes = e.minutes;
                mTvStartTime.setText(BaseUtils.getTime(mStartTime));
                break;
            case R.id.rl_end_time:
                mEndTime.hourOfDay = e.hourOfDay;
                mEndTime.minutes = e.minutes;
                mTvEndTime.setText(BaseUtils.getTime(mEndTime));
                break;
        }
    }

    private void editWorkTimeRequest() {
        URI uri = URIUtil.getSetWorkTime();
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }
        WorktimeParams param = new WorktimeParams();
        param.coachid = Session.getSession().coachid;
        param.begintimeint = mStartTime.hourOfDay + "";
        param.endtimeint = mEndTime.hourOfDay + "";
        param.worktimedesc = "";
//        mAdapter.notifyDataSetChanged();
        SparseBooleanArray sparseArray = mListView.getCheckedItemPositions();
//        int temp = 0;
        int size = mListView.getCount();
//        for(int k = 0;k < sparseArray.size();k++){
//            LogUtil.print(k+"work--week-count->"+sparseArray.get(k));
////            if(sparseArray.get(k)){
////                temp++;
////            }
//        }

        int count = sparseArray.size();

        final int[] worktime = new int[count];

        StringBuilder sb = new StringBuilder();
        int j = 0;
        for (int i = 0; i < size; i++) {
            if (sparseArray.get(i)) {
                sb.append(i+1);
                sb.append(",");
                worktime[j] = i+1;
                j++;
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        //截取掉最后一位或者多位0
        int temp3 = worktime.length-1;
        int count1 = count;//(worktime[worktime.length-1]==0 && count>1)?count-1:count;
        while (worktime[temp3]==0 && count>1){
            count1 --;
            temp3--;
        }

        final int[] worktime1 = new int[count1];
        if(worktime[worktime.length-1]==0 && count>1){

            for (int i = 0;i<worktime1.length;i++) {
                worktime1[i] = worktime[i];
            }
        }else{
            for (int i = 0;i<worktime1.length;i++) {
                worktime1[i] = worktime[i];
            }
        }

        for (int i : worktime1) {
            LogUtil.print("work--week-result->"+i);
        }
        param.workweek = sb.toString();



        Map map = new HashMap();
        map.put("authorization", Session.getToken());
        GsonIgnoreCacheHeadersRequest<Result> request = new GsonIgnoreCacheHeadersRequest<Result>(
                Request.Method.POST, url, GsonUtils.toJson(param), mType, map,
                new Response.Listener<Result>() {
                    @Override
                    public void onResponse(Result response) {
                        if (response != null && response.type == Result.RESULT_OK) {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.op_ok);
                            Session.getSession().workweek = worktime1;
                            Session.getSession().worktimespace = new Worktimespace(mStartTime.hourOfDay, mEndTime.hourOfDay);
                            Session.save(Session.getSession(), true);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(ACTION_GROUP_ID, ACTION_MENUITEM_ID0, 0, R.string.action_finish);
        MenuItemCompat.setShowAsAction(item, MenuItem.SHOW_AS_ACTION_ALWAYS
                | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == ACTION_MENUITEM_ID0) {
            if (mStartTime.hourOfDay >= mEndTime.hourOfDay) {
                ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.str_worktime_invalid);
            } else {
                editWorkTimeRequest();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
