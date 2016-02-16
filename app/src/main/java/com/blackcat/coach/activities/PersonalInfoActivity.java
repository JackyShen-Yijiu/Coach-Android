package com.blackcat.coach.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.dialogs.AsyncProgressDialog;
import com.blackcat.coach.dialogs.BaseDialogWrapper;
import com.blackcat.coach.fragments.DetailStudentFragment;
import com.blackcat.coach.fragments.PersonalInforFragment;
import com.blackcat.coach.imgs.UILHelper;
import com.blackcat.coach.models.LabelBean;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.qiniu.PhotoUtil;
import com.blackcat.coach.qiniu.QiniuUploadManager;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.widgets.WordWrapView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PersonalInfoActivity extends BaseActivity  {

    PersonalInforFragment fragment;
    private ImageView mIvAvatar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_personal_infor_container);
        configToolBar(R.mipmap.ic_back);
//        initViews();
        fragment = PersonalInforFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_container, fragment);
        transaction.commitAllowingStateLoss();



//        mIvAvatar = (ImageView) findViewById(R.id.ic_avatar);
//        if (Session.getSession().headportrait != null && !TextUtils.isEmpty(Session.getSession().headportrait.originalpic)) {
//            UILHelper.loadImage(mIvAvatar, Session.getSession().headportrait.originalpic, false, R.mipmap.ic_avatar_small);
//        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        fragment.bindViewInfo();
    }

//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragment.onActivityResult(requestCode,resultCode,data);
    }



}
