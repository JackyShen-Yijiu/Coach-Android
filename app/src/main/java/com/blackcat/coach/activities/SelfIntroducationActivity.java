package com.blackcat.coach.activities;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.UpdateCoachParams;
import com.blackcat.coach.utils.ToastHelper;

public class SelfIntroducationActivity extends BaseActivity{

    private EditText mEtSelfInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_introducation);
        configToolBar(R.mipmap.ic_back);

        mEtSelfInfo = (EditText)findViewById(R.id.et_self_intro);

        if (!TextUtils.isEmpty(Session.getSession().introduction)) {
            mEtSelfInfo.setText(Session.getSession().introduction);
        }

    }


    private void updateRequest(final String selfInfo) {
        UpdateCoachParams params = new UpdateCoachParams(Session.getSession());
        params.introduction = selfInfo;
        Session.getSession().updateRequest(this, params);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(ACTION_GROUP_ID, ACTION_MENUITEM_ID0, 0, R.string.action_finish);
        MenuItemCompat.setShowAsAction(item, MenuItem.SHOW_AS_ACTION_ALWAYS
                | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == ACTION_MENUITEM_ID0) {
            if (TextUtils.isEmpty(mEtSelfInfo.getText().toString().trim())) {
                ToastHelper.getInstance(CarCoachApplication.getInstance()).toast("保存");
            } else {
                String coachnumber = mEtSelfInfo.getText().toString().trim();
                updateRequest(coachnumber);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
