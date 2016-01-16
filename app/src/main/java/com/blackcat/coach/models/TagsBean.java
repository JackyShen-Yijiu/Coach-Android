package com.blackcat.coach.models;

import java.io.Serializable;

/**
 * Created by pengdonghua on 2016/1/15.
 */
public class TagsBean implements Serializable{

    public LabelBean[] systemtag;
    public LabelBean[] selft;

    public String toString(){
        String temp = null;
        if(null!=systemtag){
            for (LabelBean labelBean : systemtag) {
                temp = temp + labelBean.tagname;
            }
        }
        temp = temp+" 自定义标签： ";
        if(null!=selft){
            for (LabelBean labelBean : selft) {
                temp=temp + labelBean.tagname;
            }
        }
        return temp + super.toString();

    }

}
