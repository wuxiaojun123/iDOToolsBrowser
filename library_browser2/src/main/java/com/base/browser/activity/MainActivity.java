package com.base.browser.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.base.browser.R;
import com.base.browser.manager.popupwindow.MainPopupWindow;
import com.base.browser.manager.webview.WebViewManager;
import com.base.browser.manager.webview.WebviewInteface;
import com.base.browser.minterface.OnPageStartedListener;
import com.base.browser.minterface.OnReceivedErrorListener;
import com.base.browser.utils.ActivitySlideAnim;
import com.base.browser.utils.ActivityUtils;
import com.base.browser.utils.BaseConstant;
import com.base.browser.view.AnimatedProgressBar;
import com.base.browser.view.BrowserWebView;
import com.base.browser.view.SearchEditTextView;
import com.idotools.utils.InputWindowUtils;
import com.idotools.utils.JudgeNetWork;
import com.idotools.utils.LogUtils;
import com.idotools.utils.MetricsUtils;
import com.idotools.utils.MobileScreenUtils;
import com.idotools.utils.SharedPreferencesHelper;


public abstract class MainActivity extends BaseActivity implements View.OnClickListener, WebviewInteface, OnPageStartedListener, OnReceivedErrorListener, SwipeRefreshLayout.OnRefreshListener {

    RelativeLayout id_rl_main;
    ImageView iv_back;
    ImageView iv_forward;
    ImageView iv_home;
    ImageView iv_refresh;
    ImageView iv_more;
    ImageView id_iv_history;

    public LinearLayout ll_ad_container;


    public SwipeRefreshLayout swipeRefreshLayout;
    protected FrameLayout id_fl_mask;
    AnimatedProgressBar progress_view;
    ImageView iv_night_toogle;
    public LinearLayout ll_title;//标题布局
    public LinearLayout ll_bottom;//底部布局
    protected SearchEditTextView mSearchEditText;//搜索按钮
    ImageView iv_go;//前往地址
    LinearLayout id_layout_no_network;//无网络状况下显示
    LinearLayout id_layout_network_error;//网址错误的时候显示布局


    //底部导航管理
    protected MainPopupWindow mPopupWindow;
    //webview管理
    protected WebViewManager mWebViewManager;
    protected BrowserWebView mWebView;
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


