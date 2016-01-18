package com.blackcat.coach.utils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JsonUtil {



	/**
	 * 把一个json字符串变成对象
	 * 
	 * @param json
	 * @param cls
	 * @return
	 */
	public static <T> T parseJsonToBean(String json, Class<T> cls) {
		Gson gson = new Gson();
		T t = null;
		try {
			t = gson.fromJson(json, cls);
		} catch (Exception e) {
		}
		return t;
	}



	/**
	 * 把json字符串变成集合 params: new TypeToken<List<yourbean>>(){}.getType(),
	 * 
	 * @param json
	 * @param type
	 *            new TypeToken<List<yourbean>>(){}.getType()
	 * @return
	 */
	public static List<?> parseJsonToList(String json, Type type) {
		Gson gson = new Gson();
		List<?> list = gson.fromJson(json, type);
		return list;
	}



}
