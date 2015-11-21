package com.blackcat.coach.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.adapters.CommonAdapter;
import com.blackcat.coach.models.Course;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.CoachClassesParams;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.GsonUtils;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.UIUtils;
import com.blackcat.coach.utils.VolleyUtil;
import com.blackcat.coach.widgets.CheckableRelativeLayout;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassesSettingsActivity extends BaseActivity {

    private List<Course> mContent;
    private ListView mList;
    private CommonAdapter<Course> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes_settings);
        configToolBar(R.mipmap.ic_back);

        mList = (ListView) findViewById(R.id.list);
        ColorDrawable color = new ColorDrawable(this.getResources().getColor(R.color.transparent));
        mList.setDivider(color);
        mList.setDividerHeight(UIUtils.dip2px(this, 10));
        mAdapter = new CommonAdapter(this, mContent, CommonAdapter.AdapterType.TYPE_ADAPTER_COURSE);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view instanceof CheckableRelativeLayout) {
                    ((CheckableRelativeLayout) view).toggle();
                    boolean state = mAdapter.getList().get(position).is_choose;
                    mAdapter.getList().get(position).is_choose = !state;
                }
            }
        });
        fetchClassesRequest();
    }

    private Type mTokenType = new TypeToken<Result<List<Course>>>() {
    }.getType();

    protected void fetchClassesRequest() {
        URI uri = URIUtil.getClassesList();
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }

        Map map = new HashMap();
        map.put("authorization", Session.getToken());
        GsonIgnoreCacheHeadersRequest<Result<List<Course>>> request = new GsonIgnoreCacheHeadersRequest<Result<List<Course>>>(
                Request.Method.GET, url, null, mTokenType, map,
                new Response.Listener<Result<List<Course>>>() {
                    @Override
                    public void onResponse(Result<List<Course>> response) {
                        if (response != null && response.data != null) {
                            mAdapter.setList(response.data);
                            mAdapter.notifyDataSetChanged();
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
        request.setShouldCache(true);
        request.setManuallyRefresh(true);
        VolleyUtil.getQueue(this).add(request);
    }


    private Type mType = new TypeToken<Result>() {}.getType();

    protected void saveClassesRequest() {
        URI uri = URIUtil.getSaveClasses();
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }

        CoachClassesParams params = new CoachClassesParams();
        params.coachid = Session.getSession().coachid;
        params.classtypelist = "";
        List<Course> list = mAdapter.getList();
        if (list != null) {
            StringBuilder sb = new StringBuilder();
            for (Course item : list) {
                if (item.is_choose) {
                    sb.append(item.classid);
                    sb.append(",");
                }
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            params.classtypelist = sb.toString();
        }

        Map map = new HashMap();
        map.put("authorization", Session.getToken());
        GsonIgnoreCacheHeadersRequest<Result> request = new GsonIgnoreCacheHeadersRequest<Result>(
                Request.Method.POST, url, GsonUtils.toJson(params), mType, map,
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_classes_settings, menu);
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
            saveClassesRequest();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
