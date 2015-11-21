package com.blackcat.coach.utils;



public class AppEnv {

    public static final boolean bAppdebug = true; 
    public static final boolean bDebug = bAppdebug;


    /**
     * DisplayMetrics.densityDpi，用来判断手机屏幕分辨率，
     * 120，小屏幕
     * 160，中等屏幕
     * 240，HVGA屏幕
     * 320，高分辨率，比如720P的屏幕
     */
    private static int sDPI = 0;

//    public static int getDpi() {
//        if (sDPI <= 0) {
//            WindowManager wm = (WindowManager) Utils.getSystemService(UWealthApplication.getInstance(), android.content.Context.WINDOW_SERVICE);
//            DisplayMetrics dm = new DisplayMetrics();
//            Display display = wm.getDefaultDisplay();
//            if (display != null) {
//                display.getMetrics(dm);
//                sDPI = dm.densityDpi;
//            }
//        }
//
//        if (sDPI <= 0) {
//            return DisplayMetrics.DENSITY_HIGH;
//        } else {
//            return sDPI;
//        }
//    }
}
