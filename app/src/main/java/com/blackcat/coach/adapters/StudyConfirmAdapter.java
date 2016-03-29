package com.blackcat.coach.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.blackcat.coach.R;
import com.blackcat.coach.activities.StudyConfirmsAct;
import com.blackcat.coach.models.CoachClass;
import com.blackcat.coach.models.Reservation;
import com.blackcat.coach.widgets.WordWrapViewStudyContent;

import java.util.ArrayList;
import java.util.List;

/** 工时确认 列表
 * Created by pengdonghua on 2016/3/26.
 */
public class StudyConfirmAdapter extends BaseExpandableListAdapter {

    private Context context;

    private List<Reservation> list = new ArrayList<Reservation>();


    public StudyConfirmAdapter(Context context){
        this.context = context;
    }

    public void setData(List<Reservation> list){
        this.list = list;
        notifyDataSetChanged();
    }


    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return list.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        View v = View.inflate(context,R.layout.item_study_confirm_parent,null);
        TextView tvName = (TextView) v.findViewById(R.id.item_study_confirm_parent_name);
        TextView tvTime = (TextView) v.findViewById(R.id.item_study_confirm_parent_time);
        ImageView imgArrow = (ImageView) v.findViewById(R.id.item_study_confirm_parent_arrow);
        Reservation item = list.get(i);
        tvName.setText(item.userid.name);
        tvTime.setText(""+item.begintime+""+item.endtime);

        return v;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        return getChild(list.get(i),i);
    }

    private View getChild(Reservation item,int i){
        List<CoachClass> list = getLearnContent(item.subject.subjectid);

        View v = View.inflate(context, R.layout.item_study_confirm_child,null);
        WordWrapViewStudyContent wordWrap = (WordWrapViewStudyContent) v.findViewById(R.id.item_study_confirm_child_wwww);
        if(list!=null)
            wordWrap.setData(list);

        RatingBar ratBar = (RatingBar) v.findViewById(R.id.item_study_confirm_child_ratingBar);
        EditText et = (EditText) v.findViewById(R.id.item_study_confirm_child_editText);
        Button btn = (Button) v.findViewById(R.id.item_study_confirm_child_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //提交 数据
            }
        });

        return v;
    }

    private String[] mSubjectNames;
    private List<CoachClass> mCoachClassList2 = null;

    private List<CoachClass> mCoachClassList3 = null;

    /**
     * 根据当前的科目类型，获取学习内容
     * @param mSubjectType
     */
    private List<CoachClass> getLearnContent(int mSubjectType){

        if(mSubjectType == StudyConfirmsAct.SUBJECTS_TYPE_TWO) {
            if(mCoachClassList2!=null)
                return mCoachClassList2;

            mSubjectNames = context.getResources().getStringArray(R.array.subject2);
            mCoachClassList2 = new ArrayList<>();
            for (int i = 0; i < mSubjectNames.length; i++) {
                CoachClass coachClass = new CoachClass(mSubjectNames[i], false);
                mCoachClassList2.add(coachClass);
            }
            return mCoachClassList2;
        }
        else if (mSubjectType == StudyConfirmsAct.SUBJECTS_TYPE_THREE) {
            if(mCoachClassList3!=null)
                return mCoachClassList3;

            mSubjectNames = context.getResources().getStringArray(R.array.subject3);
            mCoachClassList3 = new ArrayList<>();
            for (int i = 0; i < mSubjectNames.length; i++) {
                CoachClass coachClass = new CoachClass(mSubjectNames[i], false);
                mCoachClassList3.add(coachClass);
            }
            return mCoachClassList3;
        }
        return null;

    }



    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
