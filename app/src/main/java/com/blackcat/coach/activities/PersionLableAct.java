package com.blackcat.coach.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.blackcat.coach.events.ReservationOpOk;
import com.blackcat.coach.models.ReservationStatus;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.CommentParams;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.GsonUtils;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.blackcat.coach.widgets.WordWrapView;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 个性标签
 */
public class PersionLableAct extends BaseActivity {

    private WordWrapView wordWrapDefault,wordWrapCustom;
    /**标签输入*/
    private EditText etLabel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_persion_lable);
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.activity_persion_lable);
        initView();

    }

    private void initView() {
        wordWrapDefault = (WordWrapView) findViewById(R.id.act_persion_label_default);
        wordWrapCustom = (WordWrapView) findViewById(R.id.act_persion_label_custom);
        etLabel = (EditText) findViewById(R.id.act_persion_label_et);
     }

    /**
     * 默认标签
     */
    private void addDefaultLabel(){
        if(wordWrapDefault.getChildCount()>0){
            return;
        }
        wordWrapDefault.removeAllViews();
        String[] strs = {"个性标签","包接送","五星级教练","不吸烟","态度极好","免费提供水服务","不收彩礼"};
        for (int i = 0; i < strs.length; i++) {
            TextView textview = new TextView(this);
            textview.setText(strs[i]);
            wordWrapDefault.addView(textview);
        }
    }

    /**
     * 自定义标签
     */
    private void addCustomLabel(){
        if(wordWrapCustom.getChildCount()>0){
            return;
        }
        wordWrapCustom.removeAllViews();
        String[] strs = {"个性标签","包接送","五星级教练","不吸烟","态度极好","免费提供水服务","不收彩礼"};
        for (int i = 0; i < strs.length; i++) {
            TextView textview = new TextView(this);
            textview.setText(strs[i]);
            wordWrapCustom.addView(textview);
        }
    }


    public void onClick(View view){
        switch(view.getId()){
            case R.id.act_persion_lable_submit://提交新的标签
                if(check()){

                }
                break;
        }
    }

    /**
     * 输入是否合法
     * @return
     */
    private boolean check(){
        if(TextUtils.isEmpty(etLabel.getText().toString())){//不能为空
            return false;
        }else if(etLabel.getText().toString().trim().length()>5){//不能超过5个字
            return false;
        }
        return true;
    }

    private void requestNew(){

    }
    private Type mTokenType = new TypeToken<Result>() {}.getType();

    private void sendCommentRequest(String starlevel, String attitude, String ability, String time, String content) {
        URI uri = URIUtil.getCoachComment();
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }
        CommentParams param = new CommentParams();
        param.coachid = Session.getSession().coachid;
//        param.reservationid = mReservation._id;
        param.commentcontent = content;
        param.abilitylevel = ability;
        param.starlevel = starlevel;
        param.timelevel = time;
        param.attitudelevel = attitude;
        Map map = new HashMap<>();
        map.put("authorization", Session.getToken());
        GsonIgnoreCacheHeadersRequest<Result> request = new GsonIgnoreCacheHeadersRequest<Result>(
                Request.Method.POST, url, GsonUtils.toJson(param), mTokenType, map,
                new Response.Listener<Result>() {
                    @Override
                    public void onResponse(Result response) {
//                        if (response != null && response.type == Result.RESULT_OK) {
//                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.op_ok);
//                            ReservationOpOk event = new ReservationOpOk();
//                            event.pos = mReservation.pos;
//                            event.status = ReservationStatus.FINISH;
//                            EventBus.getDefault().post(event);
//                            finish();
//                        } else if (!TextUtils.isEmpty(response.msg)) {
//                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(response.msg);
//                        }
//                        if (Constants.DEBUG) {
//                            VolleyLog.v("Response:%n %s", response);
//                        }
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


    private void requestOk(){

    }


}
