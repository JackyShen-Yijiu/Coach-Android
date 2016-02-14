package com.blackcat.coach.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.blackcat.coach.R;
import com.blackcat.coach.adapters.StudentsAdapter;
import com.blackcat.coach.fragments.StudentFragment1;
import com.blackcat.coach.lib.PagerSlidingTab;
import com.blackcat.coach.models.User;
import com.blackcat.coach.utils.LogUtil;

import java.util.List;

public class StudentsActivity1 extends BaseActivity{

    private PagerSlidingTab slidingTab;
    private ViewPager viewPager;

    private String[] title = {"理论学员","上车学员","领证学员"};

    private StudentFragment1[] students = null;

    public static int flag = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students1);
        configToolBar(R.mipmap.ic_back);
        initView();


    }

    private void initView() {
        slidingTab = (PagerSlidingTab) findViewById(R.id.act_student1_sliding_tab);
        viewPager = (ViewPager) findViewById(R.id.act_student1_view_pager);
        /**普通学员列表 0 群发短信学员列表 1*/
        flag = getIntent().getFlags();
        students = new  StudentFragment1[3];//{StudentFragment1.getInstance(0),StudentFragment1.getInstance(1)};
        for(int i =0;i<3;i++){
            students[i] =new StudentFragment1();
            Bundle b = new Bundle();
            b.putInt("type",i+1);
            students[i].setArguments(b);
        }

        StudentsAdapter adapter = new StudentsAdapter(getSupportFragmentManager(),title,students);
        viewPager.setAdapter(adapter);
        slidingTab.setViewPager(viewPager);

        slidingTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(flag == 1)
            getMenuInflater().inflate(R.menu.menu_sms, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sms) {
            sendMsg();
            Toast.makeText(StudentsActivity1.this, "发送短信", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendMsg(){

        String add = getAddress(students[viewPager.getCurrentItem()].getAdapter().getList());
        if(add.length()<1){
            Toast.makeText(StudentsActivity1.this, "请选择需要发送的学员", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri smsUri = Uri.parse("smsto:"+add);
        Intent intent = new Intent(Intent.ACTION_SENDTO,smsUri);
        intent.putExtra("sms_body", "");
        startActivity(intent);
    }

    /**
     * 获取需要发送的手机号
     * @return
     */
    private String getAddress(List<User> list){
        StringBuilder sb = new StringBuilder();
        for (User user : list) {
            LogUtil.print("address---00>" );
            if(user.seleted==1)
                sb.append(user.mobile).append(";");
        }
        return sb.toString();
    }


}
