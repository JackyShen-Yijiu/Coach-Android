package com.blackcat.coach.push;

/**
 * Created by zou on 15/11/14.
 */
public class MsgType {
    /***
     * 审核通过
     */
    public final static String MSG_AUDIT_SUCC = "auditsucess";
    /****审核不通过*/
    public final static String MSG_AUDIT_FAILED = "auditfailed";
    /***新的预约*/
    public final static String MSG_RESERVATION_NEW = "newreservation";
    /***预约取消*/
    public final static String MSG_RESERVATION_CANCEL = "reservationcancel ";
    /***得到新的评价*/
    public final static String MSG_CMT_NEW = "newcommnent";
    /**钱包更新*/
    public final static String MSG_WALLET_UPDATE = "walletupdate";
    public final static String MSG_NEW_VERSION = "newversion";

    /***
     * 课程详情
     */
    public final static String MSG_NEW_APPLY_SUCCESS = "userapplysuccess";
    public final static String MSG_NEW_RESERVATION_SUCCESS= "reservationsucess";
    public final static String MSG_NEW_RESERVATIONCOMMENT = "reservationcoachcomment";
    public final static String MSG_SYSTEM = "systemmsg";





}
