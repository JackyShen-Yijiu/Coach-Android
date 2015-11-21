//package com.blackcat.coach.imgs;
//
//import com.squareup.picasso.Transformation;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapShader;
//import android.graphics.Canvas;
//import android.graphics.Matrix;
//import android.graphics.Paint;
//
//public class CropCircleTransformation implements Transformation {
//
//    @Override
//    public Bitmap transform(Bitmap source) {
//        int size = Math.min(source.getWidth(), source.getHeight());
//
//        int width = (source.getWidth() - size) / 2;
//        int height = (source.getHeight() - size) / 2;
//
////      Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
//        Bitmap output = Bitmap.createBitmap(size, size, source.getConfig());
//
//        Canvas canvas = new Canvas(output);
//        Paint paint = new Paint();
//        BitmapShader shader = new BitmapShader(source, BitmapShader.TileMode.CLAMP,
//                BitmapShader.TileMode.CLAMP);
//        if (width != 0 || height != 0) {
//            // source isn't square, move viewport to centre
//            Matrix matrix = new Matrix();
//            matrix.setTranslate(-width, -height);
//            shader.setLocalMatrix(matrix);
//        }
//        paint.setShader(shader);
//        paint.setAntiAlias(true);
//
//        float r = size / 2f;
//        canvas.drawCircle(r, r, r, paint);
//
//        source.recycle();
//
//        return output;
//    }
//
//    @Override
//    public String key() {
//        return "CropCircleTransformation()";
//    }
//}