package com.manga.browser.manager.webview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;

import com.manga.browser.App;
import com.manga.browser.activity.MainActivity;
import com.manga.browser.minterface.OnPageStartedListener;
import com.manga.browser.minterface.OnReceivedErrorListener;
import com.manga.browser.view.BrowserWebView;

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

    private boolean isShowTitleAndBottom = true;

    private class CustomTouchListener implements View.OnTouchListener {

        private int downY;
        private int distance;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mWebViewLongClickListener.downX = (int) event.getX();
            mWebViewLongClickListener.downY = (int) event.getY();

            int y = (int) event.getY();
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    downY = y;

                    break;
                case MotionEvent.ACTION_UP:
                    distance = downY - y;
                    if (distance > 0 && isShowTitleAndBottom) {
                        //隐藏title和bottom----当滚动停止的时候才能执行隐藏动画
                        ((MainActivity) mContext).hideTitleAndBottom();
                        isShowTitleAndBottom = false;
                    } else if (distance < 0 && !isShowTitleAndBottom) {
                        //显示title和bottom
                        ((MainActivity) mContext).showTitleAndBottom();
                        isShowTitleAndBottom = true;
                    }
                    distance = 0;
                    break;
            }

            mGestureDetector.onTouchEvent(event);
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

        settings.setUseWideViewPort(true);
//        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setLoadWithOverviewMode(true);

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
        return App.get(mContext).getDir(fileName, Context.MODE_PRIVATE).getPath();
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

    private void checkWebviewIsNull() {
        if (mWebView == null) {
            return;
        }
    }

    public BrowserWebView getWebView() {
        return mWebView;
    }

}
