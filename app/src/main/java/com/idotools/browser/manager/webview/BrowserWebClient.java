package com.idotools.browser.manager.webview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.idotools.browser.bean.CartoonDetailsBean;
import com.idotools.browser.minterface.OnPageStartedListener;
import com.idotools.browser.minterface.OnReceivedErrorListener;
import com.idotools.browser.sqlite.SqliteManager;
import com.idotools.browser.utils.Constant;
import com.idotools.browser.utils.IntentUtils;
import com.idotools.browser.utils.Utils;
import com.idotools.utils.ImageFormatUtils;
import com.idotools.utils.LogUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URISyntaxException;
import java.util.Map;

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return shouldOverrideLoading(view, request.getUrl().toString()) || super.shouldOverrideUrlLoading(view, request);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean shouldOverrideUrlLoading(@NonNull WebView view, @NonNull String url) {
        return shouldOverrideLoading(view, url) || super.shouldOverrideUrlLoading(view, url);
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
        //这里保存浏览过的页面记录
        /*if (!TextUtils.isEmpty(url) && url.contains("http://m.dmzj.com/info")) {
            view.loadUrl("javascript:window.BrowserJsInterface.showResource('<head>'+" +
                    "document.getElementsByTagName('html')[0].innerHTML+'</head>','" + url + "');");
        }*/
        //保存到数据库中
        LogUtils.e("保存到数据库的title是"+view.getTitle());
        saveOrUpdateHistory(view.getTitle(),url,view.getFavicon());
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
     * @param title
     * @param url
     */
    private void saveOrUpdateHistory(String title, String url,Bitmap bitmap) {
        try {
            boolean isExist = mSqliteManager.selectByTitle(url);
            if (!isExist) {
                //将bitmap转换成byte数组,判断bytes为null的情况
                if(bitmap != null){
                    byte[] bytes = ImageFormatUtils.bitmap2Bytes(bitmap);
                    mSqliteManager.insert(new CartoonDetailsBean(title, bytes, url));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        if (mOnReceivedErrorListener != null) {
            LogUtils.e("网络出错啦");
            mOnReceivedErrorListener.onReceivedError(view, error.getErrorCode());
        }
        super.onReceivedError(view, request, error);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        if (mOnReceivedErrorListener != null) {
            LogUtils.e("网络出错啦!!!!!onReceivedError过时的方法");
            mOnReceivedErrorListener.onReceivedError(view, errorCode);
        }
        LogUtils.e("onReceivedError过时的方法中返回结果是" + errorCode);
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    public void setmOnPageStartedListener(OnPageStartedListener mOnPageStartedListener) {
        this.mOnPageStartedListener = mOnPageStartedListener;
    }

    public void setmOnReceivedErrorListener(OnReceivedErrorListener mOnReceivedErrorListener) {
        this.mOnReceivedErrorListener = mOnReceivedErrorListener;
    }

}
