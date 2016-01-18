package com.blackcat.coach.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zou on 15/10/26.
 */
public enum ReservationStatus {

//    // 报名信息处理状态S
//    exports.ReservationState={
//                //课程申请中待确认
//                applying:1,
//                // 取消预约(自己取消)
//                applycancel:2,
//                // 已确认
//                applyconfirm:3,
//                //已拒绝(教练取消)
//                applyrefuse:4,
//                //  （课程结束）待确认完成课程
//                unconfirmfinish:5,
//                //已确认完成，待评价
//                ucomments:6,
//                // 已完成 课程结束
//                finish:7
//    }
    @SerializedName("0")
    APPLYINVALID,//所有订单
    @SerializedName("1")
    APPLYING,//预约中--------------------------新订单
    @SerializedName("2")
    APPLYCANCEL,//学生取消（已取消）-----------学生取消
    @SerializedName("3")
    APPLYCONFIRM,//已确定 -- ------------------新订单
    @SerializedName("4")
    APPLYREFUSE,//教练拒绝或者取消(已取消)-----已取消
    @SerializedName("5")
    UNCONFIRMFINISH,//待确认完成(无此状态) ----待确认完成
    @SerializedName("6")
    UNCOMMENTS,//待评论 -----------------------待评论
    @SerializedName("7")
    ONCOMMENTED,//评论成功(已完成)-------------评论成功
    @SerializedName("8")
    FINISH,//订单完成（已完成）- --------------订单完成
    @SerializedName("9")
            SYSTEM_CANCEL,//系统取消(已取消)---系统取消
    @SerializedName("10")
    SIGNIN,//已签到(新订单)------------已签到
    @SerializedName("11")
    UNSINGIN ,//漏课,未签到
    @SerializedName("12")
    UNKNOWN//未知

}
