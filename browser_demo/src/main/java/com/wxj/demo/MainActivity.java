package com.wxj.demo;


import android.*;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.View;

import com.base.browser.utils.Constant;
import com.dot.analyticsone.AnalyticsOne;
import com.idotools.utils.LogUtils;
import com.idotools.utils.ToastUtils;
import com.igexin.sdk.PushManager;

import java.util.List;
import java.util.jar.*;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends com.base.browser.activity.MainActivity implements EasyPermissions.PermissionCallbacks {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PushManager.getInstance().initialize(this.getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyPicture";

                ToastClickView.show(getApplicationContext(), path);
            }
        });

    }

    @AfterPermissionGranted(123)
    private void requestPermission() {
        //这里需要请求权限
        if (EasyPermissions.hasPermissions(this, android.Manifest.permission.READ_PHONE_STATE)) {
            App.analytics = AnalyticsOne.getInstance(this);
        } else {
            EasyPermissions.requestPermissions(this, "当前app运行需要这个权限", 123, android.Manifest.permission.READ_PHONE_STATE);
        }
    }

    @Override
    public void initPopupwindow() {

    }

    @Override
    protected void onResume() {
        super.onResume();
//        requestPermission();
        if (App.analytics != null)
            DoAnalyticsManager.pageResume(this, "MainActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (App.analytics != null)
            DoAnalyticsManager.pagePause(this, "MainActivity");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        LogUtils.e("执行onPermissionsGranted");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        LogUtils.e("执行onPermissionsDenied方法");
    }

}
