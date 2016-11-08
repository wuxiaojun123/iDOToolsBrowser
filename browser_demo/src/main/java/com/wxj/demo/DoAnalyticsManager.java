package com.wxj.demo;

import android.app.Activity;


import java.util.Map;

/**
 * Created by wuxiaojun on 16-11-7.
 */
public class DoAnalyticsManager {

    public static void capture(String eventStr) {
        App.analytics.capture(eventStr);
    }

    public static void pageResume(Activity activity, String pagename) {
        App.analytics.pageResume(activity, pagename);
        App.analytics.sessionResume(activity);
    }

    public static void pagePause(Activity activity, String pagename) {
        App.analytics.pagePause(activity, pagename);
        App.analytics.sessionPause(activity);
    }

    public static void pageResumeFragment(Activity activity,String name) {
        App.analytics.pageResume(activity, name);
    }

    public static void pagePauseFragment(Activity activity,String name) {
        App.analytics.pagePause(activity, name);
    }

    public static void capture(String eventStr, Map<String, String> map) {
        App.analytics.capture(eventStr, map);
    }

}
