package com.blackcat.coach.activities;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.models.LabelBean;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.TagsBean;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.NetConstants;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.GsonUtils;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.blackcat.coach.widgets.WordWrapView;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 个性标签
 */
public class PersionLableAct<T> extends BaseActivity implements View.OnClickListener {

    private WordWrapView wordWrapDefault,wordWrapCustom;
    /**标签输入*/
    private EditText etLabel;
    /**系统标签*/
    private List<LabelBean> systemlabel = new ArrayList<LabelBean>();
    /**自定义标签*/
    private List<LabelBean> personlabel = new ArrayList<LabelBean>();
    /**添加*/
    private Set<String> addList = new ArraySet<String>() ;

    /**减少*/
    private Set<String> decreaseList = new ArraySet<String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persion_lable);
//        setContentView(R.layout.activity_persion_lable);
        initView();
        requestLabels();
    }

    private void initView() {
        ((TextView) findViewById(R.id.toolbar_title_right)).setText("完成");
//        mToolBar.setTitle("个性标签");
//        mToolBarTitle.setText("个性标签");
        configToolBar(R.mipmap.ic_back);
        wordWrapDefault = (WordWrapView) findViewById(R.id.act_persion_label_default);
        wordWrapCustom = (WordWrapView) findViewById(R.id.act_persion_label_custom);
        etLabel = (EditText) findViewById(R.id.act_persion_label_et);
        findViewById(R.id.toolbar_title_right).setOnClickListener(this);
     }

    /**
     * 默认标签
     */
    private void addDefaultLabel(final LabelBean[] labels){
        if(wordWrapDefault.getChildCount()>0){
            return;
        }
        wordWrapDefault.removeAllViews();
        for (LabelBean label : labels) {
            systemlabel.add(label);
        }

        wordWrapDefault.setData(systemlabel);

//        String[] strs = {"个性标签","包接送","五星级教练","不吸烟","态度极好","免费提供水服务","不收彩礼"};
        for (int i = 0; i < labels.length; i++) {
            TextView textview = new TextView(this);
            textview.setText(labels[i].tagname);
            textview.setClickable(true);
            textview.setTag(i);
            textview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int tag = Integer.parseInt(view.getTag().toString());
                    if(labels[tag].is_choose){//已经选中了  不选中
                        DecreaseSystemLabel(labels[tag]);
                        view.setBackgroundColor(Color.GRAY);
                    }else{//未选中// )
                        addSystemLabel(labels[tag]);
                        if(labels[tag].color!=null)
                            view.setBackgroundColor(Color.parseColor(labels[tag].color));
                    }
//                    Toast.makeText(PersionLableAct.this,tag,Toast.LENGTH_SHORT).show();

                }
            });
            wordWrapDefault.addView(textview);
        }
    }

    /**
     * 自定义标签
     */
    private void addCustomLabel(LabelBean[] labels){
        if(wordWrapCustom.getChildCount()>0){
            return;
        }
        wordWrapCustom.removeAllViews();
        for (LabelBean label : labels) {
            personlabel.add(label);
        }
        wordWrapCustom.setData(personlabel);
//        String[] strs = {"个性标签","包接送","五星级教练","不吸烟","态度极好","免费提供水服务","不收彩礼"};
        for (int i = 0; i < labels.length; i++) {
            TextView textview = new TextView(this);
            textview.setText(labels[i].tagname);
            textview.setTag(labels[i]._id);
            textview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDeleteDialog(view.getTag().toString());
                }
            });
            wordWrapCustom.addView(textview);
        }
    }

    /**
     * 添加一个 标签
     * @param bean
     */
    private void addLabel(LabelBean bean){
        personlabel.add(bean);

//        for (int i = 0; i < labels.length; i++) {
            TextView textview = new TextView(this);
            textview.setText(bean.tagname);
            textview.setTag(bean._id);
            textview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDeleteDialog(view.getTag().toString());
                }
            });
            wordWrapCustom.addView(textview);
