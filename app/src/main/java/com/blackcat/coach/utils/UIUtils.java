package com.blackcat.coach.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ListView;


public class UIUtils {

    private static final boolean DEBUG = true;
    private static final String TAG = "UIUtils";

    /**
     * 动态替换文本并改变文本颜色
     * 仅限制一种颜色
     */
    public static SpannableStringBuilder getSpannableStringBuilder(Context context, int resStrId, int resColorId,
            String arg1) {
        return getSpannableStringBuilder(context, context.getString(resStrId, arg1), resColorId, arg1);
    }

    /**
     * 动态替换文本并改变文本颜色
     * 仅限制一种颜色
     */
    public static SpannableStringBuilder getSpannableStringBuilder(Context context, int resStrId, int resColorId,
            String arg1, String arg2) {
        return getSpannableStringBuilder(context, context.getString(resStrId, arg1, arg2), resColorId, arg1, arg2);
    }

    /**
     * 动态替换文本并改变文本颜色
     * 仅限制一种颜色
     */
    public static SpannableStringBuilder getSpannableStringBuilder(Context context, String resStr, int resColorId,
            String... args) {

        SpannableStringBuilder spanStr = new SpannableStringBuilder(resStr);
        int i = -1;
        for (String arg : args) {
            i = resStr.indexOf(arg, i + 1);
            spanStr.setSpan(new ForegroundColorSpan(context.getResources().getColor(resColorId)), i, i + arg.length(),
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        return spanStr;
    }

    /**
     * 动态替换文本并改变文本颜色
     */
    public static SpannableStringBuilder changeLastColor(Context context, String text, int resColorId, String arg) {

        SpannableStringBuilder spanStr = new SpannableStringBuilder(text);
        int argIndex = text.lastIndexOf(arg);
        spanStr.setSpan(new ForegroundColorSpan(context.getResources().getColor(resColorId)), argIndex,
                argIndex + arg.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return spanStr;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
//        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (dpValue * scale + 0.5f);
    	DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, metrics);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    
    public static int sp2px(Context context, float spValue) {  
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, metrics);
    }

    public static void clearCavnasCaches() {
        // This is only effect for lower version which below 4.0
        if (android.os.Build.VERSION.SDK_INT < 19) {
            //Force the canvas cache to release
            try {
                Class<?> canvas = Class.forName("android.graphics.Canvas");
                Method mfreeCaches = canvas.getMethod("freeCaches");
                mfreeCaches.invoke(canvas);
                Method mfreeTextLayoutCaches = canvas.getMethod("freeTextLayoutCaches");
                mfreeTextLayoutCaches.invoke(canvas);
            } catch (ClassNotFoundException e) {
                if (DEBUG) {
                    Log.e(TAG, e.getMessage(), e);
                }
            } catch (NoSuchMethodException e) {
                if (DEBUG) {
                    Log.e(TAG, e.getMessage(), e);
                }
            } catch (IllegalAccessException e) {
                if (DEBUG) {
                    Log.e(TAG, e.getMessage(), e);
                }
            } catch (InvocationTargetException e) {
                if (DEBUG) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }
    }

    public static Bitmap loadBitmap(Resources r, int id, int reqWidth, int reqHeight) {
        if (reqWidth <= 0 || reqHeight <= 0) {
            return null;
        }
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeResource(r, id);
        } catch (Throwable e) {
            return null;
        }
        if (bitmap == null || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
            return null;
        }

        Bitmap target = Bitmap.createBitmap(reqWidth, reqHeight, Config.ARGB_8888);

        Canvas canvas = new Canvas();

        Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect dst = new Rect(0, 0, reqWidth, reqHeight);
        Paint paint = new Paint();

        canvas.setBitmap(target);
        canvas.drawBitmap(bitmap, src, dst, paint);

        bitmap.recycle();

        return target;
    }

    /**
     *
     * 部分第三方App在标题里加入异常空格，用来抢占Launcher抽屉里排列顺序
     *
     * 用于解析包含此类操作的App标题
     *
     * Copies this string removing white space characters from the beginning and
     * end of the string.
     *
     * @return a new string with characters <code>== \\u00a0 removed from the beginning AND <= \\u0020 </code> removed from
     *         the beginning and end.
     */
    public static String trimAppLabel(String s) {
        s = s.trim();
        int start = 0, end = s.length() - 1;
        char[] value = s.toCharArray();
        while (start < end && value[start] == ' ') {
            start++;
        }
        return s.substring(start);
    }
    
    public static class ItemPosition {
		public int index;
		public int offset;
		
		public ItemPosition(int i, int o) {
			this.index = i;
			this.offset = o;
		}
	}
    
    public static ItemPosition getPositionFromListTop(ListView list) {
    	if (list == null || list.getChildCount() <= 0) {
    		return new ItemPosition(0, 0);
    	}
    	View v = list.getChildAt(0);
    	int top = (v == null) ? 0 : (v.getTop() - list.getPaddingTop());
//    	System.out.println("list.getFirstVisiblePosition() = " + list.getFirstVisiblePosition() + "   top = " + top);
    	return new ItemPosition(list.getFirstVisiblePosition(), top);
    }
    
//    public static int getPositionFromListBottom(ListView list) {
//    	if (list == null || list.getChildCount() <= 0) {
//    		return 0;
//    	}
//    	View v = list.getChildAt(list.getLastVisiblePosition());
//    	System.out.println("list.getHeight() = " + list.getHeight());
//    	if (v != null)
//    		System.out.println("v.getBottom() = " + v.getBottom());
//    	int offset = (v == null) ? 0 : (v.getBottom() - (list.getHeight() - list.getPaddingTop() - list.getPaddingBottom()));
//    	int offset = (v == null) ? 0 : (v.getBottom() - list.getHeight());
//    	return offset;
//    }
    
    /**
     * 测量控件的尺寸
     *
     * @param view
     */
    public static void calcViewMeasure(View view) {
//        int width = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//        int height = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//        view.measure(width,height);

        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
    }
    
    /**
     * 获取控件的高度，如果获取的高度为0，则重新计算尺寸后再返回高度
     *
     * @param view
     * @return
     */
    public static int getViewMeasuredHeight(View view) {
//        int height = view.getMeasuredHeight();
//        if(0 < height){
//            return height;
//        }
        calcViewMeasure(view);
        return view.getMeasuredHeight();
    }
}
