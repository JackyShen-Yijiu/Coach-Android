package com.blackcat.coach.tasks;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

import com.blackcat.coach.utils.SDKUtils;


public abstract class BaseTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
	protected int task_request_code;
	protected OnTaskFinishedListener<Progress, Result> onTaskRequestListener;
	private boolean isPublished;

    /**
	 * 执行带有线程池的AsyncTask
	 * @param task_request_code 请求码 默认为0
	 * @param params
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void exeOnCode(OnTaskFinishedListener<Progress, Result> onNetRequestListener, int task_request_code, Params...params){
		this.onTaskRequestListener = onNetRequestListener;
        this.task_request_code = task_request_code;

        if(SDKUtils.hasHoneycomb()){
			executeOnExecutor(TaskUtil.DUAL_THREAD_EXECUTOR, params);
		}else{
			execute(params);
		}
	}
	
	/**
	 * 执行带有线程池的AsyncTask
	 * @param params
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void exe(OnTaskFinishedListener<Progress, Result> onNetRequestListener, Params...params){
		exeOnCode(onNetRequestListener, 0, params);
	}

    @Override
    protected void onProgressUpdate(Progress... values) {
        super.onProgressUpdate(values);
        if (onTaskRequestListener != null && values != null && values.length > 0) {
            onTaskRequestListener.onProgressUpdate(task_request_code, values[0]);
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        if (onTaskRequestListener != null) {
            onTaskRequestListener.onNetRequest(task_request_code, result);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCancelled(Result result) {
        super.onCancelled(result);
        if (onTaskRequestListener != null) {
            onTaskRequestListener.onNetRequest(task_request_code, result);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (onTaskRequestListener != null) {
            onTaskRequestListener.onNetRequest(task_request_code, null);
        }
    }

    public OnTaskFinishedListener<Progress, Result> getOnNetRequestListener() {
        return onTaskRequestListener;
    }

    public void deliverResultAheadOfTime(Progress result) {
        if (!isPublished) {
            publishProgress(result);
        }
        isPublished = true;
    }
}
