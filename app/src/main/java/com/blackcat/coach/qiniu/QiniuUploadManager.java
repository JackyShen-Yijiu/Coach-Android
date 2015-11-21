package com.blackcat.coach.qiniu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.models.HeadPortraint;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.NetConstants;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.tasks.GetBitmapTask;
import com.blackcat.coach.tasks.OnTaskFinishedListener;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.GsonUtils;
import com.blackcat.coach.utils.VolleyUtil;
import com.google.gson.reflect.TypeToken;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 七牛云上传图片
 */
public class QiniuUploadManager {

	
	private QiniuUploadManager() {
	}
	
	public interface QiniuUploadListener{
        public void onSucess();
        public void onError();
    }

	private static QiniuUploadManager mQiniuUploadManager = null;

	private UploadManager uploadManager = new UploadManager();
	
    
	public static QiniuUploadManager getInstance() {
		if (mQiniuUploadManager == null) {
			mQiniuUploadManager = new QiniuUploadManager();
		}
		return mQiniuUploadManager;
	}

	public void submitImage(Context context, Uri uri, QiniuUploadListener listener) {
	    System.out.println("submitImage");
	    saveFile2Disk(context, uri, listener);
	}
	
	public static boolean saveBitmapToJpegFile(Bitmap bitmap, File file, int quality) {
        try {
            FileOutputStream fileOutStr = new FileOutputStream(file);
            BufferedOutputStream bufOutStr = new BufferedOutputStream(fileOutStr);
            bitmap.compress(CompressFormat.JPEG, quality, bufOutStr);
            bufOutStr.flush();
            bufOutStr.close();
        } catch (Exception exception) {
            return false;
        }
        return true;
    }
	
    public void saveFile2Disk(final Context context, Uri uri, final QiniuUploadListener listener) {
        System.out.println("saveFile2Disk");
        new GetBitmapTask(context, uri).exeOnCode(new OnTaskFinishedListener<Void, Bitmap>() {

            @Override
            public void onNetRequest(int task_request_code, Bitmap bmp) {
                if (bmp != null) {
                    File file = PhotoUtil.getPhotoFile(context);
                    boolean res = saveBitmapToJpegFile(bmp, file, 65);
                    if (res) {
                        getQiniuToken(context, file.getAbsolutePath(), listener);
                    } else {
                        listener.onError();
                    }
                } else {
                    listener.onError();
                }
            }

        }, 0);
    }

	public void uploadImage(final Context context, String filePath, String token, final QiniuUploadListener listener) {
	    final String fileUrlUUID = getFileUrlUUID();
        uploadManager.put(filePath, fileUrlUUID, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject response) {
                        // 上传成功
                        if (info != null && info.statusCode == 200) {
                            System.out.println("uploadImage success");
                            String url = NetConstants.DOMAIN_IMGS + fileUrlUUID;
                            if (Session.getSession().headportrait == null) {
                                HeadPortraint head = new HeadPortraint();
                                head.originalpic = url;
                                Session.getSession().headportrait = head;
                            } else {
                                Session.getSession().headportrait.originalpic = url;
                            }
                            updateCoachInfoRequest(context, listener);
                        } else {
                            System.out.println("uploadImage error");
                            listener.onError();
                        }
                    }
                }, null);
	}
	
	/**
	 * 生成远程文件路径（全局唯一）
	 * 
	 * @return
	 */
	private String getFileUrlUUID() {
		return Session.getSession().coachid + System.currentTimeMillis();
	}
	
	// 获取七牛token
    private void getQiniuToken(final Context context, final String filePath, final QiniuUploadListener listener) {
        String url = null;
        URI uri = URIUtil.getQiniuToken();
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        GsonIgnoreCacheHeadersRequest<Result<String>> request = new GsonIgnoreCacheHeadersRequest<Result<String>>(
                url, new TypeToken<Result<String>>(){}.getType(), null,
                new Listener<Result<String>>() {
                    
                    @Override
                    public void onResponse(Result<String> response) {
                        if (response != null && response.data != null) {
                            String token = (String) response.data;
                            System.out.println("getQiniuToken: " + token);
                            if (!TextUtils.isEmpty(token)) {
                                uploadImage(context, filePath, token, listener);
                            } else {
                                listener.onError();
                            }
                        } else {
                            listener.onError();
                        }
                    }
                }, 
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        listener.onError();
                        if (Constants.DEBUG) {
                            VolleyLog.e("Error: ", arg0.getMessage());
                        }
                    }
                });
        // 请求加上Tag,用于取消请求
        request.setTag(context);
        request.setShouldCache(false);
        VolleyUtil.getQueue(context).add(request);
    }

    public void updateCoachInfoRequest(Context context, final QiniuUploadListener listener) {
        Type type = new TypeToken<Result>() {}.getType();
        URI uri = URIUtil.updateCoachInfo();
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Map map = new HashMap<>();
        map.put(NetConstants.KEY_AUTHORIZATION, Session.getToken());

        GsonIgnoreCacheHeadersRequest<Result> request = new GsonIgnoreCacheHeadersRequest<Result>(
                Request.Method.POST, url, GsonUtils.toJson(Session.getSession()), type, map,
                new Response.Listener<Result>() {
                    @Override
                    public void onResponse(Result response) {
                        if (response != null) {
                            if (response.type == Result.RESULT_OK) {
                                listener.onSucess();
                            } else if (!TextUtils.isEmpty(response.msg)) {
                                listener.onError();
                            } else {
                                listener.onError();
                            }
                        } else {
                            listener.onError();
                        }
                        if (Constants.DEBUG) {
                            VolleyLog.v("Response:%n %s", response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        listener.onError();
                    }
                });
        // 请求加上Tag,用于取消请求
        request.setTag(context);
        request.setShouldCache(false);
        VolleyUtil.getQueue(context).add(request);
    }
}
