package com.blackcat.coach.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    private ImageView iv_back;
    private TextView tv_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_detail_student);
         iniv();


        mUser = (User) getIntent().getSerializableExtra(Constants.DATA);

        LogUtil.print("=====sssss=="+mUser.name);


        if (mUser == null) {
//            finish();

//            return;
        }
        if (mUser.name==null){
            tv_name.setText(mUser.mobile);
        }
        tv_name.setText(mUser.name);
//        setTitle(mUser.name);

//        findViewById(R.id.ssss).setOnClickListener(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_container, NewDetailStudentFragment.newInstance(mUser));
        transaction.commitAllowingStateLoss();
    }

    private void iniv() {
        iv_back=(ImageView)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        tv_name=(TextView)findViewById(R.id.tv_name);


    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.iv_back:
               finish();
                break;

        }

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
