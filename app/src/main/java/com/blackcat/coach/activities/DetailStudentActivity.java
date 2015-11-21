package com.blackcat.coach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.blackcat.coach.R;
import com.blackcat.coach.easemob.BlackCatHXSDKHelper;
import com.blackcat.coach.easemob.Constant;
import com.blackcat.coach.fragments.DetailStudentFragment;
import com.blackcat.coach.models.User;
import com.blackcat.coach.utils.BaseUtils;
import com.blackcat.coach.utils.Constants;

public class DetailStudentActivity extends BaseActivity {


    private User mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_student);
        configToolBar(R.mipmap.ic_back);
        mUser = (User) getIntent().getSerializableExtra(Constants.DATA);
        if (mUser == null) {
            finish();
            return;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_container, DetailStudentFragment.newInstance(mUser));
        transaction.commitAllowingStateLoss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_students, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_tel) {
            if (!TextUtils.isEmpty(mUser.mobile)) {
                BaseUtils.callSomebody(this, mUser.mobile);
            }
            return true;
        } else if (id == R.id.action_msg) {
            startActivity(new Intent(this, SendCommentActivity.class));
            //noinspection SimplifiableIfStatement
            if (!BlackCatHXSDKHelper.getInstance().isLogined()) {
                return true;
            }
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra(Constant.MESSAGE_USERID_ATR_KEY, mUser._id);
            intent.putExtra(Constant.MESSAGE_NAME_ATTR_KEY, mUser.name);
            if (mUser.headportrait != null &&
                    !TextUtils.isEmpty( mUser.headportrait.originalpic)) {
                intent.putExtra(Constant.MESSAGE_AVATAR_ATTR_KEY, mUser.headportrait.originalpic);
            }
            intent.putExtra(Constant.CHAT_FROM_TYPE, Constant.CHAT_FROM_RESERVATION);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
