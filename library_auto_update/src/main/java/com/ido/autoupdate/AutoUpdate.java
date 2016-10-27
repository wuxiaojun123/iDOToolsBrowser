package com.ido.autoupdate;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.dot.analyticsone.AnalyticsOne;
import com.dot.autoupdater.AutoUpdater;
import com.dot.autoupdater.checker.UpdateResultCallback;
import com.dot.autoupdater.version.VersionProfile;

import java.util.Map;

/**
 * Created by cyly on 16/7/27.
 */
public class AutoUpdate {
    public AutoUpdate(){};

    public void update(Activity context){
        AutoUpdater.getInstance(context, new AutoUpdater.AnalyticsCallback() {
            @Override
            public void capture(Context context, String s) {
                AnalyticsOne.getInstance(context).capture(s);
            }

            @Override
            public void capture(Context context, String s, Map<String, String> map) {
                AnalyticsOne.getInstance(context).capture(s,map);
            }
        }).update(context);
    }

    public void forceUpdate(final Activity context){
        AutoUpdater.getInstance(context, new AutoUpdater.AnalyticsCallback() {
            @Override
            public void capture(Context context, String s) {
                AnalyticsOne.getInstance(context).capture(s);
            }

            @Override
            public void capture(Context context, String s, Map<String, String> map) {
                AnalyticsOne.getInstance(context).capture(s,map);
            }
        }).update(context,new UpdateResultCallback(){

            @Override
            public void foundUpdateAndShowIt(VersionProfile versionProfile) {
            }

            @Override
            public void foundUpdateAndDontShowIt(VersionProfile versionProfile) {
            }

            @Override
            public void returnUpToDate() {
                Toast.makeText(context, R.string.ido_no_update_version, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void returnUnpublished() {
                //服务器没有发布最新版本
                Toast.makeText(context, R.string.ido_no_update_version, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void returnError(int i) {
                //报错
                Toast.makeText(context,R.string.string_network_error,Toast.LENGTH_SHORT).show();
            }
        });
    }

}
