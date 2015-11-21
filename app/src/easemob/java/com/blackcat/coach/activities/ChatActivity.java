package com.blackcat.coach.activities;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.blackcat.coach.R;
import com.blackcat.coach.adapters.MessageAdapter;
import com.blackcat.coach.easemob.BlackCatHXSDKHelper;
import com.blackcat.coach.easemob.basefunction.HXSDKHelper;
import com.blackcat.coach.widgets.PullToRefreshListView;
import com.blackcat.coach.widgets.PullToRefreshView;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;

import java.util.List;

public class ChatActivity extends BaseActivity implements
        View.OnClickListener, EMEventListener
{
    public static final int PAGE_MSG_SIZE  = 20;
    public static final int CHATTYPE_SINGLE = 1;

    private EMConversation conversation;
    private String toChatUsername;
    private MessageAdapter adapter;
    private int chatType = CHATTYPE_SINGLE;

    private PullToRefreshListView mListView;
    //private PullToRefreshView mPullToRefreshView;

    private EditText mEditText;
    private Button mBtnSend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        configToolBar(R.mipmap.ic_back);
        initView();
        setUpView();
    }

    private void initView() {
        mEditText = (EditText)findViewById(R.id.ev_input);
        mListView = (PullToRefreshListView) findViewById(R.id.inner_list);
        mBtnSend = (Button) findViewById(R.id.btn_send);
        mBtnSend.setOnClickListener(this);
    }

    private void setUpView() {
        toChatUsername = getIntent().getStringExtra("userId");

        onConversationInit();
        onListViewCreate();

        //show forward message if the message

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_send) {
            String input = mEditText.getText().toString();
            sendText(input);
        }

    }

    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage:
            {
                EMMessage message = (EMMessage)event.getData();
                String username = message.getFrom();

                if(username.equals(toChatUsername)) {
                    refreshUIWithNewMessage();
                    HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(message);
                }
                else {
                    HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
                }
                break;
            }
            case EventDeliveryAck:
            {

                refreshUI() ;
                break;
            }
            case EventOfflineMessage:
            {
                refreshUI();
            }
            default:
                break;

        }
    }

    public void sendText(String content) {
        if (content.length() > 0) {
            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);

            TextMessageBody txtBody = new TextMessageBody(content);

            message.addBody(txtBody);
            message.setReceipt(toChatUsername);
            conversation.addMessage(message);

            adapter.refreshSelectLast();
            mEditText.setText("");
            setResult(RESULT_OK);

        }
    }

    protected void onConversationInit() {
        conversation = EMChatManager.getInstance().getConversationByType(toChatUsername,
                EMConversation.EMConversationType.Chat);

        conversation.markAllMessagesAsRead();

        //TODO:设置初始先显示的消息数目
        final List<EMMessage> messages = conversation.getAllMessages();
        int msgCount = messages != null ? messages.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < PAGE_MSG_SIZE) {
            String msgId = null;
            if (messages != null && messages.size() > 0) {
                msgId = messages.get(0).getMsgId();
            }
            conversation.loadMoreMsgFromDB(msgId, PAGE_MSG_SIZE);
        }
        //TODO: ChatRoom change listenser?
    }

    protected void onListViewCreate() {
        adapter = new MessageAdapter(ChatActivity.this, toChatUsername, chatType);
        mListView.setAdapter(adapter);
    }


    private void refreshUIWithNewMessage() {
        if (adapter == null) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.refreshSelectLast();
            }
        });
    }

    private void refreshUI() {
        if (adapter == null) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.refresh();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected  void onResume() {
        super.onResume();
        BlackCatHXSDKHelper sdkHelper = (BlackCatHXSDKHelper)BlackCatHXSDKHelper.getInstance();
        sdkHelper.pushActivity(this);

        EMChatManager.getInstance().registerEventListener(this,
                new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage,
                        EMNotifierEvent.Event.EventOfflineMessage,
                        EMNotifierEvent.Event.EventDeliveryAck,
                        EMNotifierEvent.Event.EventReadAck});
    }

    @Override
    protected void onStop() {
        EMChatManager.getInstance().unregisterEventListener(this);
        BlackCatHXSDKHelper sdkHelper = (BlackCatHXSDKHelper) BlackCatHXSDKHelper.getInstance();
        sdkHelper.popActivity(this);

        super.onStop();
    }

    public void back(View view) {
        EMChatManager.getInstance().unregisterEventListener(this);

        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
