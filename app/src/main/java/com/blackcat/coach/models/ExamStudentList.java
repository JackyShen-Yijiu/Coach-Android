package com.blackcat.coach.models;

import java.io.Serializable;

public class ExamStudentList implements Serializable{


    public String examinationdate;
    public String _id;
    public User userid;
    public String examinationstate; //3漏考 4 没有通过 5 通过
    public String score;//成绩 没有时显示暂无成绩



}