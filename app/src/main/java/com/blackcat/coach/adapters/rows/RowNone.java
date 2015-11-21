
package com.blackcat.coach.adapters.rows;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.blackcat.coach.adapters.BaseViewHolder;


public class RowNone {

    public static BaseViewHolder createViewHolder(ViewGroup parent, Context context) {
        FrameLayout view = new FrameLayout(context);
        NoneHolder holder = new NoneHolder(view);
        return holder;
    }

    public static void bindViewHolder(BaseViewHolder holder) {
        
    }

    static class NoneHolder extends BaseViewHolder {

        public NoneHolder(View itemView) {
            super(itemView);
        }
    }
}
