package com.blackcat.coach.net;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.blackcat.coach.utils.GsonUtils;
import com.blackcat.coach.utils.LogUtil;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Volley adapter for JSON requests that will be parsed into Java objects by
 * Gson. See: https://gist.github.com/ficusk/5474673
 */
public class GsonIgnoreCacheHeadersRequest<T> extends BaseRequest<T> {
	/** Charset for request. */
    private static final String PROTOCOL_CHARSET = "utf-8";

    /** Content type for request. */
    private static final String PROTOCOL_CONTENT_TYPE =
        String.format("application/json; charset=%s", PROTOCOL_CHARSET);
    
	private final Type type;
	private final Map<String, String> headers;
	private final Listener<T> listener;

	private final String mRequestBody;

	/**
	 * Make a GET request and return a parsed object from JSON. Assumes
	 * {@link Method#GET}.
	 * 
	 * @param url
	 *            URL of the request to make
	 * @param clazz
	 *            Relevant class object, for Gson's reflection
	 * @param headers
	 *            Map of request headers
	 */
	public GsonIgnoreCacheHeadersRequest(String url, Type type, Map<String, String> headers, Listener<T> listener, ErrorListener errorListener) {
		super(Method.GET, url, errorListener);
		this.type = type;
		this.headers = headers;
		this.listener = listener;
		this.mRequestBody = null;
	}

	/**
	 * Like the other, but allows you to specify which {@link Method} you want.
	 * 
	 * @param method
	 * @param url
	 * @param clazz
	 * @param headers
	 * @param listener
	 * @param errorListener
	 */
	public GsonIgnoreCacheHeadersRequest(int method, String url,  String requestBody, Type type, Map<String, String> headers, Listener<T> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		this.type = type;
		this.headers = headers;
		this.listener = listener;
		this.mRequestBody = requestBody;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return headers != null ? headers : super.getHeaders();
	}

	@Override
	protected void deliverResponse(T response) {
		listener.onResponse(response);
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			LogUtil.print("Result--json-->" + json);
			T parseObject = GsonUtils.fromJson(json, type);
			return Response.success(parseObject, parseIgnoreCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}
	
	/**
	 * Extracts a {@link Cache.Entry} from a {@link NetworkResponse}.
	 * Cache-control headers are ignored. SoftTtl == 3 mins, ttl == 24 hours.
	 * @param response The network response to parse headers from
	 * @return a cache entry for the given response, or null if the response is not cacheable.
	 */
	public static Cache.Entry parseIgnoreCacheHeaders(NetworkResponse response) {
	    long now = System.currentTimeMillis();

	    Map<String, String> headers = response.headers;
	    long serverDate = 0;
	    String serverEtag = null;
	    String headerValue;

	    headerValue = headers.get("Date");
	    if (headerValue != null) {
	        serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
	    }

	    serverEtag = headers.get("ETag");

	    // in 3 minutes cache will be hit, but also refreshed on background
//	    final long cacheHitButRefreshed = 3 * 60 * 1000;
	    final long cacheHitButRefreshed = 0;
	    
	    // in 24 hours this cache entry expires completely
	    final long cacheExpired = 5 * 24 * 60 * 60 * 1000; 
//	    final long cacheExpired = 60 * 1000;
	    final long softExpire = now + cacheHitButRefreshed;
	    final long ttl = now + cacheExpired;

	    Cache.Entry entry = new Cache.Entry();
	    entry.data = response.data;
	    entry.etag = serverEtag;
	    entry.softTtl = softExpire;
	    entry.ttl = ttl;
	    entry.serverDate = serverDate;
	    entry.responseHeaders = headers;

	    return entry;
	}


	/**
	 * @deprecated Use {@link #getBodyContentType()}.
	 */
	@Override
	public String getPostBodyContentType() {
		return getBodyContentType();
	}

	/**
	 * @deprecated Use {@link #getBody()}.
	 */
	@Override
	public byte[] getPostBody() {
		return getBody();
	}

	@Override
	public String getBodyContentType() {
		return PROTOCOL_CONTENT_TYPE;
	}

	@Override
	public byte[] getBody() {
		try {
			return mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
		} catch (UnsupportedEncodingException uee) {
			VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
					mRequestBody, PROTOCOL_CHARSET);
			return null;
		}
	}
}