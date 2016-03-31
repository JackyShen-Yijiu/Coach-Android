package com.blackcat.coach.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.blackcat.coach.R;
import com.blackcat.coach.fragments.NewDetailStudentFragment;
import com.blackcat.coach.models.User;
import com.blackcat.coach.utils.Constants;

/**
 * Created by aa on 2016/3/31.
 */
public class MoreActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        configToolBar(R.mipmap.ic_back);
    }
}
