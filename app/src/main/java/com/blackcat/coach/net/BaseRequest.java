package com.blackcat.coach.net;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;


// http://stackoverflow.com/questions/17094718/android-volley-timeout
public class BaseRequest<T> extends Request<T> {

    public BaseRequest(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);
        setRetryPolicy(new DefaultRetryPolicy(NetConstants.VOLLEY_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void deliverResponse(T response) {
		// TODO Auto-generated method stub
	}
}