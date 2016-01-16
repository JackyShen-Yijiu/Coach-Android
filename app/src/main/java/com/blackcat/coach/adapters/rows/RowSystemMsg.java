package com.blackcat.coach.adapters.rows;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackcat.coach.R;
import com.blackcat.coach.adapters.BaseViewHolder;
import com.blackcat.coach.models.SystemMsg;

import java.text.SimpleDateFormat;
import java.util.logging.SimpleFormatter;

/**
 * Created by aa on 2016/1/14.
 */
public class RowSystemMsg {
    public static BaseViewHolder createViewHolder(ViewGroup parent, Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_system_msg, parent, false);
        Holder holder = new Holder(view);
        holder.tv_time =(TextView) view.findViewById(R.id.tv_time);
        holder.tv_title_type = (TextView) view.findViewById(R.id.tv_title_type);
        holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
        holder.tv_content = (TextView) view.findViewById(R.id.tv_content);
        holder.tv_date = (TextView) view.findViewById(R.id.tv_date);
        holder.img_Type = (ImageView) view.findViewById(R.id.money_iv);
        return holder;
    }

    public static <T> void bindViewHolder(final Activity activity,
                                          BaseViewHolder holder, final int position, final T info) {
        final Holder viewHolder = (Holder) holder;
        SystemMsg item = (SystemMsg) info;

        if(item.newstype.equals("1")){
            viewHolder.tv_title_type.setText("笑话");//getDate();
            viewHolder.img_Type.setImageResource(R.drawable.ic_system_happy);
        }else{
            viewHolder.tv_title_type.setText("行业资讯");//getDate();
            viewHolder.img_Type.setImageResource(R.drawable.money_msg);
        }
        viewHolder.tv_date.setText(getDate(item.createtime));
        viewHolder.tv_time.setText(getTime(item.createtime));
        viewHolder.tv_content.setText(item.description);
        viewHolder.tv_title.setText(item.title);

    }

    static class Holder extends BaseViewHolder {

        private TextView tv_time;
        private TextView tv_content;
        private TextView tv_title_type;
        private TextView tv_title;
        private TextView tv_date;
        private ImageView img_Type;
        public Holder(View itemView) {
            super(itemView);
        }
    }

    private static String getTime(String time){
        if(null == time || time.length()<20)
            return time;

        return time.substring(11,16);
    }

    private static String getDate(String time){
        if(null == time || time.length()<20)
            return time;

        return time.substring(0,10);
    }




}
