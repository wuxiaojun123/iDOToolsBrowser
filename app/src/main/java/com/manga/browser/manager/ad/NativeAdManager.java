package com.manga.browser.manager.ad;

import android.content.Context;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.NativeAd;
import com.manga.browser.adapter.viewHolder.DmzjViewHolderTypeAd;
import com.manga.browser.utils.DoAnalyticsManager;
import com.idotools.utils.LogUtils;

/**
 * Created by wuxiaojun on 17-1-18.
 */

public class NativeAdManager {


    private Context mContext;

    public NativeAdManager(Context context) {
        this.mContext = context;
    }


    private class NativeAdListener implements AdListener {

        DmzjViewHolderTypeAd dmzjViewHolder;
        NativeAd mNativeAd;
        String currentPosition;

        public NativeAdListener(DmzjViewHolderTypeAd dmzjViewHolder, NativeAd mNativeAd, String currentPosition) {
            this.dmzjViewHolder = dmzjViewHolder;
            this.mNativeAd = mNativeAd;
            this.currentPosition = currentPosition;
        }

        @Override
        public void onError(Ad ad, AdError error) {
            LogUtils.e("加载失败:" + error.getErrorCode() + "=" + error.getErrorMessage());
        }

        @Override
        public void onAdLoaded(Ad ad) {
            // Set the Text.
            /*dmzjViewHolder.nativeAdTitle.setText(mNativeAd.getAdTitle());
            dmzjViewHolder.nativeAdBody.setText(mNativeAd.getAdBody());
            dmzjViewHolder.nativeAdCallToAction.setText(mNativeAd.getAdCallToAction());

            LogUtils.e("获取广告图片路径" + mNativeAd.getAdCoverImage().getUrl());
            dmzjViewHolder.nativeAdMedia.setNativeAd(mNativeAd);

            AdChoicesView adChoicesView = new AdChoicesView(mContext, mNativeAd, true);
            dmzjViewHolder.adChoicesContainer.addView(adChoicesView);
            mNativeAdChoicesMap.put(currentPosition, adChoicesView);

            // Register the Title and CTA button to listen for clicks.
            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(dmzjViewHolder.nativeAdTitle);
            clickableViews.add(dmzjViewHolder.nativeAdCallToAction);
            mNativeAd.registerViewForInteraction(dmzjViewHolder.native_ad_unit, clickableViews);
            mNativeClickViewMap.put(currentPosition, clickableViews);*/
        }

        @Override
        public void onAdClicked(Ad ad) {
            // Ad clicked callback
            DoAnalyticsManager.event(mContext, DoAnalyticsManager.DOT_KEY_UPDATE_AD_CLICK);
        }
    }

}
