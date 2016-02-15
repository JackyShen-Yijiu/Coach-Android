package com.blackcat.coach.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

public class PersonalInfoActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mIvAvatar;
    private TextView  mTvIdCard, mTvName, mTvId;
    private TextView  mTvPhone;
    private TextView  mTvDriverLicense;
    private TextView  mTvSeniority;
    private TextView  mTvSeniorityId;
    private TextView  mTvSex;
    private TextView mTvIntroduction;
    //个性标签
    private WordWrapView wordWrapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        configToolBar(R.mipmap.ic_back);
        initViews();
    }
    @Override
    protected void onResume() {
        super.onResume();
        bindViewInfo();
    }

    private void initViews() {
        findViewById(R.id.rl_avatar).setOnClickListener(this);
        findViewById(R.id.rl_id_card).setOnClickListener(this);
        findViewById(R.id.rl_modify_phone).setOnClickListener(this);
        findViewById(R.id.rl_driving_license).setOnClickListener(this);
        findViewById(R.id.rl_seniority).setOnClickListener(this);
        findViewById(R.id.rl_coach_num).setOnClickListener(this);
        findViewById(R.id.rl_gender).setOnClickListener(this);
        findViewById(R.id.rl_self_intro).setOnClickListener(this);
        mTvIdCard = (TextView)findViewById(R.id.tv_id_card);
        mTvPhone = (TextView)findViewById(R.id.tv_phone);
        mTvDriverLicense = (TextView)findViewById(R.id.tv_driver_license);
        mTvSeniority = (TextView)findViewById(R.id.tv_seniority);
        mTvSeniorityId = (TextView)findViewById(R.id.tv_coach_num);
        mTvSex = (TextView)findViewById(R.id.tv_sex);
        mTvSex.setOnClickListener(this);
        mTvIntroduction = (TextView)findViewById(R.id.tv_self);
        wordWrapView = (WordWrapView) findViewById(R.id.view_wordwrap);
        //白色
        wordWrapView.setFirstColor(true);
        wordWrapView.showColor(true);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvId = (TextView) findViewById(R.id.tv_id);
        mTvName.setText(Session.getSession().name);
        mTvId.setText(Session.getSession().displaycoachid);

        mIvAvatar = (ImageView) findViewById(R.id.ic_avatar);
        if (Session.getSession().headportrait != null && !TextUtils.isEmpty(Session.getSession().headportrait.originalpic)) {
            UILHelper.loadImage(mIvAvatar, Session.getSession().headportrait.originalpic, false, R.mipmap.ic_avatar_small);
        }
    }

    private void bindViewInfo() {
        if (!TextUtils.isEmpty(Session.getSession().name))//用户名
            mTvName.setText(Session.getSession().name);
        if (!TextUtils.isEmpty(Session.getSession().idcardnumber)) {
            mTvIdCard.setText(Session.getSession().idcardnumber);
        }
        if (!TextUtils.isEmpty(Session.getSession().mobile)){
            mTvPhone.setText(Session.getSession().mobile);
        }
        if (!TextUtils.isEmpty(Session.getSession().drivinglicensenumber)) {
            mTvDriverLicense.setText(Session.getSession().drivinglicensenumber);
        }
        if (!TextUtils.isEmpty(Session.getSession().Seniority)) {
            mTvSeniority.setText(Session.getSession().Seniority);
        }
        if (!TextUtils.isEmpty(Session.getSession().coachnumber)) {
            mTvSeniorityId.setText(Session.getSession().coachnumber);
        }

        if (!TextUtils.isEmpty(Session.getSession().Gender)) {
            mTvSex.setText(Session.getSession().Gender);
        }

        if (!TextUtils.isEmpty(Session.getSession().introduction)) {
            mTvIntroduction.setText(Session.getSession().introduction);
        }

        Log.d("tag", "Introduction-->" + Session.getSession().introduction);
        addTags();
    }

    /**
     * 设置标签
     */
    private void addTags(){
        if(Session.getSession().tagslist==null)
            return ;
        if(wordWrapView.getChildCount()>0){
            return;
        }
        LabelBean label = new LabelBean();
        label.tagname = "个性标签";
        List<LabelBean> list = new ArrayList<LabelBean>();
        list.add(label);
        for (LabelBean labelBean : Session.getSession().tagslist) {
            list.add(labelBean);
        }
        wordWrapView.setData(list);
        wordWrapView.removeAllViews();
//        String[] strs = {"个性标签","包接送","五星级教练","不吸烟","态度极好","免费提供水服务","不收彩礼"};
        for (int i = 0; i < list.size(); i++) {
            TextView textview = new TextView(this);
            textview.setText(list.get(i).tagname);
            wordWrapView.addView(textview);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.rl_name://姓名
                Intent i = new Intent(this, ModifyCoachNameAct.class);
//                i.putExtra("name",Session.getSession().name)
                startActivity(i);
                break;
            case R.id.rl_self_intro:
                startActivity(new Intent(this, SelfIntroducationActivity.class));
                break;
            case R.id.rl_modify_phone:
                startActivity(new Intent(this, BindPhoneActivity.class));
                break;
            case R.id.rl_avatar:
            case R.id.ic_avatar:
                if (mAvatarDialog == null) {
                    mAvatarDialog = new BaseDialogWrapper(PersonalInfoActivity.this, getPhotoDialogView());
                    mAvatarDialog.setFullWidth(true);
                }
                mAvatarDialog.setGravity(Gravity.BOTTOM);
                mAvatarDialog.showDialog();
                break;
            case R.id.tv_take_photo:
                mAvatarDialog.dismissDialog();
                mPhotoFile = PhotoUtil.getPhotoFile(CarCoachApplication.getInstance());
                startActivityForResult(PhotoUtil.getTakePickIntent(mPhotoFile), PhotoUtil.PICTRUE_FROM_CAMERA);
                break;
            case R.id.tv_choose_from_gallery:
                mAvatarDialog.dismissDialog();
                mPhotoFile = PhotoUtil.getPhotoFile(CarCoachApplication.getInstance());
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, PhotoUtil.PICTRUE_FROM_GALLERY);
                break;
            case R.id.tv_choose_cancel:
                mAvatarDialog.dismissDialog();
                break;
            case R.id.rl_id_card:
                startActivity(new Intent(this, ModifyIdCardActivity.class));
                break;
            case R.id.rl_driving_license:
                startActivity(new Intent(this,DrivingLicenseActivity.class));
                break;
            case R.id.rl_seniority:
                startActivity(new Intent(this, ModifySeniorityActivity.class));
                break;
            case R.id.rl_coach_num:
                startActivity(new Intent(this, ModifyCoachNumberActivity.class));
                break;
            case R.id.rl_gender:
                startActivity(new Intent(this, ModifyGenderActivity.class));
                break;
            case R.id.view_wordwrap:// 标签页面
                startActivity(new Intent(this,PersionLableAct.class));
                break;
            default:
                break;
        }

    }

    private File mPhotoFile;
    private AsyncProgressDialog mDialog;
    private BaseDialogWrapper mAvatarDialog;

    private View getPhotoDialogView() {
        View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_photo_picker, null);
        dialogView.findViewById(R.id.tv_choose_cancel).setOnClickListener(this);
        dialogView.findViewById(R.id.tv_take_photo).setOnClickListener(this);
        dialogView.findViewById(R.id.tv_choose_from_gallery).setOnClickListener(this);
        return dialogView;
    }

    private Uri mPhotoUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PhotoUtil.PICTRUE_FROM_CAMERA:
                // 拍好了照片 应该跳转
                // TODO 判断照片有没有拍照
                if (mPhotoFile != null && mPhotoFile.exists()) {
                    mPhotoUri = Uri.fromFile(mPhotoFile);
                    loadPhoto(mPhotoUri);
                }
                break;
            case PhotoUtil.PICTRUE_FROM_GALLERY:
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        mPhotoUri = uri;
                        loadPhoto(mPhotoUri);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void loadPhoto(Uri uri) {
        if (uri == null) {
            return;
        }
        mDialog = new AsyncProgressDialog(this);
        mDialog.show();
        QiniuUploadManager.getInstance().submitImage(PersonalInfoActivity.this, mPhotoUri, new QiniuUploadManager.QiniuUploadListener() {

            @Override
            public void onSucess() {
                ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.upload_avatar_ok);
                mDialog.dismiss();
                mDialog = null;
                Session.save(Session.getSession(), true);
                if (Session.getSession().headportrait != null && !TextUtils.isEmpty(Session.getSession().headportrait.originalpic)) {
                    UILHelper.loadImage(mIvAvatar, Session.getSession().headportrait.originalpic, false, R.mipmap.ic_avatar_small);
                }
            }

            @Override
            public void onError() {
                ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.upload_avatar_err);
//                mIvAvatar.setImageResource(R.mipmap.ic_avatar_small);
                mDialog.dismiss();
                mDialog = null;
            }
        });

        ImageLoader.getInstance().displayImage(uri.toString(), mIvAvatar);
    }
}
