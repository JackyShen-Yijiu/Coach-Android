package sun.bob.mcalendarview.views;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import sun.bob.mcalendarview.CellConfig;
import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.R;
import sun.bob.mcalendarview.adapters.CalendarViewExpAdapter;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.listeners.OnMonthChangeListener;
import sun.bob.mcalendarview.listeners.OnMonthScrollListener;
import sun.bob.mcalendarview.utils.CalendarUtil;
import sun.bob.mcalendarview.utils.CurrentCalendar;
import sun.bob.mcalendarview.utils.ExpCalendarUtil;
import sun.bob.mcalendarview.vo.DateData;
import sun.bob.mcalendarview.vo.MarkedDates;


/**
 * Created by 明明大美女 on 2015/12/8.
 */
public class ExpWeekCalendarView extends ExpCalendarView {
    private int dateCellViewResId = -1;
    private View dateCellView = null;
    private int markedStyle = -1;
    private int markedCellResId = -1;
    private View markedCellView = null;
    private boolean hasTitle = true;

    private boolean initted = false;

    public static DateData currentDate;
    private CalendarViewExpAdapter adapter;

    private int width, height;
    private int currentIndex;

    public ExpWeekCalendarView(Context context) {
        super(context);
        if (context instanceof FragmentActivity) {
            init((FragmentActivity) context);
        }
    }

    public ExpWeekCalendarView(Fragment f) {

        super(f.getActivity());
        Log.d("tag", "ExpCalendarView--init-frag");
        initFrag(f);
        Log.d("tag", "ExpCalendarView--end     ");
    }


    public ExpWeekCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (context instanceof FragmentActivity) {
            init((FragmentActivity) context);
        }
    }

    public void initFrag(Fragment fragment){
//        if (initted) {
//            return;
//        }
        initted = true;
        if (currentDate == null) {
            currentDate = CurrentCalendar.getCurrentDateData();
        }
        // TODO: 15/8/28 Will this cause trouble when achieved?
        if (this.getId() == View.NO_ID) {
            this.setId(R.id.calendarViewPager);
        }

        adapter = new CalendarViewExpAdapter(fragment.getChildFragmentManager()).setDate(currentDate);
        this.setAdapter(adapter);
        this.setCurrentItem(500);
//        addBackground();
        float density = getContext().getResources().getSystem().getDisplayMetrics().density;
        CellConfig.cellHeight = getContext().getResources().getSystem().getDisplayMetrics().widthPixels / 12 / density;
        CellConfig.cellWidth = getContext().getResources().getSystem().getDisplayMetrics().widthPixels / 7 / density;

    }
    
    public void init(FragmentActivity activity) {
        System.out.println("dddddddddd00000-0-0-");
//        if (initted) {
//            return;
//        }
        initted = true;
        if (currentDate == null) {
            currentDate = CurrentCalendar.getCurrentDateData();
        }
        // TODO: 15/8/28 Will this cause trouble when achieved?
//        if (this.getId() == View.NO_ID) {
//            this.setId(R.id.calendarViewPager);
//        }
        
        adapter = new CalendarViewExpAdapter(activity.getSupportFragmentManager()).setDate(currentDate);
        this.setAdapter(adapter);
        this.setCurrentItem(500);
//        addBackground();
        float density = getContext().getResources().getSystem().getDisplayMetrics().density;
        CellConfig.cellHeight = getContext().getResources().getSystem().getDisplayMetrics().widthPixels / 12 / density;
        CellConfig.cellWidth = getContext().getResources().getSystem().getDisplayMetrics().widthPixels / 7 / density;

//        this.addOnPageChangeListener(new );
    }

    //// TODO: 15/8/28 May cause trouble when invoked after inited
    public ExpWeekCalendarView travelTo(DateData dateData) {
        this.currentDate = dateData;
        CalendarUtil.date = dateData;
        this.initted = false;
        init((FragmentActivity) getContext());
        return this;
    }

    public void expand() {
        adapter.notifyDataSetChanged();
    }

    public void shrink() {
        adapter.notifyDataSetChanged();
    }


    public ExpWeekCalendarView markDate(int year, int month, int day) {
        MarkedDates.getInstance().add(new DateData(year, month, day));
        return this;
    }

    public ExpWeekCalendarView unMarkDate(int year, int month, int day) {
        MarkedDates.getInstance().remove(new DateData(year, month, day));
        return this;
    }

    public ExpWeekCalendarView markDate(DateData date) {
        MarkedDates.getInstance().add(date);
        return this;
    }

    public ExpWeekCalendarView unMarkDate(DateData date) {
        MarkedDates.getInstance().remove(date);
        return this;
    }

    public MarkedDates getMarkedDates() {
        return MarkedDates.getInstance();
    }

    public ExpWeekCalendarView setDateCell(int resId) {
        adapter.setDateCellId(resId);
        return this;
    }

    public ExpWeekCalendarView setMarkedStyle(int style, int color) {
        MarkStyle.current = style;
        MarkStyle.color = color;
        return this;
    }

    public ExpWeekCalendarView setMarkedStyle(int style) {
        MarkStyle.current = style;
        return this;
    }

    public ExpWeekCalendarView setMarkedCell(int resId) {
        adapter.setMarkCellId(resId);
        return this;
    }

    public ExpWeekCalendarView setOnMonthChangeListener(OnMonthChangeListener listener) {
        this.addOnPageChangeListener(listener);
        return this;
    }
