package com.browser.dmzj.yinyangshi.activity;

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

import com.base.browser.activity.BaseActivity;
import com.base.browser.activity.HistoryAndRecordsActivity;
import com.base.browser.utils.WebAddress;
import com.base.browser.view.SearchEditTextView;
import com.idotools.utils.DeviceUtil;
import com.idotools.utils.FileUtils;
import com.idotools.utils.InputWindowUtils;
import com.idotools.utils.JudgeNetWork;
import com.idotools.utils.MetricsUtils;
import com.idotools.utils.MobileScreenUtils;
import com.idotools.utils.SharedPreferencesHelper;
import com.idotools.utils.ToastUtils;
import com.browser.dmzj.yinyangshi.R;
import com.browser.dmzj.yinyangshi.adapter.DmzjRecyclerAdapter;
import com.browser.dmzj.yinyangshi.bean.BannerResp;
import com.browser.dmzj.yinyangshi.bean.DmzjBeanResp;
import com.browser.dmzj.yinyangshi.manager.http.AppHttpClient;
import com.browser.dmzj.yinyangshi.manager.http.BannerDataAssets;
import com.browser.dmzj.yinyangshi.manager.popupwindow.DmzjPopupWindow;
import com.browser.dmzj.yinyangshi.minterface.OnItemClickListener;
import com.browser.dmzj.yinyangshi.minterface.OnLoadBannerDataListener;
import com.browser.dmzj.yinyangshi.minterface.OnLoadDmzjHotDataListener;
import com.browser.dmzj.yinyangshi.minterface.OnLoadDmzjUpdateDataListener;
import com.browser.dmzj.yinyangshi.utils.ActivitySlideAnim;
import com.browser.dmzj.yinyangshi.utils.Constant;
import com.browser.dmzj.yinyangshi.utils.DoAnalyticsManager;
import com.browser.dmzj.yinyangshi.utils.JsonUtils;

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
    public ImageView iv_night_toogle;// 夜间模式切换
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
    //    @BindView(R.id.id_layout_no_network)
//    LinearLayout id_layout_no_network;
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
        // 必须先执行上边之后再跳转到浏览器首页，因为用户点击返回的时候需要显示内容
        String url = getIntent().getStringExtra("url");
        if (!TextUtils.isEmpty(url)) {
            goToMainActivity(url);
        }
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
//            id_layout_no_network.setVisibility(View.GONE);
            id_swiperefresh.setRefreshing(true);
            loadUpdateData(page, false);
            loadBannerData();
            loadHotData();

        } else {
//            if (mBannerBeanList == null)
//                id_layout_no_network.setVisibility(View.VISIBLE);
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
                BannerDataAssets mBannerDataAssets = new BannerDataAssets();
                List<BannerResp.BannerBean> list = mBannerDataAssets.parseJsonToBanner(mContext);
                if (list != null && !list.isEmpty()) {
                    mBannerBeanList = list;
                    mDmzjAdapter.setBannerBeanList(mBannerBeanList);
                    FileUtils.saveFile(mContext, Constant.FILE_BANNER, JsonUtils.toJsonFromList(list));
                }
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
        mAppHttpClient.requestDmzjUpdateBeanList(mpage, 8);
        mAppHttpClient.setOnLoadDmzjUpdateDataListener(new OnLoadDmzjUpdateDataListener() {
            @Override
            public void loadDmzjDataSuccessListener(DmzjBeanResp resp) {
                List<DmzjBeanResp.DmzjBean> list = resp.comics;
                if (list.size() / 4 >= 1) {
                    list.add(4, null);
                }
                if (list.size() / 4 == 2) {
                    list.add(null);
                }
                if (flag) {
                    //加载更多
                    mDmzjAdapter.addMoreItem(list, Constant.LOAD_MORE_COMPILE);
                } else {
                    //拉取最新
                    FileUtils.saveFile(mContext, Constant.FILE_UPDATE_DATA, JsonUtils.toJsonFromList(list));
//                    list.add(4, null);
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
        mAppHttpClient.requestDmzjHotBeanList(1, 19);
        mAppHttpClient.setOnLoadDmzjHotDataListener(new OnLoadDmzjHotDataListener() {
            @Override
            public void loadDmzjDataSuccessListener(DmzjBeanResp resp) { // 拉取热门的数据
                // 手动删除一拳超人漫画 id为9949
                List<DmzjBeanResp.DmzjBean> comics = resp.comics;
                int size = comics.size();
                for (int i = 0; i < size; i++) {
                    if ("9949".equals(comics.get(i).id)) {
                        comics.remove(i);
                        break;
                    }
                }
                FileUtils.saveFile(mContext, Constant.FILE_HOT_DATA, JsonUtils.toJsonFromList(comics));
                mDmzjAdapter.setHeadView2Data(comics);
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

            String hotData = FileUtils.readFile(mContext, Constant.FILE_HOT_DATA);
            if (!TextUtils.isEmpty(hotData)) { //mDmzjAdapter.setHeadView2Data(comics);
                List<DmzjBeanResp.DmzjBean> list = JsonUtils.fromJsonArray(hotData, DmzjBeanResp.DmzjBean.class);
                if (list != null && !list.isEmpty()) {
                    mDmzjAdapter.setHeadView2Data(list);
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
            /*if (id_layout_no_network.getVisibility() == View.VISIBLE) {
                id_layout_no_network.setVisibility(View.GONE);
            }*/
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
     *
     * @param isNightMode true 表示为夜间模式  false  表示白天模式
     */
    public void startDayNightModeToogleAnim(boolean isNightMode) {
        if (isNightMode) {
            iv_night_toogle.setImageResource(R.mipmap.img_day_mode);
        } else {
            iv_night_toogle.setImageResource(R.mipmap.img_night_mode);
        }
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
        Intent mIntent = new Intent(DmzjActivity.this, BrowserActivity.class);
        mIntent.putExtra("url", url);
        mIntent.putExtra("imgUrl", imgUrl);
        mIntent.putExtra("title", title);
        startActivity(mIntent);
        ActivitySlideAnim.slideInAnim(DmzjActivity.this);
    }

    private void goToMainActivity(String url) {
        Intent mIntent = new Intent(DmzjActivity.this, BrowserActivity.class);
        mIntent.putExtra("url", url);
        startActivity(mIntent);
        ActivitySlideAnim.slideInAnim(DmzjActivity.this);
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
    protected void onResume() {
        super.onResume();
        DoAnalyticsManager.pageResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DoAnalyticsManager.pagePause(this);
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