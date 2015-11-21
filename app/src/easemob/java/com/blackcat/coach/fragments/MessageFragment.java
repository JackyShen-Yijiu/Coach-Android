package com.blackcat.coach.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.blackcat.coach.R;
import com.blackcat.coach.activities.ChatActivity;
import com.blackcat.coach.adapters.CommonAdapter;
import com.blackcat.coach.easemob.BlackCatHXSDKHelper;
import com.blackcat.coach.easemob.basefunction.HXSDKHelper;
import com.blackcat.coach.easemob.domain.User;
import com.blackcat.coach.events.NewMessageReceiveEvent;
import com.blackcat.coach.models.Message;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.widgets.LoadMoreListView;
import com.blackcat.coach.widgets.PullToRefreshListView;
import com.blackcat.coach.widgets.PullToRefreshView;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MessageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageFragment extends BaseFragment {

    private PullToRefreshListView mListView;
    private PullToRefreshView mPullToRefreshView;
    private CommonAdapter<Message> mAdapter;
    private List<Message>  mMessageLists;
    private Type mType = new TypeToken<Result<Message>>(){}.getType();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessageFragment newInstance(String param1, String param2) {
        MessageFragment fragment = new MessageFragment();
        return fragment;
    }

    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_message, container, false);
        initViews(rootView);
        EventBus.getDefault().register(this);
        return rootView;
    }

    private void initViews(View rootView) {
        mPullToRefreshView = (PullToRefreshView) rootView.findViewById(R.id.pull_refresh_view);
        mPullToRefreshView.setRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                refresh(DicCode.RefreshType.R_PULL_DOWN);
            }
        });
        mListView = (PullToRefreshListView) rootView.findViewById(R.id.inner_list);
//		mListView.setOnItemClickListener(this);
        mAdapter = new CommonAdapter<Message>(mActivity, null, CommonAdapter.AdapterType.TYPE_ADAPTER_MSG);
        mListView.setAdapter(mAdapter);
        mListView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
//                refresh(DicCode.RefreshType.R_PULL_UP);
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Message message = mAdapter.getItem(position);
//                Intent intent = new Intent(getActivity(), ChatActivity.class);
//                intent.putExtra("userId", message.getUserName());
//                startActivity(intent);
            }
        });
        List<Message> list = new ArrayList<>();
        list.add(new Message("15011022834"));
        list.add(new Message("15510521096"));
        list.add(new Message("18513622989"));
        list.add(new Message());
        list.add(new Message());
        mAdapter.setList(list);
        mAdapter.notifyDataSetChanged();

//        refresh(DicCode.RefreshType.R_INIT);
    }

    public void  onEvent(NewMessageReceiveEvent event) {
        refresh();
    }

    //刷新消息界面
     void refresh() {
        refreshMessagesList();
        mAdapter.notifyDataSetChanged();
    }

    private void refreshMessagesList() {
        if (mMessageLists == null) {
            mMessageLists = new ArrayList<>();
        }
        mMessageLists.clear();

        Map<String, EMConversation> conversationMap = EMChatManager.getInstance().getAllConversations();
        //用户信息保存在User中，用于显示用户的信息，现在还不确定是否在本地保存,暂时按本地无备份
        for (EMConversation conversation : conversationMap.values()) {
            if (conversation.getMsgCount() > 0) {
                Message message = new Message(conversation);
                mMessageLists.add(message);
            }
        }
        sortMessageListByTime(mMessageLists);

    }

    private void sortMessageListByTime(List<Message> messageList) {
        Collections.sort(messageList, new Comparator<Message>() {
            @Override
            public int compare(Message lhs, Message rhs) {
                if (lhs.getTime() == rhs.getTime()) {
                    return 0;
                } else if (lhs.getTime() < rhs.getTime()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }
    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }
}
