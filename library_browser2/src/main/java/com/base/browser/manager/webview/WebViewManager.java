package com.base.browser.manager.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;

import com.base.browser.R;
import com.base.browser.activity.MainActivity;
import com.base.browser.manager.dialog.AlertDialog;
import com.base.browser.minterface.OnPageStartedListener;
import com.base.browser.minterface.OnReceivedErrorListener;
import com.base.browser.view.BrowserWebView;
import com.idotools.utils.MetricsUtils;
import com.idotools.utils.ToastUtils;

/**
 * Created by wuxiaojun on 16-10-8.
 */
public class WebViewManager {

    private Context mContext;
    private BrowserWebView mWebView;
    private GestureDetector mGestureDetector;
    private static final int API = android.os.Build.VERSION.SDK_INT;


    public WebViewManager(Activity mActivity) {
        this.mContext = mActivity;
        mWebView = new BrowserWebView(mActivity);
        initWebView(mActivity);
    }

    private void initWebView(Activity mActivity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            mWebView.setId(View.generateViewId());
        }
        mWebView.setDrawingCacheBackgroundColor(Color.WHITE);
        mWebView.setFocusableInTouchMode(true);
        mWebView.setFocusable(true);
        mWebView.setDrawingCacheEnabled(false);
        mWebView.setWillNotCacheDrawing(true);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            //noinspection deprecation
            mWebView.setAnimationCacheEnabled(false);
            //noinspection deprecation
            mWebView.setAlwaysDrawnWithCacheEnabled(false);
        }
        mWebView.setBackgroundColor(Color.WHITE);

        mWebView.setScrollbarFadingEnabled(true);
        mWebView.setSaveEnabled(true);
        mWebView.setNetworkAvailable(true);
        BrowserWebChromeClient mWebViewChromeClient = new BrowserWebChromeClient((WebviewInteface) mActivity);
        mWebView.setWebChromeClient(mWebViewChromeClient);
        BrowserWebClient mBrowserWebClient = new BrowserWebClient(mActivity);
        mBrowserWebClient.setmOnPageStartedListener((OnPageStartedListener) mActivity);
        mBrowserWebClient.setmOnReceivedErrorListener((OnReceivedErrorListener) mActivity);
        mWebView.setWebViewClient(mBrowserWebClient);
        mWebView.addJavascriptInterface(new BrowserJsInterface(mActivity.getApplicationContext()), "BrowserJsInterface");
        mWebView.setDownloadListener(new CustomDownloadListener());
        //长按事件
        mWebViewLongClickListener = new WebViewLongClickListener(mContext);
        mWebView.setOnLongClickListener(mWebViewLongClickListener);
        //触摸事件
        mGestureDetector = new GestureDetector(mContext, new CustomGestureListener());
        mWebView.setOnTouchListener(new CustomTouchListener());
        initializeSettings();
    }

    /***
     * 反注册广播
     */
    public void unRegisterReceiverDownload() {
        mWebViewLongClickListener.unRegisterReceiverDownload();
    }

    public WebViewLongClickListener mWebViewLongClickListener;

    private class CustomGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public void onLongPress(MotionEvent event) {
            mWebViewLongClickListener.downX = (int) event.getRawX();
            mWebViewLongClickListener.downY = (int) event.getRawY();
        }

    }

    private static final int SCROLL_UP_THRESHOLD = MetricsUtils.dipToPx(5);
    private boolean titleVisible = true;//true 表示标题显示

    private class CustomTouchListener implements View.OnTouchListener {

        float mLocation;
        float mY;
        int mAction;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(@Nullable View view, @NonNull MotionEvent arg1) {
            mWebViewLongClickListener.downX = (int) arg1.getX();
            mWebViewLongClickListener.downY = (int) arg1.getY();
            /*if (view == null)
                return false;
            if (!view.hasFocus()) {
                view.requestFocus();
            }
            mAction = arg1.getAction();
            mY = arg1.getY();
            if (mAction == MotionEvent.ACTION_DOWN) {
                mLocation = mY;
            } else if (mAction == MotionEvent.ACTION_UP) {
                final float distance = (mY - mLocation);
                LogUtils.e("距离是:" + distance + "--scrollY=" + mWebView.getScrollY());
                if (!titleVisible && view.getScrollY() == 0) {//禁止swipeRefresh滑动
                    mWebView.requestDisallowInterceptTouchEvent(true);
                }
                if (distance > SCROLL_UP_THRESHOLD && view.getScrollY() < SCROLL_UP_THRESHOLD) {
                    //显示头部和底部
                    ((MainActivity) mContext).showTitleAndBottom();
                    titleVisible = true;
                } else if (distance < -SCROLL_UP_THRESHOLD) {
                    //隐藏头部和底部
                    ((MainActivity) mContext).hideTitleAndBottom();
                    titleVisible = false;
                }
                mLocation = 0;
            }*/
            mGestureDetector.onTouchEvent(arg1);
            return false;
        }

    }

    private class CustomDownloadListener implements android.webkit.DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            mContext.startActivity(intent);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initializeSettings() {
        if (mWebView == null) {
            return;
        }
        final WebSettings settings = mWebView.getSettings();
        if (API < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //noinspection deprecation
            settings.setAppCacheMaxSize(Long.MAX_VALUE);
        }
        if (API < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //noinspection deprecation
            settings.setEnableSmoothTransition(true);
        }
        if (API > Build.VERSION_CODES.JELLY_BEAN) {
            settings.setMediaPlaybackRequiresUserGesture(true);
        }
        if (API >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        } else if (API >= Build.VERSION_CODES.LOLLIPOP) {
            // We're in Incognito mode, reject
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        }
        //设置缓存
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDatabaseEnabled(true);
        settings.setAppCachePath(getFilePath("appCache"));
        if (API < Build.VERSION_CODES.KITKAT) {
            //noinspection deprecation
            settings.setDatabasePath(getFilePath("databaseCache"));
        }

        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccess(true);
        if (API >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowFileAccessFromFileURLs(false);
            settings.setAllowUniversalAccessFromFileURLs(false);
        }
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
    }

    /***
     * 缓存路径
     *
     * @param fileName
     * @return
     */
    private String getFilePath(String fileName) {
        return mContext.getApplicationContext().getDir(fileName, Context.MODE_PRIVATE).getPath();
    }

    /***
     * 获取当前页面的标题
     *
     * @return
     */
    public String getCurrentTitle() {
        if (mWebView != null) {
            return mWebView.getTitle();
        }
        return null;
    }

    /***
     * 获取当前页面的url
     *
     * @return
     */
    public String getCurrentUrl() {
        if (mWebView != null)
            return mWebView.getUrl();
        return null;
    }

    /***
     * 清除缓存
     *
     * @return
     */
    public void cleanCache() {
        new AlertDialog((MainActivity) mContext).builder()
                .setMsg(R.string.string_confirm_clean_all_cache)
                .setPositiveButton(R.string.string_confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mWebView != null) {
                            mWebView.clearCache(true);
                            ToastUtils.show(mContext, mContext.getResources().getString(R.string.string_clean_success));
                        }
                    }
                }).setNegativeButton(R.string.string_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).show();
    }

    private void checkWebviewIsNull() {
        if (mWebView == null) {
            return;
        }
    }

    public BrowserWebView getWebView() {
        return mWebView;
    }

}
