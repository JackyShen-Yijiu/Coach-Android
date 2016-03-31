package com.blackcat.coach.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import com.blackcat.coach.R;
import com.blackcat.coach.fragments.AddStudentsFragment;
import com.blackcat.coach.utils.LogUtil;

/**
 * Created by aa on 2016/3/29.
 */
public class AddStudentsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add);
        configToolBar(R.mipmap.ic_back);
        //
        AddStudentsFragment fragment = new AddStudentsFragment();
        FragmentTransaction ft =getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_add_student,fragment,"addstudent");
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(ACTION_GROUP_ID, ACTION_MENUITEM_ID0, 0, R.string.action_add);
        MenuItemCompat.setShowAsAction(item, MenuItem.SHOW_AS_ACTION_ALWAYS
                | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == ACTION_MENUITEM_ID0) {
            //替学员预约
            LogUtil.print("替学员预约");
            AddStudentsFragment fragment = (AddStudentsFragment) getSupportFragmentManager().findFragmentByTag("addstudent");
            fragment.commitAppointment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




}
