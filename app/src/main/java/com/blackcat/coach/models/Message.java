package com.blackcat.coach.models;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.blackcat.coach.R;
import com.blackcat.coach.easemob.Constant;
import com.blackcat.coach.easemob.domain.*;
import com.blackcat.coach.easemob.domain.User;
import com.blackcat.coach.easemob.utils.DateUtils;
import com.blackcat.coach.imgs.UILHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContact;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.NormalFileMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;

import java.util.Date;

/**
 * Created by zou on 15/10/9.
 */
public class Message {
    private String userName;
    private String nickName;
    private String messageDesc;
    private long   time;
    private int    unReadCount;
    private String avatarUrl;

    private Context mContext;

    public Message() {
        this.userName = "15510521096";
        this.nickName = "15510521096";
        this.messageDesc ="hi,你好！";
        time = 0;
        unReadCount = 0;
    }
    public Message(String nameId) {
        this.userName = nameId;
        this.nickName = nameId;
        this.messageDesc ="hi,你好！";
        time = 0;
        unReadCount = 0;
    }
    public  Message (EMConversation conversation, User user, Context context) {
        mContext = context;
        if (user != null) {
            this.userName = user.getUsername();
            this.nickName = user.getNick();
            this.avatarUrl = user.getAvatar();
        }else {
            this.userName = conversation.getUserName();
        }
        this.unReadCount = conversation.getUnreadMsgCount();
        EMMessage message = conversation.getLastMessage();
        this.messageDesc =makeShowMessage(message);
        this.time = message.getMsgTime();

        //针对收到消息时，界面不在前台
       if (this.nickName == null || TextUtils.isEmpty(this.nickName)) {
           try {
               if (message.direct == EMMessage.Direct.RECEIVE) {
                   this.nickName = message.getStringAttribute(Constant.MESSAGE_NAME_ATTR_KEY);
               }
           }catch (Exception e) {
               this.nickName = this.userName;
           }
       }
        if (this.avatarUrl == null || TextUtils.isEmpty(this.avatarUrl)) {
            try {
                if (message.direct == EMMessage.Direct.RECEIVE) {
                    this.avatarUrl = message.getStringAttribute(Constant.MESSAGE_AVATAR_ATTR_KEY);
                }
            }catch (Exception e) {
            }
        }
    }


    private String makeShowMessage(EMMessage msg) {

        String showMessage = null;
        switch (msg.getType()) {
            case TXT:
                TextMessageBody textMessageBody = (TextMessageBody) msg.getBody();
                showMessage = textMessageBody.getMessage().toString();
                break;
            case IMAGE:
                showMessage = mContext.getResources().getString(R.string.str_picture);
                break;
            case FILE:
                NormalFileMessageBody fileMessageBody = (NormalFileMessageBody) msg.getBody();
                showMessage = fileMessageBody.getFileName();
                break;
            case VOICE:
                showMessage = mContext.getResources().getString(R.string.str_voice);
                break;
        }
        return showMessage;
    }
    public Message(EMContact contact) {

    }


    public long getTime() {
        return time;
    }

    public String getUserName() {
        return userName;
    }

    public  String getShowName() {
        return nickName;
    }

    public String getMessageDesc() {
        return  messageDesc;
    }

    public int getMsgCount() {
        return  unReadCount;
    }

    public String getShowTime() {
       return DateUtils.getTimestampString(new Date(time));
    }

    public String getAvatarUrl() {
        return  avatarUrl;
    }
}
