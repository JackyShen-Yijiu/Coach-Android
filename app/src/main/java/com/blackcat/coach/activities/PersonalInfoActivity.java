package com.blackcat.coach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.ImageView;

import com.blackcat.coach.R;
import com.blackcat.coach.fragments.PersonalInforFragment;

public class PersonalInfoActivity extends BaseActivity  {

    PersonalInforFragment fragment;
    private ImageView mIvAvatar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_personal_infor_container);
        configToolBar(R.mipmap.ic_back);
//        initViews();
        fragment = PersonalInforFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_container, fragment);
        transaction.commitAllowingStateLoss();
//        mIvAvatar = (ImageView) findViewById(R.id.ic_avatar);
//        if (Session.getSession().headportrait != null && !TextUtils.isEmpty(Session.getSession().headportrait.originalpic)) {
//            UILHelper.loadImage(mIvAvatar, Session.getSession().headportrait.originalpic, false, R.mipmap.ic_avatar_small);
//        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        fragment.bindViewInfo();
    }

//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragment.onActivityResult(requestCode,resultCode,data);
    }



}
