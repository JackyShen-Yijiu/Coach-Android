package com.blackcat.coach.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by aa on 2016/3/30.
 */
public class StudentDetailVo implements Serializable{
    private static final long serialVersionUID = 1L;

    public List<Comment> coachcommentinfo;
    public User studentinfo;


//    public class Coachcommentinfo {
//        public String _id;
//        public String coachcomment;
//        public String finishtime;
//        public String coachid;
//        public String timestamp;
//
//    }


}
