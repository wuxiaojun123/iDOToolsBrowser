package com.idotools.browser.pink.book.utils;

import android.content.Context;
import android.content.Intent;

/**
 * Created by wuxiaojun on 16-10-2.
 */
public class ShareUtils {


    public static void shareText(Context context,String content){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, content);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

}
