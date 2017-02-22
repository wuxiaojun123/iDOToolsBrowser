package com.browser.dmzj.yinyangshi.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.widget.RelativeLayout;

import com.base.browser.activity.BaseActivity;
import com.base.browser.activity.MainActivity;
import com.base.browser.utils.ActivitySlideAnim;
import com.base.browser.utils.ActivityUtils;
import com.browser.dmzj.yinyangshi.manager.ad.BannerAdUtils;
import com.browser.dmzj.yinyangshi.manager.ad.BottomNativeAdManager;
import com.browser.dmzj.yinyangshi.manager.webview.MyWebViewManager;
import com.browser.dmzj.yinyangshi.utils.Constant;
import com.browser.dmzj.yinyangshi.utils.DoAnalyticsManager;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.idotools.utils.LogUtils;
import com.idotools.utils.MetricsUtils;
import com.idotools.utils.SharedPreferencesHelper;

/**
 * Created by wuxiaojun on 17-2-17.
 */

public class BrowserActivity extends MainActivity {


    @Override
    protected void initWebViewManager() {
        mWebViewManager = new MyWebViewManager(this);
    }

    private InterstitialAd interstitialAd; // 插屏ad
    private boolean isInterstitialAd = false;

    @Override
    protected void initData() {
        super.initData();
        loadInterstitialAd();
    }

    private void loadInterstitialAd() { // 加载插屏ad
        interstitialAd = new InterstitialAd(mContext, Constant.FACEBOOK_PLACEMENT_ID_TABLE_PLAQUE);
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
            }

            @Override
            public void onError(Ad ad, AdError adError) {
            }