        initView();
        initData();

    }

    private void initView() {
        mSearchEditText = (SearchEditTextView) findViewById(R.id.id_et_search);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.id_swiperefresh);
        id_rl_main = (RelativeLayout) findViewById(R.id.id_rl_main);
        iv_back = (ImageView) findViewById(R.id.id_iv_back);
        iv_forward = (ImageView) findViewById(R.id.id_iv_forward);
        iv_home = (ImageView) findViewById(R.id.id_iv_home);
        iv_refresh = (ImageView) findViewById(R.id.id_iv_refresh);
        iv_more = (ImageView) findViewById(R.id.id_iv_more);

        progress_view = (AnimatedProgressBar) findViewById(R.id.progress_view);
        id_fl_mask = (FrameLayout) findViewById(R.id.id_fl_mask);
        iv_night_toogle = (ImageView) findViewById(R.id.id_iv_night_toogle);
        ll_title = (LinearLayout) findViewById(R.id.id_layout_title);
        ll_bottom = (LinearLayout) findViewById(R.id.id_layout_bottom);
        id_iv_history = (ImageView) findViewById(R.id.id_iv_history);
        iv_go = (ImageView) findViewById(R.id.id_iv_go);
        id_layout_no_network = (LinearLayout) findViewById(R.id.id_layout_no_network);
        id_layout_network_error = (LinearLayout) findViewById(R.id.id_layout_network_error);
        ll_ad_container = (LinearLayout) findViewById(R.id.ll_ad_container);

        swipeRefreshLayout.setColorSchemeResources(R.color.color_main_title);
        //添加蒙版
        mSearchEditText.setFrameLayout(id_fl_mask);
        mSearchEditText.setOnKeyListener(new SearchEditTextView.onKeyListener() {
            @Override
            public void onKey() {
                searchDmzj();
            }
        });

        iv_go.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        iv_more.setOnClickListener(this);
        iv_home.setOnClickListener(this);
        iv_forward.setOnClickListener(this);
        iv_refresh.setOnClickListener(this);
        id_iv_history.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    protected void initData() {

        screentHeight = MobileScreenUtils.getScreenHeight(mContext);
        nightModeTranslationY = (screentHeight - MetricsUtils.dipToPx(100)) / 2;
        boolean modeNight = SharedPreferencesHelper.getInstance(mContext).getBoolean(SharedPreferencesHelper.SP_KEY_MODE_NIGHT, false);
        if (!modeNight) {
            setDayMode();
        } else {
            setNightMode();
        }
        initWebViewManager();
        mWebView = mWebViewManager.getWebView();

        swipeRefreshLayout.addView(mWebView, new SwipeRefreshLayout.LayoutParams(SwipeRefreshLayout.LayoutParams.MATCH_PARENT, SwipeRefreshLayout.LayoutParams.MATCH_PARENT));
        Intent mIntent = getIntent();
        String url = mIntent.getStringExtra("url");
        if (TextUtils.isEmpty(url)) {
            loadUrl(BaseConstant.PATH);
        } else {
            loadUrl(url);
        }
        imgUrl = mIntent.getStringExtra("imgUrl");
        if (!TextUtils.isEmpty(imgUrl)) {
            String title = mIntent.getStringExtra("title");
        }
    }

    protected void initWebViewManager(){
        mWebViewManager = new WebViewManager(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String url = intent.getStringExtra("url");
        if (!TextUtils.isEmpty(url)) {
            loadUrl(url);
        }
        super.onNewIntent(intent);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.id_iv_back) {
            isPopupShowing();
            if (mWebView != null && mWebView.canGoBack()) {
                back();
            } else {
                backLastActivity();
            }

        } else if (id == R.id.id_iv_forward) {
            isPopupShowing();
            forward();

        } else if (id == R.id.id_iv_home) {
            isPopupShowing();
            //回到native的首页
            backLastActivity();

        } else if (id == R.id.id_iv_refresh) {
            isPopupShowing();
            refresh();

        } else if (id == R.id.id_iv_more) {
            if (mPopupWindow == null) {
                mPopupWindow = new MainPopupWindow(MainActivity.this, getIntent().getComponent().getPackageName() + ".activity.SplashActivity");
            }
            mPopupWindow.showPopupWindow(ll_bottom);

        } else if (id == R.id.id_iv_go) {//前往
            searchDmzj();
            InputWindowUtils.closeInputWindow(v, mContext);

        } else if (id == R.id.id_iv_history) {
            startActivity(new Intent(MainActivity.this, HistoryAndRecordsActivity.class));
            ActivitySlideAnim.slideInAnim(MainActivity.this);

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
        String searchEditText = mSearchEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(searchEditText)) {
            if (mWebView != null && !TextUtils.equals(searchEditText, mWebView.getUrl())) {
                mWebView.requestFocus();
                showNetworkAddressErrorLayout(false);
                showOrHiddrenLayoutNoNetwork(true);
                InputWindowUtils.closeInputWindow(mSearchEditText, mContext);
                if (Patterns.WEB_URL.matcher(searchEditText).matches()) {
                    loadUrl(searchEditText);
                } else {
                    loadUrl(BaseConstant.SEARCH_URL_GOOGLE + searchEditText + ".html");
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
            if (TextUtils.equals(url, BaseConstant.PATH)) {
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
            showInterstitialAd(progress);
        }
    }

    protected abstract void showInterstitialAd(int progress);

    @Override
    public void onPageStarted(String url) {
        //改变输入框的网址
        checkWebviewIsNull();
        try {
            if (!TextUtils.isEmpty(url) && !url.equals(BaseConstant.PATH)) {
                mSearchEditText.setText(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //友情提示一下
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode) {
        if (progress_view.getProgress() < 70) {
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
                //重新启动dmzjActivity
                toogleNightMode();
            }
        });
    }

    /***
     * 夜间模式
     */
    protected void toogleNightMode() {
        /*boolean modeNight = SharedPreferencesHelper.getInstance(mContext).getBoolean(SharedPreferencesHelper.SP_KEY_MODE_NIGHT, false);
        if (modeNight) {
            setDayMode();
        } else {
            setNightMode();
        }
        mPopupWindow.toogleNightMode(!modeNight);
        SharedPreferencesHelper.getInstance(mContext).putBoolean(SharedPreferencesHelper.SP_KEY_MODE_NIGHT, !modeNight);*/
    }

    //白天模式
    protected void setDayMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //设置底部和标题为白天底色
        ll_bottom.setBackgroundResource(R.color.color_popup_bg);
        ll_title.setBackgroundResource(R.color.color_popup_bg);
        id_rl_main.setBackgroundResource(android.R.color.white);
        //设置动画图片为月亮
        iv_night_toogle.setImageResource(R.mipmap.img_night_mode);
    }

    //使用夜间模式
    protected void setNightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        //设置底部和标题为夜间颜色
        ll_bottom.setBackgroundResource(R.color.color_while_night);
        ll_title.setBackgroundResource(R.color.color_while_night);
        id_rl_main.setBackgroundResource(android.R.color.white);
        //设置动画图片为太阳
        iv_night_toogle.setImageResource(R.mipmap.img_day_mode);
    }


    /**
     * 回到上一个页面
     */
    protected abstract void backLastActivity();
    /*{
        try {
            int size = ActivityUtils.activities.size();
            for (int i = size - 1; i >= 0; i--) {
                String simpleName = ActivityUtils.activities.get(i).getClass().getSimpleName();
                if (!simpleName.equals("com.gp.browser.video.dance.activity.DmzjActivity")) {
                    ActivityUtils.activities.get(i).finish();
                }
            }
            ActivitySlideAnim.slideOutAnim(MainActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mWebViewManager != null && mWebViewManager.mWebViewLongClickListener != null && mWebViewManager.mWebViewLongClickListener.popupWindow != null) {
            mWebViewManager.mWebViewLongClickListener.popupWindow.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onBackPressed(){
        if(backpressed()){
            super.onBackPressed();
        }
    }

    public abstract boolean backpressed();

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