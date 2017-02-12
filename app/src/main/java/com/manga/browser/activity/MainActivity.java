package com.manga.browser.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.ads.AdView;
import com.manga.browser.R;
import com.manga.browser.manager.ad.BannerAdUtils;
import com.manga.browser.manager.popupwindow.MainPopupWindow;
import com.manga.browser.manager.webview.WebViewManager;
import com.manga.browser.manager.webview.WebviewInteface;
import com.manga.browser.minterface.OnPageStartedListener;
import com.manga.browser.minterface.OnReceivedErrorListener;
import com.manga.browser.utils.ActivitySlideAnim;
import com.manga.browser.utils.ActivityUtils;
import com.manga.browser.utils.Constant;
import com.manga.browser.utils.DoAnalyticsManager;
import com.manga.browser.utils.WebAddress;
import com.manga.browser.view.AnimatedProgressBar;
import com.manga.browser.view.BrowserWebView;
import com.manga.browser.view.SearchEditTextView;
import com.idotools.utils.InputWindowUtils;
import com.idotools.utils.JudgeNetWork;
import com.idotools.utils.LogUtils;
import com.idotools.utils.MetricsUtils;
import com.idotools.utils.MobileScreenUtils;
import com.idotools.utils.SharedPreferencesHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 *
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, WebviewInteface, OnPageStartedListener, OnReceivedErrorListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.id_rl_main)
    RelativeLayout id_rl_main;
    @BindView(R.id.id_iv_back)
    ImageView iv_back;
    @BindView(R.id.id_iv_forward)
    ImageView iv_forward;
    @BindView(R.id.id_iv_home)
    ImageView iv_home;
    @BindView(R.id.id_iv_refresh)
    ImageView iv_refresh;
    @BindView(R.id.id_iv_more)
    ImageView iv_more;
    @BindView(R.id.id_iv_history)
    ImageView id_iv_history;

    @BindView(R.id.id_swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.id_fl_mask)
    FrameLayout id_fl_mask;
    @BindView(R.id.progress_view)
    AnimatedProgressBar progress_view;
    @BindView(R.id.id_iv_night_toogle)
    ImageView iv_night_toogle;
    @BindView(R.id.id_layout_title)
    LinearLayout ll_title;//标题布局
    @BindView(R.id.id_layout_bottom)
    LinearLayout ll_bottom;//底部布局

    @BindView(R.id.ll_ad_container)
    LinearLayout ll_ad_container;

    @BindView(R.id.id_et_search)//搜索按钮
            SearchEditTextView mSearchEditText;
    @BindView(R.id.id_iv_go)//前往地址
            ImageView iv_go;
    @BindView(R.id.id_layout_no_network)//无网络状况下显示
            LinearLayout id_layout_no_network;
    @BindView(R.id.id_layout_network_error)
    LinearLayout id_layout_network_error;//网址错误的时候显示布局

    //底部导航管理
    private MainPopupWindow mPopupWindow;
    //webview管理
    private WebViewManager mWebViewManager;
    private BrowserWebView mWebView;
    //屏幕高度
    private int screentHeight;
    //夜间模式切换动画平移高度
    private int nightModeTranslationY;
    //动画集合
    private AnimatorSet mAnimationSet;
    //收藏需要插入到数据库的imageUrl
    public String imgUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initData();
        //添加蒙版
        mSearchEditText.setFrameLayout(id_fl_mask);
        mSearchEditText.setOnKeyListener(new SearchEditTextView.onKeyListener() {
            @Override
            public void onKey() {
                searchDmzj();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mWebViewManager != null && mWebViewManager.mWebViewLongClickListener != null && mWebViewManager.mWebViewLongClickListener.popupWindow != null) {
            mWebViewManager.mWebViewLongClickListener.popupWindow.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void initData() {
        swipeRefreshLayout.setColorSchemeResources(R.color.color_main_title);
        swipeRefreshLayout.setOnRefreshListener(this);

        screentHeight = MobileScreenUtils.getScreenHeight(mContext);
        nightModeTranslationY = (screentHeight - MetricsUtils.dipToPx(100)) / 2;
        boolean modeNight = SharedPreferencesHelper.getInstance(mContext).getBoolean(SharedPreferencesHelper.SP_KEY_MODE_NIGHT, false);
        if (!modeNight) {
            setDayMode();
        } else {
            setNightMode();
        }
        mWebViewManager = new WebViewManager(this);
        mWebView = mWebViewManager.getWebView();

        swipeRefreshLayout.addView(mWebView, new SwipeRefreshLayout.LayoutParams(SwipeRefreshLayout.LayoutParams.MATCH_PARENT, SwipeRefreshLayout.LayoutParams.MATCH_PARENT));
        Intent mIntent = getIntent();
        String url = mIntent.getStringExtra("url");
        if (TextUtils.isEmpty(url)) {
            loadUrl(Constant.PATH);
        } else {
            loadUrl(url);
        }
        imgUrl = mIntent.getStringExtra("imgUrl");
        if (!TextUtils.isEmpty(imgUrl)) {
            String title = mIntent.getStringExtra("title");
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        String url = intent.getStringExtra("url");
        if (!TextUtils.isEmpty(url)) {
            loadUrl(url);
        }
        super.onNewIntent(intent);
    }

    @OnClick({R.id.id_iv_back, R.id.id_iv_forward, R.id.id_iv_home,
            R.id.id_iv_refresh, R.id.id_iv_more, R.id.id_iv_go, R.id.id_iv_history})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_iv_back:
                isPopupShowing();
                back();

                break;
            case R.id.id_iv_forward:
                isPopupShowing();
                forward();

                break;
            case R.id.id_iv_home:
                isPopupShowing();
                //回到native的首页
                backLastActivity();
                /*if (mWebViewManager != null && !mWebViewManager.getCurrentUrl().equals(Constant.PATH)) {
                    goHomePage();
                    loadUrl(Constant.PATH);
                }*/

                break;
            case R.id.id_iv_refresh:
                isPopupShowing();
                refresh();

                break;
            case R.id.id_iv_more:
                if (mPopupWindow == null) {
                    mPopupWindow = new MainPopupWindow(MainActivity.this);
                }
                mPopupWindow.showPopupWindow(ll_bottom);

                break;
            case R.id.id_iv_go:
                //前往
                searchDmzj();
                InputWindowUtils.closeInputWindow(v, mContext);
                break;
            case R.id.id_iv_history:
                startActivity(new Intent(MainActivity.this, HistoryAndRecordsActivity.class));
                ActivitySlideAnim.slideInAnim(MainActivity.this);
                DoAnalyticsManager.event(mContext, DoAnalyticsManager.DOT_KEY_HISTORY_CLICK);

                break;
        }
    }

    /**
     * 回到上一个页面
     */
    private void backLastActivity() {
        try {
            int size = ActivityUtils.activities.size();
            for (int i = size - 1; i >= 0; i--) {
                String simpleName = ActivityUtils.activities.get(i).getClass().getSimpleName();
                if (!simpleName.equals(DmzjActivity.class.getSimpleName())) {
                    ActivityUtils.activities.get(i).finish();
                }
            }
            ActivitySlideAnim.slideOutAnim(MainActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        //刷新界面
        isPopupShowing();
        refresh();
    }

    /***
     * 百度搜索关键词
     */
    private void searchDmzj() {
        String text = mSearchEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {
            if (mWebView != null && !TextUtils.equals(text, mWebView.getUrl())) {
                mWebView.requestFocus();
                showNetworkAddressErrorLayout(false);
                showOrHiddrenLayoutNoNetwork(true);
                InputWindowUtils.closeInputWindow(mSearchEditText, mContext);

                if (text.contains(".")) { // 包含了"."的文本，先判断是否为网址，如果不是则判断是否开头为http，如果是
                    try {
                        WebAddress webAddress = new WebAddress(text);
                        loadUrl(webAddress.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        loadUrl(Constant.SEARCH_URL_DMZJ + text + ".html");
                    }
                } else {
                    loadUrl(Constant.SEARCH_URL_DMZJ + text + ".html");
                }
            }
        }
    }

    /**
     * 判断popup是否为显示状态
     */
    private void isPopupShowing() {
        if (mPopupWindow != null && mPopupWindow.isShow()) {
            mPopupWindow.exitStartAnim();
        }
    }

    /***
     * 设置图标是否可以点击
     */
    public void setImgButtonEnable() {
        if (mWebView.canGoForward()) {
            iv_forward.setImageResource(R.drawable.selector_control_forward_click);
        } else {
            iv_forward.setImageResource(R.mipmap.img_forward_normal);
        }
    }

    @Override
    public void loadUrl(String url) {
        //检查网络
        if (JudgeNetWork.isNetAvailable(mContext) && !TextUtils.isEmpty(url)) {
            checkWebviewIsNull();
            if (TextUtils.equals(url, Constant.PATH)) {
                mSearchEditText.setText(null);
            }
            mWebView.stopLoading();
            mWebView.loadUrl(url);
        } else {
            id_layout_no_network.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void closeWebView() {
        checkWebviewIsNull();
        mWebView.stopLoading();
        finish();
    }

    @Override
    public void refresh() {
        if (mWebView != null) {
            mWebView.stopLoading();
            mWebView.reload();
        }
    }

    @Override
    public void forward() {
        checkWebviewIsNull();
        if (mWebView.canGoForward()) {
            showNetworkAddressErrorLayout(false);
            showOrHiddrenLayoutNoNetwork(true);
            mWebView.goForward();
        }
    }

    @Override
    public void back() {
        checkWebviewIsNull();
        if (mWebView.canGoBack()) {
            showNetworkAddressErrorLayout(false);
            showOrHiddrenLayoutNoNetwork(true);
            mWebView.goBack();
        } else {
            //返回上一个页面
            backLastActivity();
        }
    }

    @Override
    public void goHomePage() {
        checkWebviewIsNull();
        mWebView.clearHistory();
    }

    @Override
    public void showProgress(int progress) {
        if (isShown()) {
            progress_view.setProgress(progress);
            setImgButtonEnable();
            if (progress >= 100) {
                swipeRefreshLayout.setRefreshing(false);
            } else {
                swipeRefreshLayout.setRefreshing(true);
            }
        }
    }

    @Override
    public void onPageStarted(String url) {
        //改变输入框的网址
        checkWebviewIsNull();
        try {
            if (!TextUtils.isEmpty(url) && !url.equals(Constant.PATH)) {
                mSearchEditText.setText(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //友情提示一下
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode) {
        if (progress_view.getProgress() < 60) {
            showNetworkAddressErrorLayout(true);
        } else {
            //重新刷新
            refresh();
        }
        LogUtils.e("错误码是：" + errorCode);
    }

    /***
     * 显示网址错误布局
     */
    private void showNetworkAddressErrorLayout(boolean isShow) {
        if (isShow) {
            id_layout_network_error.setVisibility(View.VISIBLE);
            if (mWebView != null) {
                mWebView.stopLoading();
            }
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            //隐藏webview
            swipeRefreshLayout.setVisibility(View.GONE);
        } else {
            id_layout_network_error.setVisibility(View.GONE);
            //显示webview
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    /***
     * 隐藏无网络状态下的布局文件
     * forceHiddren true为隐藏
     */
    private void showOrHiddrenLayoutNoNetwork(boolean forceHiddren) {
        if (forceHiddren) {
            if (id_layout_no_network.getVisibility() == View.VISIBLE)
                id_layout_no_network.setVisibility(View.GONE);
        } else {
            if (id_layout_no_network.getVisibility() == View.GONE)
                id_layout_no_network.setVisibility(View.VISIBLE);
        }
    }

    public boolean isShown() {
        return mWebView != null && mWebView.isShown();
    }

    private void checkWebviewIsNull() {
        if (mWebView == null) {
            return;
        }
    }

    public WebViewManager getWebViewManager() {
        return mWebViewManager;
    }

    /***
     * 开始执行夜间模式的切换动画
     */
    public void startDayNightModeToogleAnim() {
        iv_night_toogle.setVisibility(View.VISIBLE);
        ObjectAnimator mTranslationYObjectAnimator = ObjectAnimator.ofFloat(iv_night_toogle, "translationY",
                screentHeight, nightModeTranslationY);

        final ObjectAnimator mAlpahAnimator = ObjectAnimator.ofFloat(iv_night_toogle, "alpha", 1.0f, 0.0f);
        final ObjectAnimator mScaleXAnimator = ObjectAnimator.ofFloat(iv_night_toogle, "scaleX", 1.0f, 5.0f);
        final ObjectAnimator mScaleYAnimator = ObjectAnimator.ofFloat(iv_night_toogle, "scaleY", 1.0f, 5.0f);

        mAnimationSet = new AnimatorSet();
        mAnimationSet.setDuration(600);
        mAnimationSet.setInterpolator(new AccelerateDecelerateInterpolator());

        mAnimationSet.play(mTranslationYObjectAnimator);
        mAnimationSet.play(mScaleXAnimator).with(mScaleYAnimator).with(mAlpahAnimator).after(mTranslationYObjectAnimator);
        mAnimationSet.start();

        mAlpahAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPopupWindow.isDayNightModeToogle = 0;
                iv_night_toogle.setAlpha(1.0f);
                iv_night_toogle.setScaleX(1.0f);
                iv_night_toogle.setScaleY(1.0f);
                iv_night_toogle.setVisibility(View.GONE);

                toogleNightMode();
            }
        });
    }

    /***
     * 夜间模式
     */
    private void toogleNightMode() {
        boolean modeNight = SharedPreferencesHelper.getInstance(mContext).getBoolean(SharedPreferencesHelper.SP_KEY_MODE_NIGHT, false);
        //重新启动dmzjActivity
        DmzjActivity activity = ActivityUtils.getDmzjActivity();
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

    //白天模式
    private void setDayMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //设置底部和标题为白天底色
        ll_bottom.setBackgroundResource(R.color.color_popup_bg);
        ll_title.setBackgroundResource(R.color.color_popup_bg);
        id_rl_main.setBackgroundResource(android.R.color.white);
        //设置动画图片为月亮
        iv_night_toogle.setImageResource(R.mipmap.img_night_mode);
    }

    //使用夜间模式
    private void setNightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        //设置底部和标题为夜间颜色
        ll_bottom.setBackgroundResource(R.color.color_while_night);
        ll_title.setBackgroundResource(R.color.color_while_night);
        id_rl_main.setBackgroundResource(android.R.color.white);
        //设置动画图片为太阳
        iv_night_toogle.setImageResource(R.mipmap.img_day_mode);
    }

    private int llTitleHeight = MetricsUtils.dipToPx(52);
    private int llBottomHeight = MetricsUtils.dipToPx(45);
    private static final int DURATION_ANIM = 250;
    private BannerAdUtils mBannerAdUtils;
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
        if (mBannerAdUtils == null) {
            mBannerAdUtils = new BannerAdUtils(mContext);
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
                //显示facebook广告
                try {
                    if (isFirst) {
                        AdView adView = mBannerAdUtils.refreshAdView();
                        mBannerAdUtils.loadAdView();
                        if (adView != null) {
                            isFirst = false;
                            if (ll_ad_container.getParent() != null) {
                                ll_ad_container.removeAllViews();
                            }
                            if (ll_ad_container.getChildCount() > 0) {
                                ll_ad_container.removeAllViews();
                            }
                            ll_ad_container.addView(adView);
                            ll_ad_container.setVisibility(View.VISIBLE);
                        }
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

    public boolean isDmzjView() {
        if (mWebView.getUrl() != null && mWebView.getUrl().startsWith("http://m.dmzj.com/view")) {
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (id_fl_mask.getVisibility() == View.VISIBLE) {
            mSearchEditText.backKey();
        } else {
            if (mPopupWindow != null && mPopupWindow.isShow()) {
                mPopupWindow.exitStartAnim();
            } else if (mWebView != null && mWebView.canGoBack()) {
                if (ll_ad_container.getVisibility() == View.VISIBLE) {
                    showTitleAndBottomStartAnim();
                    ll_ad_container.setVisibility(View.GONE);
                }
                back();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            swipeRefreshLayout.removeView(mWebView);
            mWebView.destroy();
        }
        if (mWebViewManager != null) {
            mWebViewManager.unRegisterReceiverDownload();
        }
        super.onDestroy();
    }

}