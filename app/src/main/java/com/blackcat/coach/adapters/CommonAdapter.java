
package com.blackcat.coach.adapters;

import android.app.Activity;
import android.view.ViewGroup;

import com.blackcat.coach.adapters.rows.RowComment;
import com.blackcat.coach.adapters.rows.RowCourse;
import com.blackcat.coach.adapters.rows.RowDrivingSchool;
import com.blackcat.coach.adapters.rows.RowMessage;
import com.blackcat.coach.adapters.rows.RowNone;
import com.blackcat.coach.adapters.rows.RowProduct;
import com.blackcat.coach.adapters.rows.RowReservation;
import com.blackcat.coach.adapters.rows.RowSchedule;
import com.blackcat.coach.adapters.rows.RowStudent;
import com.blackcat.coach.adapters.rows.RowWallet;

import java.util.ArrayList;
import java.util.List;


public class CommonAdapter<T> extends AbstractAdapter<BaseViewHolder> {
    private Activity mActivity;
    private int mAdapterType;
    private List<T> mList = new ArrayList<T>();

    public static class AdapterType {
    	public static final int TYPE_ADAPTER_RESERVATION = 1;
    	public static final int TYPE_ADAPTER_SCHEDULE = 2;
    	public static final int TYPE_ADAPTER_MSG = 3;
		public static final int TYPE_ADAPTER_RESERVATION_OP = 4;
		public static final int TYPE_ADAPTER_DRIVING = 5;
		public static final int TYPE_ADAPTER_STUDENT = 6;
		public static final int TYPE_ADAPTER_COMMENT = 7;
		public static final int TYPE_ADAPTER_AVATAR = 8;
        public static final int TYPE_ADAPTER_COURSE = 9;
        public static final int TYPE_ADAPTER_Class = 10;
        public static final int TYPE_ADAPTER_WALLET = 11;
        public static final int TYPE_ADAPTER_PRODUCT = 12;
    }
    
    public List<T> getList() {
        return mList;
    }

    public void setList(List<T> list) {
        this.mList = list;
    }
    
    public void appendList(List<T> list) {
    	if(mList == null) {
    		this.mList = list;
    	} else {
    		mList.addAll(list);
    	}
    }

    public CommonAdapter(Activity activity, List<T> list, int adapterType) {
        this.mActivity = activity;
        this.mList = list;
        this.mAdapterType = adapterType;
    }

    @Override
    public int getCount() {
    	if(mList != null) {
    		return mList.size();
    	}
        return 0;
    }

    @Override
    public T getItem(int position) {
    	if(mList != null) {
    		return mList.get(position);
    	}
        return null;
    }
    
    public T getLastItem() {
    	if (mList != null && mList.size() > 0) {
    		return mList.get(getCount() - 1);
    	}
    	return null;
    }
    
    public void insert(T item, int to) {
		if(mList != null) {
			mList.add(to, item);
			notifyDataSetChanged();
		}
	}
	
    public void remove(T item) {
		if (mList != null) {
			mList.remove(item);
			notifyDataSetChanged();
		}
	}
    
    public T remove(int index) {
		if (mList != null && index >= 0 && index < mList.size()) {
			T t = mList.remove(index);
			notifyDataSetChanged();
			return t;
		}
		return null;
	}

    @Override
    protected BaseViewHolder onCreateViewHolder(ViewGroup parent, int itemViewType) {
        switch (mAdapterType) {
            case AdapterType.TYPE_ADAPTER_RESERVATION:
            	return RowReservation.createViewHolder(parent, parent.getContext());
            case AdapterType.TYPE_ADAPTER_MSG:
                return RowMessage.createViewHolder(parent, parent.getContext());
            case AdapterType.TYPE_ADAPTER_SCHEDULE:
                return RowSchedule.createViewHolder(parent, parent.getContext());
            case AdapterType.TYPE_ADAPTER_COURSE:
                return RowCourse.createViewHolder(parent, parent.getContext());
            case AdapterType.TYPE_ADAPTER_STUDENT:
                return RowStudent.createViewHolder(parent, parent.getContext());
            case AdapterType.TYPE_ADAPTER_DRIVING:
                return RowDrivingSchool.createViewHolder(parent, parent.getContext());
            case AdapterType.TYPE_ADAPTER_COMMENT:
                return RowComment.createViewHolder(parent, parent.getContext());
            case AdapterType.TYPE_ADAPTER_WALLET:
                return RowWallet.createViewHolder(parent, parent.getContext());
            case AdapterType.TYPE_ADAPTER_PRODUCT:
                return RowProduct.createViewHolder(parent, parent.getContext());
            default:
                return RowNone.createViewHolder(parent, parent.getContext());
        }
    }

    @Override
    protected void onBindViewHolder(BaseViewHolder holder, int position, int itemViewType) {
        switch (mAdapterType) {
            case AdapterType.TYPE_ADAPTER_RESERVATION:
            	RowReservation.bindViewHolder(mActivity, holder, position, mList.get(position));
                break;
            case AdapterType.TYPE_ADAPTER_MSG:
                RowMessage.bindViewHolder(mActivity, holder, position, mList.get(position));
                break;
            case AdapterType.TYPE_ADAPTER_SCHEDULE:
                RowSchedule.bindViewHolder(mActivity, holder, position, mList.get(position));
                break;
            case AdapterType.TYPE_ADAPTER_COURSE:
                RowCourse.bindViewHolder(mActivity, holder, position, mList.get(position));
                break;
            case AdapterType.TYPE_ADAPTER_STUDENT:
                RowStudent.bindViewHolder(mActivity, holder, position, mList.get(position));
                break;
            case AdapterType.TYPE_ADAPTER_DRIVING:
                RowDrivingSchool.bindViewHolder(mActivity, holder, position, mList.get(position));
                break;
            case AdapterType.TYPE_ADAPTER_COMMENT:
                RowComment.bindViewHolder(mActivity, holder, position, mList.get(position));
                break;
            case AdapterType.TYPE_ADAPTER_WALLET:
                RowWallet.bindViewHolder(mActivity, holder, position, mList.get(position));
                break;
            case AdapterType.TYPE_ADAPTER_PRODUCT:
                RowProduct.bindViewHolder(mActivity, holder, position, mList.get(position));
                break;
            default:
                RowNone.bindViewHolder(holder);
                break;
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
//        switch (position) {
//            case RowType.TYPE_JUHE:
//                return 0;
//            case RowType.TYPE_SHENDU:
//                return 1;
//            case RowType.TYPE_GAME:
//                return 2;
//            default:
//                break;
//        }
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }
}