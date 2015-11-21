package com.blackcat.coach.models;
import com.android.volley.Request;
import com.blackcat.coach.easemob.domain.*;
import com.blackcat.coach.easemob.domain.User;
import com.blackcat.coach.easemob.utils.DateUtils;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContact;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;

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
    public  Message (EMConversation conversation) {
        this.userName = conversation.getUserName();
        this.nickName = this.userName;
        this.unReadCount = conversation.getUnreadMsgCount();
        EMMessage message = conversation.getLastMessage();
        TextMessageBody textMessageBody = (TextMessageBody) message.getBody();
        this.messageDesc = textMessageBody.getMessage().toString();
        this.time = message.getMsgTime();
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
        return  nickName;
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
}
