package com.blackcat.coach.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.UpdateCoachParams;
import com.blackcat.coach.utils.ToastHelper;

public class ModifyIdCardActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTvOldIdCard;
    private EditText mEtNewIdCard;
    private Button mBtnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_id_card);
        configToolBar(R.mipmap.ic_back);
        initView();
    }

    private void initView() {
        mTvOldIdCard = (TextView) findViewById(R.id.tv_idcard_num);
        if (!TextUtils.isEmpty(Session.getSession().idcardnumber)) {
            mTvOldIdCard.setText(Session.getSession().idcardnumber);
        }
        mEtNewIdCard = (EditText) findViewById(R.id.et_new_idcard);
        mBtnFinish = (Button) findViewById(R.id.btn_finish);
        mBtnFinish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_finish:
                if (TextUtils.isEmpty(mEtNewIdCard.getText().toString().trim())) {
                    ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.str_idcard_empty);
                    return;
                } else if (mEtNewIdCard.getText().length() != 16 && mEtNewIdCard.getText().length() != 19) {
                    ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.str_idcardnumber_invalid);
                    return;
                }
                String idcard = mEtNewIdCard.getText().toString().trim();
                if (idcard.equals(Session.getSession().idcardnumber)) {
                    finish();
                    return;
                }
                updateRequest(idcard);
                break;
        }
    }

    private void updateRequest(final String idcard) {
        UpdateCoachParams params = new UpdateCoachParams(Session.getSession());
        params.idcardnumber = idcard;
        Session.getSession().updateRequest(this, params);
    }
}
