package com.blackcat.coach.models;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.events.UpdateCoachInfoError;
import com.blackcat.coach.events.UpdateCoachInfoOk;
import com.blackcat.coach.models.params.UpdateCoachParams;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.NetConstants;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.GsonUtils;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by zou on 15/10/23.
 */
public class CoachInfo implements Serializable {

    public String coachid;
    public String mobile;
    public String name;
    public String createtime;
    public String email;
    public HeadPortraint headportrait;
    public String logintime;
    public String invitationcode;
    public String displaycoachid;
    public boolean is_lock;
    public String address;
    public String Gender;
    public boolean is_validation;
    public String validationstate;
    public DrivingSchool driveschoolinfo;
    public int studentcoount;
    public int commentcount;
    public String Seniority;
    public int passrate;
    public int starlevel;
    public List<Subject> subject;
    public CarModel carmodel;
    public TrainField trainfieldlinfo;
    public boolean is_shuttle;
    public String coachnumber;
    public String platenumber;
    public String drivinglicensenumber;
    public String shuttlemsg;
    public String introduction;
    public String worktimedesc;
    //worktime
    public int[] workweek;
    public Worktimespace worktimespace;
    public String token;
    public String idcardnumber;
    public String coursestudentcount;
    public String driveschoolid;
    /**标签*/
    public LabelBean[] tagslist;


    // 更新个人信息
    public static void updateRequest(final Activity activity, final UpdateCoachParams params) {

        if (Session.getSession() == null || activity == null) {
            return;
        }
        Type type = new TypeToken<Result>() {}.getType();
        URI uri = URIUtil.updateCoachInfo();
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("tag","update-->"+url +GsonUtils.toJson(params));
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Map map = new HashMap<>();
        map.put(NetConstants.KEY_AUTHORIZATION, Session.getToken());

        GsonIgnoreCacheHeadersRequest<Result> request = new GsonIgnoreCacheHeadersRequest<Result>(
                Request.Method.POST, url, GsonUtils.toJson(params), type, map,
                new Response.Listener<Result>() {
                    @Override
                    public void onResponse(Result response) {
                        if (response != null) {
                            if (response.type == Result.RESULT_OK) {
                                ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.op_ok);
                                Session.getSession().updateCoachInfo(params);
                                Session.save(Session.getSession(), true);
                                EventBus.getDefault().post(new UpdateCoachInfoOk());
                                activity.finish();
                            } else if (!TextUtils.isEmpty(response.msg)) {
                                ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(response.msg);
                                EventBus.getDefault().post(new UpdateCoachInfoError());
                            }
                        } else {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.net_err);
                            EventBus.getDefault().post(new UpdateCoachInfoError());
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
                        EventBus.getDefault().post(new UpdateCoachInfoError());
                    }
                });
        // 请求加上Tag,用于取消请求
        request.setTag(activity);
        request.setShouldCache(false);
        VolleyUtil.getQueue(activity).add(request);
    }

    public static void updateCoachInfo(UpdateCoachParams params) {
        Session.getSession().coachid = params.coachid;
        Session.getSession().name = params.name;
        Session.getSession().subject = params.subject;
        Session.getSession().introduction = params.introduction;
        Session.getSession().Seniority = params.Seniority;
        Session.getSession().coachnumber = params.coachnumber;
        Session.getSession().Gender = params.Gender;
        Session.getSession().drivinglicensenumber = params.drivinglicensenumber;
        Session.getSession().idcardnumber = params.idcardnumber;
        Session.getSession().mobile = params.mobile;
        Session.getSession().driveschoolinfo = params.driveschoolinfo;
        Session.getSession().driveschoolid = params.driveschoolid;
        Session.getSession().trainfieldlinfo = params.trainfieldlinfo;
    }
}
