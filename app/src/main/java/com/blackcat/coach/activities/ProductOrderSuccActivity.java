package com.blackcat.coach.activities;

import android.os.Bundle;
import android.view.View;

import com.blackcat.coach.R;

public class ProductOrderSuccActivity extends BaseNoFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_order_succ);
        configToolBar(R.mipmap.ic_back);
        findViewById(R.id.order_success_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
