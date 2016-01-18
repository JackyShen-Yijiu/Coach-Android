package com.blackcat.coach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.blackcat.coach.R;
import com.blackcat.coach.fragments.ReservationFragment;

/**
 * Created by aa on 2016/1/14.
 */
public class OrderMsgActivity extends BaseActivity implements AdapterView.OnItemClickListener{
    private ListView inner_list;
    private LinearLayout order_list_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_orders_msg);
        configToolBar(R.mipmap.ic_back);

        inner_list=(ListView)findViewById(R.id.inner_list);
        order_list_item=(LinearLayout)findViewById(R.id.order_list_item);
        inner_list.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (view.getId()) {

            case R.id.order_list_item:
                    Intent intent = new Intent(this, WalletActivity.class);
                    startActivity(intent);
            break;
    }
}
}
