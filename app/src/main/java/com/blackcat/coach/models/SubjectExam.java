package com.blackcat.coach.models;

import java.io.Serializable;

/**
 * Created by aa on 2016/3/30.
 */
public class SubjectExam implements Serializable{
    /**v-examinationresult : "科目一考试状态 考试结果状态 0 未考核 1 未通过 2 通过"*/
    public int examinationresult;
    public int score;
    public int testcount;
    public String examinationresultdesc;
    public String examinationdate;
}
