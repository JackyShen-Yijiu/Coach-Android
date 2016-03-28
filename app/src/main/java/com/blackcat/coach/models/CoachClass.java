package com.blackcat.coach.models;

/**
 * Created by wangxiaoqing02 on 2015/10/28.
 */
public class CoachClass {
    private String className;
    private boolean checked;
    public boolean is_choose;

    public CoachClass() {
        className = "";
        checked = false;
    }

    public CoachClass(String className, boolean checked) {
        this.className = className;
        this.checked = checked;
    }

    public String getClassName() {
        return  className;
    }

    public boolean isChecked() {
        return  checked;
    }

    public void SetChecked(boolean checked) {
        this.checked = checked;
    }
}
