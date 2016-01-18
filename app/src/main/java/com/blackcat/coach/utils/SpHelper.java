package com.blackcat.coach.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by pengdonghua on 2016/1/16.
 */
public class SpHelper {


    SharedPreferences sp;
    public SpHelper(Context c){
        if(sp ==null)
            sp = c.getSharedPreferences("coache1234", Context.MODE_PRIVATE);
    }

    public void set(String key,String value){
        LogUtil.print("save-->"+key+"value->"+value);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key,value);
        editor.commit();
    }

    public void set(String key,int value){
        LogUtil.print("save-->"+key+"value->"+value);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public String get(String key,String defaultValue){
        return sp.getString(key,defaultValue);
    }
    public int get(String key,int defaultValue){
        return sp.getInt(key,defaultValue);
    }

}
