package com.blackcat.coach.cache;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.blackcat.coach.R;
import com.blackcat.coach.activities.BaseActivity;

public class CacheMainActivity extends BaseActivity {


    private Button btn_clear;
    private WebView wv;

    @Override  
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_main);  
        btn_clear = (Button) findViewById(R.id.btn_clear);
        wv = (WebView) findViewById(R.id.wv);

        btn_clear.setOnClickListener(new View.OnClickListener() {  
              
            @Override  
            public void onClick(View arg0) {  
                // TODO Auto-generated method stub  
                try {  
                    //查看缓存的大小  
                    Log.e("YQY", DataCleanManager.getTotalCacheSize(CacheMainActivity.this));
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
                //清除操作  
                DataCleanManager.clearAllCache(CacheMainActivity.this);
                try {  
                    //清除后的操作  
                    Log.e("YQY", DataCleanManager.getTotalCacheSize(CacheMainActivity.this));
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }  
        });  
          
        wv.loadUrl("http://www.baidu.com");  
          
    }  
  

}  