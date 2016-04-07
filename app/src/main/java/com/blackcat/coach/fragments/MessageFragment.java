package com.blackcat.coach.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
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
import com.blackcat.coach.models.MessageCount;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.NetConstants;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.SpHelper;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.blackcat.coach.widgets.SelectableRoundedImageView;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
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
    private TextView order_time,system_time,Tv_toast,Tv_system_toast,tv_unread_count,TV_system_messeage;


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
        mListView = (ListView) rootView.findViewById(R.id.inner_list);
        mRLNetErrorItem = rootView.findViewById(R.id.rl_error_item);
        View headerView = View.inflate(mActivity,R.layout.fragment_system_message,null);
        mListView.addHeaderView(headerView);
        initViews(headerView);
        EventBus.getDefault().register(this);
        refreshMessagesList();
        MessageInfo();
        return rootView;
    }

    
    private void initViews(View rootView) {

        SelectableRoundedImageView imgSystemInfor = (SelectableRoundedImageView) rootView
                .findViewById(R.id.iv_messeage);

        imgSystemInfor.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgSystemInfor.setImageResource(R.drawable.messeage);
        imgSystemInfor.setOval(true);

        tv_unread_count=(TextView)rootView.findViewById(R.id.tv_unread_count);
        TV_system_messeage=(TextView)rootView.findViewById(R.id.TV_system_messeage);

        Tv_toast = (TextView) rootView.findViewById(R.id.Tv_toast);
        Tv_system_toast = (TextView) rootView.findViewById(R.id.Tv_system_toast);
        order_time=(TextView)rootView.findViewById(R.id.order_time);
        system_time=(TextView)rootView.findViewById(R.id.system_time);
//
        mAdapter = new CommonAdapter<Message>(mActivity, null, CommonAdapter.AdapterType.TYPE_ADAPTER_MSG);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!BlackCatHXSDKHelper.getInstance().isLogined()) {
                    return;
                }
                Message message = mAdapter.getItem(position-1);
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
//        system_msg=(RelativeLayout)rootView.findViewById(R.id.rl_system_messeage);
//        system_msg.setOnClickListener(this);
                

        mTvErrorMsg = (TextView)rootView.findViewById(R.id.tv_connect_errormsg);

    }
    public void onClick(View v){
        switch(v.getId()){
            case R.id.rl_order_messeage:  //系统消息
                startActivity(new Intent(mActivity, OrderMsgActivity.class));
                tv_unread_count.setVisibility(View.GONE);
                Tv_toast.setVisibility(View.GONE);
                break;
//            case R.id.rl_system_messeage://行业资讯
//                startActivity(new Intent(mActivity, SystemMsgActivity.class));
//                TV_system_messeage.setVisibility(View.GONE);
//                break;
       }
    }

    private void MessageInfo() {
        SpHelper sp = new SpHelper(getActivity());
        URI uri = URIUtil.getMessageInfo(sp.get(NetConstants.KEY_NEWSID,0),sp.get(NetConstants.KEY_MESSAGEID,0),Session.getSession().coachid);
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }
        LogUtil.print("redPoint-->"+url);
        Type mMessageCountType = new TypeToken<Result<MessageCount>>(){}.getType();

        GsonIgnoreCacheHeadersRequest<Result<MessageCount>> request = new GsonIgnoreCacheHeadersRequest<Result<MessageCount>>(
                url, mMessageCountType, null,
                new Response.Listener<Result<MessageCount>>() {
                    @Override
                    public void onResponse(Result<MessageCount> response) {
                        if (response != null && response.type == Result.RESULT_OK && response.data != null) {
                            Tv_toast.setText(response.data.messageinfo.message);
                            tv_unread_count.setText(String.valueOf(response.data.messageinfo.messagecount));
                            Tv_system_toast.setText(response.data.Newsinfo.news);

                            TV_system_messeage.setText(String.valueOf(response.data.Newsinfo.newscount));
                            order_time.setText(response.data.messageinfo.messagetime);
                            system_time.setText(response.data.Newsinfo.newstime);
                            if(response.data.messageinfo.messagecount==0){
                                tv_unread_count.setVisibility(View.INVISIBLE);
                                Tv_toast.setVisibility(View.INVISIBLE);
                            }else{
                                tv_unread_count.setVisibility(View.VISIBLE);
                            }

                            if(response.data.Newsinfo.newscount==0){
                                TV_system_messeage.setVisibility(View.INVISIBLE);
                            }else{
                                TV_system_messeage.setVisibility(View.VISIBLE);
                            }

                        } else if (response != null && !TextUtils.isEmpty(response.msg)) {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(response.msg);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.net_err);
                    }
                });
        // 请求加上Tag,用于取消请求
        request.setTag(this);
        request.setManuallyRefresh(true);
        request.setShouldCache(true);

        VolleyUtil.getQueue(getActivity()).add(request);
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
