package com.gp.browser.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.List;

/**
 * Created by wuxiaojun on 16-12-2.
 */

public class GooglePlayUtils {

    public static void openGooglePlay(Context context,String pkgName){
        Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="
                + pkgName));
        boolean marketFound = false;

        // find all applications able to handle our rateIntent
        final List<ResolveInfo> otherApps = context.getApplicationContext().getPackageManager()
                .queryIntentActivities(rateIntent, 0);
        for (ResolveInfo otherApp : otherApps) {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName.equals("com.android.vending")) {

                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName, otherAppActivity.name);
                rateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                rateIntent.setComponent(componentName);
                rateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(rateIntent);
                marketFound = true;

                break;
            }
        }

        // if GP not present on device, open web browser
        if (!marketFound) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + pkgName));
            webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(webIntent);
        }
    }

}
