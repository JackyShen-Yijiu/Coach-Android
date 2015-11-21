package com.blackcat.coach.imgs;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class UILHelper {
    public static void loadImage(ImageView view, String url,
            boolean isCircle, int placeHolderRes) {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565);
        if (placeHolderRes > 0) {
            builder.showImageOnLoading(placeHolderRes)
            .showImageForEmptyUri(placeHolderRes)
            .showImageOnFail(placeHolderRes);
        }
        DisplayImageOptions options;
        if (isCircle) {
            options = builder.displayer(new RoundedBitmapDisplayer(1000)).build();
        } else {
            options = builder.build();
        }
        ImageLoader.getInstance().displayImage(url, view, options);
    }
}
