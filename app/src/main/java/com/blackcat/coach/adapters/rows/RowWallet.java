package com.blackcat.coach.adapters.rows;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blackcat.coach.R;
import com.blackcat.coach.adapters.BaseViewHolder;
import com.blackcat.coach.models.WalletRecord;
import com.blackcat.coach.utils.UTC2LOC;

/**
 * Created by zou on 15/11/17.
 */
public class RowWallet {
    public static BaseViewHolder createViewHolder(ViewGroup parent, Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_wallet, parent, false);
        Holder holder = new Holder(view);
        holder.rootView = view.findViewById(R.id.rootView);
        holder.tvName = (TextView) view.findViewById(R.id.tv_wallet_name);
        holder.tvTime = (TextView) view.findViewById(R.id.tv_wallet_time);
        holder.tvMoney = (TextView) view.findViewById(R.id.tv_wallet_money);
        return holder;
    }

    public static  <T> void bindViewHolder(final Activity activity,
                                           BaseViewHolder holder, final int position, final T info) {
        final Holder viewHolder = (Holder) holder;
        WalletRecord item = (WalletRecord) info;
        viewHolder.tvTime.setText(UTC2LOC.instance.getDate(item.createtime, "yyyy/mm/dd"));
        viewHolder.tvMoney.setText(String.valueOf(item.amount));
        switch (item.type) {
            case WalletType.TYPE_1:
                viewHolder.tvName.setText(WalletType.TYPE_STR_1);
                break;
            case WalletType.TYPE_2:
                viewHolder.tvName.setText(WalletType.TYPE_STR_2);
                break;
            case WalletType.TYPE_3:
                viewHolder.tvName.setText(WalletType.TYPE_STR_3);
                break;
            default:
                break;
        }
    }

    static class WalletType {
        //"type": 3, 类型 //1 注册发放 2 邀请好友发放 3 购买商
        public static final int TYPE_1 = 1;
        public static final int TYPE_2 = 2;
        public static final int TYPE_3 = 3;

        public static final String TYPE_STR_1 = "注册发放";
        public static final String TYPE_STR_2 = "邀请好友发放";
        public static final String TYPE_STR_3 = "购买商";
    }

    static class Holder extends BaseViewHolder {

        public TextView tvTime, tvName, tvMoney;
        public View rootView;

        public Holder(View itemView) {
            super(itemView);
        }
    }
}
