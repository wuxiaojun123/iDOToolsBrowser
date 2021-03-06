package com.idotools.browser.manager.webview;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.idotools.utils.LogUtils;

/**
 * Created by wuxiaojun on 16-10-8.
 */
public class BrowserWebChromeClient extends WebChromeClient {

    private WebviewInteface mWebViewInteface;

    public BrowserWebChromeClient(WebviewInteface mWebViewInteface) {
        this.mWebViewInteface = mWebViewInteface;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if (mWebViewInteface != null) {
            mWebViewInteface.showProgress(newProgress);
        }
    }

}
