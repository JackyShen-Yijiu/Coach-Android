package com.blackcat.coach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.blackcat.coach.R;

/**
 * Created by aa on 2016/1/14.
 */
public class SystemMsgActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ListView inner_list;
    private LinearLayout row_system_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_system_msg);
        configToolBar(R.mipmap.ic_back);

        inner_list = (ListView) findViewById(R.id.inner_list);
        row_system_msg = (LinearLayout) findViewById(R.id.rootView);
        inner_list.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (view.getId()) {

            case R.id.rootView:
                Intent intent = new Intent(this, WebViewMsg.class);

                startActivity(intent);
                break;
        }
    }
}
