package com.gp.browser.video.dance.manager.ad;

import android.content.Context;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.idotools.utils.LogUtils;

/**
 * Created by wuxiaojun on 17-1-9.
 */

public class BannerAdUtils {

    public static final String FACEBOOK_PLACEMENT_ID_BANNER = "276736496093340_276736909426632";

    private Context context;
    private AdView adView;

    public BannerAdUtils(Context context) {
        this.context = context;
        adView = new AdView(context, FACEBOOK_PLACEMENT_ID_BANNER, AdSize.BANNER_320_50);
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
//                DoAnalyticsManager.event(context,DoAnalyticsManager.DOT_KEY_BANNER_AD_CLICK);
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
        adView = new AdView(context, FACEBOOK_PLACEMENT_ID_BANNER, AdSize.BANNER_320_50);
        return adView;
    }

}