//public ExpCalendarView setOn
    public ExpWeekCalendarView setOnMonthScrollListener(OnMonthScrollListener listener) {

        this.addOnPageChangeListener(listener);
        return this;
    }

    public ExpWeekCalendarView setOnDateClickListener(OnDateClickListener onDateClickListener) {
        OnDateClickListener.instance = onDateClickListener;
        return this;
    }

    public ExpWeekCalendarView hasTitle(boolean hasTitle) {
        this.hasTitle = hasTitle;
        adapter.setTitle(hasTitle);
        return this;
    }

    @Override
    protected void onMeasure(int measureWidthSpec, int measureHeightSpec) {
        width = measureWidth(measureWidthSpec);
        height = measureHeight(measureHeightSpec);
        measureHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(measureWidthSpec, measureHeightSpec);
    }

    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = 0;
        if (specMode == MeasureSpec.AT_MOST) {
            float destiney = getContext().getResources().getSystem().getDisplayMetrics().density;
            result = (int) (CellConfig.cellWidth * 7 * destiney);
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = (int) CellConfig.cellHeight;
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        float density = getContext().getResources().getSystem().getDisplayMetrics().density;
        if (specMode == MeasureSpec.AT_MOST) {
            if (CellConfig.ifMonth)
                return (int) (CellConfig.cellHeight * 6 * density);
            else
                return (int) (CellConfig.cellHeight * density);
        } else if (specMode == MeasureSpec.EXACTLY) {
            return specSize;
        } else {
            return (int) CellConfig.cellHeight;
        }
    }



    public void measureCurrentView(int currentIndex) {
        this.currentIndex = currentIndex;
        requestLayout();
    }

    public void setPointDisplay(List<Calendar> selectDate){
        adapter.setDisplayData(selectDate);
        adapter.notifyDataSetChanged();
        this.getCurrentItem();
    }

    public DateData getCurrentMonth(){
        DateData date;
        if (CellConfig.ifMonth)
            date = ExpCalendarUtil.position2Month(this.getCurrentItem());
        else
            date = ExpCalendarUtil.position2Week(this.getCurrentItem());
        return date;
    }


    public void setSelectDate(Date seleDate) {

//        int days = daysBetween(AppointmentCarActivity.selectedDate, seleDate);
//        this.setCurrentItem(this.getCurrentItem() + days / 7);

    }

    public int daysBetween(Date smdate, Date bdate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // smdate = sdf.parse(sdf.format(smdate));
        // bdate = sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);

        long time2 = cal.getTimeInMillis();

        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }


}

