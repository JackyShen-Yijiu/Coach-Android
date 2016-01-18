package com.blackcat.coach.models.params;

import com.blackcat.coach.models.CoachInfo;
import com.blackcat.coach.models.DrivingSchool;
import com.blackcat.coach.models.Subject;
import com.blackcat.coach.models.TrainField;

import java.util.List;

/**
 * Created by wangxiaoqing02 on 2015/10/31.
 */
public class UpdateCoachParams {
    public UpdateCoachParams(CoachInfo coachInfo) {
        this.coachid = coachInfo.coachid;
        this.name = coachInfo.name;
        this.subject = coachInfo.subject;
        this.introduction = coachInfo.introduction;
        this.Seniority = coachInfo.Seniority;
        this.Gender = coachInfo.Gender;
        this.idcardnumber = coachInfo.idcardnumber;
        this.driveschoolid = coachInfo.driveschoolid;;
        this.trainfieldlinfo = coachInfo.trainfieldlinfo;
        this.drivinglicensenumber = coachInfo.drivinglicensenumber;
        this.coachnumber = coachInfo.coachnumber;
        this.mobile = coachInfo.mobile;
        this.driveschoolinfo = coachInfo.driveschoolinfo;
    }

    public String coachid;
    public String name;
    //public String address;
    public List<Subject> subject;
    public String introduction;
    public String Seniority;
    public String Gender;
    public String GenderOne;
    public String GenderJob;
    //public String platenumber;
    public String idcardnumber;
    public String driveschoolid;
    public String drivinglicensenumber;
    public String coachnumber;
    public TrainField trainfieldlinfo;
    public String mobile;
    public DrivingSchool driveschoolinfo;
}
