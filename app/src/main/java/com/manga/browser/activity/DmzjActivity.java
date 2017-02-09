package com.manga.browser.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.idotools.utils.DeviceUtil;
import com.idotools.utils.FileUtils;
import com.idotools.utils.InputWindowUtils;
import com.idotools.utils.JudgeNetWork;
import com.idotools.utils.MetricsUtils;
import com.idotools.utils.MobileScreenUtils;
import com.idotools.utils.SharedPreferencesHelper;
import com.idotools.utils.ToastUtils;
import com.manga.browser.R;
import com.manga.browser.adapter.DmzjRecyclerAdapter;
import com.manga.browser.bean.BannerResp;
import com.manga.browser.bean.DmzjBeanResp;
import com.manga.browser.manager.http.AppHttpClient;
import com.manga.browser.manager.popupwindow.DmzjPopupWindow;
import com.manga.browser.manager.popupwindow.MainPopupWindow;
import com.manga.browser.minterface.OnItemClickListener;
import com.manga.browser.minterface.OnLoadBannerDataListener;
import com.manga.browser.minterface.OnLoadDmzjHotDataListener;
import com.manga.browser.minterface.OnLoadDmzjUpdateDataListener;
import com.manga.browser.utils.ActivitySlideAnim;
import com.manga.browser.utils.ActivityUtils;
import com.manga.browser.utils.Constant;
import com.manga.browser.utils.DoAnalyticsManager;
import com.manga.browser.utils.JsonUtils;
import com.manga.browser.utils.WebAddress;
import com.manga.browser.view.SearchEditTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 动漫之家首页
 * 采用retrofit和rxandroid把数据拉取下来
 * 使用recyclerView进行显示头部和下边布局
 * Created by wuxiaojun on 16-11-9.
 */
public class DmzjActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, OnItemClickListener {

    @BindView(R.id.id_layout_bottom)
    LinearLayout ll_bottom;// 底部布局
    @BindView(R.id.id_iv_night_toogle)
    ImageView iv_night_toogle;// 夜间模式切换
    @BindView(R.id.id_layout_title)
    LinearLayout ll_title;//标题布局

    @BindView(R.id.id_et_search)
    SearchEditTextView mSearchEditText;
    @BindView(R.id.id_swiperefresh)
    SwipeRefreshLayout id_swiperefresh;

    @BindView(R.id.id_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.id_return_to_top)
    ImageView id_return_to_top;
    @BindView(R.id.id_layout_no_network)
    LinearLayout id_layout_no_network;
    @BindView(R.id.id_fl_mask)
    FrameLayout id_fl_mask;

    /**
     * 底部导航
     */
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

    private int page = 1;//当前页
    private int lastVisiblePosition;
    private List<DmzjBeanResp.DmzjBean> mDmzjBeanList;
    private AppHttpClient mAppHttpClient;//网络请求业务类
    private DmzjRecyclerAdapter mDmzjAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private List<BannerResp.BannerBean> mBannerBeanList;//banner list


