package com.blackcat.coach.activities;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.UpdateCoachParams;
import com.blackcat.coach.utils.ToastHelper;

public class ModifyCoachNumberActivity extends BaseActivity {

    private EditText mEtNewCoachNum;
    private Button  mBtnFinish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_coach_number);
        configToolBar(R.mipmap.ic_back);
        initView();
    }

    private void initView() {
        mEtNewCoachNum = (EditText)findViewById(R.id.et_new_coach_num);
        mEtNewCoachNum.setText(Session.getSession().coachnumber);
    }

//   @Override
//    public void onClick(View v) {
//       int id = v.getId();
//       switch (id) {
//           case R.id.btn_finish:
//               if (TextUtils.isEmpty(mEtNewCoachNum.getText().toString().trim())) {
//                   ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.str_license_empty);
//                   return;
//               }
//               String coachnumber = mEtNewCoachNum.getText().toString().trim();
//               if (coachnumber.equals(Session.getSession().coachnumber)) {
//                   finish();
//                   return;
//               }
//               updateRequest(coachnumber);
//               break;
//       }
//   }

    private void updateRequest(final String coachNumber) {
        UpdateCoachParams params = new UpdateCoachParams(Session.getSession());
        params.coachnumber = coachNumber;
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
            if (TextUtils.isEmpty(mEtNewCoachNum.getText().toString().trim())) {
                ToastHelper.getInstance(CarCoachApplication.getInstance()).toast("保存");
            }else {
            String coachnumber = mEtNewCoachNum.getText().toString().trim();
            if (coachnumber.equals(Session.getSession().coachnumber)) {
                finish();
            }
            updateRequest(coachnumber);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
