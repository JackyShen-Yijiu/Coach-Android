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

    @SerializedName("1")
    APPLYING,
    @SerializedName("2")
    APPLYCANCEL,
    @SerializedName("3")
    APPLYCONFIRM,
    @SerializedName("4")
    APPLYREFUSE,
    @SerializedName("5")
    UNCONFIRMFINISH,
    @SerializedName("6")
    UNCOMMENTS,
    @SerializedName("7")
    FINISH,
    UNKNOWN
}
