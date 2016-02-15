package com.blackcat.coach.models;

import java.io.Serializable;

public class User implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public String name;
    public String _id;
    public String displayuserid;
    public String mobile;
    public DrivingSchool applyschoolinfo;
    public HeadPortraint headportrait;
    public CarModel carmodel;
    public Subject subject;
    public Subject2 subjecttwo;
    public Subject3 subjectthree;
    public String subjectprocess;
    public String address;
    /**剩余课程*/
    public String leavecoursecount;
    /**遗漏课程*/
    public String missingcoursecount;
    /**发送短信  是否需要发送，1：发送 0.不发送*/
    public int seleted = 1;

}
