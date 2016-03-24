package com.blackcat.coach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.blackcat.coach.R;
import com.blackcat.coach.adapters.CoachTypeAdapter;

/**
 *  选择教练 类型
 */
public class PickCoachTypeAct extends ChatBaseActivity implements  AdapterView.OnItemClickListener{

    private CoachTypeAdapter adapter;

    private String[] coaches = {"挂靠教练","直营教练"};

    protected Toolbar mToolBar;


    /**
     *  标题  返回 事件
     * @param navigationIconId
     * @return
     */
    protected Toolbar configToolBar(int navigationIconId) {
        mToolBar.setNavigationIcon(navigationIconId);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        return mToolBar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_coach_type);
        initView();
        View v = findViewById(R.id.toolbar);
        if (v != null) {
            mToolBar = (Toolbar) v;
            configToolBar(R.mipmap.ic_back);
            ((TextView) findViewById(R.id.toolbar_title )).setText(getString(R.string.title_coach_type));

        }
    }

    private void initView(){

        ListView lv = (ListView ) findViewById(R.id.act_pick_coach_type_lv);
        adapter = new CoachTypeAdapter(this,coaches);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener( this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent in = new Intent();
        //返回当前选中的 项
        in.putExtra("name",coaches[i]);
        in.putExtra("type",i);
        setResult(0,in);
        finish();
    }
}
