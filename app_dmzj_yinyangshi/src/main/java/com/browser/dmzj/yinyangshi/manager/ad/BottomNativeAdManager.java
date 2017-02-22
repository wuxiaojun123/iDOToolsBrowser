package com.browser.dmzj.yinyangshi.manager.ad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.browser.dmzj.yinyangshi.R;
import com.browser.dmzj.yinyangshi.utils.Constant;
import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.idotools.utils.LogUtils;
import com.browser.dmzj.yinyangshi.adapter.viewHolder.DmzjViewHolderTypeAd;
import com.browser.dmzj.yinyangshi.utils.DoAnalyticsManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wuxiaojun on 17-1-18.
 */

public class BottomNativeAdManager {

    ImageView native_ad_icon; // ad icon
    Button native_ad_call_to_action; // 点击下载按钮
    TextView native_ad_title; // 标题

    private RelativeLayout rl_native_content;
    private Context mContext;
    private NativeAd nativeAd;
    private View nativeView; // native ad 总布局


    public BottomNativeAdManager(Context context) {
        this.mContext = context;
    }

    public void loadAdView(final LinearLayout ll_ad_container){
//        nativeAd = new NativeAd(mContext, Constant.FACEBOOK_PLACEMENT_ID_BANNER);
        nativeAd.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {
                // setNativeView
                nativeView = LayoutInflater.from(mContext).inflate(R.layout.layout_ad_bottom, null);
                rl_native_content = (RelativeLayout) nativeView.findViewById(R.id.rl_native_content);
                native_ad_icon = (ImageView) nativeView.findViewById(R.id.native_ad_icon);
                native_ad_call_to_action = (Button) nativeView.findViewById(R.id.native_ad_call_to_action);
                native_ad_title = (TextView) nativeView.findViewById(R.id.native_ad_title);

                // Set the Text.
                native_ad_title.setText(nativeAd.getAdTitle());
                native_ad_call_to_action.setText(nativeAd.getAdCallToAction());

                // Download and display the ad icon.
                NativeAd.Image adIcon = nativeAd.getAdIcon();
                NativeAd.downloadAndDisplayImage(adIcon, native_ad_icon);

                // Register the Title and CTA button to listen for clicks.
                List<View> clickableViews = new ArrayList<>();
                clickableViews.add(rl_native_content);
                clickableViews.add(native_ad_call_to_action);
                nativeAd.registerViewForInteraction(rl_native_content, clickableViews);

                if(ll_ad_container != null){
                    if (ll_ad_container.getParent() != null) {
                        ll_ad_container.removeAllViews();
                    }
                    if (ll_ad_container.getChildCount() > 0) {
                        ll_ad_container.removeAllViews();
                    }
                    ll_ad_container.addView(nativeView);
                    ll_ad_container.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                DoAnalyticsManager.event(mContext,DoAnalyticsManager.DOT_KEY_BANNER_AD_CLICK);
            }
        });

        nativeAd.loadAd();
    }

    public void refreshAdView(){
        if(nativeAd != null){
            nativeAd.destroy();
        }
        if(nativeView != null){
            nativeView = null;
        }
        nativeAd = new NativeAd(mContext, Constant.FACEBOOK_PLACEMENT_ID_BANNER);
    }


}
