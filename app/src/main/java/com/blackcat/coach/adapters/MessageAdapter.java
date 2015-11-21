package com.blackcat.coach.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;

import com.blackcat.coach.activities.AlertDialog;
import com.blackcat.coach.activities.ChatActivity;
import com.blackcat.coach.activities.ContextMenu;
import com.blackcat.coach.activities.DetailStudentActivity;
import com.blackcat.coach.activities.PersonalInfoActivity;
import com.blackcat.coach.activities.ShowBigImage;
import com.blackcat.coach.easemob.listener.VoicePlayClickListener;
import com.blackcat.coach.easemob.task.LoadImageTask;
import com.blackcat.coach.easemob.utils.DateUtils;
import com.blackcat.coach.easemob.utils.ImageCache;
import com.blackcat.coach.imgs.UILHelper;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.User;
import com.blackcat.coach.utils.Constants;
import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;

import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blackcat.coach.activities.ShowNormalFileActivity;

import com.blackcat.coach.R;
import com.blackcat.coach.easemob.Constant;
import com.easemob.chat.FileMessageBody;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.NormalFileMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.FileUtils;
import  com.blackcat.coach.easemob.utils.ImageUtils;
import com.easemob.util.TextFormater;

import java.io.File;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class MessageAdapter extends BaseAdapter {

    private static final int MESSAGE_TYPE_RECV_TXT = 0;
    private static final int MESSAGE_TYPE_SENT_TXT = 1;
    private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
    private static final int MESSAGE_TYPE_RECV_IMAGE = 3;
    private static final int MESSAGE_TYPE_SENT_VOICE = 4;
    private static final int MESSAGE_TYPE_RECV_VOICE = 5;
    private static final int MESSAGE_TYPE_SENT_FILE = 6;
    private static final int MESSAGE_TYPE_RECV_FILE = 7;
    private String mUsername;
    private LayoutInflater mInflater;
    private Activity mActivity;
    private EMConversation mConversation;
    private EMMessage[] messages;

    private Context mContext;

    private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;
    private static final int HANDLER_MESSAGE_SELECT_LAST = 1;
    private static final int HANDLER_MESSAGE_SEEK_TO = 2;

    public static final String IMAGE_DIR = "chat/image/";

    private Map<String, Timer> timers = new Hashtable<String, Timer>();



    public MessageAdapter(Context context, String username, int chatType) {
        mContext = context;
        mUsername = username;
        mInflater = LayoutInflater.from(mContext);
        mActivity = (Activity) mContext;
        mConversation = EMChatManager.getInstance().getConversation(mUsername);

    }

    Handler handler = new Handler() {

        private void refreshList() {

            messages = (EMMessage[]) mConversation.getAllMessages()
                    .toArray(new EMMessage[mConversation.getAllMessages().size()]);
            for (int i = 0; i < messages.length; i++) {

                mConversation.getMessage(i);
            }
            notifyDataSetChanged();
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_MESSAGE_REFRESH_LIST: {
                    refreshList();
                    break;
                }
                case HANDLER_MESSAGE_SELECT_LAST:
                    if(mActivity != null && mActivity instanceof ChatActivity) {
                        if (messages.length > 0)
                            ((ChatActivity) mActivity).setListViewLastSelection(messages.length - 1);
                    }
                    break;
                case HANDLER_MESSAGE_SEEK_TO:
                    int pos = msg.arg1;
                    if(mActivity != null && mActivity instanceof ChatActivity) {
                        ((ChatActivity) mActivity).setListViewLastSelection(pos);
                    }
                    break;
            }
        }
     };
    public int getCount() {

        return messages == null ? 0 : messages.length;
    }

    public Object getItem(int arg0) {

        if (messages != null && arg0 < messages.length) {
            return messages[arg0];
        }
        return null;
    }

    public long getItemId(int arg0) {

        return arg0;
    }

    public int getViewTypeCount() {
        return 8;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final EMMessage message = (EMMessage)getItem(position);
        EMMessage.ChatType chatType = message.getChatType();
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = createViewByMessage(message, position);
            holder.tv_timestamp = (TextView)convertView.findViewById(R.id.tv_timestamp);
            if (message.getType() == EMMessage.Type.TXT) {
                holder.pb_sending = (ProgressBar)convertView.findViewById(R.id.pb_sending);
                holder.iv_avater = (ImageView)convertView.findViewById(R.id.iv_avatar);
                holder.tv_content = (TextView)convertView.findViewById(R.id.tv_chatcontent);
                holder.iv_staus = (ImageView)convertView.findViewById(R.id.msg_status);
                //holder.tv_userID = (TextView)convertView.findViewById(R.id.tv_userid);
            }
            else if (message.getType() == EMMessage.Type.VOICE) {
                holder.iv_avater = (ImageView)convertView.findViewById(R.id.iv_avatar);
                holder.tv_content = (TextView)convertView.findViewById(R.id.tv_length);
                holder.iv_content = (ImageView)convertView.findViewById(R.id.iv_voice);
                holder.iv_read_status = (ImageView)convertView.findViewById(R.id.iv_unread_voice);
                holder.iv_staus = (ImageView)convertView.findViewById(R.id.msg_status);
                holder.pb_sending = (ProgressBar) convertView.findViewById(R.id.pb_sending);
            }
            else if (message.getType() == EMMessage.Type.FILE) {
                holder.iv_avater = (ImageView) convertView.findViewById(R.id.iv_avatar);
                holder.tv_file_name = (TextView)convertView.findViewById(R.id.tv_file_name);
                holder.tv_file_size = (TextView)convertView.findViewById(R.id.tv_file_size);
                holder.pb_sending = (ProgressBar) convertView.findViewById(R.id.pb_sending);
                holder.iv_staus = (ImageView)convertView.findViewById(R.id.msg_status);
                holder.tv_download_state = (TextView) convertView.findViewById(R.id.tv_file_state);
                holder.ll_container = (LinearLayout) convertView.findViewById(R.id.ll_file_container);
                holder.tv_content = (TextView) convertView.findViewById(R.id.percentage);
            }
            else if (message.getType() == EMMessage.Type.IMAGE) {
                holder.iv_avater = (ImageView) convertView.findViewById(R.id.iv_avatar);
                holder.iv_content = (ImageView) convertView.findViewById(R.id.iv_send_Picture);
                holder.tv_content = (TextView) convertView.findViewById(R.id.percentage);
                holder.pb_sending = (ProgressBar) convertView.findViewById(R.id.progressBar);
                holder.iv_staus = (ImageView)convertView.findViewById(R.id.msg_status);

            }
            else {
                return null;
            }
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        //显示用户名（昵称）
        if(message.direct == EMMessage.Direct.SEND){
            //UserUtils.setCurrentUserNick(holder.tv_usernick);
        }

        //设置用户头像
        setUserAvatar(message, holder.iv_avater);
        if (message.getType() == EMMessage.Type.TXT) {
            handleTextMessage(message, holder, position);
        }
        else if (message.getType() == EMMessage.Type.VOICE) {
            handleVoiceMessage(message, holder, position, convertView);
        }
        else if (message.getType() == EMMessage.Type.FILE) {
            handleFileMessage(message, holder, position, convertView);
        }
        else if (message.getType() == EMMessage.Type.IMAGE) {
            handleImageMessage(message, holder, position, convertView);
        }

        final int messagePos = position;
        if (message.direct == EMMessage.Direct.SEND) {
            View statusView = convertView.findViewById(R.id.msg_status);
            // 重发按钮点击事件
            statusView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // 显示重发消息的自定义alertdialog
                    Intent intent = new Intent(mActivity, AlertDialog.class);
                    intent.putExtra("msg", mActivity.getString(R.string.confirm_resend));
                    intent.putExtra("title", mActivity.getString(R.string.resend));
                    intent.putExtra("cancel", true);
                    intent.putExtra("position", messagePos);
                    if (message.getType() == EMMessage.Type.TXT)
                        mActivity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_TEXT);
                    else if (message.getType() == EMMessage.Type.VOICE)
                        mActivity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_VOICE);
                    else if (message.getType() == EMMessage.Type.IMAGE)
                        mActivity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_PICTURE);
                    //else if (message.getType() == EMMessage.Type.LOCATION)
                        //activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_LOCATION);
                    else if (message.getType() == EMMessage.Type.FILE)
                        mActivity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_FILE);
                    //else if (message.getType() == EMMessage.Type.VIDEO)
                        //activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_VIDEO);

                }
            });

        }

        //消息时间
        if (position == 0) {
            holder.tv_timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
            holder.tv_timestamp.setVisibility(View.VISIBLE);
        } else {
            // 两条消息时间离得如果稍长，显示时间
            EMMessage prevMessage = (EMMessage)getItem(position - 1);
            if (prevMessage != null && DateUtils.isCloseEnough(message.getMsgTime(), prevMessage.getMsgTime())) {
                holder.tv_timestamp.setVisibility(View.GONE);
            } else {
                holder.tv_timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
                holder.tv_timestamp.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    private void setUserAvatar(final EMMessage message, ImageView imageView){
        if (imageView == null) {
            return;
        }
        if(message.direct == EMMessage.Direct.SEND){
            //显示自己头像
            if (Session.getSession().headportrait != null && !TextUtils.isEmpty(Session.getSession().headportrait.originalpic)) {
                UILHelper.loadImage(imageView, Session.getSession().headportrait.originalpic, false, R.mipmap.ic_avatar_small);
            }
            else {
            }
        }else{
            //好友的头像,获取方式1.由消息的额外信息获取,2由好友ID拉去好友信息//采取方法1简单些
            try {
                String avatarUrl = message.getStringAttribute(Constant.MESSAGE_AVATAR_ATTR_KEY);
                if (avatarUrl != null && !TextUtils.isEmpty(avatarUrl)) {
                    UILHelper.loadImage(imageView, avatarUrl, false, R.mipmap.ic_avatar_small);
                }
            }catch (EaseMobException e){
                e.printStackTrace();
            }
        }
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (message.direct == EMMessage.Direct.RECEIVE) {
                    User user = new User();
                    try {
                        user._id = message.getStringAttribute(Constant.MESSAGE_USERID_ATR_KEY);
                        Intent intent = new Intent();
                        intent.setClass(mContext, DetailStudentActivity.class);
                        intent.putExtra(Constants.DATA, user);
                        mContext.startActivity(intent);
                    }catch (Exception e) {

                    }

                }else {
                    Intent intent = new Intent();
                    intent.setClass(mContext, PersonalInfoActivity.class);
                    mContext.startActivity(intent);
                }

            }
        });
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        if (handler.hasMessages(HANDLER_MESSAGE_REFRESH_LIST)) {
            return;
        }
        Message msg = handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST);
        handler.sendMessage(msg);
    }

    /**
     * 刷新页面, 选择最后一个
     */
    public void refreshSelectLast() {
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_SELECT_LAST));
    }

    /**
     * 刷新页面, 选择Position
     */
    public void refreshSeekTo(int position) {
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
        Message msg = handler.obtainMessage(HANDLER_MESSAGE_SEEK_TO);
        msg.arg1 = position;
        handler.sendMessage(msg);
    }

    public int getItemViewType(int position) {
        EMMessage message = (EMMessage)getItem(position);
        if (message == null) {
            return -1;
        }
        if (message.getType() == EMMessage.Type.TXT) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
        }
        if (message.getType() == EMMessage.Type.IMAGE) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE : MESSAGE_TYPE_SENT_IMAGE;

        }
        if (message.getType() == EMMessage.Type.VOICE) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE : MESSAGE_TYPE_SENT_VOICE;
        }
        if (message.getType() == EMMessage.Type.FILE) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_FILE : MESSAGE_TYPE_SENT_FILE;
        }
        return  -1;
    }

    private View createViewByMessage(EMMessage message, int position) {

        switch(message.getType()) {
            case TXT:
                return message.direct == EMMessage.Direct.RECEIVE ?
                        mInflater.inflate(R.layout.row_received_message, null) :
                        mInflater.inflate(R.layout.row_send_message, null);
            case VOICE:
                return message.direct == EMMessage.Direct.RECEIVE ?
                        mInflater.inflate(R.layout.row_received_message_voice, null) :
                        mInflater.inflate(R.layout.row_send_message_voice, null);
            case FILE:
                return  message.direct == EMMessage.Direct.RECEIVE ?
                        mInflater.inflate(R.layout.row_received_file, null) :
                        mInflater.inflate(R.layout.row_send_file, null);
            case IMAGE:
                return  message.direct == EMMessage.Direct.RECEIVE ?
                        mInflater.inflate(R.layout.row_received_picture, null) :
                        mInflater.inflate(R.layout.row_send_picture, null);
        }
        return null;
    }


    private void handleTextMessage(EMMessage message, ViewHolder holder, final int position) {
        TextMessageBody txtBody = (TextMessageBody) message.getBody();
        //Spannable span = SmileUtils.getSmiledText(mContext, txtBody.getMessage());
        // 设置内容
        holder.tv_content.setText(txtBody.getMessage().toString());
        // 设置长按事件监听
        holder.tv_content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(mActivity, ContextMenu.class);
                intent.putExtra("position", position);
                intent.putExtra("type", EMMessage.Type.TXT.ordinal());
                mActivity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });

        if (message.direct == EMMessage.Direct.SEND) {
            switch (message.status) {
                case SUCCESS: // 发送成功
                    holder.pb_sending.setVisibility(View.GONE);
                    holder.iv_staus.setVisibility(View.GONE);
                    break;
                case FAIL: // 发送失败
                    holder.pb_sending.setVisibility(View.GONE);
                    holder.iv_staus.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS: // 发送中
                    holder.pb_sending.setVisibility(View.VISIBLE);
                    holder.iv_staus.setVisibility(View.GONE);
                    break;
                default:
                    // 发送消息
                    sendMsgInBackground(message, holder);
            }
        }
    }

    private void handleVoiceMessage(final EMMessage message, final ViewHolder holder, final int position, View convertView) {
        VoiceMessageBody voiceBody = (VoiceMessageBody) message.getBody();
        int len = voiceBody.getLength();
        if(len>0){
            holder.tv_content.setText(voiceBody.getLength() + "\"");
            holder.tv_content.setVisibility(View.VISIBLE);
        }else{
            holder.tv_content.setVisibility(View.INVISIBLE);
        }
        if (holder.iv_content == null) {
            holder.iv_content = (ImageView)convertView.findViewById(R.id.iv_voice);
        }
        if (holder.iv_content != null) {
            holder.iv_content.setOnClickListener(new VoicePlayClickListener(message,
                    holder.iv_content, holder.iv_read_status, this, mActivity));
            holder.iv_content.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mActivity.startActivityForResult(
                            (new Intent(mActivity, ContextMenu.class)).putExtra("position", position).putExtra("type",
                                    EMMessage.Type.VOICE.ordinal()), ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                    return true;
                }
            });
        }

        if (ChatActivity.playMsgId != null &&
                ChatActivity.playMsgId.equals(message.getMsgId()) &&
                VoicePlayClickListener.mIsPlaying) {
            AnimationDrawable voiceAnimation;
            if (message.direct == EMMessage.Direct.RECEIVE) {
                holder.iv_content.setImageResource(R.anim.voice_from_icon);
            } else {
                holder.iv_content.setImageResource(R.anim.voice_to_icon);
            }
            voiceAnimation = (AnimationDrawable) holder.iv_content.getDrawable();
            voiceAnimation.start();
        } else {
            if (message.direct == EMMessage.Direct.RECEIVE) {
                holder.iv_content.setImageResource(R.mipmap.chatfrom_voice_playing);
            } else {
                holder.iv_content.setImageResource(R.mipmap.chatto_voice_playing);
            }
        }

        if (message.direct == EMMessage.Direct.RECEIVE) {
            if (message.isListened()) {
                // 隐藏语音未听标志
                holder.iv_read_status.setVisibility(View.INVISIBLE);
            } else {
                holder.iv_read_status.setVisibility(View.VISIBLE);
            }

            if (message.status == EMMessage.Status.INPROGRESS) {
                holder.pb_sending.setVisibility(View.VISIBLE);

                ((FileMessageBody) message.getBody()).setDownloadCallback(new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        mActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                holder.pb_sending.setVisibility(View.INVISIBLE);
                                notifyDataSetChanged();
                            }
                        });

                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }

                    @Override
                    public void onError(int code, String message) {
                        mActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                holder.pb_sending.setVisibility(View.INVISIBLE);
                            }
                        });

                    }
                });

            } else {
                holder.pb_sending.setVisibility(View.INVISIBLE);

            }
            return;
        }

        // until here, deal with send voice msg
        switch (message.status) {
            case SUCCESS:
                holder.pb_sending.setVisibility(View.GONE);
                holder.iv_staus.setVisibility(View.GONE);
                break;
            case FAIL:
                holder.pb_sending.setVisibility(View.GONE);
                holder.iv_staus.setVisibility(View.GONE);
                break;
            case INPROGRESS:
                holder.pb_sending.setVisibility(View.GONE);
                holder.iv_staus.setVisibility(View.GONE);
                break;
            default:
                sendMsgInBackground(message, holder);
        }
    }


    private void handleImageMessage(final EMMessage message, final ViewHolder holder, final int position, View convertView) {
        holder.pb_sending.setTag(position);
        holder.iv_content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
               mActivity.startActivityForResult(
                        (new Intent(mContext, ContextMenu.class)).putExtra("position", position).putExtra("type",
                                EMMessage.Type.IMAGE.ordinal()), ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });

        // 接收方向的消息
        if (message.direct == EMMessage.Direct.RECEIVE) {
            // "it is receive msg";
            if (message.status == EMMessage.Status.INPROGRESS) {
                // "!!!! back receive";
                holder.iv_content.setImageResource(R.mipmap.default_image);
                showDownloadImageProgress(message, holder);
                // downloadImage(message, holder);
            } else {
                // "!!!! not back receive, show image directly");
                holder.pb_sending.setVisibility(View.GONE);
                holder.tv_content.setVisibility(View.GONE);
                holder.iv_content.setImageResource(R.mipmap.default_image);
                ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
                if (imgBody.getLocalUrl() != null) {
                    // String filePath = imgBody.getLocalUrl();
                    String remotePath = imgBody.getRemoteUrl();
                    String filePath = ImageUtils.getImagePath(remotePath);
                    String thumbRemoteUrl = imgBody.getThumbnailUrl();
                    if(TextUtils.isEmpty(thumbRemoteUrl)&&!TextUtils.isEmpty(remotePath)){
                        thumbRemoteUrl = remotePath;
                    }
                    String thumbnailPath = ImageUtils.getThumbnailImagePath(thumbRemoteUrl);
                    showImageView(thumbnailPath, holder.iv_content, filePath, imgBody.getRemoteUrl(), message);
                }
            }
            return;
        }

        // 发送的消息
        // process send message
        // send pic, show the pic directly
        ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
        String filePath = imgBody.getLocalUrl();
        if (filePath != null && new File(filePath).exists()) {
            showImageView(ImageUtils.getThumbnailImagePath(filePath), holder.iv_content, filePath, null, message);
        } else {
            showImageView(ImageUtils.getThumbnailImagePath(filePath), holder.iv_content, filePath, IMAGE_DIR, message);
        }

        switch (message.status) {
            case SUCCESS:
                holder.pb_sending.setVisibility(View.GONE);
                holder.tv_content.setVisibility(View.GONE);
                holder.iv_staus.setVisibility(View.GONE);
                break;
            case FAIL:
                holder.pb_sending.setVisibility(View.GONE);
                holder.tv_content.setVisibility(View.GONE);
                holder.iv_staus.setVisibility(View.VISIBLE);
                break;
            case INPROGRESS:
                holder.iv_staus.setVisibility(View.GONE);
                holder.pb_sending.setVisibility(View.VISIBLE);
                holder.tv_content.setVisibility(View.VISIBLE);
                if (timers.containsKey(message.getMsgId()))
                    return;
                // set a timer
                final Timer timer = new Timer();
                timers.put(message.getMsgId(), timer);
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        mActivity.runOnUiThread(new Runnable() {
                            public void run() {
                                holder.pb_sending.setVisibility(View.VISIBLE);
                                holder.tv_content.setVisibility(View.VISIBLE);
                                holder.tv_content.setText(message.progress + "%");
                                if (message.status == EMMessage.Status.SUCCESS) {
                                    holder.pb_sending.setVisibility(View.GONE);
                                    holder.tv_content.setVisibility(View.GONE);
                                    // message.setSendingStatus(Message.SENDING_STATUS_SUCCESS);
                                    timer.cancel();
                                } else if (message.status == EMMessage.Status.FAIL) {
                                    holder.pb_sending.setVisibility(View.GONE);
                                    holder.tv_content.setVisibility(View.GONE);

                                    holder.iv_staus.setVisibility(View.VISIBLE);
                                    timer.cancel();
                                }

                            }
                        });

                    }
                }, 0, 500);
                break;
            default:
                sendPictureMessage(message, holder);
        }
    }

    private void sendPictureMessage(final EMMessage message, final ViewHolder holder) {
        try {
            String to = message.getTo();

            // before send, update ui
            holder.iv_staus.setVisibility(View.GONE);
            holder.pb_sending.setVisibility(View.VISIBLE);
            holder.tv_content.setVisibility(View.VISIBLE);
            holder.tv_content.setText("0%");

            final long start = System.currentTimeMillis();
            // if (chatType == ChatActivity.CHATTYPE_SINGLE) {
            EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

                @Override
                public void onSuccess() {
                    mActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            // send success
                            holder.pb_sending.setVisibility(View.GONE);
                            holder.tv_content.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onError(int code, String error) {

                    mActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            holder.pb_sending.setVisibility(View.GONE);
                            holder.tv_content.setVisibility(View.GONE);
                            // message.setSendingStatus(Message.SENDING_STATUS_FAIL);
                            holder.iv_staus.setVisibility(View.VISIBLE);
                            //Toast.makeText(mActivity,
                                    //mActivity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), 0).show();
                        }
                    });
                }

                @Override
                public void onProgress(final int progress, String status) {
                    mActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            holder.tv_content.setText(progress + "%");
                        }
                    });
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDownloadImageProgress(final EMMessage message, final ViewHolder holder) {

        final FileMessageBody msgbody = (FileMessageBody) message.getBody();
        if(holder.pb_sending!=null)
            holder.pb_sending.setVisibility(View.VISIBLE);
        if(holder.tv_content!=null)
            holder.tv_content.setVisibility(View.VISIBLE);

        msgbody.setDownloadCallback(new EMCallBack() {

            @Override
            public void onSuccess() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (message.getType() == EMMessage.Type.IMAGE) {
                            holder.pb_sending.setVisibility(View.GONE);
                            holder.tv_content.setVisibility(View.GONE);
                        }
                        notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(int code, String message) {

            }

            @Override
            public void onProgress(final int progress, String status) {
                if (message.getType() == EMMessage.Type.IMAGE) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.tv_content.setText(progress + "%");

                        }
                    });
                }

            }

        });
    }
    private boolean showImageView(final String thumbernailPath, final ImageView iv, final String localFullSizePath, String remoteDir,
                                  final EMMessage message) {
        // String imagename =
        // localFullSizePath.substring(localFullSizePath.lastIndexOf("/") + 1,
        // localFullSizePath.length());
        // final String remote = remoteDir != null ? remoteDir+imagename :
        // imagename;
        final String remote = remoteDir;
        EMLog.d("###", "local = " + localFullSizePath + " remote: " + remote);
        // first check if the thumbnail image already loaded into cache
        Bitmap bitmap = ImageCache.getInstance().get(thumbernailPath);
        if (bitmap != null) {
            // thumbnail image is already loaded, reuse the drawable
            iv.setImageBitmap(bitmap);
            iv.setClickable(true);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(mActivity, ShowBigImage.class);
                    File file = new File(localFullSizePath);
                    if (file.exists()) {
                        Uri uri = Uri.fromFile(file);
                        intent.putExtra("uri", uri);

                    } else {
                        ImageMessageBody body = (ImageMessageBody) message.getBody();
                        intent.putExtra("secret", body.getSecret());
                        intent.putExtra("remotepath", remote);
                    }
                    if (message != null && message.direct == EMMessage.Direct.RECEIVE && !message.isAcked
                            && message.getChatType() != EMMessage.ChatType.GroupChat &&
                            message.getChatType() != EMMessage.ChatType.ChatRoom) {
                        try {
                            EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
                            message.isAcked = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mActivity.startActivity(intent);
                }
            });
            return true;
        } else {

            new LoadImageTask().execute(thumbernailPath, localFullSizePath, remote, message.getChatType(), iv, mActivity, message);
            return true;
        }

    }

    private void handleFileMessage(final EMMessage message, final ViewHolder holder, int position, View convertView) {
        final NormalFileMessageBody fileMessageBody = (NormalFileMessageBody) message.getBody();
        final String filePath = fileMessageBody.getLocalUrl();
        holder.tv_file_name.setText(fileMessageBody.getFileName());
        holder.tv_file_size.setText(TextFormater.getDataSize(fileMessageBody.getFileSize()));
        holder.ll_container.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                File file = new File(filePath);
                if (file != null && file.exists()) {
                    // 文件存在，直接打开
                    FileUtils.openFile(file, (Activity) mContext);
                } else {
                    // 下载
                    mContext.startActivity(new Intent(mContext, ShowNormalFileActivity.class).putExtra("msgbody", fileMessageBody));
                }
                if (message.direct == EMMessage.Direct.RECEIVE && !message.isAcked &&
                        message.getChatType() != EMMessage.ChatType.GroupChat &&
                        message.getChatType() != EMMessage.ChatType.ChatRoom) {
                    try {
                        EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
                        message.isAcked = true;
                    } catch (EaseMobException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });
        String st1 = mContext.getResources().getString(R.string.Have_downloaded);
        String st2 = mContext.getResources().getString(R.string.Did_not_download);
        if (message.direct == EMMessage.Direct.RECEIVE) { // 接收的消息
            File file = new File(filePath);
            if (file != null && file.exists()) {
                holder.tv_download_state.setText(st1);
            } else {
                holder.tv_download_state.setText(st2);
            }
            return;
        }

        // until here, deal with send voice msg
        switch (message.status) {
            case SUCCESS:
                holder.pb_sending.setVisibility(View.INVISIBLE);
                holder.tv_content.setVisibility(View.INVISIBLE);
                holder.iv_staus.setVisibility(View.INVISIBLE);
                break;
            case FAIL:
                holder.pb_sending.setVisibility(View.INVISIBLE);
                holder.tv_content.setVisibility(View.INVISIBLE);
                holder.iv_content.setVisibility(View.VISIBLE);
                break;
            case INPROGRESS:
                if (timers.containsKey(message.getMsgId()))
                    return;
                // set a timer
                final Timer timer = new Timer();
                timers.put(message.getMsgId(), timer);
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        mActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                holder.pb_sending.setVisibility(View.VISIBLE);
                                holder.tv_content.setVisibility(View.VISIBLE);
                                holder.tv_content.setText(message.progress + "%");
                                if (message.status == EMMessage.Status.SUCCESS) {
                                    holder.pb_sending.setVisibility(View.INVISIBLE);
                                    holder.tv_content.setVisibility(View.INVISIBLE);
                                    timer.cancel();
                                } else if (message.status == EMMessage.Status.FAIL) {
                                    holder.pb_sending.setVisibility(View.INVISIBLE);
                                    holder.tv_content.setVisibility(View.INVISIBLE);
                                    holder.iv_staus.setVisibility(View.VISIBLE);
                                    timer.cancel();
                                }
                            }
                        });

                    }
                }, 0, 500);
                break;
            default:
                // 发送消息
                sendMsgInBackground(message, holder);
        }

    }

    public void sendMsgInBackground(final EMMessage message, final ViewHolder holder) {

        final long start = System.currentTimeMillis();
        // 调用sdk发送异步发送方法
        EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

            @Override
            public void onSuccess() {
                updateSendedView(message, holder);
            }
            @Override
            public void onError(int code, String error) {
                updateSendedView(message, holder);
            }

            @Override
            public void onProgress(int progress, String status) {
            }

        });

    }


    private void updateSendedView(final EMMessage message, final ViewHolder holder) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (message.status == EMMessage.Status.SUCCESS) {


                } else if (message.status == EMMessage.Status.FAIL) {


                    if (message.getError() == EMError.MESSAGE_SEND_INVALID_CONTENT) {

                    } else if (message.getError() == EMError.MESSAGE_SEND_NOT_IN_THE_GROUP) {

                    } else {

                    }
                }
                notifyDataSetChanged();
            }
        });
    }


    public static class ViewHolder {
        TextView tv_timestamp;
        ImageView iv_avater;
        TextView tv_content;
        ImageView iv_content;
        ImageView iv_read_status;
        TextView  tv_file_name;
        TextView  tv_file_size;
        ProgressBar pb_sending;
        TextView tv_userID;
        ImageView iv_staus;
        TextView tv_download_state;
        LinearLayout ll_container;
    }

}
