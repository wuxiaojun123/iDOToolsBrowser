package com.base.browser.utils;

import android.app.Activity;
import android.content.Context;


import java.util.HashMap;

/**
 * Created by wuxiaojun on 16-11-7.
 */
public class DoAnalyticsManager {

    // 分享
    public static final String DOT_KEY_SHARE_CLICK = "key_share_click";
    // 快捷方式
    public static final String DOT_KEY_SHORTCUT_CLICK = "key_shortcut_click";
    // 夜间模式
    public static final String DOT_KEY_NIGHT_MODE_CLICK = "key_night_mode_click";
    //收藏
    public static final String DOT_KEY_RECORDS_CLICK = "key_records_click";
    //历史记录
    public static final String DOT_KEY_HISTORY_CLICK = "key_history_click";


    public  static void pageResume(Activity activity){
    }


    public static void pagePause(Activity activity) {
    }
    public static void pageFragmentResume(String name, Context context) {
    }
    public static void pageFragmentPause(String name, Context context) {
    }

    public static void event(Context context, String eventId) {
    }

    public static void event(Context context, String eventId, HashMap<String, String> map) {
    }

}
