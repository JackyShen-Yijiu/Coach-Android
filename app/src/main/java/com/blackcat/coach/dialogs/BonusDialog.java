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

import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.models.Session;

@SuppressLint("InflateParams")
public class BonusDialog extends Dialog {
    private Context context;
    private ImageView closeIv;
    private Button send;
    private TextView title;

    public BonusDialog(Context context) {
        super(context, R.style.dialog);
        this.context = context;
        create(this.context);
    }

    private void create(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_bonus, null);
        closeIv = (ImageView) view.findViewById(R.id.dialog_bonus_close_im);
        send = (Button) view.findViewById(R.id.dialog_bonus_send_btn);
        title = (TextView) view.findViewById(R.id.dialog_bonus_title_tv);

        CarCoachApplication app = CarCoachApplication.getInstance();
        title.setText(title.getText().toString() + "( " + Session.getSession().invitationcode + " )");
        closeIv.setOnClickListener(new MyOnClickListener());
        send.setOnClickListener(new MyOnClickListener());

        setContentView(view);
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.widthPixels * 0.9); // 宽度设置为屏幕宽度的0.9
        dialogWindow.setAttributes(p);
        setCanceledOnTouchOutside(true);// 设置点击Dialog外部任意区域关闭Dialog
    }

    private class MyOnClickListener implements android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.dialog_bonus_close_im:
                    dismiss();
                    break;
                case R.id.dialog_bonus_send_btn:
//                    context.sendBroadcast(new Intent(WalletActivity.class.getName()).putExtra("sendInvite", true));
                    dismiss();
                    break;
            }
        }

    }
}