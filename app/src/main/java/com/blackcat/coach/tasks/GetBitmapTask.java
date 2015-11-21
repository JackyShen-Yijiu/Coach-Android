package com.blackcat.coach.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.io.IOException;

public class GetBitmapTask extends BaseTask<Uri, Void, Bitmap> {

    private Uri mUri;
    private Context mContext;
    
    public final static int MAX_WIDTH = 720;
    public final static int MAX_HEIGHT = 1080;
    
    public GetBitmapTask(Context context, Uri uri) {
        mUri = uri;
        mContext = context;
    }
    
    
    @Override
    protected Bitmap doInBackground(Uri... params) {
        Bitmap bmp = null;
        try {
//            bmp = Picasso.with(mContext).load(mUri).memoryPolicy(MemoryPolicy.NO_CACHE)
//                    .networkPolicy(NetworkPolicy.NO_CACHE)
//                    .resize(MAX_WIDTH, MAX_HEIGHT).centerInside().get();
            DisplayImageOptions options;
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(false)
                    .cacheOnDisk(false)
                    .considerExifParams(true).build();
            bmp = ImageLoader.getInstance().loadImageSync(mUri.toString(), new ImageSize(MAX_WIDTH, MAX_HEIGHT), options);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bmp;
    }

}
