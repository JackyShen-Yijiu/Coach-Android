package com.blackcat.coach.activities;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.models.CoachInfo;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.TrainField;
import com.blackcat.coach.models.params.UpdateCoachParams;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;

public class TrainFieldActivity extends BaseActivity {

    private ListView mFieldListView;
    private TextView mTvDrivingSchool;
    private String[] mFieldListContent;
    private ArrayAdapter  mAdapter;

    private List<TrainField> mFieldList;
    protected URI mURI = null;
    protected Type mType = new TypeToken<Result<List<TrainField>>>() {}.getType();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Session.getSession().driveschoolinfo == null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_train_field);
        configToolBar(R.mipmap.ic_back);
        initView();
        getFieldRequest(Session.getSession().driveschoolinfo.id);
    }

    private void initView() {
        mTvDrivingSchool = (TextView)findViewById(R.id.tv_driving_school);
        mTvDrivingSchool.setText(Session.getSession().driveschoolinfo.name);
        mFieldListView = (ListView)findViewById(R.id.lv_list);
        mFieldListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    private void getFieldRequest(String schoolid) {
        if (Session.isUserInfoEmpty()) {
            return;
        }
        mURI = URIUtil.getFieldsList(schoolid);

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
        GsonIgnoreCacheHeadersRequest<Result<List<TrainField>>> request = new GsonIgnoreCacheHeadersRequest<Result<List<TrainField>>>(
                url, mType, null,
                new Response.Listener<Result<List<TrainField>>>() {
                    @Override
                    public void onResponse(Result<List<TrainField>> response) {
                        if(response != null && response.type == Result.RESULT_OK && response.data != null) {
                            mFieldList = response.data;
                            mFieldListContent = new String[mFieldList.size()];
                            int index = 0;
                            int pos = -1;
                            for (TrainField field : mFieldList){
                                if (TextUtils.isEmpty(field.name)){
                                    mFieldListContent[index++] = "";
                                } else {
                                    if (Session.getSession().trainfieldlinfo != null && field.name.equals(Session.getSession().trainfieldlinfo.name)) {
                                        pos = index;
                                    }
                                    mFieldListContent[index++] = field.name;
                                }
                            }
                            mAdapter = new ArrayAdapter(TrainFieldActivity.this, R.layout.row_select_item, mFieldListContent);
                            mFieldListView.setAdapter(mAdapter);
                            if (pos > -1) {
                                mFieldListView.setItemChecked(pos, true);
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
            updateCoachInfo();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateCoachInfo() {
        int checkedPos = mFieldListView.getCheckedItemPosition();
        if(checkedPos < 0) {
            return;
        }
        TrainField trainField = mFieldList.get(checkedPos);
        updateRequest(trainField);
    }

    protected Type mUpdateType = new TypeToken<Result>() {}.getType();
    private void updateRequest(final TrainField trainField) {
        UpdateCoachParams params = new UpdateCoachParams(Session.getSession());
        params.trainfieldlinfo = trainField;
        CoachInfo.updateRequest(this, params);
    }
}
