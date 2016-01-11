package com.blackcat.coach.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blackcat.coach.R;
import com.blackcat.coach.models.Reservation;

import java.util.ArrayList;

/**
 * Created by aa on 2016/1/9.
 */
public class ItemFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View contextView = inflater.inflate(R.layout.fragment_item, container, false);
        TextView mTextView = (TextView) contextView.findViewById(R.id.textview);

        //获取Activity传递过来的参数
        Bundle mBundle = getArguments();
        String title = mBundle.getString("arg");

//        mTextView.setText(title);
        initData();

        return contextView;
    }

    private void initData() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}

