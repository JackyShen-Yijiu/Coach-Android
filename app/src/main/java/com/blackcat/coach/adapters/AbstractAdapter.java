
package com.blackcat.coach.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class AbstractAdapter<V extends BaseViewHolder> extends BaseAdapter {

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        V holder;
        int itemViewType = getItemViewType(position);
        if (convertView == null) {
            holder = onCreateViewHolder(parent, itemViewType);
            convertView = holder.itemView;
            convertView.setTag(holder);
        } else {
            holder = (V) convertView.getTag();
        }
        onBindViewHolder(holder, position, itemViewType);
        return convertView;
    }

    protected abstract V onCreateViewHolder(ViewGroup parent, int viewType);

    protected abstract void onBindViewHolder(V holder, int position, int itemViewType);
}
