package com.idotools.browser.utils;

import android.app.Activity;


import com.idotools.browser.App;
import com.umeng.analytics.MobclickAgent;

import java.util.Map;

/**
 * Created by wuxiaojun on 16-11-7.
 */
public class DoAnalyticsManager {

    public static void pageResume(Activity activity) {
//        MobclickAgent.onPageStart(activity.getClass().getSimpleName()); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(activity);          //统计时长
    }

    public static void pagePause(Activity activity) {
//        MobclickAgent.onPageEnd(activity.getClass().getSimpleName()); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(activity);
    }

    public static void pageEvent(Activity activity, String eventId) {
        MobclickAgent.onEvent(activity, eventId);
    }

}
