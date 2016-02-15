package sun.bob.mcalendarview.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import sun.bob.mcalendarview.MarkStyleExp;
import sun.bob.mcalendarview.R;


/**
 * Created by 明明大美女 on 2015/12/9.
 */
public class ExpandCellView extends DefaultCellView {

private Context context;
    public ExpandCellView(Context context) {
        super(context);
        this.context =context;
    }

    public ExpandCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context =context;
    }

    public void setText_Color(String text, int color) {
        textView.setText(text);
        if (color != 0) {
            textView.setTextColor(color);
        }
    }

    public void setLunarText(String text, int color){
        if (color != 0) {
            lunarTextView.setTextColor(color);
        }
        lunarTextView.setText(text);
    }

    /**
     * 下面三个可以做一个判断，且回调。 执行是否自动换页的功能
     */
    public boolean setDateChoose() {
//        setBackgroundDrawable(MarkStyleExp.choose);
        textView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.point));
        textView.setTextColor(Color.WHITE);
        return true ;
    }

    public void setDateToday(){
        textView.setBackgroundDrawable(null);
        textView.setTextColor(Color.rgb(40,121,243));
    }

    public void showDay(boolean isShow){
        if(isShow){
            textView.setVisibility(View.VISIBLE);
            lunarTextView.setVisibility(View.VISIBLE);
        }else{
            textView.setVisibility(INVISIBLE);
            lunarTextView.setVisibility(INVISIBLE);
        }
    }

    public void setDateNormal() {
        textView.setTextColor(Color.parseColor("#999999"));
        textView.setBackgroundDrawable(null);
    }

    @Override
    public void showPoint(boolean b) {
        if(b){
            point.setVisibility(View.VISIBLE);
        }else{
            point.setVisibility(View.INVISIBLE);
        }
    }
}
