package com.blackcat.coach.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.blackcat.coach.R;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.Subject;
import com.blackcat.coach.utils.CommonUtil;
import com.blackcat.coach.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * 科目1 ~~ 科目4
 * 全部，未考，约考，补考
 * Created by pengdonghua on 2016/3/29.
 */
public class MainStudentFragment extends BaseFragment{

    List<MainStudentTabsFragment> frags = new ArrayList<MainStudentTabsFragment>();


    public static MainStudentFragment newInstance(String param1, String param2) {
        MainStudentFragment fragment = new MainStudentFragment();
        return fragment;
    }

    public MainStudentFragment() {
        // Required empty public constructor
    }

    private RadioGroup rg;

    private RadioButton rb1,rb2,rb3,rb4;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_student, container, false);
        initView(rootView);
        initData();
        return rootView;
    }

    private void initData() {
        /***/
        List<Subject> subjects =  Session.getSession().subject;
        MainStudentTabsFragment[] item ={null,null,null,null};
        for (Subject subject : subjects) {
            switch(subject.subjectid){
                case 1://
                    item[0] = MainStudentTabsFragment.newInstance(subject.subjectid,"");
                    break;
                case 2:
                    item[1] = MainStudentTabsFragment.newInstance(subject.subjectid,"");
                    break;
                case 3:
                    item[2] = MainStudentTabsFragment.newInstance(subject.subjectid,"");
                    break;
                case 4:
                    item[3] = MainStudentTabsFragment.newInstance(subject.subjectid,"");
                    break;
            }
        }
        for (MainStudentTabsFragment mainStudentTabsFragment : item) {
            if(mainStudentTabsFragment!=null){
                frags.add(mainStudentTabsFragment);
            }
        }

        FragmentTransaction fragmentTransaction =  getChildFragmentManager().beginTransaction();
        if(frags.size()>0){
            fragmentTransaction.add(R.id.fragment_main_student_content,frags.get(0));
            fragmentTransaction.commit();
        }else{
            Toast.makeText(getActivity(), "暂无可授课程", Toast.LENGTH_SHORT).show();
        }

//        frags = new MainStudentTabsFragment
    }


    private void initView(View rootView) {
        int width = CommonUtil.getWindowsWidth(getActivity());
        int itemWidth = width/5;
        RadioGroup.LayoutParams p = new RadioGroup.LayoutParams(itemWidth, RadioGroup.LayoutParams.WRAP_CONTENT);
        p.setMargins(-2,0,0,0);

        rg = (RadioGroup) rootView.findViewById(R.id.fragment_main_student_rg);
        rb1 = (RadioButton) rootView.findViewById(R.id.fragment_main_student_rb1);
        rb2 = (RadioButton) rootView.findViewById(R.id.fragment_main_student_rb2);
        rb3 = (RadioButton) rootView.findViewById(R.id.fragment_main_student_rb3);
        rb4 = (RadioButton) rootView.findViewById(R.id.fragment_main_student_rb4);

        rb1.setLayoutParams(p);
        rb2.setLayoutParams(p);
        rb3.setLayoutParams(p);
        rb4.setLayoutParams(p);
        rb1.setGravity(Gravity.CENTER);
        rb2.setGravity(Gravity.CENTER);
        rb3.setGravity(Gravity.CENTER);
        rb4.setGravity(Gravity.CENTER);


        getActivity().setTitle("学员");
        //动态 改变 科目二 科目三
        switch (Session.getSession().subject.size()){

            case 0:

            case 1://隐藏 group
                rg.setVisibility(View.GONE);
                getActivity().setTitle(Session.getSession().subject.get(0).name);
                break;
            case 2://隐藏中间两个
                rb2.setVisibility(View.GONE);
                rb3.setVisibility(View.GONE);
                rb1.setText(Session.getSession().subject.get(0).name);
                rb4.setText(Session.getSession().subject.get(1).name);
                break;
            case 3:
                rb2.setVisibility(View.GONE);
                rb1.setText(Session.getSession().subject.get(0).name);
                rb3.setText(Session.getSession().subject.get(1).name);
                rb4.setText(Session.getSession().subject.get(2).name);
                break;
            case 4:
                rb1.setText(Session.getSession().subject.get(0).name);
                rb2.setText(Session.getSession().subject.get(1).name);
                rb3.setText(Session.getSession().subject.get(2).name);
                rb4.setText(Session.getSession().subject.get(3).name);
                break;
        }
        for (Subject subject : Session.getSession().subject) {
            LogUtil.print("id-->"+subject.subjectid+subject.name);
            switch(subject.subjectid){
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
            }
        }

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                FragmentTransaction fragmentTransaction =  getChildFragmentManager().beginTransaction();
                switch(i){
                    case R.id.fragment_main_student_rb1:
                        fragmentTransaction.replace(R.id.fragment_main_student_content,frags.get(0));
                        break;
                    case R.id.fragment_main_student_rb2:
                        fragmentTransaction.replace(R.id.fragment_main_student_content,frags.get(1));
                        break;
                    case R.id.fragment_main_student_rb3:
                        // 3, 4
                        if(Session.getSession().subject.size()==3){
                            fragmentTransaction.replace(R.id.fragment_main_student_content,frags.get(1));

                        }else{//4个
                            fragmentTransaction.replace(R.id.fragment_main_student_content,frags.get(2));

                        }
                        break;
                    case R.id.fragment_main_student_rb4:
                        if(Session.getSession().subject.size()==2){
                            fragmentTransaction.replace(R.id.fragment_main_student_content,frags.get(1));

                        }else  if(Session.getSession().subject.size()==3){
                            fragmentTransaction.replace(R.id.fragment_main_student_content,frags.get(2));

                        }else{//4个
                            fragmentTransaction.replace(R.id.fragment_main_student_content,frags.get(3));

                        }
                        break;
                }
                fragmentTransaction.commit();
            }
        });

    }


}
