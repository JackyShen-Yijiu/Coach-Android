package com.blackcat.coach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.blackcat.coach.R;
import com.blackcat.coach.utils.Constants;

/**
 * Created by aa on 2016/1/15.
 */
public class SignInFaild extends BaseActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_faild);
      //  configToolBar(R.mipmap.ic_back);

    }
    @Override
    public void onClick (View view){
        int id = view.getId();
        switch (id) {
            case R.id.sign_commit:
               // finish();
                Intent intent = new Intent(this, CaptureActivity.class);
                startActivity(intent);
                break;
        }
    }
}