package com.blackcat.coach.models;

import java.util.List;

/**
 * Created by zou on 15/10/17.
 */
public class Course {
    public String classid;
    public String classname;
    public String price;
    public String onsaleprice;
    public String address;
    public boolean is_choose;
    public List<Vip> vipserverlist;
}
