package com.blackcat.coach.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.activities.StudyConfirmsAct;
import com.blackcat.coach.events.ReservationOpOk;
import com.blackcat.coach.models.CoachClass;
import com.blackcat.coach.models.LabelBean;
import com.blackcat.coach.models.Reservation;
import com.blackcat.coach.models.ReservationStatus;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.NewParams;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.GsonUtils;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.UTC2LOC;
import com.blackcat.coach.utils.VolleyUtil;
import com.blackcat.coach.widgets.WordWrapViewStudyContent;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/** 工时确认 列表
 * Created by pengdonghua on 2016/3/26.
 */
public class StudyConfirmAdapter extends BaseExpandableListAdapter {

    private Context context;

    private List<Reservation> list = new ArrayList<Reservation>();

    private Type mTokenType;

//    /**临时输入的内容*/
//    private List<NewParams> inputTemp = new ArrayList<NewParams>();

    public StudyConfirmAdapter(Context context){
        this.context = context;
        mTokenType = new TypeToken<Result>() {}.getType();

    }

    public void setData(List<Reservation> list){
        this.list = list;
        notifyDataSetChanged();
    }


    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return list.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        View v = View.inflate(context,R.layout.item_study_confirm_parent,null);
        TextView tvName = (TextView) v.findViewById(R.id.item_study_confirm_parent_name);
        TextView tvTime = (TextView) v.findViewById(R.id.item_study_confirm_parent_time);
        ImageView imgArrow = (ImageView) v.findViewById(R.id.item_study_confirm_parent_arrow);
        Reservation item = list.get(i);
        tvName.setText(item.userid.name);

        tvTime.setText(UTC2LOC.instance.getDate(item.begintime,"HH:mm")+" ~ "+UTC2LOC.instance.getDate(item.endtime,"HH:mm"));

