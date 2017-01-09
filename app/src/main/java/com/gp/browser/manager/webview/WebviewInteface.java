package com.gp.browser.manager.webview;

/**
 * Created by wuxiaojun on 16-10-8.
 */
public interface WebviewInteface {

    void loadUrl(String url);

    void closeWebView();

    void refresh();

    void forward();

    void back();

    void goHomePage();

    void showProgress(int progress);

}
