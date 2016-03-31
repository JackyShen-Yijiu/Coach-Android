package com.blackcat.coach.models;

import com.blackcat.coach.models.CourseTime;
import com.blackcat.coach.models.Subject;
import com.blackcat.coach.models.User;

import java.io.Serializable;
import java.util.List;

public class DaytimelysReservation implements Serializable{


    public String _id;
    public String coachid;
    public String coursebegintime;
    public String coursedate;
    public String courseendtime;
    public List<String> coursereservation;
    public List<Coursereservationdetial> coursereservationdetial;
    public int coursestudentcount;
    public CourseTime coursetime;
    public List<String> courseuser;
    public String createtime;
    public String driveschool;
    public int selectedstudentcount;
    public int signinstudentcount;


    public class Coursereservationdetial implements Serializable{

        public int reservationstate;
        public String reservationcreatetime;
        public String courseprocessdesc;
        public String endtime;
        public String begintime;
        public String _id;
        public Subject subject;



        public User userid;


    }





}
