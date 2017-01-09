package com.gp.browser.manager.webview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.MailTo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gp.browser.App;
import com.gp.browser.R;
import com.gp.browser.bean.CartoonDetailsBean;
import com.gp.browser.minterface.OnPageStartedListener;
import com.gp.browser.minterface.OnReceivedErrorListener;
import com.gp.browser.sqlite.SqliteManager;
import com.gp.browser.utils.Constant;
import com.gp.browser.utils.IntentUtils;
import com.gp.browser.utils.Utils;
import com.gp.utils.ImageFormatUtils;
import com.gp.utils.LogUtils;

import java.net.URISyntaxException;

/**
 * Created by wuxiaojun on 16-10-8.
 */
public class BrowserWebClient extends WebViewClient {

    private OnPageStartedListener mOnPageStartedListener;
    private OnReceivedErrorListener mOnReceivedErrorListener;
    private Activity mActivity;
    private IntentUtils mIntentUtils;
    private SqliteManager mSqliteManager;

    public BrowserWebClient(Activity mActivity) {
        this.mActivity = mActivity;
        this.mIntentUtils = new IntentUtils(mActivity);
        mSqliteManager = new SqliteManager(mActivity.getApplicationContext());
    }


    /*@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//        if (view != null && request != null)
//            System.out.println("执行webResourceResponse" + view.getUrl() + "====" + request.getUrl());
        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
//        System.out.println("过时的执行webResourceResponse" + view.getUrl() + "====" + url);
        return super.shouldInterceptRequest(view, url);
    }*/

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return shouldOverrideLoading(view, request.getUrl().toString()) || super.shouldOverrideUrlLoading(view, request);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean shouldOverrideUrlLoading(@NonNull WebView view, @NonNull String url) {
        boolean result = shouldOverrideLoading(view, url) || super.shouldOverrideUrlLoading(view, url);
        return result;
    }

    private boolean shouldOverrideLoading(WebView view, String url) {
        if (url.startsWith(Constant.ABOUT)) {
            // If this is an about page, immediately load, we don't need to leave the app
            return false;
        }
        if (isMailOrIntent(url, view) || mIntentUtils.startActivityForUrl(view, url)) {
            // If it was a mailto: link, or an intent, or could be launched elsewhere, do that
            return true;
        }
        // If none of those instances was true, revert back to the old way of loading
        return false;
    }

    private boolean isMailOrIntent(@NonNull String url, @NonNull WebView view) {
        if (url.startsWith("mailto:")) {
            MailTo mailTo = MailTo.parse(url);
            Intent i = Utils.newEmailIntent(mailTo.getTo(), mailTo.getSubject(),
                    mailTo.getBody(), mailTo.getCc());
            mActivity.startActivity(i);
            view.reload();
            return true;
        } else if (url.startsWith("intent://")) {
            Intent intent;
            try {
                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            } catch (URISyntaxException ignored) {
                intent = null;
            }
            if (intent != null) {
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setComponent(null);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    intent.setSelector(null);
                }
                try {
                    mActivity.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    LogUtils.e("ActivityNotFoundException");
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        //保存cookie
        CookieManager cookieManager = CookieManager.getInstance();
        App.cookie = cookieManager.getCookie(url);
        App.referer = url;
        //保存到数据库中
        saveOrUpdateHistory(view.getTitle(), url, view.getFavicon());
        //执行隐藏div代码
        /***
         * javascript:
         var adDiv0= document.getElementById('div-gpt-ad-1443488528500-4');
         if(adDiv0 != null)
         adDiv0.parentNode.removeChild(adDiv0);
         */

    }



    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (mOnPageStartedListener != null) {
            mOnPageStartedListener.onPageStarted(url);
        }
    }

    /***
     * 保存到数据库中
     *
     * @param title
     * @param url
     */
    private void saveOrUpdateHistory(String title, String url, Bitmap bitmap) {
        try {
            boolean isExist = mSqliteManager.selectByUrl(url);
            if (!isExist) {
                //将bitmap转换成byte数组,判断bytes为null的情况
                if (bitmap == null) {
                    bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.mipmap.icon);
                }
                byte[] bytes = ImageFormatUtils.bitmap2Bytes(bitmap);
                mSqliteManager.insert(new CartoonDetailsBean(title, bytes, url));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        /*if (mOnReceivedErrorListener != null) {
            LogUtils.e("网络出错啦onReceivedError方法"+error.getErrorCode());
            mOnReceivedErrorListener.onReceivedError(view, error.getErrorCode());
        }
        super.onReceivedError(view, request, error);*/
        onReceivedError(view, error.getErrorCode(), error.getDescription().toString(), request.getUrl().toString());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        if (mOnReceivedErrorListener != null) {
            LogUtils.e("网络出错啦!!!!!onReceivedError过时的方法" + errorCode);
            mOnReceivedErrorListener.onReceivedError(view, errorCode);
        }
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    public void setmOnPageStartedListener(OnPageStartedListener mOnPageStartedListener) {
        this.mOnPageStartedListener = mOnPageStartedListener;
    }

    public void setmOnReceivedErrorListener(OnReceivedErrorListener mOnReceivedErrorListener) {
        this.mOnReceivedErrorListener = mOnReceivedErrorListener;
    }

}
