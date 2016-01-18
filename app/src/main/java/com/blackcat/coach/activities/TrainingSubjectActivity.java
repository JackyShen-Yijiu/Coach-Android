package com.blackcat.coach.activities;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.Subject;
import com.blackcat.coach.models.params.UpdateCoachParams;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class TrainingSubjectActivity extends BaseActivity {

    private ListView mListView;
    private List<Subject> mSubjectList;
    private ArrayAdapter mAdapter;
    private String[] mSubjects;
    protected URI mURI = null;
    protected Type mType = new TypeToken<Result<List<Subject>>>() {}.getType();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_subject);
        configToolBar(R.mipmap.ic_back);
        initView();
        getSubjectRequest();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.lv_list);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    private void bindListData() {
        if (mAdapter == null) {
            mAdapter = new ArrayAdapter(this, R.layout.row_select_item, mSubjects);
            mListView.setAdapter(mAdapter);
        }
        if (Session.getSession().subject != null) {
            for(Subject sub : Session.getSession().subject) {
                for (int i = 0; i < mSubjects.length; i++) {
                    if (!TextUtils.isEmpty(sub.name) && sub.name.equals(mSubjects[i])) {
                        mListView.setItemChecked(i, true);
                    }
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void getSubjectRequest() {
        if (Session.isUserInfoEmpty()) {
            //return;
        }
        mURI = URIUtil.getSubjectsList();
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
        GsonIgnoreCacheHeadersRequest<Result<List<Subject>>> request = new GsonIgnoreCacheHeadersRequest<Result<List<Subject>>>(
                url, mType, null,
                new Response.Listener<Result<List<Subject>>>() {
                    @Override
                    public void onResponse(Result<List<Subject>> response) {
                        if (response != null && response.type == Result.RESULT_OK && response.data != null) {
                            mSubjectList = response.data;
                            mSubjects = new String[mSubjectList.size()];
                            int index = 0;
                            for (Subject subject : mSubjectList) {
                                if (TextUtils.isEmpty(subject.name)) {
                                    mSubjects[index++] = "";
                                } else {
                                    mSubjects[index++] = subject.name;
                                }
                            }
                            bindListData();
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

    private void updateCoachInfo() {
        if (mSubjectList == null || mSubjectList.size() <= 0) {
            return;
        }
        List<Subject> checkedSubjects = new ArrayList<>();
        SparseBooleanArray checkedPos = mListView.getCheckedItemPositions();
        for (int i = 0; i < mSubjectList.size(); i++) {
            if(i ==1){

            }else if(i==2){

            }
            if (checkedPos.get(i) == true) {
                checkedSubjects.add(mSubjectList.get(i));
            }
        }
        Session.getSession().subject = checkedSubjects;
        updateRequest(checkedSubjects);
    }


    private void updateRequest(final List<Subject> checkedSubject) {
        UpdateCoachParams params = new UpdateCoachParams(Session.getSession());
        params.subject = checkedSubject;
        Session.getSession().updateRequest(this, params);
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
}
