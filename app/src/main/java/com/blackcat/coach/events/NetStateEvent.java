package com.blackcat.coach.events;

/**
 * Created by wangxiaoqing02 on 2015/11/16.
 */
public class NetStateEvent {
    public NetStateEvent() {
        mIsNetOk =true;
        mErrorMsg = null;
    }
    public boolean mIsNetOk;
    public String  mErrorMsg;
}