//        }

    }



    private void addSystemLabel(LabelBean bean){
        addList.add(bean._id);
        //减少
        decreaseList.remove(bean._id);
        //已经选择
        bean.is_choose = true;
    }

    private void DecreaseSystemLabel(LabelBean bean){
        decreaseList.add(bean._id);
        //减少
        addList.remove(bean._id);
        //已经选择
        bean.is_choose = false;
    }





    public void onClick(View view){
        switch(view.getId()){
            case R.id.act_persion_lable_submit://提交新的标签
                if(check()){
                    sendNewLabelRequest(etLabel.getText().toString());
                }
                break;
            case R.id.toolbar_title_right://完成
                requestOk();
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

    /***
     * 请求标签列表
     */
    private void requestLabels(){

        Type mType = new TypeToken<Result<TagsBean>>() {}.getType();

        URI uri = URIUtil.getLabels();
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
        map.put("authorization", Session.getToken());
        GsonIgnoreCacheHeadersRequest<Result<T>> request = new GsonIgnoreCacheHeadersRequest<Result<T>>(
                Request.Method.GET, url, null, mType, map,
                new Response.Listener<Result<T>>() {
                    @Override
                    public void onResponse(Result<T> response) {
                        if (response != null && response.type == Result.RESULT_OK) {
                            Log.d("tag", "result-->" + response.data);
                            TagsBean bean = (TagsBean) response.data;
                            addDefaultLabel(bean.systemtag);
                            addCustomLabel(bean.selft);
//                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.op_ok);

                        } else if (!TextUtils.isEmpty(response.msg)) {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(response.msg);
                        }
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

//    private Type mTokenType = new TypeToken<Result>() {}.getType();
    private Type mAddType = new TypeToken<Result<LabelBean>>() {}.getType();

//
    /**
     * 添加新的标签
     * @param tagName
     *
     */
    private void sendNewLabelRequest(String tagName) {
        URI uri = URIUtil.getLabelAdd();
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(url)) {
            return;
        }
        CoachTagParams param = new CoachTagParams();
        param.coachid = Session.getSession().coachid;
        param.tagname = tagName;
        LogUtil.print("coachid->"+param.coachid+"<<--name>"+param.tagname);
        Map map = new HashMap<>();
        map.put(NetConstants.KEY_AUTHORIZATION, Session.getToken());
        GsonIgnoreCacheHeadersRequest<Result<LabelBean>> request = new GsonIgnoreCacheHeadersRequest<Result<LabelBean>>(
                Request.Method.POST, url, GsonUtils.toJson(param), mAddType, map,
                new Response.Listener<Result<LabelBean>>() {
                    @Override
                    public void onResponse(Result<LabelBean> response) {
                        if (response != null && response.type == Result.RESULT_OK) {
                            Log.d("tag", "result-->" + response.data);
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.op_ok);
//                            ReservationOpOk event = new ReservationOpOk();
//                            event.pos = mReservation.pos;
//                            event.status = ReservationStatus.FINISH;
//                            EventBus.getDefault().post(event);
//                            finish();
                        } else if (!TextUtils.isEmpty(response.msg)) {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(response.msg);
                        }
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

    /**
     * 删除标签
     * @param tagid
     */
    private void sendDelLabelRequest(final String tagid) {
        URI uri = URIUtil.getLabelDelete();
        Type mDeleteType = new TypeToken<Result>() {}.getType();
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(url)) {
            return;
        }
        CoachDelteParams param = new CoachDelteParams();
        param.coachid = Session.getSession().coachid;
        param.tagid = tagid;
        Map map = new HashMap<>();
        map.put("authorization", Session.getToken());
        GsonIgnoreCacheHeadersRequest<Result> request = new GsonIgnoreCacheHeadersRequest<Result>(
                Request.Method.POST, url, GsonUtils.toJson(param), mDeleteType, map,
                new Response.Listener<Result>() {
                    @Override
                    public void onResponse(Result response) {
                        if (response != null && response.type == Result.RESULT_OK) {
                            Log.d("tag", "result-->" + response.data);
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.op_ok);
                            int temp = 0;
                            for(int i = 0;i<personlabel.size();i++){
                                if(personlabel.get(i)._id.equals(tagid)){
                                    temp = i;
                                    break;
                                }
                            }
                            personlabel.remove(temp);
                            wordWrapCustom.removeViewAt(temp);
                        } else if (!TextUtils.isEmpty(response.msg)) {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(response.msg);
                        }
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

    class CoachTagParams{
        String coachid;
        String tagname;
    }

    class CoachDelteParams{
        String coachid;
        String tagid;
    }


    private void requestOk(){
        Type mTokenType = new TypeToken<Result>() {}.getType();
        URI uri = URIUtil.setLable();
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }
        SetTagParams param = new SetTagParams();
        param.coachid = Session.getSession().coachid;
        param.tagslist = getSelectedystemTag();
        LogUtil.print("tags-->"+param.tagslist);
        Map map = new HashMap<>();
        map.put(NetConstants.KEY_AUTHORIZATION, Session.getToken());
        GsonIgnoreCacheHeadersRequest<Result> request = new GsonIgnoreCacheHeadersRequest<Result>(
                Request.Method.POST, url, GsonUtils.toJson(param), mTokenType, map,
                new Response.Listener<Result>() {
                    @Override
                    public void onResponse(Result response) {
                        if (response != null && response.type == Result.RESULT_OK) {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.op_ok);
//                            ReservationOpOk event = new ReservationOpOk();
//                            event.pos = mReservation.pos;
//                            event.status = ReservationStatus.FINISH;
//                            EventBus.getDefault().post(event);
//                            finish();
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
                        ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(arg0.getMessage()+"123"+arg0.getCause());
//                        ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.net_err);
                    }
                });
        // 请求加上Tag,用于取消请求
        request.setTag(this);
        request.setShouldCache(false);

        VolleyUtil.getQueue(this).add(request);
    }

    private String getSelectedystemTag(){
        String temp ="";
        String temp1 ="";
        //系统标签
        for (LabelBean labelBean : systemlabel) {
            if(labelBean.is_choose)
                temp = temp+labelBean._id+",";
        }
        if(temp.length()>0)
            temp = temp.substring(0,temp.length()-1);


        //所有的自定义标签
        for (LabelBean labelBean : personlabel) {
            if(labelBean.is_choose)
                temp1 = temp1+labelBean._id+",";
        }
        if(temp1.length()>0)
            temp1 = temp1.substring(0,temp1.length()-1);
        if(temp1.length()>0)
            temp = temp +","+ temp1;
         return temp;
    }

    class SetTagParams{
        String coachid;
        String tagslist;
    }

    private void showDeleteDialog(final String id){
        LayoutInflater inflater = LayoutInflater.from(this);
        final View textEntryView = inflater.inflate(
                R.layout.dialog_delete_tag, null);
//        final EditText edtInput=(EditText)textEntryView.findViewById(R.id.edtInput);

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(textEntryView);
        final android.app.AlertDialog d =  builder.create();
        textEntryView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });

        textEntryView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
                sendDelLabelRequest(id);
            }
        });

//      builder.show();
        d.show();


    }


}
