package com.blackcat.coach.widgets;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.blackcat.coach.models.LabelBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by pengdonghua on 2016/1/11.
 */


public class WordWrapView extends ViewGroup {
    private static final int PADDING_HOR = 10;//水平方向padding
    private static final int PADDING_VERTICAL = 5;//垂直方向padding
    private static final int SIDE_MARGIN = 24;//左右间距
    private static final int TEXT_MARGIN = 24;

    private boolean firstWhite = false;

    private String[] colors = {"ab414e","9d3e7a","7f3e9d","4249a7","42a3a7","3a93a0","3aa087","3aa063","43a03a",
            "73a03a","98a03a","cd9637"};

    private List<LabelBean> lables = new ArrayList<LabelBean>();

    /**
     * @param context
     */
    public WordWrapView(Context context) {
        super(context);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public WordWrapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }



    /**
     * @param context
     * @param attrs
     */
    public WordWrapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        Log.d("tag", "random--childCount::" + childCount);
        int autualWidth = r - l;
        int x = SIDE_MARGIN;// 横坐标开始
        int y = 0;//纵坐标开始
        int rows = 1;
        for(int i=0;i<childCount;i++){
            View view = getChildAt(i);

            if(i==0 && firstWhite)
                view.setBackgroundColor(Color.WHITE);
            else if(i<lables.size() && !lables.get(i).is_choose){//没有选中 。。颜色灰色
                view.setBackgroundColor(Color.GRAY);
            }
            else
//                view.setBackgroundColor(Color.parseColor("#"+getcolor(i)));
//            }
            if(i<lables.size() && lables.get(i).color!=null)
                view.setBackgroundColor(Color.parseColor(lables.get(i).color));

            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight();
            x += width+TEXT_MARGIN;
            if(x>autualWidth){
                x = width+SIDE_MARGIN;
                rows++;
            }
            y = rows*(height+TEXT_MARGIN);
            if(i==0){
                view.layout(x-width-TEXT_MARGIN, y-height, x-TEXT_MARGIN, y);
            }else{
                view.layout(x-width, y-height, x, y);
            }
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int x = 0;//横坐标
        int y = 0;//纵坐标
        int rows = 1;//总行数
        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        int actualWidth = specWidth - SIDE_MARGIN * 2;//实际宽度
        int childCount = getChildCount();
        for(int index = 0;index<childCount;index++){
            View child = getChildAt(index);
            child.setPadding(PADDING_HOR, PADDING_VERTICAL, PADDING_HOR, PADDING_VERTICAL);
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            x += width+TEXT_MARGIN;
            if(x>actualWidth){//换行
                x = width;
                rows++;
            }
            y = rows*(height+TEXT_MARGIN);
        }
        setMeasuredDimension(actualWidth, y);
    }

    public void setFirstColor(boolean flag){
        firstWhite = flag;
    }

    public String[] getColor(){
        return colors;
    }

    public void setData(List<LabelBean> lables){
        this.lables = lables;
    }

    /**
     * 获取十六进制的颜色代码.例如 "#6E36B4" , For HTML ,
     * @return String
     */
    public static String getRandColorCode(){
        String r,g,b;
        Random random = new Random();
        int temp1 = random.nextInt(256);
        int temp2 = random.nextInt(256);
        int temp3= random.nextInt(256);
        //如果都是大于100  第一个减去100 ，依旧大于
        while(temp1>100 && temp2> 100 && temp3 >100){
            temp1 = temp1 -100;
            if(temp1<100)
                break;
        }
        if(temp1<100 && temp2< 100 && temp3 <100){
            temp1 = temp1 + 100;
        }


        r = Integer.toHexString(temp1).toUpperCase();
        g = Integer.toHexString(temp2).toUpperCase();
        b = Integer.toHexString(temp3).toUpperCase();

        r = r.length()==1 ? "0" + r : r ;
        g = g.length()==1 ? "0" + g : g ;
        b = b.length()==1 ? "0" + b : b ;

        return r+g+b;
    }

    public String getcolor(int po){
        int temp = po%colors.length;
        Log.d("tag",temp+"iii>>"+colors[temp]);
        return colors[temp];
    }

}

