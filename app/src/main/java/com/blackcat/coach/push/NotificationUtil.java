package com.blackcat.coach.push;

public class NotificationUtil {
    
//    public static int getNotificationIcon() {
//        boolean whiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
//        return whiteIcon ? R.drawable.notification_icon_silhouette : R.drawable.notification_icon;
//    }
//
//    public static void buildNotification(Context context, JPushMessage msg) {
//        // Create a Notification Builder
//        NotificationCompat.Builder builder =
//                new NotificationCompat.Builder(YYDoctorApplication.getInstance())
//                .setSmallIcon(getNotificationIcon())
//                .setContentTitle(msg.title)
//                .setTicker(msg.title)
//                .setAutoCancel(true)
//                .setWhen(System.currentTimeMillis())
//                .setContentText(msg.message);
//
//        // Define the Notification's Action
//        Intent intent = null;
//        if (msg.category == Category.CATEORDER) {
//            // 登录用户才能收到订单推送
//            if (Session.isUserInfoEmpty()) {
//                return;
//            }
//            intent = new Intent(context, DetailOrderActivity.class);
//            if (msg.payloadContent != null) {
//                intent.putExtra(Constants.ID, msg.payloadContent.orderId);
//            }
//        } else {
//            intent = new Intent(context, IndexActivity.class);
//        }
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//
//        // Because clicking the notification opens a new ("special") activity, there's
//        // no need to create an artificial back stack.
//        PendingIntent resultPendingIntent =
//            PendingIntent.getActivity(
//            context,
//            0,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT
//        );
//
//        // Set the Notification's Click Behavior
//        builder.setContentIntent(resultPendingIntent);
//
//        // Issue the Notification
//        // Sets an ID for the notification
//        int mNotificationId = 001;
//        if (msg.payloadContent != null && !TextUtils.isEmpty(msg.payloadContent.orderId)) {
//            try {
//                mNotificationId = Integer.parseInt(msg.payloadContent.orderId);
//            } catch(NumberFormatException e) {
//                e.printStackTrace();
//            }
//        }
//        // Gets an instance of the NotificationManager service
//        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        // Builds the notification and issues it.
//        mNotifyMgr.notify(mNotificationId, builder.build());
//    }
}
