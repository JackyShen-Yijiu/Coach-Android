package com.blackcat.coach.adapters.rows;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackcat.coach.R;
import com.blackcat.coach.adapters.BaseViewHolder;
import com.blackcat.coach.imgs.UILHelper;
import com.blackcat.coach.models.Comment;

/**
 * Created by zou on 15/10/17.
 */
public class RowComment {
    public static BaseViewHolder createViewHolder(ViewGroup parent, Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_comment, parent, false);
        Holder holder = new Holder(view);
        holder.rootView = view.findViewById(R.id.rootView);
        holder.ivAvatar = (ImageView) view.findViewById(R.id.iv_cmt_avatar);
        holder.tvName = (TextView) view.findViewById(R.id.tv_cmt_name);
        holder.tvContent = (TextView) view.findViewById(R.id.tv_cmt_content);
        holder.tvTime = (TextView) view.findViewById(R.id.tv_cmt_time);
        return holder;
    }

    public static <T> void bindViewHolder(final Activity activity,
                                          BaseViewHolder holder, final int position, final T info) {
        final Holder viewHolder = (Holder) holder;
        Comment item = (Comment) info;
        if (item.coachid != null) {
            viewHolder.tvName.setText(item.coachid.name);
            if (item.coachid.headportrait != null && !TextUtils.isEmpty(item.coachid.headportrait.originalpic)) {
                //TODO
//                PicassoUtil.loadImage(activity, viewHolder.ivAvatar, item.coachid.headportrait.originalpic, R.dimen.comment_avatar, R.dimen.comment_avatar, false, R.mipmap.ic_avatar_small);
                UILHelper.loadImage(viewHolder.ivAvatar, item.coachid.headportrait.originalpic, false, R.mipmap.ic_avatar_small);

            } else {
                viewHolder.ivAvatar.setImageResource(R.mipmap.ic_avatar_small);
            }
        } else {
            viewHolder.tvName.setText("");
            viewHolder.ivAvatar.setImageResource(R.mipmap.ic_avatar_small);
        }
        if (item.coachcomment != null) {
            viewHolder.tvContent.setText(item.coachcomment.commentcontent);
        } else {
            viewHolder.tvContent.setText("");
        }
    }

    static class Holder extends BaseViewHolder {

        private View rootView;
        private ImageView ivAvatar;
        private TextView tvName, tvContent, tvTime;
        public Holder(View itemView) {
            super(itemView);
        }
    }
}
