package com.blackcat.coach.models;

import com.blackcat.coach.R;

import java.io.Serializable;

/**
 * Created by zou on 15/10/10.
 */
public class Reservation implements Serializable {
    public String endtime;
    public String begintime;
    public String shuttleaddress;
    public String _id;
    public Subject subject;
    public boolean is_shuttle;
    public String reservationcreatetime;
    public String courseprocessdesc;
    public String classdatetimedesc;
    public ReservationStatus reservationstate;
    public User userid;
    public TrainField trainfieldlinfo;

    public Cancelreason cancelreason;
    public int leavecoursecount;
    public int missingcoursecount;

    //用来更新Reservation状态
    public int pos;

    public ReservationStatus getReservationstate() {
        if (this.reservationstate == null) {
            this.reservationstate = ReservationStatus.UNKNOWN;
        }
        return this.reservationstate;
    }

    public int getReservationStatusDescId() {
        int id = R.string.reservation_unknown;
        switch (this.getReservationstate()) {
            case APPLYING:
                id = R.string.reservation_applying;
                break;
            case APPLYCANCEL:
                id = R.string.reservation_applycancel;
                break;
            case APPLYCONFIRM:
                id = R.string.reservation_confirm;
                break;
            case APPLYREFUSE:
                id = R.string.reservation_applyrefuse;
                break;
            case UNCONFIRMFINISH:
                id = R.string.reservation_unconfirmfinish;
                break;
            case UNCOMMENTS:
                id = R.string.reservation_uncomments;
                break;
//            case ONCOMMENTED:
//                id = R.string.reservation_oncomments;
//                break;
            case FINISH:
                id = R.string.reservation_finish;
                break;
            case SYSTEM_CANCEL:
                id = R.string.reservation_canceled;
                break;
            case SIGNIN:
                id = R.string.reservation_sign_in;
                break;
            case UNSINGIN://未签到
                 id = R.string.reservation_leakage_class;
                break;
            case UNKNOWN:
                default:
                break;
        }
        return id;
    }

}