            @Override
            public void onAdLoaded(Ad ad) { // 加载完成
                isInterstitialAd = true;
                if (isDmzjView()) {
                    interstitialAd.show();
                    isInterstitialAd = false;
                    DoAnalyticsManager.event(mContext, DoAnalyticsManager.DOT_KEY_TABLE_PLAQUE_AD_SHOW);
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                DoAnalyticsManager.event(mContext, DoAnalyticsManager.DOT_KEY_TABLE_PLAQUE_AD_CLICK);
            }
        });
        DoAnalyticsManager.event(mContext, DoAnalyticsManager.DOT_KEY_TABLE_PLAQUE_AD_REQUEST);
        interstitialAd.loadAd();
    }

    @Override
    protected void showInterstitialAd(int progress) {
        if (isDmzjView()) {
            if (interstitialAd != null) {
                if (progress >= 100) {
                    interstitialAd.destroy();
                    loadInterstitialAd();
                } else {
                    if (isInterstitialAd) { // 表示加载完成
                        interstitialAd.show();
                        isInterstitialAd = false;
                        DoAnalyticsManager.event(mContext, DoAnalyticsManager.DOT_KEY_TABLE_PLAQUE_AD_SHOW);
                    }
                }
            }
        }
    }

    protected void backLastActivity() {
        try {
            int size = ActivityUtils.activities.size();
            for (int i = size - 1; i >= 0; i--) {
                String simpleName = ActivityUtils.activities.get(i).getClass().getSimpleName();
                if (!simpleName.equals(DmzjActivity.class.getSimpleName())) {
                    ActivityUtils.activities.get(i).finish();
                }
            }
            ActivitySlideAnim.slideOutAnim(BrowserActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 夜间模式
     */
    protected void toogleNightMode() {
        boolean modeNight = SharedPreferencesHelper.getInstance(mContext).getBoolean(SharedPreferencesHelper.SP_KEY_MODE_NIGHT, false);
        //重新启动dmzjActivity
        DmzjActivity activity = getDmzjActivity();
        if (modeNight) {
            setDayMode();
            activity.setDayMode();
        } else {
            setNightMode();
            activity.setNightMode();
        }
        mPopupWindow.toogleNightMode(!modeNight);
        SharedPreferencesHelper.getInstance(mContext).putBoolean(SharedPreferencesHelper.SP_KEY_MODE_NIGHT, !modeNight);
    }

    public DmzjActivity getDmzjActivity() {
        for (BaseActivity activity : ActivityUtils.activities) {
            if (DmzjActivity.class.getSimpleName().equals(activity.getClass().getSimpleName())) {
                return (DmzjActivity) activity;
            }
        }
        return null;
    }


    private int llTitleHeight = MetricsUtils.dipToPx(52);
    private int llBottomHeight = MetricsUtils.dipToPx(45);
    private static final int DURATION_ANIM = 250;
    private BottomNativeAdManager mBottomNativeAdManager;
    private boolean isFirst = true;//

    /***
     * 显示广告的逻辑
     * 如果当前页面开头包含：http://m.dmzj.com/view
     * 如果当前地址和上一页地址不一样，则重新加载广告
     * 如果当前地址和上一页地址一样，则使用原来的广告
     */
    public void hideTitleAndBottom() {
        if (!isDmzjView()) {
            return;
        }
        if (mBottomNativeAdManager == null) {
            mBottomNativeAdManager = new BottomNativeAdManager(mContext);
        }
        ObjectAnimator titleAnim = ObjectAnimator.ofInt(ll_title, "translationY", -llTitleHeight).setDuration(DURATION_ANIM);
        titleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) swipeRefreshLayout.getLayoutParams();
                lp.setMargins(0, llTitleHeight + value, 0, 0);
                swipeRefreshLayout.requestLayout();
            }
        });
        titleAnim.start();

        ObjectAnimator bottomAnim = ObjectAnimator.ofInt(ll_bottom, "translationY", 0, llBottomHeight).setDuration(DURATION_ANIM);
        bottomAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                ll_bottom.setTranslationY(value);
            }
        });
        bottomAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                try { // 显示facebook广告
                    if (isFirst) {
                        mBottomNativeAdManager.refreshAdView();
                        mBottomNativeAdManager.loadAdView(ll_ad_container);
                        isFirst = false;
                    } else {
                        if (ll_ad_container.getChildCount() > 0) {
                            ll_ad_container.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        bottomAnim.start();
    }


    public void showTitleAndBottom() {
        if (!isDmzjView()) {
            isFirst = true;
            return;
        }
        showTitleAndBottomStartAnim();
    }

    private void showTitleAndBottomStartAnim() {
        ObjectAnimator titleAnim = ObjectAnimator.ofInt(ll_title, "translationY", llTitleHeight).setDuration(DURATION_ANIM);
        titleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) swipeRefreshLayout.getLayoutParams();
                if (value >= llTitleHeight) {
                    lp.setMargins(0, value, 0, llBottomHeight);
                } else {
                    lp.setMargins(0, value, 0, 0);
                }
                swipeRefreshLayout.requestLayout();
            }
        });
        titleAnim.start();

        ObjectAnimator bottomAnim = ObjectAnimator.ofInt(ll_bottom, "translationY", llBottomHeight, 0).setDuration(DURATION_ANIM);
        bottomAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                ll_bottom.setTranslationY(value);
            }
        });
        bottomAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //隐藏facebook广告  逻辑
                try {
                    if (ll_ad_container.getVisibility() == View.VISIBLE) {
                        ll_ad_container.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        bottomAnim.start();
    }

    /***
     * 判断是否显示banner ad
     *
     * @return true 满足条件显示
     */
    public boolean isDmzjView() {
        String url = mWebView.getUrl();
        if (url != null &&
                (url.startsWith("http://m.dmzj.com/view") || url.startsWith("http://m.u17.com/r")
                        || url.startsWith("http://m.kuaikanmanhua.com/mobile/comics") || url.startsWith("http://h5.ishangman.com/read"))) {
            return true;
        }
        return false;
    }

    @Override
    public boolean backpressed() {
        boolean result = false;
        if (id_fl_mask.getVisibility() == View.VISIBLE) {
            mSearchEditText.backKey();
        } else {
            if (mPopupWindow != null && mPopupWindow.isShow()) {
                mPopupWindow.exitStartAnim();
            } else if (mWebView != null && mWebView.canGoBack()) {
                if (ll_ad_container.getVisibility() == View.VISIBLE) {
                    showTitleAndBottomStartAnim();
                }
                back();
            } else {
                result = true;
            }
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
    }
}
