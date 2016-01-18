package com.blackcat.coach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.blackcat.coach.R;

/**
 * Created by aa on 2016/1/15.
 */
public class SignInSucceed extends BaseActivity implements View.OnClickListener {


    private Button sign_commit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_succeed);
       // configToolBar(R.mipmap.ic_back);

        sign_commit = (Button) findViewById(R.id.sign_commit);
        sign_commit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.sign_commit:
//                Intent intent = new Intent(SignInSucceed.this, IndexActivity.class);
//                startActivity(intent);
                this.finish();
                break;

        }
    }
}
