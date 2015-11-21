package com.blackcat.coach.utils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.blackcat.coach.BuildConfig;
import com.blackcat.coach.net.OkHttpStack;
import com.squareup.okhttp.OkHttpClient;

public class VolleyUtil {
	// http://arnab.ch/blog/2013/08/asynchronous-http-requests-in-android-using-volley/
	private volatile static RequestQueue sRequestQueue;
	private final static OkHttpClient client = new OkHttpClient();
	
	static {
		client.setCache(null);
	}
	
	/** get the single instance of RequestQueue **/
	public static RequestQueue getQueue(Context context) {
		if (sRequestQueue == null) {
			synchronized (VolleyUtil.class) {
				if (sRequestQueue == null) {
//					client.networkInterceptors().add(new StethoInterceptor());
					client.setCache(null);
					sRequestQueue = Volley.newRequestQueue(context.getApplicationContext(), new OkHttpStack(client));
					VolleyLog.DEBUG = BuildConfig.DEBUG;
//					sRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
				}
			}
		}
		return sRequestQueue;
	}
	
	public static OkHttpClient getOkHttpClient() {
		return VolleyUtil.client;
	}
}
