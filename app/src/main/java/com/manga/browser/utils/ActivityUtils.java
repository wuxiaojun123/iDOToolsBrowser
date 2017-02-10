package com.manga.browser.utils;

import com.manga.browser.activity.BaseActivity;
import com.manga.browser.activity.DmzjActivity;

import java.util.LinkedList;

/**
 * Created by wuxiaojun on 16-11-14.
 */
public class ActivityUtils {

    public static LinkedList<BaseActivity> activities = new LinkedList<>();

    public static void addActivitys(BaseActivity activity) {
        activities.add(activity);
    }

    public static void finishAll() {
        for (BaseActivity activity : activities) {
            activity.finish();
        }
    }

    /***
     * 删除某一个activity
     *
     * @param activity
     */
    public static void removeActivity(BaseActivity activity) {
        activities.remove(activity);
    }


    public static DmzjActivity getDmzjActivity() {
        for (BaseActivity activity : activities) {
            if (DmzjActivity.class.getSimpleName().equals(activity.getClass().getSimpleName())) {
                return (DmzjActivity) activity;
            }
        }
        return null;
    }


    public static int getSizie() {
        return activities.size();
    }

}