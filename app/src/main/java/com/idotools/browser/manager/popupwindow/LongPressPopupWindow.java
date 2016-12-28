package com.idotools.browser.manager.popupwindow;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.idotools.browser.App;
import com.idotools.browser.R;
import com.idotools.utils.MetricsUtils;
import com.idotools.utils.ToastUtils;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by wuxiaojun on 16-12-22.
 */

public class LongPressPopupWindow extends PopupWindow implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    @BindView(R.id.id_tv_download_image)
    TextView tv_download_image;

    public String imgUrl;
    private Context mContext;
    private View contentView;
    private String saveImagePath;

    public BroadcastReceiver mReceiverDownload;
    private long refernece;
    private DownloadManager dManager;

    public View snakeView;


    public LongPressPopupWindow(Context context) {
        this.mContext = context;
        this.contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_long_press_saveimage, null);
        ButterKnife.bind(this, contentView);
        saveImagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyPicture";
        dManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        registerReceiverDownload();
        init();
    }

    private void init() {
        setWidth(MetricsUtils.dipToPx(250));
        setHeight(MetricsUtils.dipToPx(50));
        setContentView(contentView);
        setOutsideTouchable(true);//点击其他地方会消食
        setBackgroundDrawable(new ColorDrawable());
        setFocusable(true);
    }


    @OnClick({R.id.id_tv_download_image})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_tv_download_image:
                //下载图片----采用retrofit来下载图片
                if (!TextUtils.isEmpty(imgUrl)) {
                    try {
                        requestPermission();
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtils.show(mContext, R.string.string_download_fail);
                    }
                    dismiss();
                }

                break;
        }
    }


    @AfterPermissionGranted(123)
    private void requestPermission() {
        if (EasyPermissions.hasPermissions(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            downloadImageBySystem(imgUrl);
        } else {
            EasyPermissions.requestPermissions(mContext, mContext.getString(R.string.string_request_write_external_storage_permission), 123, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }


    private void downloadImageBySystem(String dowloadPath) {
        Uri uri = Uri.parse(dowloadPath);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        if (App.cookie != null) {
            request.addRequestHeader("Cookie", App.cookie);
        }
        if (App.referer != null) {
            request.addRequestHeader("Referer", App.referer);
        }
        // 设置下载路径和文件名
        File file = new File(saveImagePath);
        //如果file不存在,就创建这个file
        if (!file.exists()) {
            file.mkdir();
        }
        final String fileName = dowloadPath.substring(dowloadPath.lastIndexOf("/") + 1, dowloadPath.length());
        request.setDestinationInExternalPublicDir(saveImagePath, fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // 设置为可被媒体扫描器找到
        request.allowScanningByMediaScanner();
        // 设置为可见和可管理
        request.setVisibleInDownloadsUi(true);
        // 获取此次下载的ID
        refernece = dManager.enqueue(request);
    }


    private void registerReceiverDownload() {
        // 注册广播接收器，当下载完成时自动安装
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        mReceiverDownload = new BroadcastReceiver() {

            public void onReceive(final Context context, Intent intent) {
                try {
                    long myDwonloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    if (refernece == myDwonloadID) {
                        //弹出查看  路径：dManager.getUriForDownloadedFile(refernece).getPath()

                        Snackbar snackbar = Snackbar.make(snakeView, R.string.string_finish, Snackbar.LENGTH_LONG).setAction(R.string.string_look, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setAction(android.content.Intent.ACTION_VIEW);
                                intent.setDataAndType(dManager.getUriForDownloadedFile(refernece), "image/*");
                                context.startActivity(intent);
                            }
                        });
                        snackbar.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.show(context, R.string.string_download_fail);
                }
            }

        };
        mContext.registerReceiver(mReceiverDownload, filter);
    }

    public void unRegisterReceiverDownload() {
        if (mReceiverDownload != null) {
            mContext.unregisterReceiver(mReceiverDownload);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        downloadImageBySystem(imgUrl);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadImageBySystem(imgUrl);
        }
    }


}
