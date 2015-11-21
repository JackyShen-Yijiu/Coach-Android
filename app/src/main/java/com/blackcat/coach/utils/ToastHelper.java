package com.blackcat.coach.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

public class ToastHelper {
	
	private static ToastHelper mInstance;
	private Context mContext;
	private Toast mToast;
	
	private ToastHelper(Context context) {
		mContext = context;
	}
	
	public static ToastHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new ToastHelper(context);
		}

		return mInstance;
	}

	public void toast(int resourceId) {
		toast(resourceId, false);
	}
	
	public void toast(String msg) {
		toast(msg, false);
	}

	public void debug(String message) {
		if (mToast == null) {
	    	mToast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
	    	mToast.show();
		} else {
    		// mToast.cancel();

	        mToast.setText(message);
	        mToast.show();
		}
	}
	
	public void toast(String message, boolean forceShow) {
	    if (TextUtils.isEmpty(message)) {
	        return;
	    }
		if (mToast == null) {
	    	mToast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
	    	mToast.show();
		} else {
			if (forceShow || !mToast.getView().isShown()) {
				try {
					mToast.setText(message);
		        	mToast.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
			}
		}
	}
	
	public void toast(int resourceId, boolean forceShow) {
		if(mContext == null) {
			return;
		}
		String message = mContext.getString(resourceId);
		toast(message, forceShow);
	}
}
