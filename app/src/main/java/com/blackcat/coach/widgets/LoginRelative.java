package com.blackcat.coach.widgets;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.blackcat.coach.utils.LogUtil;

/**
 * Created by pengdonghua on 2016/4/8.
 */
public class LoginRelative  extends LinearLayout{

    public static final int KEYBORAD_HIDE  = 0;

    public static final int KEYBORAD_SHOW = 1;

    private static final int SOFTKEYPAD_MIN_HEIGHT = 50;

    private Handler uiHandler = new Handler();

    public LoginRelative(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoginRelative(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected  void onSizeChanged(int w,final int h,final int oldw,final int oldh ){
        super.onSizeChanged(w,h,oldw,oldh);
        LogUtil.print("size--changed-->" + w + h + oldw);
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (oldh - h > SOFTKEYPAD_MIN_HEIGHT) {
                    stateListener.statechange(KEYBORAD_SHOW);
                } else if (stateListener != null) {
                    stateListener.statechange(KEYBORAD_HIDE);
                }
            }
        });
    }

    KeyBordStateListener stateListener;

    public void setStateListener(KeyBordStateListener stateListener){
        this.stateListener = stateListener;
    }

    public interface KeyBordStateListener{

        public void statechange(int state);

    }
}



