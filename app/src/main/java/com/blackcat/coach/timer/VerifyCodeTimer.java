package com.blackcat.coach.timer;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.blackcat.coach.R;

/**
 * Created by wangxiaoqing02 on 2015/10/25.
 */
public class VerifyCodeTimer extends CountDownTimer {

    TextView mSendCode;
    Context mContext;
    public VerifyCodeTimer(long millisInFuture, long countDownInterval, Context context, TextView sendCode) {
        super(millisInFuture, countDownInterval);
        mContext = context;
        mSendCode = sendCode;
    }

    @Override
    public void onFinish() {
        mSendCode.setText(R.string.register_get_code);
        mSendCode.setEnabled(true);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        // some script here
        mSendCode.setText(String.format(
                mContext.getResources().getString(R.string.register_time_left),
                millisUntilFinished / 1000));
    }
}