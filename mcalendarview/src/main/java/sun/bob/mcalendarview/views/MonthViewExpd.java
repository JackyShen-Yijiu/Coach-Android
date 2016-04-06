package sun.bob.mcalendarview.views;

import android.content.Context;
import android.util.AttributeSet;

import java.util.Calendar;
import java.util.List;

import sun.bob.mcalendarview.adapters.CalendarExpAdapter;
import sun.bob.mcalendarview.vo.MonthWeekData;

/**
 * Created by Bigflower on 2015/12/8.
 */
public class MonthViewExpd extends MonthView {
    private MonthWeekData monthWeekData;
    private CalendarExpAdapter adapter;
    private List<Calendar> selectDate;
    public MonthViewExpd(Context context) {
        super(context);
    }

    public MonthViewExpd(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDisplayData(List<Calendar> selectDate){
        this.selectDate = selectDate;
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }

    }
    public void initMonthAdapter(int pagePosition, int cellView, int markView) {
        System.out.println(pagePosition+"----pagePosition");
        getMonthWeekData(pagePosition);
        adapter = new CalendarExpAdapter(getContext(), 1, monthWeekData.getData()).setCellViews(cellView, markView);
        adapter.setDisplayData(selectDate);
        this.setAdapter(adapter);

    }

    private void getMonthWeekData(int position) {
        monthWeekData = new MonthWeekData(position);
    }


}
