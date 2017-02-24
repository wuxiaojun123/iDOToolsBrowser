package com.manga.country.utils;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by wuxiaojun on 16-12-2.
 */

public class GooglePlayUtils {

    public static void openGooglePlayByPkg(Context context, String pkgName) throws ActivityNotFoundException{
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

    /***
     * @param context
     * @param uriString market://details?id=com.xxx.xxx
     */
    public static void openGooglePlayByUri(Context context, String uriString) throws ActivityNotFoundException{
        if(!TextUtils.isEmpty(uriString)){
            Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
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
                //截取包名
                String pkgName = uriString.substring(uriString.indexOf("=")+1);
                if(!TextUtils.isEmpty(pkgName)){
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + pkgName));
                    webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(webIntent);
                }
            }
        }
    }


}
