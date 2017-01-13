package com.idotools.browser.utils;

import android.app.Activity;
import android.content.Context;


import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * Created by wuxiaojun on 16-11-7.
 */
public class DoAnalyticsManager {

    // banner 的点击数
    public static final String DOT_KEY_BANNER_CLICK = "key_banner_click";
    //  热门推荐上的原生广告
    public static final String DOT_KEY_MAIN_HOT_AD_CLICK = "key_main_hot_ad_click";
    //  热门推荐单独页面上的原生广告
    public static final String DOT_KEY_ACTIVITY_HOT_AD_CLICK = "key_activity_hot_ad_click";
    // 最新更新上的原生广告点击数
    public static final String DOT_KEY_UPDATE_AD_CLICK = "key_update_ad_click";
    // 观看漫画的ad点击数
    public static final String DOT_KEY_BANNER_AD_CLICK = "key_banner_ad_click";

    // 点击更多
    public static final String DOT_KEY_MORE_CLICK = "key_more_click";
    // 分享
    public static final String DOT_KEY_SHARE_CLICK = "key_share_click";
    // 快捷方式
    public static final String DOT_KEY_SHORTCUT_CLICK = "key_shortcut_click";
    // 夜间模式
    public static final String DOT_KEY_NIGHT_MODE_CLICK = "key_night_mode_click";




    public static void pageResume(Activity activity) {
//        MobclickAgent.onPageStart(activity.getClass().getSimpleName()); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(activity);          //统计时长
    }

    public static void pagePause(Activity activity) {
//        MobclickAgent.onPageEnd(activity.getClass().getSimpleName()); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(activity);
    }

    public static void event(Context context, String eventId) {
        MobclickAgent.onEvent(context, eventId);
    }

    /*public static void event(Activity activity, String eventKey, String eventValue) {
        MobclickAgent.onEvent(activity, eventKey, eventValue);
    }*/

    public static void event(Context context, String eventId, HashMap<String, String> map) {
        MobclickAgent.onEvent(context, eventId, map);
    }

}
