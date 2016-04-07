package com.blackcat.coach.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.activities.StudentAppointmentAct;
import com.blackcat.coach.adapters.CommonAdapter;
import com.blackcat.coach.adapters.rows.RowAddStudents;
import com.blackcat.coach.dialogs.ApplySuccessDialog;
import com.blackcat.coach.events.MonthApplyEvent;
import com.blackcat.coach.models.AddStudentsVO;
import com.blackcat.coach.models.DaytimelysReservation;
import com.blackcat.coach.models.DicCode;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.CoachAppointParams;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.GsonUtils;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.UTC2LOC;
import com.blackcat.coach.utils.VolleyUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by aa on 2016/3/29.
 */
public class AddStudentsFragment extends BaseListFragment<AddStudentsVO> implements CompoundButton.OnCheckedChangeListener {

    /**当前选中的项*/
    public static int index = -1;
    public static AddStudentsVO selectUser=null;
    private static RadioButton rbLast = null;

    private int type = 0;
    private CheckBox checkBoxOne, checkBoxTwo, checkBoxThree, checkBoxFour;
    private TextView fulledTvOne, fulledTvTwo, fulledTvThree, fulledTvFour;
    private TextView startTimeOneTv;
    private TextView startTimeTwoTv;
    private TextView startTimeThreeTv;
    private TextView startTimeFourTv;
    private TextView endTimeFourTv;
    private TextView endTimeThreeTv;
    private TextView endTimeTwoTv;
    private TextView endTimeOneTv;

    DaytimelysReservation student1, student2, student3, student4;

    private List<DaytimelysReservation> selectCourseList = new ArrayList<DaytimelysReservation>();
    private Comparator<Object> courseComp;
    private RelativeLayout layout_null;
    private ImageView null_iv;

    public static AddStudentsFragment newInstance(String param1, String param2) {
        AddStudentsFragment fragment = new AddStudentsFragment();
        return fragment;
    }

