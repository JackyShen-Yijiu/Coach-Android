package com.blackcat.coach.models;

import java.io.Serializable;

/**
 * Created by pengdonghua on 2016/1/21.
 */
public class JpushNew implements Serializable{

    public String alert;
    public String title;
    public int builder_id;
    public JpushExtras extras;
    public String msg_content;
    public String content_type;


}
