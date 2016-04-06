package sun.bob.mcalendarview.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.utils.CurrentCalendar;
import sun.bob.mcalendarview.utils.SolarToLunar;
import sun.bob.mcalendarview.views.BaseCellView;
import sun.bob.mcalendarview.views.BaseMarkView;
import sun.bob.mcalendarview.views.DefaultMarkView;
import sun.bob.mcalendarview.views.ExpandCellView;
import sun.bob.mcalendarview.vo.DayData;
import sun.bob.mcalendarview.vo.MarkedDates;

/**
 * Created by Bigflower on 2015/12/8.
 */
public class CalendarExpAdapter extends ArrayAdapter {
    private ArrayList data;
    private int cellView = -1;
    private int markView = -1;

    private List<Calendar> selectDate;

    public CalendarExpAdapter(Context context, int resource, ArrayList data) {
        super(context, resource);
        this.data = data;
    }


    public CalendarExpAdapter setCellViews(int cellView, int markView) {
        this.cellView = cellView;
        this.markView = markView;
        return this;
    }

    public void setDisplayData(List<Calendar> selectDate){
        this.selectDate = selectDate;
    }

    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View ret = null;
        DayData dayData = (DayData) data.get(position);
        SolarToLunar lunar = new SolarToLunar(dayData.getCalendar());
        if (MarkedDates.getInstance().check(dayData.getDate())) {
            if (markView > 0) {

                BaseMarkView baseMarkView = (BaseMarkView) View.inflate(getContext(), markView, null);
                baseMarkView.setDisplayText(dayData.getText());
                baseMarkView.setDisplayLunarText(lunar.toString());
                ret = baseMarkView;
            } else {
                ret = new DefaultMarkView(getContext());
                ((DefaultMarkView) ret).setDisplayText(dayData.getText());
                ((DefaultMarkView) ret).setDisplayLunarText(lunar.toString());

            }
        } else {
            if (cellView > 0) {
                System.out.println("cccc----=-=-=-"+dayData.getText());
                BaseCellView baseCellView = (BaseCellView) View.inflate(getContext(), cellView, null);
                baseCellView.setDisplayText(dayData.getText());
                baseCellView.setDisplayLunarText(lunar.toString());
                ret = baseCellView;
            } else {
                System.out.println(dayData.getMonth()+"--"+dayData.getText()+"qqq-----"+selectDate);
                ret = new ExpandCellView(getContext());
                ((ExpandCellView) ret).setText_Color(dayData.getText(), dayData.getTextColor());
                ((ExpandCellView) ret).setLunarText(lunar.toString(), dayData.getTextColor());
                ((ExpandCellView) ret).showPoint(false);
//                if(!dayData.getIfCurrentMonth()){
//                    ((ExpandCellView) ret).showDay(true);
//                }else{
//                    ((ExpandCellView) ret).showDay(false);
//                }

                if(selectDate != null){

                    for (int i = 0; i < selectDate.size(); i++) {
                        System.out.println(selectDate.get(i).get(Calendar.MONTH));
                        if(dayData.getMonth().equals(String.valueOf(selectDate.get(i).get(Calendar.MONTH)))
                                &&dayData.getText().equals(String.valueOf(selectDate.get(i).get(Calendar.DAY_OF_MONTH)))){
                            ((ExpandCellView) ret).showPoint(true);
                        }

                    }
                }
            }
        }
        ((BaseCellView) ret).setDate(dayData.getDate());
        if (OnDateClickListener.instance != null) {
            ((BaseCellView) ret).setOnDateClickListener(OnDateClickListener.instance);
        }
        if (dayData.getDate().equals(CurrentCalendar.getCurrentDateData())) {
            ((ExpandCellView) ret).setDateToday();
        }
//        if(dayData.getMonth().equals(CurrentCalendar.getCurrentMonth())){
//            ((ExpandCellView) ret).showDay(true);
//        }else{
//            ((ExpandCellView) ret).showDay(false);
//        }
        return ret;
    }

    @Override
    public int getCount() {
        return data.size();
    }

}