package com.blackcat.coach.models;

import java.io.Serializable;

/**
 * Created by aa on 2016/3/29.
 */
public class AddStudentsVO implements Serializable {
    private static final long serialVersionUID = 1L;
    public HeadPortraint headportrait;
    public String name;
    public String mobile;
    public String userid;
    public Subjectone subjectone;
    public Subjecttwo subjecttwo;
    public Subjectthree subjectthree;
}
