package com.blackcat.coach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.blackcat.coach.R;
import com.blackcat.coach.easemob.BlackCatHXSDKHelper;
import com.blackcat.coach.easemob.Constant;
import com.blackcat.coach.fragments.DetailStudentFragment;
import com.blackcat.coach.fragments.NewDetailStudentFragment;
import com.blackcat.coach.models.User;
import com.blackcat.coach.utils.BaseUtils;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.LogUtil;

public class NewDetailStudentAct extends BaseActivity implements View.OnClickListener {


    private User mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_student);
        configToolBar(R.mipmap.ic_back);
        mUser = (User) getIntent().getSerializableExtra(Constants.DATA);
        LogUtil.print("=====sssss=="+mUser.userid);
        if (mUser == null) {
//            finish();

//            return;
        }
        setTitle(mUser.name);

//        findViewById(R.id.ssss).setOnClickListener(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_container, NewDetailStudentFragment.newInstance(mUser));
        transaction.commitAllowingStateLoss();
    }


    @Override
    public void onClick(View view) {

    }

    public void intoChat(){
        if (!BlackCatHXSDKHelper.getInstance().isLogined()) {
            return;
        }
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constant.MESSAGE_USERID_ATR_KEY, mUser._id);
        intent.putExtra(Constant.MESSAGE_NAME_ATTR_KEY, mUser.name);
        if (mUser.headportrait != null &&
                !TextUtils.isEmpty( mUser.headportrait.originalpic)) {
            intent.putExtra(Constant.MESSAGE_AVATAR_ATTR_KEY, mUser.headportrait.originalpic);
        }
        intent.putExtra(Constant.CHAT_FROM_TYPE, Constant.CHAT_FROM_RESERVATION);
        LogUtil.print("on---sssss--start"+mUser.name);
        startActivity(intent);
    }

}
