package com.blackcat.coach.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pengdonghua on 2016/1/20.
 */
public class DateUtil {

    /**
     *
     * @param time 2015-12-15T15:59:43.308Z
     * @return 2015-12-15 15:59
     */
    public static String parseTime(String time)  {
//        2015-12-15T15:59:43.308Z
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        if(null == time){
            return time;
        }
        try {
            Date d = f.parse(time);
            return f1.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
            return time;
        }

    }

    public static boolean isIdCard(String str){
        Pattern pt = Pattern.compile("\\d{15,17}([\\dxX]{1})?");
        Matcher mt = pt.matcher(str);
        if (!mt.find()) {
            return false;
        }
        return true;
    }


}
