package com.blackcat.coach.fragments;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.blackcat.coach.R;
import com.blackcat.coach.adapters.CommonAdapter;

import com.blackcat.coach.models.DicCode;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.User;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.VolleyUtil;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MainStudentItemFragment extends BaseListFragment {

    // TODO: Customize parameter argument names
    private static final String SUBJECT_ID = "subject_id";
    private static final String SUBJECT_TYPE = "subject_type";

    // TODO: Customize parameters
    private int subjectid = 1;
    //0 全部学员 1在学学员 2未考学员 3约考学员 4补考学员 5通过学员
    private int state = 0;
    private OnListFragmentInteractionListener mListener;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MainStudentItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MainStudentItemFragment newInstance(int subjectId,int type) {
//        subjectid = subjectId;
//        state = type;
        LogUtil.print("student---->item==newInstance"+subjectId+"state:::>"+type);
        MainStudentItemFragment fragment = new MainStudentItemFragment();
        Bundle args = new Bundle();
        args.putInt(SUBJECT_ID, subjectId);
        args.putInt(SUBJECT_TYPE,type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            subjectid = getArguments().getInt(SUBJECT_ID);
            state = getArguments().getInt(SUBJECT_TYPE);
            LogUtil.print("student---->item-->getArgument>"+subjectid+"state-->"+state);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_pulltorefresh_list, container, false);
        // Set the adapter
        initView(view, inflater);
//        test();
        return view;
    }

    private void initView(View rootView,LayoutInflater inflater){
        mType = new TypeToken<Result<List<User>>>(){}.getType();

        initViews(rootView, inflater, CommonAdapter.AdapterType.TYPE_ADAPTER_STUDENT_NEW);
        mListView.setPadding(0,0,0,10);
        mPage = 1;
        if (!Session.isUserInfoEmpty()) {
            request(mPage);
        }
//        EventBus.getDefault().register(this);
    }

    private  void request(int page){
//        int subjectId,int state,int pageIndex,int count
        mURI = URIUtil.getStudentListNew(subjectid,state,page,10);
        if(page==1)
            refresh(DicCode.RefreshType.R_PULL_DOWN, mURI);
        else{
            refresh(DicCode.RefreshType.R_PULL_UP, mURI);
        }
    }

    private void test(){
        URI mURI = URIUtil.getTest();
        String url = null;
        if (mURI != null) {
            try {
                url = mURI.toURL().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Map map = new HashMap<>();
        Toast.makeText(getActivity(), "test", Toast.LENGTH_SHORT).show();
        GsonIgnoreCacheHeadersRequest<Result> request = new GsonIgnoreCacheHeadersRequest<Result>(
                url, mType, map,
                new Response.Listener<Result>() {
                    @Override
                    public void onResponse(Result response) {
                        LogUtil.print("onResponse-->"+response);
//                        onFeedsResponse(response, refreshType);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        LogUtil.print("onResponse-->onErrorResponse"+arg0);
//                        onFeedsErrorResponse(arg0, refreshType);
                    }
                });
        // 请求加上Tag,用于取消请求
        request.setTag(this);
//        if (refreshType == DicCode.RefreshType.R_PULL_DOWN) {
//            request.setManuallyRefresh(true);
//        } else if (refreshType == DicCode.RefreshType.R_PULL_UP) {
//            request.setShouldCache(false);
//        }

        VolleyUtil.getQueue(getActivity()).add(request);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnListFragmentInteractionListener) {
//            mListener = (OnListFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh() {
        request(1);
    }

    @Override
    public void onLoadMore() {
        mPage++;
        request(mPage);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
//        void onListFragmentInteraction(DummyItem item);
    }

    @Override
    public void noData() {
        mListView.setVisibility(View.GONE);
        mNullIv.setImageResource(R.mipmap.no_student);
        mNullLayout.setVisibility(View.VISIBLE);
    }
}
