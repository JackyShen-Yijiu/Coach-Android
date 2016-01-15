package com.blackcat.coach.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blackcat.coach.R;
import com.blackcat.coach.activities.CaptureActivity;
import com.blackcat.coach.activities.ChatActivity;
import com.blackcat.coach.activities.OrderMsgActivity;
import com.blackcat.coach.activities.SystemMsgActivity;
import com.blackcat.coach.adapters.CommonAdapter;
import com.blackcat.coach.easemob.BlackCatHXSDKHelper;
import com.blackcat.coach.easemob.Constant;
import com.blackcat.coach.easemob.domain.User;
import com.blackcat.coach.events.NetStateEvent;
import com.blackcat.coach.events.NewMessageReceiveEvent;
import com.blackcat.coach.models.Message;
import com.blackcat.coach.models.Result;
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

public class MessageFragment extends BaseFragment implements View.OnClickListener {

    private ListView mListView;
    private CommonAdapter<Message> mAdapter;
    private List<Message>  mMessageLists;
    private Type mType = new TypeToken<Result<Message>>(){}.getType();

    private View mRLNetErrorItem;
    private TextView mTvErrorMsg;
    private RelativeLayout order_msg;
    private RelativeLayout system_msg;

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
        refreshMessagesList();
        return rootView;
    }

    
    private void initViews(View rootView) {
        mListView = (ListView) rootView.findViewById(R.id.inner_list);
        mAdapter = new CommonAdapter<Message>(mActivity, null, CommonAdapter.AdapterType.TYPE_ADAPTER_MSG);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!BlackCatHXSDKHelper.getInstance().isLogined()) {
                    return;
                }
                Message message = mAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(Constant.MESSAGE_USERID_ATR_KEY, message.getUserName());
                intent.putExtra(Constant.MESSAGE_NAME_ATTR_KEY, message.getShowName());
                intent.putExtra(Constant.MESSAGE_AVATAR_ATTR_KEY, message.getAvatarUrl());
                intent.putExtra(Constant.CHAT_FROM_TYPE, Constant.CHAT_FROM_MESSAGES);
                startActivity(intent);
            }
        });
        order_msg=(RelativeLayout)rootView.findViewById(R.id.rl_order_messeage);
        order_msg.setOnClickListener(this);
        system_msg=(RelativeLayout)rootView.findViewById(R.id.rl_system_messeage);
        system_msg.setOnClickListener(this);
                
        mRLNetErrorItem = rootView.findViewById(R.id.rl_error_item);
        mTvErrorMsg = (TextView)rootView.findViewById(R.id.tv_connect_errormsg);

    }
    public void onClick(View v){
        switch(v.getId()){
            case R.id.rl_order_messeage:  //预约消息
                startActivity(new Intent(mActivity, OrderMsgActivity.class));

                break;
            case R.id.rl_system_messeage://系统消息
                startActivity(new Intent(mActivity, SystemMsgActivity.class));
                break;
       }
    }

    public void  onEvent(NewMessageReceiveEvent event) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshMessagesList();
            }
        });
    }

    private boolean mIsNetOk = true;
    private String  mErrorMsg;
    public  void onEvent(NetStateEvent event) {
        mIsNetOk = event.mIsNetOk;
        mErrorMsg = event.mErrorMsg;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRLNetErrorItem.setVisibility(mIsNetOk ? View.GONE : View.VISIBLE);
                if (!TextUtils.isEmpty(mErrorMsg)) {
                    mTvErrorMsg.setText(mErrorMsg);
                }
            }
        });
    }
    private void refreshMessagesList() {
        if (mMessageLists == null) {
            mMessageLists = new ArrayList<>();
        } else {
            mMessageLists.clear();
        }

        Map<String, User> userList = ((BlackCatHXSDKHelper)BlackCatHXSDKHelper.getInstance()).getContactList();
        Map<String, EMConversation> conversationMap = EMChatManager.getInstance().getAllConversations();
        //用户信息保存在User中，用于显示用户的信息，现在还不确定是否在本地保存,暂时按本地无备份
        for (EMConversation conversation : conversationMap.values()) {
            if (conversation.getMsgCount() > 0) {
                User user = null;
                if (userList != null) {
                    user = userList.get(conversation.getUserName());
                }
                Message message = new Message(conversation,user, mActivity);
                mMessageLists.add(message);
            }
        }
        sortMessageListByTime(mMessageLists);
        mAdapter.setList(mMessageLists);
        mAdapter.notifyDataSetChanged();
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
