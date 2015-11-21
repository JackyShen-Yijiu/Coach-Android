package com.blackcat.coach.activities;


import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blackcat.coach.R;
import com.blackcat.coach.adapters.MessageAdapter;
import com.blackcat.coach.easemob.BlackCatHXSDKHelper;
import com.blackcat.coach.easemob.Constant;
import com.blackcat.coach.easemob.basefunction.HXSDKHelper;
import com.blackcat.coach.easemob.domain.User;
import com.blackcat.coach.easemob.listener.VoicePlayClickListener;
import com.blackcat.coach.easemob.utils.CommonUtils;

import com.blackcat.coach.events.NewMessageReceiveEvent;

import com.blackcat.coach.models.Session;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.NormalFileMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.util.PathUtil;
import com.easemob.util.VoiceRecorder;

import java.io.File;
import java.util.List;

import de.greenrobot.event.EventBus;

public class ChatActivity extends BaseActivity implements
        View.OnClickListener, EMEventListener
{
    public static final int PAGE_MSG_SIZE  = 20;
    public static final int CHAT_TYPE_SINGLE = 1;

    public static final int REQUEST_CODE_LOCAL = 1;
    public static final int REQUEST_CODE_SELECT_FILE = 2;
    public static final int REQUEST_CODE_CAMERA = 3;
    public static final int REQUEST_CODE_CONTEXT_MENU = 4;

    public static final int REQUEST_CODE_TEXT = 5;
    public static final int REQUEST_CODE_VOICE = 6;
    public static final int REQUEST_CODE_PICTURE = 7;
    public static final int REQUEST_CODE_FILE = 8;

    public static final int RESULT_CODE_COPY = 1;
    public static final int RESULT_CODE_DELETE = 2;
    public static final int RESULT_CODE_FORWARD = 3;
    public static final int RESULT_CODE_OPEN = 4;
    public static final int RESULT_CODE_DOWNLOAD = 5;
    public static final int RESULT_CODE_TO_CLOUD = 6;
    public static final int RESULT_CODE_EXIT_GROUP = 7;

    public static String playMsgId;
    public static int resendPos;
    public static ChatActivity activityInstance = null;
    private EMConversation conversation;

    private String toChatUsername;
    private String nickName;
    private String avatarUrl;
    private int    userType;
    private MessageAdapter adapter;
    private int chatType = CHAT_TYPE_SINGLE;

    private ListView mListView;


    private VoiceRecorder mVoiceRecorder;
    private ClipboardManager mClipboard;

    private EditText mEditText;
    private Button mBtnSend;
    private Button mBtnModVoice;
    private Button mBtnModKeyboard;
    private TextView mTvPressSpeak;
    private LinearLayout mLLPressToSpeak;
    private LinearLayout mLLSendTxtMsg;
    private RelativeLayout mRLRecordingContainer;
    private ImageView mIvMicImage;
    private TextView mTvRecordHint;
    private InputMethodManager mInputManager;
    private Drawable[] mMicImages;

    private Button  mBtnMore;
    private LinearLayout mLLSendTypeContainer;
    private ImageView mIvSendFile;
    private ImageView mIvSendPicture;
    private ImageView mIvTakePicture;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean isloading = false;
    private boolean haveMoreData = true;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            mIvMicImage.setImageDrawable(mMicImages[msg.what]);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        configToolBar(R.mipmap.ic_back);
        activityInstance = this;
        initView();
        setUpView();
    }

    private void initView() {
        mEditText = (EditText)findViewById(R.id.ev_input);
        mListView = (ListView) findViewById(R.id.lv_list);
        mBtnSend = (Button) findViewById(R.id.btn_send);
        mBtnSend.setOnClickListener(this);
        mBtnModVoice = (Button)findViewById(R.id.btn_mod_voice);
        mBtnModVoice.setOnClickListener(this);
        mBtnModKeyboard = (Button)findViewById(R.id.btn_mod_keyboard);
        mBtnModKeyboard.setOnClickListener(this);
        mTvPressSpeak = (TextView)findViewById(R.id.tv_press_speak);
        mTvPressSpeak.setOnTouchListener(new PressToSpeakListener());

        mVoiceRecorder = new VoiceRecorder(mHandler);

        mLLPressToSpeak = (LinearLayout)findViewById(R.id.ll_press_to_speak);
        mLLSendTxtMsg = (LinearLayout)findViewById(R.id.ll_send_txt_msg);

        mRLRecordingContainer = (RelativeLayout)findViewById(R.id.rl_recording_container);

        mIvMicImage = (ImageView)findViewById(R.id.iv_mic_image);
        mTvRecordHint = (TextView)findViewById(R.id.tv_recording_hint);

        // 动画资源文件,用于录制语音时
        mMicImages = new Drawable[] { getResources().getDrawable(R.mipmap.record_animate_01),
                getResources().getDrawable(R.mipmap.record_animate_02),
                getResources().getDrawable(R.mipmap.record_animate_03),
                getResources().getDrawable(R.mipmap.record_animate_04),
                getResources().getDrawable(R.mipmap.record_animate_05),
                getResources().getDrawable(R.mipmap.record_animate_06),
                getResources().getDrawable(R.mipmap.record_animate_07),
                getResources().getDrawable(R.mipmap.record_animate_08),
                getResources().getDrawable(R.mipmap.record_animate_09),
                getResources().getDrawable(R.mipmap.record_animate_10),
                getResources().getDrawable(R.mipmap.record_animate_11),
                getResources().getDrawable(R.mipmap.record_animate_12),
                getResources().getDrawable(R.mipmap.record_animate_13),
                getResources().getDrawable(R.mipmap.record_animate_14) };

        mBtnMore = (Button)findViewById(R.id.btn_more);
        mBtnMore.setOnClickListener(this);
        mLLSendTypeContainer = (LinearLayout)findViewById(R.id.ll_btn_container);
        mIvSendFile = (ImageView)findViewById(R.id.iv_send_file);
        mIvSendFile.setOnClickListener(this);
        mIvSendPicture = (ImageView)findViewById(R.id.iv_send_picture1);
        mIvSendPicture.setOnClickListener(this);
        mIvTakePicture = (ImageView)findViewById(R.id.iv_take_picture);
        mIvTakePicture.setOnClickListener(this);

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.chat_swipe_layout);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (mListView.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
                            List<EMMessage> _messages;
                            try {
                                if (chatType == CHAT_TYPE_SINGLE) {
                                    _messages = conversation.loadMoreMsgFromDB(((EMMessage) adapter.getItem(0)).getMsgId(), PAGE_MSG_SIZE);
                                } else {
                                    _messages = conversation.loadMoreGroupMsgFromDB(((EMMessage) adapter.getItem(0)).getMsgId(), PAGE_MSG_SIZE);
                                }
                            } catch (Exception e1) {
                                mSwipeRefreshLayout.setRefreshing(false);
                                return;
                            }

                            if (_messages.size() > 0) {
                                adapter.notifyDataSetChanged();
                                adapter.refreshSeekTo(_messages.size() - 1);
                                if (_messages.size() != PAGE_MSG_SIZE) {
                                    haveMoreData = false;
                                }
                            } else {
                                haveMoreData = false;
                            }

                            isloading = false;

                        } else {
                            Toast.makeText(ChatActivity.this, getResources().getString(R.string.no_more_messages), Toast.LENGTH_SHORT).show();
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    mBtnSend.setVisibility(View.VISIBLE);
                    mBtnMore.setVisibility(View.GONE);
                }else {
                    mBtnSend.setVisibility(View.GONE);
                    mBtnMore.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEditText.setOnClickListener(this);
    }
    private void setUpView() {
        mWakeLock = ((PowerManager)getSystemService(Context.POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK, "coach");

        mInputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mClipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        toChatUsername = getIntent().getStringExtra(Constant.MESSAGE_USERID_ATR_KEY);
        nickName = getIntent().getStringExtra(Constant.MESSAGE_NAME_ATTR_KEY);
        avatarUrl = getIntent().getStringExtra(Constant.MESSAGE_AVATAR_ATTR_KEY);
        userType = getIntent().getIntExtra(Constant.MESSAGE_USERTYPE_ATR_KEY, 1);
        if (toChatUsername == null || TextUtils.isEmpty(toChatUsername)) {
            finish();
            return;
        }
        if (!TextUtils.isEmpty(nickName)){
            setTitle(nickName);
        }
        int fromType = getIntent().getIntExtra(Constant.CHAT_FROM_TYPE, Constant.CHAT_FROM_MESSAGES);
        if (fromType == Constant.CHAT_FROM_RESERVATION ||
                fromType == Constant.CHAT_FROM_NOTIFY) {
            User user = new User(toChatUsername);
            user.setNick(nickName);
            user.setAvatar(avatarUrl);
            user.setUserType(userType);
            ((BlackCatHXSDKHelper)HXSDKHelper.getInstance()).saveContact(user);
        }

        onConversationInit();
        onListViewCreate();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case  R.id.btn_send:
                String input = mEditText.getText().toString();
                sendText(input);
                break;
            case R.id.btn_mod_voice:
                setModVoice();
                break;
            case R.id.btn_mod_keyboard:
                setModKeyboard();
                break;
            case R.id.btn_more:
                mLLSendTypeContainer.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_send_file:
                getLocalFile();
                break;
            case R.id.iv_send_picture1:
                getLocalPicture();
                break;
            case R.id.iv_take_picture:
                selectPicFromCamera();
                break;
            case R.id.ev_input:
                editClick();
                break;
        }
    }

    private void editClick() {
        mListView.setSelection(mListView.getCount() - 1);
        if (mLLSendTypeContainer.getVisibility() == View.VISIBLE) {
            mLLSendTypeContainer.setVisibility(View.GONE);
        }
    }
    private void setModVoice() {
        hideKeyboard();
        mBtnModVoice.setVisibility(View.GONE);
        mBtnModKeyboard.setVisibility(View.VISIBLE);
        mLLSendTxtMsg.setVisibility(View.GONE);
        mLLPressToSpeak.setVisibility(View.VISIBLE);
        mBtnSend.setVisibility(View.GONE);
        mBtnMore.setVisibility(View.VISIBLE);
        mLLSendTypeContainer.setVisibility(View.GONE);
    }

    private void setModKeyboard() {
        mBtnModVoice.setVisibility(View.VISIBLE);
        mBtnModKeyboard.setVisibility(View.GONE);
        mLLSendTxtMsg.setVisibility(View.VISIBLE);
        mLLPressToSpeak.setVisibility(View.GONE);
        mLLSendTypeContainer.setVisibility(View.GONE);
        if (TextUtils.isEmpty(mEditText.getText())) {
            mBtnMore.setVisibility(View.VISIBLE);
            mBtnSend.setVisibility(View.GONE);
        }
        else {
            mBtnMore.setVisibility(View.GONE);
            mBtnSend.setVisibility(View.VISIBLE);
        }

    }

    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode !=
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                mInputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private void getLocalFile() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        else {
            intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }

    private void getLocalPicture() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        }
        else {
            intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
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

    private void addExtraInfoToMessage(EMMessage message) {
        message.setAttribute(Constant.MESSAGE_NAME_ATTR_KEY, Session.getSession().name);
        if (Session.getSession().headportrait != null && !TextUtils.isEmpty(Session.getSession().headportrait.originalpic)) {
            message.setAttribute(Constant.MESSAGE_AVATAR_ATTR_KEY, Session.getSession().headportrait.originalpic);
        }
        message.setAttribute(Constant.MESSAGE_USERTYPE_ATR_KEY, 2);
        message.setAttribute(Constant.MESSAGE_USERID_ATR_KEY, Session.getSession().coachid);
    }
    public void sendText(String content) {
        if (content.length() > 0) {
            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);

            TextMessageBody txtBody = new TextMessageBody(content);

            message.addBody(txtBody);
            message.setReceipt(toChatUsername);
            addExtraInfoToMessage(message);
            conversation.addMessage(message);

            adapter.refreshSelectLast();
            mEditText.setText("");
            setResult(RESULT_OK);

        }
    }
    private void sendVoice(String filePath, String fileName, String length, boolean isResend) {
        if (!(new File(filePath).exists())) {
            return;
        }
        try {
            final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
            message.setReceipt(toChatUsername);
            int len = Integer.parseInt(length);
            VoiceMessageBody body = new VoiceMessageBody(new File(filePath), len);
            message.addBody(body);
            addExtraInfoToMessage(message);
            conversation.addMessage(message);
            adapter.refreshSelectLast();
            setResult(RESULT_OK);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected void onConversationInit() {
        try {
            conversation = EMChatManager.getInstance().getConversationByType(toChatUsername,
                    EMConversation.EMConversationType.Chat);
        }catch (Exception e) {
            e.printStackTrace();
            finish();
            return;
        }

        conversation.markAllMessagesAsRead();

        //TODO:设置初始先显示的消息数目
        final List<EMMessage> messages = conversation.getAllMessages();
        int msgCount = messages != null ? messages.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < PAGE_MSG_SIZE) {
            String msgId = null;
            if (messages != null && messages.size() > 0) {
                msgId = messages.get(0).getMsgId();
            }
            if (chatType == CHAT_TYPE_SINGLE) {
                conversation.loadMoreMsgFromDB(msgId, PAGE_MSG_SIZE);
            }else {
                conversation.loadMoreGroupMsgFromDB(msgId, PAGE_MSG_SIZE);
            }

        }
        //TODO: ChatRoom change listenser?
    }

    public void setListViewLastSelection(int pos) {

        mListView.setSelection(pos);
    }
    protected void onListViewCreate() {
        adapter = new MessageAdapter(ChatActivity.this, toChatUsername, chatType);
        mListView.setAdapter(adapter);
        mListView.setOnScrollListener(new ListScrollListener());
        adapter.refreshSelectLast();

        mListView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                mLLSendTypeContainer.setVisibility(View.GONE);
                return false;
            }
        });
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
            switch (resultCode) {
                case RESULT_CODE_COPY: // 复制消息
                    EMMessage copyMsg = ((EMMessage) adapter.getItem(data.getIntExtra("position", -1)));
                    mClipboard.setText(((TextMessageBody) copyMsg.getBody()).getMessage());
                    break;
                case RESULT_CODE_DELETE: // 删除消息
                    EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1));
                    conversation.removeMessage(deleteMsg.getMsgId());
                    adapter.refreshSeekTo(data.getIntExtra("position", adapter.getCount()) - 1);
                    break;

                case RESULT_CODE_FORWARD: // 转发消息
                    EMMessage forwardMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", 0));
                    Intent intent = new Intent(this, ForwardMessageActivity.class);
                    intent.putExtra("forward_msg_id", forwardMsg.getMsgId());
                    startActivity(intent);

                    break;

                default:
                    break;
            }
        }

        if (resultCode == RESULT_OK) { // 清空消息

            if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
            } else if (requestCode == REQUEST_CODE_SELECT_FILE) { // 发送选择的文件
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        sendFile(uri);
                    }
                }
            } else if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
                if (cameraFile != null && cameraFile.exists()) {
                    sendPicture(cameraFile.getAbsolutePath());
                }
            } else if (requestCode == REQUEST_CODE_TEXT
                    || requestCode == REQUEST_CODE_VOICE
                    || requestCode == REQUEST_CODE_PICTURE
                    || requestCode == REQUEST_CODE_FILE) {
                resendMessage();
            }
        }
    }

    private void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            if (picturePath == null || picturePath.equals("null")) {

                return;
            }
            sendPicture(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                return;

            }
            sendPicture(file.getAbsolutePath());
        }

    }

    private void sendPicture(final String filePath) {
        String to = toChatUsername;
        final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
        // 如果是群聊，设置chattype,默认是单聊
        message.setChatType(EMMessage.ChatType.ChatRoom);

        message.setReceipt(to);
        ImageMessageBody body = new ImageMessageBody(new File(filePath));
        // 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
        // body.setSendOriginalImage(true);
        message.addBody(body);
        addExtraInfoToMessage(message);
        conversation.addMessage(message);
        mListView.setAdapter(adapter);
        adapter.refreshSelectLast();
        setResult(RESULT_OK);
    }


    private void sendFile(Uri uri) {
        String filePath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }
        File file = new File(filePath);
        if (file == null || !file.exists()) { //x选择的文件没找到，或不存在
            return;
        }
        if (file.length() > 10 * 1024 * 1024) { //文件太大时的处理
            return;
        }

        // 创建一个文件消息
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.FILE);
        message.setChatType(EMMessage.ChatType.ChatRoom);


        message.setReceipt(toChatUsername);
        // add message body
        NormalFileMessageBody body = new NormalFileMessageBody(new File(filePath));
        message.addBody(body);
        addExtraInfoToMessage(message);
        conversation.addMessage(message);
        mListView.setAdapter(adapter);
        adapter.refreshSelectLast();
        setResult(RESULT_OK);
    }

    private File cameraFile;
    public void selectPicFromCamera() {
        if (!CommonUtils.isExitsSdcard()) {

            return;
        }

        cameraFile = new File(PathUtil.getInstance().getImagePath(), Session.getSession().name
                + System.currentTimeMillis() + ".jpg");
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_CAMERA);
    }

    private void resendMessage() {
        EMMessage msg = null;
        msg = conversation.getMessage(resendPos);
        msg.status = EMMessage.Status.CREATE;
        adapter.refreshSeekTo(resendPos);
    }

    @Override
    protected void onDestroy() {
        activityInstance = null;
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
        EventBus.getDefault().post(new NewMessageReceiveEvent());
        super.onStop();
    }

    public void back(View view) {
        EMChatManager.getInstance().unregisterEventListener(this);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (mLLSendTypeContainer.getVisibility() == View.VISIBLE) {
            mLLSendTypeContainer.setVisibility(View.GONE);
        }
        else {
            super.onBackPressed();
        }

    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_chat, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private class ListScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                    break;
            }
        }
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }

    }


    private PowerManager.WakeLock mWakeLock;

    class PressToSpeakListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if(!CommonUtils.isExitsSdcard()) {
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        mWakeLock.acquire();
                        if (VoicePlayClickListener.mIsPlaying) {
                            VoicePlayClickListener.mCurrentPlayListener.stopPlayVoice();
                        }
                        mRLRecordingContainer.setVisibility(View.VISIBLE);
                        mTvRecordHint.setText(getString(R.string.str_move_up_cancel));
                        mTvRecordHint.setBackgroundColor(Color.TRANSPARENT);
                        mVoiceRecorder.startRecording(null, toChatUsername, getApplicationContext());
                    }catch(Exception e) {
                        e.printStackTrace();
                        v.setPressed(false);

                        if(mWakeLock.isHeld()) {
                            mWakeLock.release();
                        }
                        if (mVoiceRecorder != null) {
                            mVoiceRecorder.discardRecording();
                        }
                        mRLRecordingContainer.setVisibility(View.INVISIBLE);
                        return false;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (event.getY() < 0) {
                        mTvRecordHint.setText(getString(R.string.str_release_cancel));
                        mTvRecordHint.setBackgroundResource(R.drawable.recording_text_hint_bg);
                    }
                    else {
                        mTvRecordHint.setText(getString(R.string.str_move_up_cancel));
                        mTvRecordHint.setBackgroundColor(Color.TRANSPARENT);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    mRLRecordingContainer.setVisibility(View.INVISIBLE);

                    if (mWakeLock.isHeld()) {
                        mWakeLock.release();
                    }
                    if (event.getY() < 0) {
                        mVoiceRecorder.discardRecording();
                    }
                    else {
                       try {
                           int len = mVoiceRecorder.stopRecoding();
                           if (len > 0) {
                               sendVoice(mVoiceRecorder.getVoiceFilePath(),
                                       mVoiceRecorder.getVoiceFileName(toChatUsername),
                                       Integer.toString(len), false);
                           }
                       }catch (Exception e) {
                           e.printStackTrace();
                       }
                    }
                    break;
                default:
                    mRLRecordingContainer.setVisibility(View.INVISIBLE);
                    if (mVoiceRecorder != null) {
                        mVoiceRecorder.discardRecording();
                    }
                    break;
            }
            return true;
        }
    }
}
