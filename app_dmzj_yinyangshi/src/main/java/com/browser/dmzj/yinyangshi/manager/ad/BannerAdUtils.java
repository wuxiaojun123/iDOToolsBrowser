package com.browser.dmzj.yinyangshi.manager.ad;

import android.content.Context;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.idotools.utils.LogUtils;
import com.browser.dmzj.yinyangshi.utils.Constant;
import com.browser.dmzj.yinyangshi.utils.DoAnalyticsManager;

/**
 * Created by wuxiaojun on 17-1-9.
 */

public class BannerAdUtils {

    private Context context;
    private AdView adView;

    public BannerAdUtils(Context context) {
        this.context = context;
        adView = new AdView(context, Constant.FACEBOOK_PLACEMENT_ID_BANNER, AdSize.BANNER_320_50);
    }

    public void loadAdView(){
        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                LogUtils.e("onError  "+adError.getErrorCode()+"  "+adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                LogUtils.e("onAdLoaded");
            }

            @Override
            public void onAdClicked(Ad ad) {
                DoAnalyticsManager.event(context,DoAnalyticsManager.DOT_KEY_BANNER_AD_CLICK);
            }
        });
        adView.loadAd();
    }

    public AdView getBannerAd() {
        return adView;
    }

    public AdView refreshAdView(){
        if(adView != null){
            adView.destroy();
        }
        adView = new AdView(context, Constant.FACEBOOK_PLACEMENT_ID_BANNER, AdSize.BANNER_320_50);
        return adView;
    }

}