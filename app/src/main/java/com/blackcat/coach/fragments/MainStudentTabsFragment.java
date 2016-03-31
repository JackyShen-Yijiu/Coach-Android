package com.blackcat.coach.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blackcat.coach.R;
import com.blackcat.coach.activities.IndexActivity;
import com.blackcat.coach.adapters.MainStudentPagerAdapter;
import com.blackcat.coach.adapters.ReservationAdapter;
import com.blackcat.coach.lib.PagerSlidingTab;
import com.blackcat.coach.utils.LogUtil;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainStudentTabsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainStudentTabsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainStudentTabsFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "subject_id";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
//    private int subjectId;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private PagerSlidingTab slidingTab;
    private ViewPager viewPager;

    private String[] titles = new String[] { "全部", "未考", "约考", "补考","通过" };
    private int[] resdefault = new int[]{R.mipmap.student_all_off,R.mipmap.student_study_off,R.mipmap.student_exam_off,R.mipmap.student_examed_off,R.mipmap.student_pass_off};
    private int[] res = new int[]{R.mipmap.student_all_on,R.mipmap.student_study_on,R.mipmap.student_exam_on,R.mipmap.student_examed_on,R.mipmap.student_pass_on};
    /**科目1 2  3  4*/
    private int subjectId = 1;



    private Fragment[] frags = {MainStudentItemFragment.newInstance(subjectId,0),MainStudentItemFragment.newInstance(subjectId,2)
            ,MainStudentItemFragment.newInstance(subjectId,3),MainStudentItemFragment.newInstance(subjectId,4),
            MainStudentItemFragment.newInstance(subjectId,5)};;

    public MainStudentTabsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param subjectId Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainStudentTabsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainStudentTabsFragment newInstance(int subjectId, String param2) {

        MainStudentTabsFragment fragment = new MainStudentTabsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, subjectId);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            subjectId = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            LogUtil.print("student---->item-->tab" + subjectId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_reservation, container, false);
        initView(rootView);
        initData();
        return rootView;
    }

    private void initData() {

//            frags = {MainStudentItemFragment.newInstance(subjectId,0),MainStudentItemFragment.newInstance(subjectId,2)
//                ,MainStudentItemFragment.newInstance(subjectId,3),MainStudentItemFragment.newInstance(subjectId,4),
//                MainStudentItemFragment.newInstance(subjectId,5)};
            // 初始化数据
            final MainStudentPagerAdapter adapter = new MainStudentPagerAdapter(getChildFragmentManager(),titles,resdefault,res,frags);

            viewPager.setAdapter(adapter);
            slidingTab.setViewPager(viewPager);

            slidingTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
//                Toast.makeText(getActivity(),"position"+position,Toast.LENGTH_SHORT).show();
//                    currentPage = position;
                    if(position==0){//新订单,显示签到
                        ((IndexActivity) getActivity()).showHideQianDao(true,position,0);
                    }else
                        ((IndexActivity) getActivity()).showHideQianDao(true,position,0);

//                    ((MainStudentItemFragment) adapter.getItem(position)).reRusume();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });


    }

    /**
     * 初始化
     * @param rootView
     */
    private void initView(View rootView) {
        rootView.setBackgroundColor(Color.WHITE);
        slidingTab = (PagerSlidingTab) rootView
                .findViewById(R.id.fragment_reservation_sliding_tab);
        viewPager = (ViewPager) rootView
                .findViewById(R.id.fragment_reservation_view_pager);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