    public AddStudentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mType = new TypeToken<Result<List<AddStudentsVO>>>() {
        }.getType();
        View rootView = inflater.inflate(R.layout.add_student, container, false);
        initViews(rootView, inflater, CommonAdapter.AdapterType.TYPE_ADD_STUDENTS);
        mListView.setOnLoadMoreListener(null);
        initView(rootView);
        initData();
        mPage = 1;
        if (!Session.isUserInfoEmpty()) {
            mURI = URIUtil.getAddStudentsList("-1", mPage);
            refresh(DicCode.RefreshType.R_INIT, mURI);
        }
        mListView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                LogUtil.print("mListView--------------");
                RadioButton rb =(RadioButton) view.findViewById(R.id.iv_check);
                mAdapter.select(mAdapter.getItem((int)l),(int)l,rb);

            }
        });
        return rootView;
    }

    private void initView(View view) {


        checkBoxOne = (CheckBox) view.findViewById(R.id.rb_check_one);
        checkBoxTwo = (CheckBox) view.findViewById(R.id.rb_check_two);
        checkBoxThree = (CheckBox) view.findViewById(R.id.rb_check_three);
        checkBoxFour = (CheckBox) view.findViewById(R.id.rb_check_four);
        fulledTvOne = (TextView) view.findViewById(R.id.student_fulled_one_tv);
        fulledTvTwo = (TextView) view.findViewById(R.id.student_fulled_two_tv);
        fulledTvThree = (TextView) view.findViewById(R.id.student_fulled_three_tv);
        fulledTvFour = (TextView) view.findViewById(R.id.student_fulled_four_tv);

        startTimeOneTv = (TextView) view.findViewById(R.id.start_time_one);
        startTimeTwoTv = (TextView) view.findViewById(R.id.start_time_two);
        startTimeThreeTv = (TextView) view.findViewById(R.id.start_time_three);
        startTimeFourTv = (TextView) view.findViewById(R.id.start_time_four);

        endTimeOneTv = (TextView) view.findViewById(R.id.end_time_one);
        endTimeTwoTv = (TextView) view.findViewById(R.id.end_time_two);
        endTimeThreeTv = (TextView) view.findViewById(R.id.end_time_three);
        endTimeFourTv = (TextView) view.findViewById(R.id.end_time_four);

        checkBoxOne.setOnCheckedChangeListener(this);
        checkBoxTwo.setOnCheckedChangeListener(this);
        checkBoxThree.setOnCheckedChangeListener(this);
        checkBoxFour.setOnCheckedChangeListener(this);
    }

    private void initData() {

        student1 = (DaytimelysReservation) getActivity().getIntent().getSerializableExtra("student1");
        student2 = (DaytimelysReservation) getActivity().getIntent().getSerializableExtra("student2");
        student3 = (DaytimelysReservation) getActivity().getIntent().getSerializableExtra("student3");
        student4 = (DaytimelysReservation) getActivity().getIntent().getSerializableExtra("student4");
        //当学员列表为空时
        if (student1 != null) {
            if (student1.coursestudentcount == student1.selectedstudentcount) {
                //该课程已预约满
                checkBoxOne.setVisibility(View.GONE);
                fulledTvOne.setVisibility(View.VISIBLE);
            }
            startTimeOneTv.setText(UTC2LOC.instance.getDate(student1.coursebegintime, "HH:mm"));
            endTimeOneTv.setText(UTC2LOC.instance.getDate(student1.courseendtime, "HH:mm"));
        }else{
            checkBoxOne.setVisibility(View.GONE);
        }
        if (student2 != null) {
            if (student2.coursestudentcount == student2.selectedstudentcount) {
                //该课程已预约满
                checkBoxTwo.setVisibility(View.GONE);
                fulledTvTwo.setVisibility(View.VISIBLE);
            }
            startTimeTwoTv.setText(UTC2LOC.instance.getDate(student2.coursebegintime, "HH:mm"));
            endTimeTwoTv.setText(UTC2LOC.instance.getDate(student2.courseendtime, "HH:mm"));
        }else{
            checkBoxTwo.setVisibility(View.GONE);
        }
        if (student3 != null) {
            if (student3.coursestudentcount == student3.selectedstudentcount) {
                //该课程已预约满
                checkBoxThree.setVisibility(View.GONE);
                fulledTvThree.setVisibility(View.VISIBLE);
            }
            startTimeThreeTv.setText(UTC2LOC.instance.getDate(student3.coursebegintime, "HH:mm"));
            endTimeThreeTv.setText(UTC2LOC.instance.getDate(student3.courseendtime, "HH:mm"));
        }else{
            checkBoxThree.setVisibility(View.GONE);
        }
        if (student4 != null) {
            if (student4.coursestudentcount == student4.selectedstudentcount) {
                //该课程已预约满
                checkBoxFour.setVisibility(View.GONE);
                fulledTvFour.setVisibility(View.VISIBLE);
            }
            startTimeFourTv.setText(UTC2LOC.instance.getDate(student4.coursebegintime, "HH:mm"));
            endTimeFourTv.setText(UTC2LOC.instance.getDate(student4.courseendtime, "HH:mm"));
        }else{
            checkBoxFour.setVisibility(View.GONE);
        }


    }

    @Override
    protected void initViews(View rootView, LayoutInflater inflater,
                             int adapterType) {
        super.initViews(rootView, inflater, adapterType);
        mListView.setDividerHeight(0);
        mListView.setDivider(null);
    }


    @Override
    public void onRefresh() {
        mPage = 1;
        mURI = URIUtil.getAddStudentsList("-1", mPage);
        refresh(DicCode.RefreshType.R_PULL_DOWN, mURI);
    }

    @Override
    public void onLoadMore() {
//        mPage++;
//        mURI = URIUtil.getAddStudentsList("-1", mPage);
//        refresh(DicCode.RefreshType.R_PULL_UP, mURI);
    }

    @Override
    protected void onFeedsResponse(Result<List<AddStudentsVO>> response, int refreshType) {
        super.onFeedsResponse(response, refreshType);
        if (response != null && response.type == Result.RESULT_OK && response.data != null) {
            List<AddStudentsVO> list = response.data;
            Context c = getActivity();
//            if(c!=null && list.size()>0)
//                new SpHelper(c).set(NetConstants.KEY_MESSAGEID,list.get(0).);
        }
    }

    @Override
    protected void onFeedsErrorResponse(VolleyError arg0, int refreshType) {
        super.onFeedsErrorResponse(arg0, refreshType);
        if (refreshType == DicCode.RefreshType.R_PULL_UP) {
            mPage--;
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.rb_check_one:
                if (isChecked) {
                    selectCourseList.add(student1);
                    if (!isCourseContinuous()) {
                        //课程不连续
                        selectCourseList.remove(student1);
                        ToastHelper.getInstance(CarCoachApplication.getInstance()).toast("请选择连续的课程");
                        checkBoxOne.setChecked(false);
                    }
                } else {
                    if (selectCourseList.contains(student1)) {
                        selectCourseList.remove(student1);
                    }
                }
                break;
            case R.id.rb_check_two:
                if (isChecked) {
                    selectCourseList.add(student2);
                    if (!isCourseContinuous()) {
                        //课程不连续
                        selectCourseList.remove(student2);
                        ToastHelper.getInstance(CarCoachApplication.getInstance()).toast("请选择连续的课程");
                        checkBoxTwo.setChecked(false);
                    }
                } else {
                    if (selectCourseList.contains(student2)) {
                        selectCourseList.remove(student2);
                    }
                }

                break;
            case R.id.rb_check_three:
                if (isChecked) {
                    selectCourseList.add(student3);
                    if (!isCourseContinuous()) {
                        //课程不连续
                        selectCourseList.remove(student3);
                        ToastHelper.getInstance(CarCoachApplication.getInstance()).toast("请选择连续的课程");
                        checkBoxThree.setChecked(false);
                    }
                } else {
                    if (selectCourseList.contains(student3)) {
                        selectCourseList.remove(student3);
                    }
                }
                break;
            case R.id.rb_check_four:
                if (isChecked) {
                    selectCourseList.add(student4);
                    if (!isCourseContinuous()) {
                        //课程不连续
                        selectCourseList.remove(student4);
                        ToastHelper.getInstance(CarCoachApplication.getInstance()).toast("请选择连续的课程");
                        checkBoxFour.setChecked(false);
                    }
                } else {
                    if (selectCourseList.contains(student4)) {
                        selectCourseList.remove(student4);
                    }
                }
                break;
        }

    }


    /**
     * 提交预约
     */
    public void commitAppointment() {

        //获取学员id
        if ( RowAddStudents.selectUser == null) {
            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast("请选择学员");
            return;
        }
        if (selectCourseList == null || selectCourseList.size() == 0) {
            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast("请选择课程");
            return;
        }
        URI uri = URIUtil.getStudentAppointList(null, null, "");
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }
        Collections.sort(selectCourseList, courseComp);
        String courselistString = "";
        int length = selectCourseList.size();
        for (int i = 0; i < length; i++) {
            courselistString += selectCourseList.get(i)._id;
            courselistString += ",";
        }
        courselistString = courselistString.substring(0, courselistString.length() - 1);
        int shuttle = 0;
        if (Session.getSession().is_shuttle) {
            shuttle = 1;
        } else {
            shuttle = 0;
        }
