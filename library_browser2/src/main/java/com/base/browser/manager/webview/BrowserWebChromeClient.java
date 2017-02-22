package com.base.browser.manager.webview;

import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Created by wuxiaojun on 16-10-8.
 */
public class BrowserWebChromeClient extends WebChromeClient {

    private WebviewInteface mWebViewInteface;

    // 屏蔽首页的图像  style.visibility="hidden";
    private StringBuilder bottomSb = new StringBuilder("javascript:app_ad.in_home();");
    // 屏蔽下方的ad
    private StringBuilder bottomSb2 = new StringBuilder("javascript:var ad = document.getElementById('khdDown');if(ad != null){ad.parentNode.removeChild(ad)};");

    // 屏蔽快看漫画的ad
    private StringBuilder kuaikanSb = new StringBuilder("javascript:var ad = document.getElementsByClassName('download').item(0);if(ad != null){ad.parentNode.removeChild(ad)};");
    private StringBuilder kuaikanSb2 = new StringBuilder("javascript:var ad = document.getElementById('download-center');if(ad != null){ad.parentNode.removeChild(ad) };" +
            "var ad2 = document.getElementById('mod_box_pop');if(ad2 != null){ad2.parentNode.removeChild(ad2) };");
    private StringBuilder kuaikanSb3 = new StringBuilder("javascript:var ad2 = document.getElementById('mod_box_pop');if(ad2 != null){ad2.parentNode.removeChild(ad2) };");

    // 屏蔽有妖气漫画的ad
    private StringBuilder secretSb = new StringBuilder("javascript:m_util.hide_element('down_load');");

    // 屏蔽腾讯漫画的ad 首页
    private StringBuilder tencentSb = new StringBuilder("javascript:var ad = document.getElementsByClassName('app-guiding-download').item(0);if(ad != null){ad.parentNode.removeChild(ad)};");
    // 条漫详情页
    private StringBuilder tencentSb2 = new StringBuilder("javascript:var ad = document.getElementsByClassName('app-guiding').item(0);if(ad != null){ad.parentNode.removeChild(ad)};");
    // 漫画某一章节最底部
//    private StringBuilder tencentSb3 = new StringBuilder("javascript:var ad2 = document.getElementsByClassName('app-guiding').item(0);if(ad2 != null){ad2.parentNode.removeChild(ad2)};");

    // 屏蔽网易漫画的ad
    private StringBuilder wangyiSb = new StringBuilder("javascript:var ad = document.getElementById('foot-fixed');if(ad != null){ad.parentNode.removeChild(ad)};");


    public BrowserWebChromeClient(WebviewInteface mWebViewInteface) {
        this.mWebViewInteface = mWebViewInteface;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if (mWebViewInteface != null) {
            mWebViewInteface.showProgress(newProgress);
        }
        interceptAds(view);
    }

    private void interceptAds(WebView view){
        String url = view.getUrl();
        if (!TextUtils.isEmpty(url)) {
            if ("http://m.dmzj.com/".equals(url)) {
                view.loadUrl(bottomSb.toString());
                view.loadUrl(bottomSb2.toString());
            } else if (url.contains("dmzj")) {
                view.loadUrl(bottomSb2.toString());

            } else if(url.equals("http://m.kuaikanmanhua.com/")){
                view.loadUrl(kuaikanSb.toString());

            } else if(url.startsWith("http://m.kuaikanmanhua.com/mobile")){
                view.loadUrl(kuaikanSb2.toString());

            }else if(url.startsWith("http://m.u17.com/c")){
                view.loadUrl(secretSb.toString());
            } else if(url.equals("http://m.ac.qq.com/")){
                view.loadUrl(tencentSb.toString());
            } else if(url.startsWith("http://m.ac.qq.com/comic/index/id")){
                view.loadUrl(tencentSb2.toString());
            } else if(url.startsWith("http://m.ac.qq.com/chapter/index/id")){
                view.loadUrl(tencentSb2.toString());
            }else if(url.equals("http://h5.ishangman.com/") || url.startsWith("https://h5.manhua.163.com/reader/")
                    || url.startsWith("https://h5.manhua.163.com/source")){
//                view.loadUrl(wangyiSb.toString());
            }
        }
    }

}
