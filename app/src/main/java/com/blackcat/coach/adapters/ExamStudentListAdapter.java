package com.blackcat.coach.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackcat.coach.R;
import com.blackcat.coach.fragments.StudentFragment1;
import com.blackcat.coach.imgs.UILHelper;
import com.blackcat.coach.models.ExamStudentList;
import com.blackcat.coach.widgets.SelectableRoundedImageView;

import java.util.List;

public class ExamStudentListAdapter extends BaseAdapter {


    private List<ExamStudentList> list;
    private Context mContext;
    public ExamStudentListAdapter(Context context ,List<ExamStudentList> list){
        this.list = list;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if(convertView == null){
            convertView = View.inflate(mContext, R.layout.item_exam_student_list,null);
            holder = new ViewHolder();
            holder.userPicIv = (SelectableRoundedImageView) convertView.findViewById(R.id.exam_student_pic_iv);
            holder.userNameTv= (TextView) convertView.findViewById(R.id.exam_student_name_tv);
            holder.scoreTv= (TextView) convertView.findViewById(R.id.exam_student_score_tv);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

       ExamStudentList item =  list.get(position);

        holder.userPicIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.userPicIv.setImageResource(R.mipmap.schedule_item_no_student);
        holder.userPicIv.setOval(true);
        if (item.userid != null && item.userid.headportrait != null && !TextUtils.isEmpty(item.userid.headportrait.originalpic)) {
            UILHelper.loadImage(holder.userPicIv, item.userid.headportrait.originalpic, false, R.mipmap.schedule_item_no_student);
        }

        holder.userNameTv.setText(item.userid.name);
        if("3".equals(item.examinationstate)){
            //漏考
            holder.scoreTv.setText("缺考");
        }else if("4".equals(item.examinationstate)){
            //未通过
            holder.scoreTv.setText("未通过");

        }else if("5".equals(item.examinationstate)){
            //通过
            if(!TextUtils.isEmpty(item.score)){
                holder.scoreTv.setText(item.score+"分");
            }else{
                holder.scoreTv.setText("暂无成绩");
            }
        }
        return convertView;
    }

    class ViewHolder{
        public SelectableRoundedImageView userPicIv;
        public TextView userNameTv,scoreTv;
    }

}
