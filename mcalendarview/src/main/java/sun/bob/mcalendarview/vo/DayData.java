package sun.bob.mcalendarview.vo;

import java.util.Calendar;

/**
 * Created by bob.sun on 15/8/27.
 */
public class DayData {

    private DateData date;
    private int textColor;
    private int textSize;
    private boolean ifCurrentMonth;

    public DayData(DateData date){
        this.date = date;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setIfCurrentMonth(boolean b){this.ifCurrentMonth = b;}
    public boolean getIfCurrentMonth(){
        return this.ifCurrentMonth;
    }
    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public String getText(){
        return "" + date.getDay();
    }

    public String getMonth(){
        return ""+date.getMonth();
    }
    public Calendar getCalendar(){
        return date.getCalendar();
    }
    public DateData getDate(){
        return date;
    }

}