        return v;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        return getChild(list.get(i),i);
    }

    private View getChild(final Reservation item,final int i){
        final List<CoachClass> list = getLearnContent(item.subject.subjectid);

        View v = View.inflate(context, R.layout.item_study_confirm_child,null);
        WordWrapViewStudyContent wordWrap = (WordWrapViewStudyContent) v.findViewById(R.id.item_study_confirm_child_wwww);
        if(list!=null)
            initWWW(list,wordWrap);

        final RatingBar ratBar = (RatingBar) v.findViewById(R.id.item_study_confirm_child_ratingBar);
        final EditText et = (EditText) v.findViewById(R.id.item_study_confirm_child_editText);
        Button btn = (Button) v.findViewById(R.id.item_study_confirm_child_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(et.getText())) {
                    ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.comment_empty);
                    return;
                }
                String coachid = Session.getSession().coachid;
                String reservationId = item._id;
                String commentcontent = et.getText().toString();
                String starlevel = ratBar.getRating()+"";
                String learnContent = getLearnedContent(list);
                //提交 数据
                sendCommentRequest(coachid,reservationId,commentcontent,starlevel,learnContent,i);
            }
        });

        return v;
    }

    /**
     * 发送 评论
     * @param coachid
     * @param reservationid
     * @param commentcontent
     * @param starlevel
     * @param learningcontent
     */
    private void sendCommentRequest(String coachid, String reservationid, String commentcontent, String starlevel, String learningcontent,final int pos) {
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

        NewParams param = new NewParams();

        param.coachid = coachid;
        param.reservationid = reservationid;
        param.commentcontent = commentcontent;
        param.starlevel = starlevel;
        param.learningcontent = learningcontent;

        Map map = new HashMap<>();
        map.put("authorization", Session.getToken());
        GsonIgnoreCacheHeadersRequest<Result> request = new GsonIgnoreCacheHeadersRequest<Result>(
                Request.Method.POST, url, GsonUtils.toJson(param), mTokenType, map,
                new Response.Listener<Result>() {

                    @Override
                    public void onResponse(Result response) {
                        if (response != null && response.type == Result.RESULT_OK) {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.op_ok);
                            ReservationOpOk event = new ReservationOpOk();
                            list.remove(pos);
                            StudyConfirmAdapter.this.notifyDataSetChanged();
//                            event.pos = mReservation.pos;
//                            event.status = ReservationStatus.FINISH;
//                            EventBus.getDefault().post(event);

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

        VolleyUtil.getQueue(context).add(request);
    }

    private String[] mSubjectNames;
    private List<CoachClass> mCoachClassList2 = null;

    private List<CoachClass> mCoachClassList3 = null;

    /**
     * 根据当前的科目类型，获取学习内容
     * @param mSubjectType
     */
    private List<CoachClass> getLearnContent(int mSubjectType){

        if(mSubjectType == StudyConfirmsAct.SUBJECTS_TYPE_TWO) {
            if(mCoachClassList2!=null)
                return mCoachClassList2;

            mSubjectNames = context.getResources().getStringArray(R.array.subject2);
            mCoachClassList2 = new ArrayList<>();
            for (int i = 0; i < mSubjectNames.length; i++) {
                CoachClass coachClass = new CoachClass(mSubjectNames[i], false);
                mCoachClassList2.add(coachClass);
            }
            return mCoachClassList2;
        }
        else if (mSubjectType == StudyConfirmsAct.SUBJECTS_TYPE_THREE) {
            if(mCoachClassList3!=null)
                return mCoachClassList3;

            mSubjectNames = context.getResources().getStringArray(R.array.subject3);
            mCoachClassList3 = new ArrayList<>();
            for (int i = 0; i < mSubjectNames.length; i++) {
                CoachClass coachClass = new CoachClass(mSubjectNames[i], false);
                mCoachClassList3.add(coachClass);
            }
            return mCoachClassList3;
        }
        return null;

    }

    /**
     * 选中的 学习内容
     * @return
     */
    private String getLearnedContent(List<CoachClass> list){
        StringBuilder sb = new StringBuilder();

        for(CoachClass coachClass : list) {
            if(coachClass.isChecked()) {
                String name = coachClass.getClassName();
                if(!TextUtils.isEmpty(sb.toString())) {
                    sb.append(",").append(name);
                }else {
                    sb.append(name);
                }
            }
        }
        return sb.toString();

    }
    /**
     *
     */
    private void initWWW(final List<CoachClass> list,WordWrapViewStudyContent wordWrap){

            if(wordWrap.getChildCount()>0){
                return;
            }
            wordWrap.removeAllViews();

            wordWrap.setData(list);

            for (int i = 0; i < list.size(); i++) {

                CheckBox checkBox = (CheckBox) View.inflate(context,R.layout.checkbox,null);
//                checkBox.set
//                checkBox.setButtonDrawable(null);
//                checkBox.setBackground(context.getResources().getDrawable(R.drawable.selector_study_content));
//                checkBox.setBackground(R.drawable.selector_study_content);
                checkBox.setText(list.get(i).getClassName());
                checkBox.setTag(i);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        final int tag = Integer.parseInt(compoundButton.getTag().toString());
//                        if(list.get(tag).is_choose){//已经选中了  不选中
//                            view.setBackgroundColor(Color.BLUE);
//                        }else{//未选中// )
//                            view.setBackgroundColor(Color.WHITE);
//                        }
                        LogUtil.print("checked--changed-->"+b);
                        list.get(tag).is_choose = b;//! list.get(tag).is_choose;
                    }
                });
//                TextView textview = new TextView(context)
//                textview.setText(list.get(i).getClassName());
//                textview.setClickable(true);
//                textview.setTag(i);
//                textview.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        final int tag = Integer.parseInt(view.getTag().toString());
//                        if(list.get(tag).is_choose){//已经选中了  不选中
//                            view.setBackgroundColor(Color.BLUE);
//                        }else{//未选中// )
//                            view.setBackgroundColor(Color.WHITE);
//                        }
//                        list.get(tag).is_choose = ! list.get(tag).is_choose;
//                    }
//                });
                wordWrap.addView(checkBox);

        }

    }



    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
