package com.idotools.browser.utils;

import android.content.Context;
import android.content.Intent;

/**
 * Created by wuxiaojun on 16-10-2.
 */
public class ShareUtils {


    public static void shareText(Context context){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "超赞的浏览器，快到极致");
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

}
