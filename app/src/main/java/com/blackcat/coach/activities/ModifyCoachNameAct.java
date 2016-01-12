package com.blackcat.coach.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.events.UpdatePwdOk;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.UpdateCoachParams;
import com.blackcat.coach.models.params.UpdatePwdParams;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.BaseUtils;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.GsonUtils;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;

import java.net.URI;

import de.greenrobot.event.EventBus;

public class ModifyCoachNameAct extends BaseActivity {

    private EditText etName;

    private TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_coach_name);
        configToolBar(R.mipmap.ic_back);
        initView();
    }

    private void initView() {
        etName = (EditText) findViewById(R.id.activity_modify_coach_name_name_et);
        tvName = (TextView) findViewById(R.id.activity_modify_coach_name_name_tv);

        String name = Session.getSession().name;
        if(null!=name)
            tvName.setText(name);
//
    }

    private boolean checkInput(){
        if(TextUtils.isEmpty(etName.getText().toString())){
            Toast.makeText(this,"姓名不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }else if(etName.getText().toString().length()>8){
            Toast.makeText(this,"姓名太长!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public  void onClick(View view){
        switch(view.getId()){
            case R.id.activity_modify_coach_name_btn://完成
                if(checkInput()){
                    request();
                }
                break;
        }

    }

    private void request(){
        UpdateCoachParams params = new UpdateCoachParams(Session.getSession());
        params.name = etName.getText().toString();
        params.coachid = Session.getSession().coachid;
        Session.getSession().updateRequest(this, params);

    }


}
