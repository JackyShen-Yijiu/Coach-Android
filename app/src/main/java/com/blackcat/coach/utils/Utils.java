package com.blackcat.coach.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;


public class Utils {

    private static final boolean DEBUG = AppEnv.bAppdebug;

    private static final String TAG = Utils.class.getSimpleName();

    public static boolean wildcardMatches(String pattern, String str) {
        if (pattern == null || pattern.length() == 0) {
            return false;
        }
        if (str == null || str.length() == 0) {
            return false;
        }

        // 对于 +86139* 这种规则，需要把 + 号去掉，否则正则表达是有问题
        if (pattern.charAt(0) == '+') {
            if (str.charAt(0) != '+') {
                return false;
            }

            pattern = pattern.substring(1);
            str = str.substring(1);
        }

        String newpattern = "^";
        for (int i = 0; i < pattern.length(); i++) {
            char ch = pattern.charAt(i);
            if (ch == '*' || ch == '?') {
                newpattern += ".";
            }
            newpattern += ch;
        }
        newpattern += "$";

        boolean match = false;
        try {
            match = Pattern.matches(newpattern, str);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return match;

    }

    public static boolean isNumber(char ch) {
        if (ch >= '0' && ch <= '9') {
            return true;
        }
        return false;
    }

    public static boolean isLetter(char ch) {
        if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {
            return true;
        }
        return false;
    }

    public static boolean isChinese(char ch) {
        if (ch >= 0x4e00 && ch <= 0x9fa5) {
            return true;
        }
        return false;
    }

    public static boolean isChineseBiaodiao(char ch) {
        if (ch >= 0x3000 && ch <= 0x303F) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符是否是构成域名的合法字符。只有数字、字母、“-”、下划线才可以构成域名。
     *
     * @param ch
     *            要检查的字符
     * @return true or false.
     */
    public static boolean isValidDomainChar(char ch) {
        if (Utils.isLetter(ch)) {
            return true;
        }
        if (Utils.isNumber(ch)) {
            return true;
        }
        if ((ch == '-') || (ch == '_')) {
            return true;
        }

        return false;
    }

    public static char quanjiao2Banjiao(char aUnicode) {
        if ((aUnicode >= 0xff10 && aUnicode <= 0xff19) || // 0~9
                (aUnicode >= 0xFF21 && aUnicode <= 0xFF3A) || // A~Z
                (aUnicode >= 0xFF41 && aUnicode <= 0xFF5A)) {// a~z
            aUnicode -= 0xfee0;
        }
        return aUnicode;
    }
    public static long getMemoryTotalKb() {
        long totalSize = -1L;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/meminfo"));
            String line, totle = null;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("MemTotal:")) {
                    totle = line.split(" +")[1];
                    break;
                }
            }

            totalSize = Long.valueOf(totle);

            return totalSize;
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, "Get Memory Err", e);
            }
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }

        return totalSize;
    }

    public static long getMemoryFreeKb() {
        long freeSize = -1L;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/meminfo"));
            String line, buff = null, cache = null, free = null;
            int count = 0;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("MemFree")) {
                    count++;
                    free = line.split(" +")[1];
                    if (count >= 3) {
                        break;
                    }
                } else if (line.startsWith("Buffers")) {
                    count++;
                    buff = line.split(" +")[1];
                    if (count >= 3) {
                        break;
                    }
                } else if (line.startsWith("Cached")) {
                    count++;
                    cache = line.split(" +")[1];
                    if (count >= 3) {
                        break;
                    }
                } else {
                    continue;
                }
            }

            freeSize = (Long.valueOf(free) + Long.valueOf(buff) + Long.valueOf(cache));

            return freeSize;
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, "get Free Memory err", e);
            }
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        return freeSize;
    }

    /**
     * 返回总的内存.
     *
     * @return 返回整数，单位 MB
     */
    public static int getMemoryTotal() {
        long totalSize = getMemoryTotalKb();
        if (totalSize == -1L) {
            return -1;
        }

        return (int) (totalSize / 1024F);
    }

    /**
     * 返回可用的内存.
     *
     * @return 返回整数，单位 MB
     */
    public static int getMemoryFree() {
        long freeSize = getMemoryFreeKb();
        if (freeSize == -1L) {
            return -1;
        }

        return (int) (freeSize / 1024F);
    }

    /** 如失败返回空列表，不会为NULL */
    public static List<String> parseConfigFile(Context context, String filename) {
        if (DEBUG) {
            Log.d(TAG, "read config file: " + filename);
        }
        File file = context.getFileStreamPath(filename);
        if (!file.exists()) {
            if (DEBUG) {
                Log.d(TAG, "user config file not exists: " + file.getPath());
            }
            return new ArrayList<String>(0);
        }

        try {
            return parseConfigFile(new FileReader(file));
        } catch (FileNotFoundException e) {
            if (DEBUG) {
                Log.e(TAG, "file not found", e);
            }
            return new ArrayList<String>(0);
        } finally {
            if (DEBUG) {
                Log.d(TAG, "read config file Done: " + filename);
            }
        }
    }

    /** 读配置文件，获取配置列表，如失败返回空列表，不会为NULL */
    public static List<String> parseConfigFile(Reader in) {
        List<String> configList = new ArrayList<String>();

        BufferedReader br = null;
        try {
            br = new BufferedReader(in, 1024);
            String line;
            while (!TextUtils.isEmpty(line = br.readLine())) {
                if (!line.startsWith("#")) {
                    // # 开头的行为注释，跳过
                    configList.add(line.trim());
                }
                if (DEBUG) {
                    Log.d(TAG, "get config: " + line);
                }
            }
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, "read config file failed", e);
            }
            configList = null;

            return new ArrayList<String>(0);
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                // ignore
            }
        }

        if (DEBUG) {
            Log.d(TAG, "Get config list Done!");
        }

        if (configList.size() > 0) {
            return configList;
        } else {
            return new ArrayList<String>(0);
        }
    }

    /** Save the config into config file by line */
    public static boolean saveUserConfigFile(Context context, String filename, List<String> configList) {
        if (DEBUG) {
            Log.d(TAG, "Save config file: " + filename);
        }

        File file = context.getFileStreamPath(filename);

        if (configList == null) {
            if (DEBUG) {
                Log.d(TAG, "config null");
            }

            return true;
        }

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file), 512);
            for (String str : configList) {
                bw.write(str);
                bw.newLine();
            }
        } catch (Exception e) {
            if (DEBUG) {
                Log.d(TAG, "save config file failed: " + filename, e);
            }

            return false;
        } finally {
            try {
                bw.close();
            } catch (Exception e) {

            }
        }

        if (DEBUG) {
            Log.d(TAG, "Save config file Done: " + filename);
        }

        return true;
    }

    /**
     * Formats data size in KB, MB, from the given bytes.
     *
     * @param context
     *            the application context
     * @param bytes
     *            data size in bytes
     * @return the formatted size such as 4.52 MB or 245 KB or 332 bytes
     */
    public static String formatBytes(Context context, double bytes) {
        return formatBytes(context, bytes, 1000f);
    }

    /**
     * Formats data size in KB, MB, from the given bytes, with carry: 1024.
     *
     * @param context
     *            the application context
     * @param bytes
     *            data size in bytes
     * @return the formatted size such as 4.52 MB or 245 KB or 332 bytes
     */
    public static String formatBytes1024(Context context, double bytes) {
        return formatBytes(context, bytes, 1024f);
    }

    /**
     * Formats data size in KB, MB, from the given bytes.
     *
     * @param context
     *            the application context
     * @param bytes
     *            data size in bytes
     * @param carry
     *            1000 or 1024
     * @return the formatted size such as 4.52 MB or 245 KB or 332 bytes
     */
    public static String formatBytes(Context context, double bytes, double carry) {
        // TODO: I18N
        if (bytes > carry * carry) {
            return String.format("%.2f MB", bytes / carry / carry);
        } else if (bytes > carry) {
            return String.format("%.2f KB", bytes / carry);
        } else {
            return String.format("%d bytes", (int) bytes);
        }
    }

    // 调用系统界面,显示程序详细信息
    // 注意2.3开始修改界面调用接口,因此需要做判断
    public static void showPackageDetial(Context context, String packageName) {
        try {
            if (VERSION.SDK_INT >= 9) {
                Uri localUri = Uri.parse("package:" + packageName);
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", localUri);
                context.startActivity(intent);
            } else {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                intent.putExtra("com.android.settings.ApplicationPkgName", packageName);
                intent.putExtra("pkg", packageName);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 判断列表是否为空 */
    public static boolean isListEmpty(List<?> list) {
        if (list != null && !list.isEmpty()) {
            return false;
        }

        return true;
    }


    /**
     * 创建文件夹快捷方式图标，文件夹中包含appIcon
     * @param context 用于获取系统图标默认大小
     * @param bgBitmap 文件夹背景
     * @param icos 需要绘制的图标 (最多绘4个图标)
     * @return
     */
     public static Bitmap createShortIcon(Context context,Bitmap bgBitmap,List<Bitmap> icons){

         int mPaddingLeft = 8;
         int mPaddingTop = 8;
         int mAppMargin = 4;

         if ( icons.size() > 4 ) {
             throw new IllegalArgumentException("the icon size must less than 4!");
         }
         //默认图标大小
         int iconSize = (int)context.getResources().getDimension(android.R.dimen.app_icon_size);
         //创建一个空内容的bitmap，用于拷贝背景
         Bitmap blank = Bitmap.createBitmap(iconSize, iconSize, Config.ARGB_8888);
         //初始化画布
         Canvas canvas = new Canvas( blank );
         //拷贝背景
         Paint iconPaint=new Paint();
         iconPaint.setDither(true);//防抖动
         iconPaint.setFilterBitmap(true);//用来对Bitmap进行滤波处理，这样，当你选择Drawable时，会有抗锯齿的效果
         Rect src=new Rect(0, 0, bgBitmap.getWidth(), bgBitmap.getHeight());
         Rect dst=new Rect(0, 0, iconSize, iconSize);
         canvas.drawBitmap(bgBitmap, src, dst, iconPaint);

         Bitmap icon = null;
         for(int i = 0; i < icons.size(); i++){
             icon = icons.get(i);
             //绘制图标
             src = new Rect(0, 0, icon.getWidth() , icon.getWidth());
             int appSize = (iconSize - mAppMargin  - mPaddingLeft - mPaddingTop) / 2;
             int drawLeft = mPaddingLeft;
             int drawTop = mPaddingTop;

             switch (i) {
             case 0:
                 drawLeft = mPaddingLeft;
                 drawTop = mPaddingTop;
                 break;
             case 1:
                 drawLeft = mPaddingLeft + mAppMargin + appSize;
                 drawTop = mPaddingTop;
                 break;
             case 2:
                 drawLeft = mPaddingLeft;
                 drawTop = mPaddingTop + mAppMargin + appSize;
                 break;
             case 3:
                 drawLeft = mPaddingLeft + mAppMargin + appSize;
                 drawTop = mPaddingTop + mAppMargin + appSize;
                 break;
             }

             dst = new Rect(drawLeft, drawTop, drawLeft + appSize , drawTop + appSize);
             canvas.drawBitmap(icon, src, dst, iconPaint);
         }

       return blank;

     }

    public static String desStr(byte[] plain, byte[] key) {
        byte[] decryptedData = null;
        try {
            // DES算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();

            // 从原始密钥数据创建DESKeySpec对象
            // 由于 DES 要求秘钥是 64bit 的，而用户直接输入的 key 可能长度不够，这里简单点，先对 key 进行 md5，截断取前
            // 8 个字节
            DESKeySpec dks = new DESKeySpec(key);

            // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
            // 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);

            // using DES in ECB mode
            Cipher cipher = Cipher.getInstance("DES");

            // 用密匙初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);

            // 正式执行解密操作
            decryptedData = cipher.doFinal(plain);
            return new String(decryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /** DES加密字符串 */
    public static byte[] desString(String plain, String key) {
        byte[] encryptedData = null;
        try {
            // DES算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();

            // 从原始密钥数据创建DESKeySpec对象, DES 要求秘钥是 64bit的
            DESKeySpec dks = new DESKeySpec(key.getBytes());

            // 创建一个密匙工厂，然后用它把DESKeySpec转换成 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);

            // using DES in ECB mode
            Cipher cipher = Cipher.getInstance("DES");

            // 用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);

            // 执行加密操作
            encryptedData = cipher.doFinal(plain.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encryptedData;
    }

    /**
     * 字符串转换成int
     *
     * @param str
     * @return
     */
    public static int str2Int(String str, int defValue) {
        int ret = defValue;
        try {
            if (!TextUtils.isEmpty(str)) {
                ret = Integer.parseInt(str.trim());
            }
        } catch (Exception ex) {
        }
        return ret;
    }

    /**
     * 字符串转换成long
     *
     * @param str
     * @return
     */
    public static long str2Long(String str, long defValue) {
        long ret = defValue;
        try {
            if (!TextUtils.isEmpty(str)) {
                ret = Long.parseLong(str.trim());
            }
        } catch (Exception ex) {
        }
        return ret;
    }

    /**
     * 字符串转换成float
     *
     * @param str
     * @return
     */
    public static float str2Float(String str, float defValue) {
        float ret = defValue;
        try {
            if (!TextUtils.isEmpty(str)) {
                ret = Float.parseFloat(str.trim());
            }
        } catch (Exception ex) {
        }
        return ret;
    }

    /**
     * 字符串转换成double
     *
     * @param str
     * @return
     */
    public static double str2Double(String str, double defValue) {
        double ret = defValue;
        try {
            if (!TextUtils.isEmpty(str)) {
                ret = Double.parseDouble(str.trim());
            }
        } catch (Exception ex) {
        }
        return ret;
    }

    /**
     * 动态替换文本并改变文本颜色
     *
     * @param context
     * @param resStrId
     * @param resColorId
     * @param object
     * @return
     */
    public static SpannableStringBuilder getSpannableStringBuilder(Context context, int resStrId, int resColorId,
            String... object) {
        String processScanReport = context.getString(resStrId, (Object[]) object);

        int len = object.length;
        int index[] = new int[len];
        for (int i = 0; i < len; i++) {
            index[i] = processScanReport.indexOf(object[i]);
        }
        SpannableStringBuilder processSpanStr = new SpannableStringBuilder(processScanReport);
        for (int i = 0; i < len; i++) {
            index[i] = processScanReport.indexOf(object[i]);
            processSpanStr.setSpan(new ForegroundColorSpan(context.getResources().getColor(resColorId)), index[i],
                    index[i] + object[i].length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        return processSpanStr;
    }


    /**
     * 得到背景设置值
     *
     * @param c
     * @return
     */
    // zhong: 主屏背景不再推送，删除
    //    public static int getBgSettingsValue(Context c) {
    //
    //        int value = SharedPref.getInt(c, SharedPref.MAIN_SCREEN_BG, SharedPref.MAIN_SCREEN_BG_INITIAL);
    //
    //        if (value == SharedPref.MAIN_SCREEN_BG_USER) {
    //            //取用户自定义的背景
    //            String sdcardPath = Utils.getSDPathBySDKApi();
    //            if (sdcardPath != null) {
    //                File bmpFile = new File(sdcardPath, MainScreenBg.APP_FILE_FULL_PATH);
    //                if (bmpFile.exists()) {
    //                    return SharedPref.MAIN_SCREEN_BG_USER;
    //                }
    //            }
    //        }
    //
    //        if (value != SharedPref.MAIN_SCREEN_BG_DEFAULT) {
    //            //取推送的背景
    //            File bmpFile = new File(c.getFilesDir(), MainScreenBg.IMAGE_SAVE_NAME);
    //            if (bmpFile.exists()) {
    //                return SharedPref.MAIN_SCREEN_BG_PUSH;
    //            }
    //        }
    //
    //        return SharedPref.MAIN_SCREEN_BG_DEFAULT;
    //    }


    public static void closeCursor(Cursor cursor) {
        try {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        } catch (Exception ex) {
            // java.lang.IllegalStateException: stableCount < 0: -1
            // at android.os.Parcel.readException(Parcel.java:1433)
            // at android.os.Parcel.readException(Parcel.java:1379)
            // at android.app.ActivityManagerProxy.refContentProvider(ActivityManagerNative.java:2405)
            // at android.app.ActivityThread.releaseProvider(ActivityThread.java:4406)
            // at android.app.ContextImpl$ApplicationContentResolver.releaseProvider(ContextImpl.java:1730)
            // at android.content.ContentResolver$CursorWrapperInner.close(ContentResolver.java:1841)
        }
    }

    /**
     * 得到天数
     *
     * @param lastExamTime
     * @return
     */
    public static long getIntervalTime(long lastExamTime) {

        long now = System.currentTimeMillis();
        long sinceLastExam = now - lastExamTime;
        long daysSinceLastExam = sinceLastExam / (24 * 60 * 60 * 1000);

        return daysSinceLastExam;
    }


    /**
     * 通过反射加载安装包Resources
     */
    public static Resources getApkResByRefrect(Context context, String apkFilePath) {
        Method addAssetPathMethod = null;
        Object instance = null;
        Class<?> clazz = null;
        Resources apkRes = null;
        try {
            clazz = Class.forName("android.content.res.AssetManager");
            instance = clazz.newInstance();
            addAssetPathMethod = clazz.getMethod("addAssetPath", String.class);
            addAssetPathMethod.invoke(instance, apkFilePath);
            Resources res = context.getResources();
            apkRes = new Resources((AssetManager) instance, res.getDisplayMetrics(), res.getConfiguration());
        } catch (Throwable e) {
            if (DEBUG) {
                Log.e(TAG, "Class.forName(\"android.content.res.AssetManager\") error", e);
            }
        }
        return apkRes;
    }

    /**
     * 通过反射加载安装包Icon
     */
    public static Drawable loadApkIcon(Context context, int iconID, String apkFilePath) {
        if (0 == iconID) {
            return null;
        }

        Drawable icon = null;
        Resources res = getApkResByRefrect(context, apkFilePath);
        if (res != null) {
            try {
                icon = res.getDrawable(iconID);
            } catch (Throwable e) {
                if (DEBUG) {
                    Log.e(TAG, "getDrawable error", e);
                }
            }
        }
        return icon;
    }

    /**
     * 外置存储卡是否可以移除
     * */
    public static boolean isExternalStorageRemovable() {
        Method isExternalStorageRemovableMethod = null;
        boolean isExternalStorageRemovable = false;
        try {
            isExternalStorageRemovableMethod = Environment.class.getMethod("isExternalStorageRemovable");
            isExternalStorageRemovable = (Boolean) isExternalStorageRemovableMethod.invoke(Environment.class,
                    (Object[]) null);
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return isExternalStorageRemovable;
    }

    /**
     * SD卡是否是虚拟出来的（软件搬家不支持虚拟的SD卡）
     * */
    public static boolean isExternalStorageEmulated() {
        if (VERSION.SDK_INT >= 14) {
            try {
                Class<?> classEnvironment = Class.forName("android.os.Environment");
                Method method_isExternalStorageEmulated = classEnvironment.getMethod("isExternalStorageEmulated",
                        new Class[0]);
                Boolean isExternalStorageEmulated = (Boolean) method_isExternalStorageEmulated.invoke(
                        classEnvironment.getClass(), new Object[0]);
                if (isExternalStorageEmulated) {
                    return true;
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

    public static void finishActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
        }
    }

    public static View findViewById(Activity activity, int id) {
        if (activity != null) {
            return activity.findViewById(id);
        }
        return null;
    }

    public static View findViewById(View view, int id) {
        if (view != null) {
            return view.findViewById(id);
        }
        return null;
    }

    public static View findViewById(Dialog dialog, int id) {
        if (dialog != null) {
            return dialog.findViewById(id);
        }
        return null;
    }

    public static View findViewById(Window window, int id) {
        if (window != null) {
            return window.findViewById(id);
        }
        return null;
    }

//    public static View findViewById(IDialog idialog, int id) {
//        if (idialog != null) {
//            return idialog.findViewById(id);
//        }
//        return null;
//    }

    public static Intent getActivityIntent(Activity activity) {
        if (activity != null) {
            return activity.getIntent();
        }
        return null;
    }

    public static boolean isActivityFinishing(Activity activity) {
        return activity != null && activity.isFinishing();
    }

    public static void setContentView(Activity act, int id) {
        if(act == null) {
            if(AppEnv.bAppdebug) {
                throw new SecurityException(">>>>  setContentView(activity is null)   <<<<");
            }
            return;
        }

        act.setContentView(id);
    }
    public static void setContentView(Dialog dialog, int id) {
        if(dialog == null) {
            if(AppEnv.bAppdebug) {
                throw new SecurityException(">>>>  setContentView(dialog is null)   <<<<");
            }
            return;
        }

        dialog.setContentView(id);
    }
    public static void setContentView(Activity act, View view) {
        if(act == null) {
            if(AppEnv.bAppdebug) {
                throw new SecurityException(">>>>  setContentView(activity is null)   <<<<");
            }
            return;
        }
        act.setContentView(view);
    }
    public static void setContentView(Dialog dialog, View view) {
        if(dialog == null) {
            if(AppEnv.bAppdebug) {
                throw new SecurityException(">>>>  setContentView(dialog is null)   <<<<");
            }
            return;
        }
        dialog.setContentView(view);
    }

    public static void startActivity(Activity activity, Intent intent) {
        if (activity != null) {
            try {
                activity.startActivity(intent);
            } catch (Exception e) {
                if (AppEnv.bAppdebug) {
                    throw new SecurityException("Utils.startActivity: got Exception:" + e.getMessage());
                }
            }
        }
    }

    public static void startActivity(Context context, Intent intent) {
        if (context != null) {
            try {
            	context.startActivity(intent);
            } catch (Exception e) {
                if (AppEnv.bAppdebug) {
                    throw new SecurityException("Utils.startActivity: got Exception:" + e.getMessage());
                }
            }
        }
    }

    public static void startActivityForResult(Activity activity, Intent intent, int requestCode) {
        if (activity != null) {
            try {
                activity.startActivityForResult(intent, requestCode);
            } catch (Exception e) {
                if (AppEnv.bAppdebug) {
                    throw new SecurityException("Utils.startActivityForResult: got Exception:" + e.getMessage());
                }
            }
        }
    }

    /*
     *  判断360搜索apk是否下载
     */
    public static boolean IsV5DownloadSearchApk(Context context) {
        File sdir = context.getFilesDir();
        File f = new File(sdir, "plugin-s-funpicture.jar");
    	return f.exists();
    }

    public static Intent registerReceiver(Context context, BroadcastReceiver receiver, IntentFilter filter) {
        if (context != null) {
            return context.registerReceiver(receiver, filter);
        }
        return null;
    }

    public static Intent registerReceiver(Context context, BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler) {
        if(context != null) {
            return context.registerReceiver(receiver, filter, broadcastPermission, scheduler);
        }
        return null;
    }
    public static void unRegisterReceiver(Context context, BroadcastReceiver receiver) {
        if (context != null) {
            context.unregisterReceiver(receiver);
        }
    }

    public static String getActivityString(Context context, int id) {
        if (context != null) {
            return context.getString(id);
        }
        return null;
    }

    public static String getActivityString(Context context, int id, Object... formatArgs) {
        if (context != null) {
            return context.getString(id, formatArgs);
        }
        return null;
    }

    public static Activity getFragmentActivity(Fragment fragment) {
        if (fragment != null) {
            return fragment.getActivity();
        }
        return null;
    }

    public static void startService(Context context, Intent intent) {
        if (context != null) {
            context.startService(intent);
        }
    }

    /**
     * 在一些ROM上，如果Notification.when不设置为一个固定值，每次更新通知栏的时候，通知栏就会闪烁一下。
     * 例如：三星通信研究院反馈，在Samsung N9006/ N9008上通知栏中的卫士下载手机助手的下载条一直闪烁。
     * 所以对于频繁更新的Notification，统一调用该函数设置when属性。
     *
     * @param notify
     */
    public static void setNotificationWhen(Notification notify) {
        long notifyWhen = 10000000L;
        if (VERSION.SDK_INT >= 14) {
            notifyWhen = (System.currentTimeMillis() / 3600000L + 1) * 3600000L;
        }
        if (DEBUG) {
            Log.i(TAG, "notifyWhen=" + notifyWhen);
        }
        notify.when = notifyWhen;
    }
    /**
     * 是否是已经激活了的设备管理器
     */
    public static boolean isActiveAdmin(Context context, String pkgName) {
        boolean isActiveAdmin = false;
        if (VERSION.SDK_INT >= 8) {
            try {
                Class<?> clazz = Class.forName("android.app.admin.DevicePolicyManager");
                Object instance = context.getSystemService("device_policy");
                Method method = clazz.getMethod("packageHasActiveAdmins", String.class);
                isActiveAdmin = (Boolean) method.invoke(instance, pkgName);
            } catch (Exception e) {
            }
        }
        return isActiveAdmin;
    }

    /**
     * 打开设备管理器设置页面
     */
    public static void openDeviceAdminSetting(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.DeviceAdminSettings");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            if (DEBUG) {
                Log.w(TAG, "openDeviceAdminSetting startActivity failed" + e);
            }
        }
    }

    public static void openDeviceAdminForPackage(Context context, String packageName) {
        List<ComponentName> activeAdminComponents = getPackageDeviceAdminComponents(context.getApplicationContext(),
                packageName);
        if (null != activeAdminComponents && activeAdminComponents.size() > 0) {
            for (ComponentName componentName : activeAdminComponents) {
                openDeviceAdminForComponent(context, componentName);
            }
        }
    }

    /**
     * 打开设备管理器设置页面
     */
    public static void openDeviceAdminForComponent(Context context, ComponentName componentName) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.DeviceAdminAdd");
        intent.putExtra("android.app.extra.DEVICE_ADMIN", componentName);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            if (DEBUG) {
                Log.w(TAG, "openDeviceAdminAdd startActivity failed" + e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<ComponentName> getPackageDeviceAdminComponents(Context context,
            String packageName) {
        try {
            Class<?> clazz_DevicePolicyManager = Class
                    .forName("android.app.admin.DevicePolicyManager");
            Method method_getActiveAdmins = clazz_DevicePolicyManager.getMethod("getActiveAdmins",
                    (Class[]) null);
            List<ComponentName> activeAdmins = (List<ComponentName>) method_getActiveAdmins.invoke(
                    context.getSystemService("device_policy"), (Object[]) null);
            if (null != activeAdmins && activeAdmins.size() > 0) {
                ArrayList<ComponentName> packageComponents = new ArrayList<ComponentName>();
                for (ComponentName adminComponent : activeAdmins) {
                    if (adminComponent.getPackageName().equals(packageName)) {
                        packageComponents.add(adminComponent);
                    }
                }
                return packageComponents;
            }
        } catch (Exception e) {
        }
        return null;
    }

    /** 只含有'0-9'、'+' */
    public static boolean isTelNum(String str) {
        boolean result = true;
        if (TextUtils.isEmpty(str)) {
            result = false;
        } else {
            int count = str.length();
            for (int i = 0; i < count; i++) {
                char ch = str.charAt(i);
                if (!Character.isDigit(ch) && ch != '+') {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }


     public static String cutOffAppLabel(String appLabel) {
         if (TextUtils.isEmpty(appLabel)) {
             return appLabel;
         }
         if (appLabel.length() > 32) {
             return appLabel.substring(0, 32) + "...";
         }
         return appLabel;
     }
     
     /*
      * 获取StatusBar的高度
      */
     public static int getStatusBarHeight(Context context) {

         Class<?> c = null;
         Object obj = null;
         Field field = null;
         int x = 0, sbar = 0;
         try {
             c = Class.forName("com.android.internal.R$dimen");
             obj = c.newInstance();
             field = c.getField("status_bar_height");
             x = Integer.parseInt(field.get(obj).toString());
             sbar = context.getResources().getDimensionPixelSize(x);
         } catch (Exception e1) {
             e1.printStackTrace();
         }
         return sbar;
     }
}
