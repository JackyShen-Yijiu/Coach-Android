package com.blackcat.coach.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blackcat.coach.R;
import com.blackcat.coach.activities.DetailProductActivity;
import com.blackcat.coach.imgs.UILHelper;
import com.blackcat.coach.models.Product;
import com.blackcat.coach.utils.Constants;

public class ImageFragment extends BaseFragment {

	private Product mBanner;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_image, container, false);
		ImageView img = (ImageView) rootView;
		img.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, DetailProductActivity.class);
				intent.putExtra(Constants.DETAIL, mBanner);
				startActivity(intent);
			}
			
		});
		
		if (mBanner != null && !TextUtils.isEmpty(mBanner.productimg)) {
			UILHelper.loadImage(img, mBanner.productimg,false, R.mipmap.ic_banner_default);
		}
		return rootView;
	}
	
	public static ImageFragment newInstance(Product banner) {
		ImageFragment fragment = new ImageFragment();
		fragment.mBanner = banner;
        return fragment;
    }
}
