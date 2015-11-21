package com.blackcat.coach.net;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.blackcat.coach.utils.GsonUtils;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Volley adapter for JSON requests that will be parsed into Java objects by
 * Gson. See: https://gist.github.com/ficusk/5474673
 */
public class GsonRequest<T> extends Request<T> {
	/** Charset for request. */
    private static final String PROTOCOL_CHARSET = "utf-8";

    /** Content type for request. */
    private static final String PROTOCOL_CONTENT_TYPE =
        String.format("application/json; charset=%s", PROTOCOL_CHARSET);
    
	private final Type type;
	private final Map<String, String> headers;
	private final Listener<T> listener;

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
	public GsonRequest(String url, Type type, Map<String, String> headers, Listener<T> listener, ErrorListener errorListener) {
		super(Method.GET, url, errorListener);
		this.type = type;
		this.headers = headers;
		this.listener = listener;
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
	public GsonRequest(int method, String url, Type type, Map<String, String> headers, Listener<T> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		this.type = type;
		this.headers = headers;
		this.listener = listener;
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
//			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			String json = new String(response.data, PROTOCOL_CHARSET);
			T parseObject = GsonUtils.fromJson(json, type);
			return Response.success(parseObject, HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}
}