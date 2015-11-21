package com.blackcat.coach.tasks;

/**
 * Task请求回调类
 * @param <T>
 */
public abstract class OnTaskFinishedListener<Progress, Result> {
	/**
	 * 网络请求监听
	 * @param task_request_code 请求码 用于区分不同的请求 默认值为0 
	 * @param result 请求返回值
	 *
	 */
	public abstract void onNetRequest(int task_request_code, Result result);

	public void onProgressUpdate(int task_request_code, Progress progress) {}
}