    //底部导航管理
    private DmzjPopupWindow mPopupWindow;
    //屏幕高度
    private int screentHeight;
    //夜间模式切换动画平移高度
    private int nightModeTranslationY;
    //动画集合
    private AnimatorSet mAnimationSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dmzj);
        setSwipeBackEnable(false);
        ButterKnife.bind(this);

        initView();
        initData();
        getGeTuiMsg();
    }

    /***
     * 加载数据
     */
    private void initData() {
        screentHeight = MobileScreenUtils.getScreenHeight(mContext);
        nightModeTranslationY = (screentHeight - MetricsUtils.dipToPx(100)) / 2;
        initCacheData();
        mAppHttpClient = new AppHttpClient();
        if (JudgeNetWork.isNetAvailable(mContext)) {
            id_layout_no_network.setVisibility(View.GONE);
            id_swiperefresh.setRefreshing(true);
            loadUpdateData(page, false);
            loadBannerData();
            loadHotData();

        } else {
            if (mBannerBeanList == null)
                id_layout_no_network.setVisibility(View.VISIBLE);
        }
    }

    /***
     * 加载banner数据
     */
    private void loadBannerData() {
        mAppHttpClient.requestBannerPath(DeviceUtil.getPackageName(mContext), DeviceUtil.getVersionCode(mContext));
        mAppHttpClient.setOnLoadBannerDataListener(new OnLoadBannerDataListener() {
            @Override
            public void loadBannerDataSuccessListener(List<BannerResp.BannerBean> list) {
                if (list != null && !list.isEmpty()) {
                    mBannerBeanList = list;
                    mDmzjAdapter.setBannerBeanList(mBannerBeanList);
                    //将最新数据保存到file中
                    FileUtils.saveFile(mContext, Constant.FILE_BANNER, JsonUtils.toJsonFromList(list));
                }
            }

            @Override
            public void loadBannerDataFailedListener() {
                if (mDmzjAdapter != null) {
                    mDmzjAdapter.changeAddMoreStatus(Constant.LOAD_MORE_COMPILE);
                }
            }
        });
    }

    /***
     * 拉取最新的动漫之家的数据
     */
    private void loadUpdateData(int mpage, final boolean flag) {
        mAppHttpClient.requestDmzjUpdateBeanList(mpage, 14);
        mAppHttpClient.setOnLoadDmzjUpdateDataListener(new OnLoadDmzjUpdateDataListener() {
            @Override
            public void loadDmzjDataSuccessListener(DmzjBeanResp resp) {
                List<DmzjBeanResp.DmzjBean> list = resp.comics;
                if (flag) {
                    if (list.size() / 7 >= 1) {
                        list.add(7, null);
                    }
                    if (list.size() / 7 == 2) {
                        list.add(null);
                    }
                    //加载更多
                    mDmzjAdapter.addMoreItem(list, Constant.LOAD_MORE_COMPILE);
                } else {
                    //拉取最新
                    FileUtils.saveFile(mContext, Constant.FILE_UPDATE_DATA, JsonUtils.toJsonFromList(list));
                    list.add(7, null);
                    mDmzjAdapter.resetAdapter(list);//刷新界面数据
                }
                id_swiperefresh.setRefreshing(false);//刷新完成
            }

            @Override
            public void loadDmzjDataFailedListener() {
                id_swiperefresh.setRefreshing(false);//刷新完成
                if (flag)
                    mDmzjAdapter.changeAddMoreStatus(Constant.LOAD_MORE_COMPILE);
            }
        });
    }

    /***
     * 拉取热门的动漫之家的数据
     */
    private void loadHotData() {
        mAppHttpClient.requestDmzjHotBeanList(1, 18);
        mAppHttpClient.setOnLoadDmzjHotDataListener(new OnLoadDmzjHotDataListener() {
            @Override
            public void loadDmzjDataSuccessListener(DmzjBeanResp resp) {
                //拉取热门的数据
                FileUtils.saveFile(mContext, Constant.FILE_HOT_DATA, JsonUtils.toJsonFromList(resp.comics));
                mDmzjAdapter.setHeadView2Data(resp.comics);
            }

            @Override
            public void loadDmzjDataFailedListener() {
            }
        });
    }

    /***
     * 使用缓存数据
     */
    private void initCacheData() {
        try {
            String bannerData = FileUtils.readFile(mContext, Constant.FILE_BANNER);
            if (!TextUtils.isEmpty(bannerData)) {
                List<BannerResp.BannerBean> list = JsonUtils.fromJsonArray(bannerData, BannerResp.BannerBean.class);
                if (list != null && !list.isEmpty()) {
                    mBannerBeanList = list;
                    mDmzjAdapter.setBannerBeanList(mBannerBeanList);
                }
            }
            String mainData = FileUtils.readFile(mContext, Constant.FILE_UPDATE_DATA);
            if (!TextUtils.isEmpty(mainData)) {
                List<DmzjBeanResp.DmzjBean> list = JsonUtils.fromJsonArray(mainData, DmzjBeanResp.DmzjBean.class);
                if (list != null && !list.isEmpty()) {
                    mDmzjAdapter.resetAdapter(list);//刷新界面数据
                    id_swiperefresh.setRefreshing(false);//刷新完成
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /***
     * 初始化view
     */
    private void initView() {
        initNightMode();
        initRecycler();
        initScrollListener();
        // 设置下拉刷新样式
        id_swiperefresh.setColorSchemeResources(R.color.color_main_title);
        id_swiperefresh.setOnRefreshListener(this);
        // 设置搜索
        mSearchEditText.setFrameLayout(id_fl_mask);
        mSearchEditText.setOnKeyListener(new SearchEditTextView.onKeyListener() {
            @Override
            public void onKey() {
                searchDmzj();
            }
        });
        // 设置底部导航样式
        iv_back.setImageResource(R.mipmap.img_back_normal);
        iv_forward.setImageResource(R.mipmap.img_forward_normal);
        iv_home.setImageResource(R.mipmap.img_home_normal);

    }

    private void initNightMode() {
        boolean modeNight = SharedPreferencesHelper.getInstance(mContext).getBoolean(SharedPreferencesHelper.SP_KEY_MODE_NIGHT, false);
        if (modeNight) { // 夜间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            iv_night_toogle.setImageResource(R.mipmap.img_day_mode);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            iv_night_toogle.setImageResource(R.mipmap.img_night_mode);
        }
    }

    /**
     * 初始化recyclerView
     */
    private void initRecycler() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        mDmzjBeanList = new ArrayList<>();
        mDmzjAdapter = new DmzjRecyclerAdapter(this, mDmzjBeanList);
        mDmzjAdapter.setFooterView(LayoutInflater.from(mContext).inflate(R.layout.layout_footer, recyclerView, false));
        mDmzjAdapter.setFragmentManager(getSupportFragmentManager());
        recyclerView.setAdapter(mDmzjAdapter);
        mDmzjAdapter.setOnItemClickListener(this);
    }

    /***
     * 添加滑动事件的监听，以便可以出现加载更多
     */
    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && (lastVisiblePosition + 1) == mDmzjAdapter.getItemCount()) {
                    if (JudgeNetWork.isNetAvailable(mContext)) {
                        mDmzjAdapter.changeAddMoreStatus(Constant.LOAD_MORE_LOADING);
                        page += 1;
                        loadUpdateData(page, true);
                    } else {
                        mDmzjAdapter.changeAddMoreStatus(Constant.LOAD_MORE_COMPILE);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                lastVisiblePosition = mLinearLayoutManager.findLastVisibleItemPosition();
                if (lastVisiblePosition > 8) {
                    id_return_to_top.setVisibility(View.VISIBLE);
                } else {
                    id_return_to_top.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        refresh();
    }


    /***
     * 刷新数据
     */
    public void refresh() {
        if (JudgeNetWork.isNetAvailable(mContext)) {
            if (id_layout_no_network.getVisibility() == View.VISIBLE) {
                id_layout_no_network.setVisibility(View.GONE);
            }
            page = 1;
            loadUpdateData(page, false);
            loadHotData();
            if (mDmzjAdapter != null && mBannerBeanList == null) {
                loadBannerData();
            }
        } else {
            id_swiperefresh.setRefreshing(false);
        }
    }

    @OnClick({R.id.id_iv_go, R.id.id_return_to_top, R.id.id_iv_history, R.id.id_iv_refresh, R.id.id_iv_more})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_iv_go:
                //前往mainActivity
                searchDmzj();

                break;
            case R.id.id_return_to_top:
                //返回顶部
                if (mDmzjAdapter != null && !mDmzjBeanList.isEmpty()) {
                    recyclerView.scrollToPosition(0);
                }

                break;
            case R.id.id_iv_history:
                //历史记录
                startActivity(new Intent(DmzjActivity.this, HistoryAndRecordsActivity.class));
                ActivitySlideAnim.slideInAnim(DmzjActivity.this);
                DoAnalyticsManager.event(mContext, DoAnalyticsManager.DOT_KEY_HISTORY_CLICK);

                break;
            case R.id.id_iv_refresh:
                // 刷新
                id_swiperefresh.setRefreshing(true);
                refresh();

                break;
            case R.id.id_iv_more:
                // 更多
                if (mPopupWindow == null) {
                    mPopupWindow = new DmzjPopupWindow(DmzjActivity.this);
                }
                mPopupWindow.showPopupWindow(ll_bottom);

                break;
        }
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
    public void setDayMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //设置底部和标题为白天底色
        ll_bottom.setBackgroundResource(R.color.color_popup_bg);
        ll_title.setBackgroundResource(R.color.color_popup_bg);
        recyclerView.setBackgroundResource(android.R.color.white);
        //设置动画图片为月亮
        iv_night_toogle.setImageResource(R.mipmap.img_night_mode);
        mDmzjAdapter.refreshNightMode(1);
    }

    //使用夜间模式
    public void setNightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        //设置底部和标题为夜间颜色
        ll_bottom.setBackgroundResource(R.color.color_while_night);
        ll_title.setBackgroundResource(R.color.color_while_night);
        recyclerView.setBackgroundResource(R.color.color_while_night);
        //设置动画图片为太阳
        iv_night_toogle.setImageResource(R.mipmap.img_day_mode);
        mDmzjAdapter.refreshNightMode(2);
    }

    /***
     * 搜索
     */
    private void searchDmzj() {
        String text = mSearchEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {
            if (text.contains(".")) { // 包含了"."的文本，先判断是否为网址，如果不是则判断是否开头为http，如果是
                try {
                    WebAddress webAddress = new WebAddress(text);
                    goToMainActivity(webAddress.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    goToMainActivity(Constant.SEARCH_URL_DMZJ + text + ".html");
                }
            } else {
                goToMainActivity(Constant.SEARCH_URL_DMZJ + text + ".html");
            }
            mSearchEditText.setText(null);
            InputWindowUtils.closeInputWindow(mSearchEditText, mContext);
        }
    }

    @Override
    public void onItemClickListener(String url, String imgUrl, String title) {
        Intent mIntent = new Intent(DmzjActivity.this, MainActivity.class);
        mIntent.putExtra("url", url);
        mIntent.putExtra("imgUrl", imgUrl);
        mIntent.putExtra("title", title);
        startActivity(mIntent);
        ActivitySlideAnim.slideInAnim(DmzjActivity.this);
    }

    private void goToMainActivity(String url) {
        Intent mIntent = new Intent(DmzjActivity.this, MainActivity.class);
        mIntent.putExtra("url", url);
        startActivity(mIntent);
        ActivitySlideAnim.slideInAnim(DmzjActivity.this);
    }

    /***
     * 获取个推消息是否前往mainActivity页面
     */
    public void getGeTuiMsg() {
        String url = getIntent().getStringExtra("url");
        if (!TextUtils.isEmpty(url)) {
            goToMainActivity(url);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String url = intent.getStringExtra("url");
        if (!TextUtils.isEmpty(url)) {
            goToMainActivity(url);
        }
        super.onNewIntent(intent);
    }

    @Override
    public void onBackPressed() {
        if (id_fl_mask.getVisibility() == View.VISIBLE) {
            mSearchEditText.backKey();
        } else {
            //提示用户是否退出
            exit();
        }
    }

    private long mExitTime;

    /**
     * 退出应用
     */
    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            ToastUtils.show(mContext, R.string.string_press_again);
            mExitTime = System.currentTimeMillis();
        } else {
            //System.exit(0);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        /*if (mDmzjAdapter != null && mViewPagerManager != null) {
            mViewPagerManager.destroyViewPager();
        }*/
        super.onDestroy();
    }


}
