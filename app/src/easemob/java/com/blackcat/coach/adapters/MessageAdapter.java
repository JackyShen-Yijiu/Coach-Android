package com.blackcat.coach.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.blackcat.coach.activities.PersonalInfoActivity;
import com.blackcat.coach.easemob.utils.DateUtils;
import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;

import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.blackcat.coach.R;
import com.blackcat.coach.easemob.Constant;
import com.blackcat.coach.easemob.utils.UserUtils;
import com.easemob.chat.TextMessageBody;

import java.util.Date;


public class MessageAdapter extends BaseAdapter {

    private static final int MESSAGE_TYPE_RECV_TXT = 0;
    private static final int MESSAGE_TYPE_SENT_TXT = 1;

    private String mUsername;
    private LayoutInflater mInflater;
    private Activity mActivity;
    private EMConversation mConversation;
    private EMMessage[] messages;

    private Context mContext;

    private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;
    private static final int HANDLER_MESSAGE_SELECT_LAST = 1;
    private static final int HANDLER_MESSAGE_SEEK_TO = 2;

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

                    break;
                case HANDLER_MESSAGE_SEEK_TO:
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

    public View getView(int position, View convertView, ViewGroup parent) {
        final EMMessage message = (EMMessage)getItem(position);
        EMMessage.ChatType chatType = message.getChatType();
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = createViewByMessage(message, position);
            if (message.getType() == EMMessage.Type.TXT) {
                //holder.pb_sending = (ProgressBar)convertView.findViewById(R.id.pb_sending);
                holder.iv_avater = (ImageView)convertView.findViewById(R.id.iv_avatar);
                holder.tv_content = (TextView)convertView.findViewById(R.id.tv_chatcontent);
                holder.tv_timestamp = (TextView)convertView.findViewById(R.id.tv_timestamp);
                //holder.tv_userID = (TextView)convertView.findViewById(R.id.tv_userid);
            }
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(message.direct == EMMessage.Direct.SEND){
            //UserUtils.setCurrentUserNick(holder.tv_usernick);
        }


        //设置用户头像
       // setUserAvatar(message, holder.iv_avater);
        if (message.getType() == EMMessage.Type.TXT) {
            handleTextMessage(message, holder, position);
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

    /**
     * 显示用户头像
     * @param message
     * @param imageView
     */
    private void setUserAvatar(final EMMessage message, ImageView imageView){
        if(message.direct == EMMessage.Direct.SEND){
            //显示自己头像
            UserUtils.setCurrentUserAvatar(mContext, imageView);
        }else{
            UserUtils.setUserAvatar(mContext, message.getFrom(), imageView);
        }
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, PersonalInfoActivity.class);
                intent.putExtra("username", message.getFrom());
                mContext.startActivity(intent);
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
        return  -1;
    }

    private View createViewByMessage(EMMessage message, int position) {
        return message.direct == EMMessage.Direct.RECEIVE ?
                mInflater.inflate(R.layout.row_received_message, null) :
                mInflater.inflate(R.layout.row_send_message, null);
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

                return true;
            }
        });

        if (message.direct == EMMessage.Direct.SEND) {
            switch (message.status) {
                case SUCCESS: // 发送成功
                    //holder.pb_sending.setVisibility(View.GONE);
                    //holder.staus_iv.setVisibility(View.GONE);
                    break;
                case FAIL: // 发送失败
                    //holder.pb_sending.setVisibility(View.GONE);
                    //holder.staus_iv.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS: // 发送中
                    //.pb_sending.setVisibility(View.VISIBLE);
                    //holder.staus_iv.setVisibility(View.GONE);
                    break;
                default:
                    // 发送消息
                    sendMsgInBackground(message, holder);
            }
        }
    }


    public void sendMsgInBackground(final EMMessage message, final ViewHolder holder) {
        //holder.staus_iv.setVisibility(View.GONE);
        //holder.pb_sending.setVisibility(View.VISIBLE);

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
                //notifyDataSetChanged();
            }
        });
    }


    public static class ViewHolder {
        TextView tv_timestamp;
        ImageView iv_avater;
        TextView tv_content;
        //ProgressBar pb_sending;
        TextView tv_userID;

    }

}