//
        CoachAppointParams params = new CoachAppointParams();
        params.userid = RowAddStudents.selectUser.userid;
        params.coachid = Session.getSession().coachid;
        params.courselist = courselistString;
        params.is_shuttle = shuttle;
        params.address = Session.getSession().address;
        params.begintime = UTC2LOC.instance.getDate(selectCourseList.get(0).coursebegintime, "yyyy-MM-dd HH:mm:ss");
        params.endtime = UTC2LOC.instance.getDate(selectCourseList.get(selectCourseList.size() - 1).courseendtime, "yyyy-MM-dd HH:mm:ss");

        LogUtil.print(GsonUtils.toJson(params));
        Map map = new HashMap<>();
        map.put("authorization", Session.getToken());
        Type mTokenType = new TypeToken<Result>() {
        }.getType();
        GsonIgnoreCacheHeadersRequest<Result> request = new GsonIgnoreCacheHeadersRequest<Result>(
                Request.Method.POST, url, GsonUtils.toJson(params), mTokenType, map,
                new Response.Listener<Result>() {
                    @Override
                    public void onResponse(Result response) {
                        String msg = response.msg;
                        if (response != null && response.type == Result.RESULT_OK) {
                            final ApplySuccessDialog dialog = new ApplySuccessDialog(getActivity());
                            dialog.setTextAndImage("是",
                                    "恭喜您预约成功，是否短信通知学员", "否",
                                    R.drawable.ic_dialog);
                            dialog.setConfirmListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View arg0) {
                                    //发短信
//                                    ToastHelper.getInstance(CarCoachApplication.getInstance()).toast("发短差");
                                    MonthApplyEvent event = new MonthApplyEvent();
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(UTC2LOC.instance.getDates(selectCourseList.get(0).coursebegintime,"yyyy-MM-dd"));
                                    event.calendar = calendar;
                                    EventBus.getDefault().post(event);
                                    sendMsg(RowAddStudents.selectUser.mobile);
                                    dialog.dismiss();
                                    getActivity().finish();
                                }
                            });
                            dialog.setCancelListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View arg0) {
                                    MonthApplyEvent event = new MonthApplyEvent();
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(UTC2LOC.instance.getDates(selectCourseList.get(0).coursebegintime,"yyyy-MM-dd"));
                                    event.calendar = calendar;
                                    EventBus.getDefault().post(event);
                                    dialog.dismiss();
                                    getActivity().finish();
                                }
                            });
                            dialog.show();
                        } else if (response != null && !TextUtils.isEmpty(response.msg)) {
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

        VolleyUtil.getQueue(getActivity()).add(request);
    }


    //判断课程是否连续
    public boolean isCourseContinuous() {
        courseComp = new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                DaytimelysReservation s1 = (DaytimelysReservation) o1;
                DaytimelysReservation s2 = (DaytimelysReservation) o2;
                try {
                    int c1 = Integer.parseInt(s1.coursetime.getTimeid());
                    int c2 = Integer.parseInt(s2.coursetime.getTimeid());
                    if (c1 < c2)
                        return -1;
                    else if (c1 == c2)
                        return 0;
                    else if (c1 > c2)
                        return 1;
                } catch (Exception e) {

                }
                return 0;
            }
        };

        Collections.sort(selectCourseList, courseComp);
        int size = selectCourseList.size() - 1;
        for (int i = 0; i < size; i++) {
            if ((Integer.parseInt(selectCourseList.get(i).coursetime.getTimeid()) + 1) != Integer
                    .parseInt(selectCourseList.get(i + 1).coursetime.getTimeid())) {

                return false;
            }
        }
        return true;
    }

    //发短信
    private void sendMsg(String phone) {

        Uri smsUri = Uri.parse("smsto:" + phone);
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsUri);
        intent.putExtra("sms_body", "成功预约我的课程，请准时练车");
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RowAddStudents.index=-1;
        RowAddStudents.selectUser=null;
    }
}
