package com.blackcat.coach.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.blackcat.coach.activities.AboutActivity;
import com.blackcat.coach.activities.DetailReservationActivity;
import com.blackcat.coach.activities.IndexActivity;
import com.blackcat.coach.activities.OrderMsgActivity;
import com.blackcat.coach.activities.WalletActivity;
import com.blackcat.coach.models.Reservation;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.GsonUtils;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import cn.jpush.android.api.JPushInterface;


/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JpushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "[JpushReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
        
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[JpushReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...
                        
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[JpushReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            //TODO
//            processCustomMessage(context, bundle);
        
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[JpushReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[JpushReceiver] 接收到推送下来的通知的ID: " + notifactionId);
            
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Log.d(TAG, "[JpushReceiver] 用户点击打开了通知:  " + extra);
            //打开自定义的Activity
            processNotificationMessage(context, extra);
//            toSystemMsg(context);
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[JpushReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
            
        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[JpushReceiver]" + intent.getAction() +" connected state change to "+connected);
        } else {
            Log.d(TAG, "[JpushReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    private void toSystemMsg(Context context){
        Intent  intent = new Intent(context, OrderMsgActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 自定义消息
     */
    private void toCustoMsg(){
//        Notification myNotify = new Notification();
//        myNotify.icon = R.drawable.message;
//        myNotify.tickerText = "TickerText:您有新短消息，请注意查收！";
//        myNotify.when = System.currentTimeMillis();
//        myNotify.flags = Notification.FLAG_NO_CLEAR;// 不能够自动清除
//        RemoteViews rv = new RemoteViews(getPackageName(),
//                R.layout.my_notification);
//        rv.setTextViewText(R.id.text_content, "hello wrold!");
//        myNotify.contentView = rv;
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 1,
//                intent, 1);
//        myNotify.contentIntent = contentIntent;
//        manager.notify(NOTIFICATION_FLAG, myNotify);
    }

    private void processNotificationMessage(Context context, String extra) {
        if (TextUtils.isEmpty(extra)) {
            return;
        }

//        JsonElement rootElement = new JsonParser().parse(new JsonReader(new StringReader(extra)));
//        if (rootElement != null) {
//            JsonObject jobj = rootElement.getAsJsonObject();
//            if (jobj != null) {
//                String type = jobj.get(NetConstants.KEY_TYPE).getAsString();
//                if (MsgType.MSG_AUDIT_FAILED.equals(type)) {
//
//                } else if (MsgType.MSG_AUDIT_SUCC.equals(type)) {
//
//                } else if (MsgType.MSG_CMT_NEW.equals(type)) {
//
//                } else if (MsgType.MSG_NEW_VERSION.equals(type)) {
//
//                } else if (MsgType.MSG_RESERVATION_CANCEL.equals(type)) {
//
//                } else if (MsgType.MSG_RESERVATION_NEW.equals(type)) {
//
//                } else if (MsgType.MSG_WALLET_UPDATE.equals(type)) {
//
//                } else {
//                    //TOOD
//                }
//            }
//        }
        Type type = new TypeToken<JpushExtra<JpushExtraData>>() {}.getType();
        try {
            JpushExtra<JpushExtraData> jpushExtra = GsonUtils.fromJson(extra, type);
            if (jpushExtra != null && !TextUtils.isEmpty(jpushExtra.type)) {
                Intent intent = null;
                switch (jpushExtra.type) {
                    case MsgType.MSG_AUDIT_FAILED:
                        intent = new Intent(context, IndexActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        break;
                    case MsgType.MSG_AUDIT_SUCC:
                        intent = new Intent(context, IndexActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        break;
                    case MsgType.MSG_RESERVATION_NEW:
                    case MsgType.MSG_RESERVATION_CANCEL:
                    case MsgType.MSG_CMT_NEW:
                        intent = new Intent(context, DetailReservationActivity.class);
                        Reservation reservation = new Reservation();
                        reservation._id = jpushExtra.data.reservationid;
                        intent.putExtra(Constants.DETAIL, reservation);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        break;
                    case MsgType.MSG_NEW_VERSION:
                        intent = new Intent(context, AboutActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        break;
                    case MsgType.MSG_WALLET_UPDATE:
                        intent = new Intent(context, WalletActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            }else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } 
            else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }
    
    private void processCustomMessage(Context context, Bundle bundle) {
        //TODO
        try {
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            if (TextUtils.isEmpty(extras)) {
                return;
            }
//            extras = BaseUtils.decodeUnicode(BaseUtils.decodeUnicode(extras));
//            JPushMessageWrapper msg = GsonUtils.fromJson(extras, JPushMessageWrapper.class);
//            if (msg != null && msg.txt != null) {
//                NotificationUtil.buildNotification(context, msg.txt);
//            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void showNotifycation(){

    }

}
