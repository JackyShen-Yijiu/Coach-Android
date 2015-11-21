package com.blackcat.coach.caldroid;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by crocodile2u on 3/30/15.
 */
public class CellView extends TextView {

    public static final int STATE_ATTR_TODAY = R.attr.state_date_today;
    public static final int STATE_ATTR_SELECTED = R.attr.state_date_selected;
    public static final int STATE_ATTR_DISABLED = R.attr.state_date_disabled;
    public static final int STATE_ATTR_PREV_NEXT_MONTH = R.attr.state_date_prev_next_month;

    public static final int STATE_NORMAL = -1;
    public static final int STATE_TODAY = 0;
    public static final int STATE_SELECTED = 1;
    public static final int STATE_DISABLED = 2;
    public static final int STATE_PREV_NEXT_MONTH = 3;

    private int mState = STATE_NORMAL;


    public CellView(Context context) {
        super(context);
    }

    public CellView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CellView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCellViewState(int state) {
        if (mState != state) {
            refreshDrawableState();
            mState = state;
        }
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState;
        int[] stateArray;
        switch (mState) {
            case STATE_NORMAL:
                return super.onCreateDrawableState(extraSpace);
            case STATE_TODAY:
                drawableState = super.onCreateDrawableState(extraSpace + 1);
                stateArray = new int[]{STATE_ATTR_TODAY};
                mergeDrawableStates(drawableState, stateArray);
                return drawableState;
            case STATE_SELECTED:
                drawableState = super.onCreateDrawableState(extraSpace + 1);
                stateArray = new int[]{STATE_ATTR_SELECTED};
                mergeDrawableStates(drawableState, stateArray);
                return drawableState;
            case STATE_DISABLED:
                drawableState = super.onCreateDrawableState(extraSpace + 1);
                stateArray = new int[]{STATE_ATTR_DISABLED};
                mergeDrawableStates(drawableState, stateArray);
                return drawableState;
            case STATE_PREV_NEXT_MONTH:
                drawableState = super.onCreateDrawableState(extraSpace + 1);
                stateArray = new int[]{STATE_ATTR_PREV_NEXT_MONTH};
                mergeDrawableStates(drawableState, stateArray);
                return drawableState;
            default:
                return super.onCreateDrawableState(extraSpace);
        }
    }
}
