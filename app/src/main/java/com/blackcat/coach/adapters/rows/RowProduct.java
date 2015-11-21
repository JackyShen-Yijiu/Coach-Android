package com.blackcat.coach.adapters.rows;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blackcat.coach.R;
import com.blackcat.coach.activities.DetailProductActivity;
import com.blackcat.coach.adapters.BaseViewHolder;
import com.blackcat.coach.imgs.UILHelper;
import com.blackcat.coach.models.Product;
import com.blackcat.coach.utils.Constants;

/**
 * Created by zou on 15/11/18.
 */
public class RowProduct {
    public static BaseViewHolder createViewHolder(ViewGroup parent, Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_product, parent, false);
        Holder holder = new Holder(view);
        holder.rootView = view.findViewById(R.id.rootView);
        holder.ivImage = (ImageView)view.findViewById(R.id.iv_image);
        return holder;
    }

    public static <T> void bindViewHolder(final Activity activity,
                                          BaseViewHolder holder, final int position, final T info) {
        final Holder viewHolder = (Holder) holder;
        final Product oro = (Product)info;
        if (oro != null && !TextUtils.isEmpty(oro.productimg)) {
            UILHelper.loadImage(viewHolder.ivImage, oro.productimg, false, R.mipmap.ic_banner_default);
        } else {
            viewHolder.ivImage.setImageResource(R.mipmap.ic_banner_default);
        }
        viewHolder.rootView.setOnClickListener(new MyOnClickListener(activity, oro));
    }

    static class Holder extends BaseViewHolder {

        private View rootView;
        private ImageView ivImage;
        public Holder(View itemView) {
            super(itemView);
        }
    }

    static class MyOnClickListener implements View.OnClickListener {
        private Activity activity;
        private Product product;
        public MyOnClickListener(Activity act, Product pro) {
            this.activity = act;
            this.product = pro;
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(activity, DetailProductActivity.class);
            intent.putExtra(Constants.DETAIL, product);
            activity.startActivity(intent);
        }
    }
}
