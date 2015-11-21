//package com.blackcat.coach.imgs;
//
//import android.content.res.Resources;
//import android.widget.ImageView;
//
//import com.blackcat.coach.CarCoachApplication;
//import com.blackcat.coach.R;
//import com.squareup.picasso.Picasso;
//import com.squareup.picasso.RequestCreator;
//
//import java.io.File;
//
///**
// * Picasso工具类.
// */
//public class PicassoUtil {
//    /**
//     * 注意，使用默认Picasso实例时，Picasso缓存为如下，如果使用了自定义缓存目录，则需要修改
//     */
//    private static final String PICASSO_CACHE = "picasso-cache";
//
//    public static void clearCache() {
//        final File cache = new File(CarCoachApplication.getInstance().getCacheDir(), PICASSO_CACHE);
//        if (cache.exists()) {
//            deleteFolder(cache);
//        }
//    }
//
//    public static File getPicassoCacheDir() {
//        return new File(CarCoachApplication.getInstance().getCacheDir(), PICASSO_CACHE);
//    }
//
//    private static void deleteFolder(File fileOrDirectory) {
//        if (fileOrDirectory.isDirectory()) {
//            for (File child : fileOrDirectory.listFiles())
//                deleteFolder(child);
//        }
//        fileOrDirectory.delete();
//    }
//
//    public static void loadImageInPx(Object tag, ImageView view, String url, int widthPx, int heightPx, boolean isCircle, int placeHolderRes) {
////        if (TextUtils.isEmpty(url)) {
////            return;
////        }
//        RequestCreator req = Picasso.with(CarCoachApplication.getInstance())
//                .load(url)
//                .resize(widthPx, heightPx)
//                .centerCrop();
//        if (placeHolderRes != 0) {
//            req.placeholder(placeHolderRes);
//        }
//        if (tag != null) {
//            req.tag(tag);
//        }
//
//        if (isCircle) {
//            req.transform(new CropCircleTransformation());
//        }
//        req.into(view);
//    }
//
//    public static void loadImage(Object tag, ImageView view, String url, int widthDimenRes, int heightDimenRes, boolean isCircle, int placeHolderRes) {
//        Resources res = CarCoachApplication.getInstance().getResources();
//        int width = res.getDimensionPixelSize(widthDimenRes);
//        int height = res.getDimensionPixelSize(heightDimenRes);
//        String affix = res.getString(R.string.img_url, width, height, 65);
//        url = url.concat(affix);
//        loadImageInPx(tag, view, url, width, height, isCircle, placeHolderRes);
//    }
//
//}