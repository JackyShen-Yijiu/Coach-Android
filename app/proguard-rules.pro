# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/zou/Work/android/devtool/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

### 环信
-keep class com.easemob.** {*;}
-keep class com.blackcat.coach.easemob.** {*;}
-keep class org.jivesoftware.** {*;}
-keep class org.apache.** {*;}
-dontwarn  com.blackcat.coach.easemob.**
-dontwarn  com.easemob.**
#2.0.9后的不需要加下面这个keep
#-keep class org.xbill.DNS.** {*;}
#另外，demo中发送表情的时候使用到反射，需要keep SmileUtils
-keep class com.easemob.chatuidemo.utils.SmileUtils {*;}
#注意前面的包名，如果把这个类复制到自己的项目底下，比如放在com.example.utils底下，应该这么写(实际要去掉#)
#-keep class com.example.utils.SmileUtils {*;}
#如果使用easeui库，需要这么写

-keep class com.easemob.easeui.utils.EaseSmileUtils {*;}

#2.0.9后加入语音通话功能，如需使用此功能的api，加入以下keep
-dontwarn ch.imvs.**
-dontwarn org.slf4j.**
-keep class org.ice4j.** {*;}
-keep class net.java.sip.** {*;}
-keep class org.webrtc.voiceengine.** {*;}
-keep class org.bitlet.** {*;}
-keep class org.slf4j.** {*;}
-keep class ch.imvs.** {*;}



###Baidu map
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.**


# for v4, v7
-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }

-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }




#added for picasso and okhttp
-dontwarn com.squareup.okhttp.**
-dontwarn okio.**




#类型转换错误，因为我用的泛型，所以在调用某些方法的时候，会出现这种错误，后面在混淆配置文件加了一个过滤泛型的语句，如下。
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }
#这样就能过滤掉所有的annotation，否则也会抛出空指针异常。
-keepattributes *Annotation*



#facebook stetho
-keep class com.facebook.stetho.**{ *; }





#防止微信被混淆
-dontwarn com.tencent.mm.**
-keep class com.tencent.mm.sdk.openapi.WXMediaMessage { *;}
-keep class com.tencent.mm.sdk.openapi.**{*;}
-keep class com.tencent.mm.sdk.openapi.** implements com.tencent.mm.sdk.openapi.WXMediaMessage$IMediaObject {*;}
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage { *;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
-dontwarn com.youyi.mobile.doctorclient.wxapi.**
-keep class com.youyi.mobile.doctorclient.wxapi.** {*;}





#友盟混淆
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keep public class com.youyi.mobile.doctorclient.R$*{
	public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class com.umeng.fb.ui.ThreadView {
}




#TODO###################################################
#反射类不能进行混淆编译，需加入
-dontwarn com.blackcat.coach.activities.**
-dontwarn com.blackcat.coach.fragments.**
-dontwarn com.blackcat.coach.models.**
-dontwarn com.blackcat.coach.models.params.**
-dontwarn com.blackcat.coach.models.events.**
-dontwarn com.blackcat.coach.models.push.**

-keep class com.blackcat.coach.activities.** {
    public <methods>;
}

-keep class com.blackcat.coach.fragments.** {
    public <methods>;
}

-keep class com.blackcat.coach.models.** {*;}

-keep class com.blackcat.coach.models.params.** {*;}

-keep class com.blackcat.coach.models.events.** {*;}

-keep class com.blackcat.coach.models.push.** {*;}





# for volley
-keep class com.android.volley.** { *; }





# jpush
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }





#jar
#-libraryjars libs/android-async-http-1.4.8.jar
#-libraryjars libs/android-support-v4.jar
#-libraryjars libs/BaiduLBS_Android.jar
#-libraryjars libs/bolts-android-1.2.0.jar
#-libraryjars libs/easemobchat_2.2.2.jar

#-libraryjars libs/eventbus-2.4.0.jar
#-libraryjars libs/gson-2.3.1.jar
#-libraryjars libs/happy-dns-0.2.3.2.jar
#-libraryjars libs/jpush-sdk-release1.8.0.jar
#-libraryjars libs/library_nineoldandroids.jar
#-libraryjars libs/okhttp-2.3.0.jar
#-libraryjars libs/okhttp-urlconnection-2.3.0.jar
#-libraryjars libs/okio-1.3.0.jar
#-libraryjars libs/Parse-1.9.4.jar
#-libraryjars libs/photoview-1.2.4.jar

#-libraryjars libs/picasso-2.5.2.jar
#-libraryjars libs/qiniu-android-sdk-7.0.7.2.jar
#-libraryjars libs/umeng-analytics-v5.2.4.jar
#-libraryjars libs/universal-image-loader-1.9.4.jar




#bugtags
-keep class com.bugtags.library.** {*;}


#parse
-keep class com.parse.** {*;}
-dontwarn com.parse.**

#具体行号代码，要你在proguard设置
-keepattributes LineNumberTable,SourceFile