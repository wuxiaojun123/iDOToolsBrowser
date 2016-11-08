package com.base.browser.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.base.browser.R;
import com.base.browser.manager.popupwindow.MainPopupWindow;
import com.base.browser.manager.webview.WebViewManager;
import com.base.browser.manager.webview.WebviewInteface;
import com.base.browser.minterface.OnPageStartedListener;
import com.base.browser.minterface.OnReceivedErrorListener;
import com.base.browser.utils.Constant;
import com.base.browser.utils.StatusBarUtils;
import com.base.browser.view.AnimatedProgressBar;
import com.base.browser.view.BrowserWebView;
import com.dot.autoupdater.utils.NetworkUtils;
import com.idotools.utils.LogUtils;
import com.idotools.utils.MetricsUtils;
import com.idotools.utils.MobileScreenUtils;
import com.idotools.utils.ToastUtils;

public class MainActivity extends BaseActivity implements View.OnClickListener, WebviewInteface, OnPageStartedListener, OnReceivedErrorListener {

    protected RelativeLayout id_rl_main;
    protected ImageView id_iv_start_page;
    protected ImageView iv_back;
    protected ImageView iv_forward;
    protected ImageView iv_home;
    protected ImageView iv_refresh;
    protected ImageView iv_more;
    protected FrameLayout id_fl_content;
    protected AnimatedProgressBar progress_view;
    protected ImageView iv_night_toogle;
    protected RelativeLayout rl_title;//标题布局
    protected LinearLayout ll_bottom;//底部布局
    protected ImageButton id_iv_right;
    protected LinearLayout id_layout_no_network;//无网络状况下显示
    protected LinearLayout id_layout_network_error;//网址错误的时候显示布局
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
        initView();
        startPageAnim();
        initData();
    }

    @Override
    protected void setStatusColor() {
        StatusBarUtils.setTranslucent(this);
    }

    protected void startPageAnim() {
        MobileScreenUtils.full(MainActivity.this, true);
        id_iv_start_page.animate().alpha(0f).scaleX(3.0f).scaleY(3.0f).setDuration(1000).setStartDelay(2000).
                setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        id_iv_start_page.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        MobileScreenUtils.full(MainActivity.this, false);
                    }
                }).start();
    }

    private void initView() {
        id_rl_main = (RelativeLayout) findViewById(R.id.id_rl_main);
        id_iv_start_page = (ImageView) findViewById(R.id.id_iv_start_page);
        iv_back = (ImageView) findViewById(R.id.id_iv_back);
        iv_forward = (ImageView) findViewById(R.id.id_iv_forward);
        iv_home = (ImageView) findViewById(R.id.id_iv_home);
        iv_refresh = (ImageView) findViewById(R.id.id_iv_refresh);
        iv_more = (ImageView) findViewById(R.id.id_iv_more);
        id_fl_content = (FrameLayout) findViewById(R.id.id_fl_content);
        progress_view = (AnimatedProgressBar) findViewById(R.id.progress_view);
        iv_night_toogle = (ImageView) findViewById(R.id.id_iv_night_toogle);
        rl_title = (RelativeLayout) findViewById(R.id.id_layout_title);
        ll_bottom = (LinearLayout) findViewById(R.id.id_layout_bottom);
        id_iv_right = (ImageButton) findViewById(R.id.id_iv_right);
        id_layout_no_network = (LinearLayout) findViewById(R.id.id_layout_no_network);
        id_layout_network_error = (LinearLayout) findViewById(R.id.id_layout_network_error);
        //设置点击事件
        iv_back.setOnClickListener(this);
        iv_forward.setOnClickListener(this);
        iv_home.setOnClickListener(this);
        iv_refresh.setOnClickListener(this);
        iv_more.setOnClickListener(this);
    }

    private void initData() {
        id_iv_right.setVisibility(View.GONE);
        screentHeight = MobileScreenUtils.getScreenHeight(mContext);
        nightModeTranslationY = (screentHeight - MetricsUtils.dipToPx(100)) / 2;
        mWebViewManager = new WebViewManager(this);
        mWebView = mWebViewManager.getWebView();
        mWebView.requestFocus();
        id_fl_content.addView(mWebView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        String url = getIntent().getStringExtra("url");
        if (!TextUtils.isEmpty(url)) {
            loadUrl(url);
        } else {
            loadUrl(Constant.PATH);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String url = intent.getStringExtra("url");
        LogUtils.e("获取到的url是"+url);
        if (!TextUtils.isEmpty(url)) {
            loadUrl(url);
        }
        super.onNewIntent(intent);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.id_iv_right) {
            startActivity(new Intent(MainActivity.this, HistoryActivity.class));

        } else if (id == R.id.id_iv_back) {
            isPopupShowing();
            back();

        } else if (id == R.id.id_iv_forward) {
            isPopupShowing();
            forward();

        } else if (id == R.id.id_iv_home) {
            isPopupShowing();
            //加载主页
            if (mWebViewManager != null && !mWebViewManager.getCurrentUrl().equals(Constant.PATH)) {
                goHomePage();
                loadUrl(Constant.PATH);
            }

        } else if (id == R.id.id_iv_refresh) {
            isPopupShowing();
            refresh();

        } else if (id == R.id.id_iv_more) {
            if (mPopupWindow == null) {
                mPopupWindow = new MainPopupWindow(MainActivity.this);
            }
            if (mPopupWindow.isShow()) {
                mPopupWindow.exitStartAnim();
            } else {
                mPopupWindow.showPopupWindow(ll_bottom);
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
        if (mWebView.canGoBack()) {
            iv_back.setImageResource(R.drawable.selector_control_back_clickable);
        } else {
            iv_back.setImageResource(R.mipmap.img_back_normal);
        }
        if (mWebView.canGoForward()) {
            iv_forward.setImageResource(R.drawable.selector_control_forward_click);
        } else {
            iv_forward.setImageResource(R.mipmap.img_forward_normal);
        }
    }

    @Override
    public void loadUrl(String url) {
        //检查网络
        if (NetworkUtils.isNetworkAvailable(mContext) && !TextUtils.isEmpty(url)) {
            checkWebviewIsNull();
            /*if (TextUtils.equals(url, Constant.PATH)) {
                mSearchEditText.setText(null);
            }*/
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
        /*checkWebviewIsNull();
        if (!TextUtils.isEmpty(url) && !url.equals(Constant.PATH)) {
            mSearchEditText.setText(url);
        }*/
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
            }
        });
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

    private long mExitTime;

    @Override
    public void onBackPressed() {
        if (mPopupWindow != null && mPopupWindow.isShow()) {
            mPopupWindow.exitStartAnim();
        } else if (mWebView != null && mWebView.canGoBack()) {
            back();
        } else {
            //提示用户是否退出
            exit();
        }
    }

    /**
     * 退出应用
     */
    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            ToastUtils.show(mContext, R.string.string_press_again);
            mExitTime = System.currentTimeMillis();
        } else {
            System.exit(0);
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