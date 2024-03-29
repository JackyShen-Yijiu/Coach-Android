package com.blackcat.coach.dialogs;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackcat.coach.R;

/**
 * 左红又白
 * @author sun  2016-1-29 下午2:24:37
 *
 */
@SuppressLint("InflateParams")
public class ApplySuccessDialog extends Dialog implements
		View.OnClickListener {

	private Context context;
	private ImageView image;
	private TextView content;
	private Button confirmBtn, cancelBtn;

	public ApplySuccessDialog(Context context) {
		super(context, R.style.dialog);
		this.context = context;
		create(this.context);
	}

	private void create(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_apply_success, null);
		image = (ImageView) view.findViewById(R.id.dialog_no_login_im);
		content = (TextView) view.findViewById(R.id.dialog_no_login_content);
		confirmBtn = (Button) view
				.findViewById(R.id.dialog_no_login_confirm_btn);
		cancelBtn = (Button) view.findViewById(R.id.dialog_no_login_cancel_btn);
//		setTextAndImage();
		setContentView(view);
		DisplayMetrics d = context.getResources().getDisplayMetrics();
		Window dialogWindow = getWindow();
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (d.widthPixels * 0.9); // 宽度设置为屏幕宽度的0.9
		dialogWindow.setAttributes(p);
		setCanceledOnTouchOutside(true);// 设置点击Dialog外部任意区域关闭Dialog

		confirmBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
	}

	public void setTextAndImage(String left,String top,String right,int drawable) {
		image.setBackgroundResource(drawable);
		content.setText(top);
		confirmBtn.setText(left);
		cancelBtn.setText(right);
	}
	
	/**
	 * 确认事件
	 * @param listener
	 */
	
	public void setListener(View.OnClickListener listener){
		confirmBtn.setOnClickListener(listener);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_no_login_confirm_btn:
			dismiss();
			break;
		case R.id.dialog_no_login_cancel_btn:
			
//			context.sendBroadcast(new Intent(MyAppointmentActivity.class
//					.getName()).putExtra("isApplyExam", true));
			dismiss();
			break;
		}
	}
}
