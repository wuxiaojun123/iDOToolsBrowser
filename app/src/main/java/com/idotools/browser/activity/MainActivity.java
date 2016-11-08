package com.idotools.browser.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.idotools.browser.R;
import com.idotools.browser.manager.popupwindow.MainPopupWindow;
import com.idotools.browser.manager.webview.WebViewManager;
import com.idotools.browser.manager.webview.WebviewInteface;
import com.idotools.browser.minterface.OnPageStartedListener;
import com.idotools.browser.minterface.OnReceivedErrorListener;
import com.idotools.browser.utils.Constant;
import com.idotools.browser.view.AnimatedProgressBar;
import com.idotools.browser.view.BrowserWebView;
import com.idotools.browser.view.SearchEditTextView;
import com.idotools.utils.InputWindowUtils;
import com.idotools.utils.LogUtils;
import com.idotools.utils.MetricsUtils;
import com.idotools.utils.MobileScreenUtils;
import com.idotools.utils.SharedPreferencesHelper;
import com.ta.utdid2.android.utils.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements View.OnClickListener, WebviewInteface, OnPageStartedListener, OnReceivedErrorListener {

    @BindView(R.id.id_rl_main)
    RelativeLayout id_rl_main;
    @BindView(R.id.id_iv_start_page)
    ImageView id_iv_start_page;
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
    @BindView(R.id.id_fl_content)
    FrameLayout id_fl_content;
    @BindView(R.id.progress_view)
    AnimatedProgressBar progress_view;
    @BindView(R.id.id_iv_night_toogle)
    ImageView iv_night_toogle;
    @BindView(R.id.id_layout_title)
    LinearLayout rl_title;//标题布局
    @BindView(R.id.id_layout_bottom)
    LinearLayout ll_bottom;//底部布局
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        startPageAnim();
        initData();
    }

    private void initData() {
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
        mWebView.requestFocus();
        id_fl_content.addView(mWebView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        loadUrl(Constant.PATH);
    }

    private void startPageAnim() {
        id_iv_start_page.animate().alpha(0f).scaleX(3.0f).scaleY(3.0f).setDuration(1000).setStartDelay(2000).
                setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        id_iv_start_page.setVisibility(View.GONE);
                    }
                }).start();
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
            R.id.id_iv_refresh, R.id.id_iv_more, R.id.id_iv_go})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_iv_right:
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));

                break;
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
                //加载主页
                if (mWebViewManager != null && !mWebViewManager.getCurrentUrl().equals(Constant.PATH)) {
                    goHomePage();
                    loadUrl(Constant.PATH);
                }

                break;
            case R.id.id_iv_refresh:
                isPopupShowing();
                refresh();

                break;
            case R.id.id_iv_more:
                if (mPopupWindow == null) {
                    mPopupWindow = new MainPopupWindow(MainActivity.this);
                }
                if (mPopupWindow.isShow()) {
                    mPopupWindow.exitStartAnim();
                } else {
                    mPopupWindow.showPopupWindow(ll_bottom);
                }
                break;
            case R.id.id_iv_go:
                //前往
                goToUrl();
                InputWindowUtils.closeInputWindow(v, mContext);
                break;
        }
    }

    /***
     * 百度搜索关键词
     */
    private void goToUrl() {
        String searchEditText = mSearchEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(searchEditText)) {
            if (mWebView != null && !TextUtils.equals(searchEditText, mWebView.getUrl())) {
                mWebView.requestFocus();
                showNetworkAddressErrorLayout(false);
                showOrHiddrenLayoutNoNetwork(true);
                InputWindowUtils.closeInputWindow(mSearchEditText, mContext);
                loadUrl("http://www.baidu.com/#wd=" + searchEditText);
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
        iv_back.setEnabled(mWebView.canGoBack());
        iv_forward.setEnabled(mWebView.canGoForward());
    }

    @Override
    public void loadUrl(String url) {
        //检查网络
        if (NetworkUtils.isConnectInternet(mContext) && !TextUtils.isEmpty(url)) {
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
        System.exit(0);
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
        }
    }

    @Override
    public void onPageStarted(String url) {
        //改变输入框的网址
        checkWebviewIsNull();
        if (!TextUtils.isEmpty(url) && !url.equals(Constant.PATH)) {
            mSearchEditText.setText(url);
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode) {
        showNetworkAddressErrorLayout(true);
        LogUtils.e("错误码是：" + errorCode);
    }

    /***
     * 显示网址错误布局
     */
    private void showNetworkAddressErrorLayout(boolean isShow) {
        if (isShow) {
            id_layout_network_error.setVisibility(View.VISIBLE);
            //隐藏webview
            id_fl_content.setVisibility(View.GONE);
        } else {
            id_layout_network_error.setVisibility(View.GONE);
            //显示webview
            id_fl_content.setVisibility(View.VISIBLE);
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
        if (modeNight) {
            setDayMode();
        } else {
            setNightMode();
        }
        mPopupWindow.toogleNightMode(!modeNight);
        SharedPreferencesHelper.getInstance(mContext).putBoolean(SharedPreferencesHelper.SP_KEY_MODE_NIGHT, !modeNight);
    }

    //白天模式
    private void setDayMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //设置底部和标题为白天底色
        ll_bottom.setBackgroundResource(R.color.color_popup_bg);
        rl_title.setBackgroundResource(R.color.color_popup_bg);
        id_rl_main.setBackgroundResource(android.R.color.white);
        //设置动画图片为月亮
        iv_night_toogle.setImageResource(R.mipmap.img_night_mode);
    }

    //使用夜间模式
    private void setNightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        //设置底部和标题为夜间颜色
        ll_bottom.setBackgroundResource(R.color.color_while_night);
        rl_title.setBackgroundResource(R.color.color_while_night);
        id_rl_main.setBackgroundResource(android.R.color.white);
        //设置动画图片为太阳
        iv_night_toogle.setImageResource(R.mipmap.img_day_mode);
    }

    @Override
    public void onBackPressed() {
        if (mPopupWindow != null && mPopupWindow.isShow()) {
            mPopupWindow.exitStartAnim();
        } else if (mWebView != null && mWebView.canGoBack()) {
            back();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            id_fl_content.removeView(mWebView);
            mWebView.destroy();
        }
        super.onDestroy();
    }

}