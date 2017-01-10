package com.idotools.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by wuxiaojun on 16-10-2.
 */
public class ToastUtils {

    private static Toast mToast;

    public static void show(Context mContext, String content) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, content, Toast.LENGTH_SHORT);
        }

        mToast.setText(content);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

    public static void show(Context context, int resId) {
        if (mToast == null) {
            mToast = Toast.makeText(context, context.getResources().getString(resId), Toast.LENGTH_SHORT);
        }
        mToast.setText(context.getResources().getString(resId));
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

}
