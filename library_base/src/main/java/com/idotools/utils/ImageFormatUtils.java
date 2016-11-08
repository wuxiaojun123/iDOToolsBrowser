package com.idotools.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.DrawableUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by wuxiaojun on 16-10-28.
 */
public class ImageFormatUtils {

    /***
     * 将bitmap转成byte数组
     *
     * @param bitmap
     * @return
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        boolean result = bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        if (result) {
            bytes = bos.toByteArray();
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }

    /***
     * @param bitmap
     * @return
     */
    public static Drawable bitmap2Drawable(Bitmap bitmap, Resources resources) {
        if (bitmap != null) {
            Drawable drawable = new BitmapDrawable(resources, bitmap);
            if (drawable != null) {
                return drawable;
            }
        }
        return null;
    }

    /**
     * 将bytes转成drawable
     *
     * @param bytes
     * @return
     */
    public static Drawable bytes2Drawable(byte[] bytes, Resources resources) {
        if (bytes != null && bytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            if (bitmap != null) {
                return bitmap2Drawable(bitmap, resources);
            }
        }
        return null;
    }


}
