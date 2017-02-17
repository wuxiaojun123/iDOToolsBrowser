package com.gp.browser.video.dance.activity;

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
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.base.browser.activity.BaseActivity;
import com.base.browser.activity.HistoryAndRecordsActivity;
import com.base.browser.activity.MainActivity;
import com.gp.browser.video.dance.bean.BannerResp;
import com.gp.browser.video.dance.bean.DmzjBean;
import com.gp.browser.video.dance.manager.http.AppHttpClient;
import com.base.browser.minterface.OnItemClickListener;
import com.base.browser.utils.ActivitySlideAnim;
import com.base.browser.view.SearchEditTextView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.gp.browser.video.dance.adapter.DmzjRecyclerAdapter;
import com.gp.browser.video.dance.manager.popupwindow.DmzjPopupWindow;
import com.gp.browser.video.dance.utils.DoAnalyticsManager;
import com.gp.browser.video.dance.utils.JsonUtils;
import com.gp.browser.video.dance.utils.Constant;
import com.gp.browser.video.dance.R;
import com.idotools.utils.DeviceUtil;
import com.idotools.utils.FileUtils;
import com.idotools.utils.JudgeNetWork;
import com.idotools.utils.SharedPreferencesHelper;
import com.idotools.utils.ToastUtils;

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


    @BindView(R.id.id_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.id_swiperefresh)
    SwipeRefreshLayout id_swiperefresh;
    @BindView(R.id.id_et_search)
    SearchEditTextView mSearchEditText;
    @BindView(R.id.id_return_to_top)
    ImageView id_return_to_top;
    @BindView(R.id.id_layout_no_network)
    LinearLayout id_layout_no_network;
    @BindView(R.id.id_iv_history)//历史记录
            ImageView id_iv_history;
    @BindView(R.id.id_fl_mask)
    FrameLayout id_fl_mask;

    private LinearLayoutManager mLinearLayoutManager;
    private DmzjRecyclerAdapter mDmzjAdapter;
    private List<DmzjBean.PostsBean> mDmzjBeanList;
    private int lastVisiblePosition;
    private AppHttpClient mAppHttpClient;//网络请求业务类
    private int page = 1;//当前页
    private int totalPage;//总页数

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
        ButterKnife.bind(this);
        setSwipeBackEnable(false);


        initToogleNightMode();
        initView();
        initRecycler();
        initLastTimeData();
        initData();
        loadMore();
        getGeTuiMsg();

        mSearchEditText.setFrameLayout(id_fl_mask);
        mSearchEditText.setOnKeyListener(new SearchEditTextView.onKeyListener() {
            @Override
            public void onKey() {
                searchDmzj();
            }
        });
    }

    /***
     * 使用缓存数据
     */
    private void initLastTimeData() {
        try {
            String bannerData = FileUtils.readFile(mContext, Constant.FILE_BANNER);
            if (!TextUtils.isEmpty(bannerData)) {
                List<BannerResp.BannerBean> list = JsonUtils.fromJsonArray(bannerData, BannerResp.BannerBean.class);
                if (list != null && !list.isEmpty()) {
                    mDmzjAdapter.mBannerBeanList = list;
                    mDmzjAdapter.notifyDataSetChanged();
                }
            }
            String mainData = FileUtils.readFile(mContext, Constant.FILE_MAIN_DATA);
            if (!TextUtils.isEmpty(mainData)) {
                List<DmzjBean.PostsBean> list = JsonUtils.fromJsonArray(mainData, DmzjBean.PostsBean.class);
                if (list != null && !list.isEmpty()) {
                    mDmzjAdapter.resetAdapter(list);//刷新界面数据
                    id_swiperefresh.setRefreshing(false);//刷新完成
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initToogleNightMode() {
        boolean modeNight = SharedPreferencesHelper.getInstance(mContext).getBoolean(SharedPreferencesHelper.SP_KEY_MODE_NIGHT, false);
        if (!modeNight) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    /***
     * 添加滑动事件的监听，以便可以出现加载更多
     */
    private void loadMore() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && (lastVisiblePosition + 1) == mDmzjAdapter.getItemCount()) {
                    if (JudgeNetWork.isNetAvailable(mContext)) {
                        page += 1;
                        if (page <= totalPage) {
                            loadLatestData(page, true);
                            mDmzjAdapter.changeAddMoreStatus(DmzjRecyclerAdapter.LOAD_MORE_LOADING);
                        }
                    } else {
                        mDmzjAdapter.changeAddMoreStatus(DmzjRecyclerAdapter.LOAD_MORE_COMPILE);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisiblePosition = mLinearLayoutManager.findLastVisibleItemPosition();
                if (lastVisiblePosition > 6) {
                    id_return_to_top.setVisibility(View.VISIBLE);
                } else {
                    id_return_to_top.setVisibility(View.GONE);
                }
            }
        });
    }

    /***
     * 初始化view
     */
    private void initView() {
        id_swiperefresh.requestFocus();
        id_swiperefresh.setColorSchemeResources(R.color.color_main_title);
        id_swiperefresh.setOnRefreshListener(this);

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
        recyclerView.setAdapter(mDmzjAdapter);
        recyclerView.setHasFixedSize(true);
        mDmzjAdapter.setOnItemClickListener(this);
    }

    /***
     * 加载数据
     */
    private void initData() {
        mAppHttpClient = new AppHttpClient();
        if (JudgeNetWork.isNetAvailable(mContext)) {
            id_layout_no_network.setVisibility(View.GONE);
            id_swiperefresh.setRefreshing(true);
            loadLatestData(page, false);
            loadBannerData();
        } else {
            id_layout_no_network.setVisibility(View.VISIBLE);
        }
    }


    /***
     * 加载banner数据
     */
    private void loadBannerData() {
        mAppHttpClient.requestBannerPath(DeviceUtil.getPackageName(mContext), DeviceUtil.getVersionCode(mContext));
        mAppHttpClient.setOnLoadBannerDataListener(new AppHttpClient.OnLoadBannerDataListener() {
            @Override
            public void loadBannerDataSuccessListener(List<BannerResp.BannerBean> list) {
                if (list != null && !list.isEmpty()) {
                    mDmzjAdapter.mBannerBeanList = list;
                    mDmzjAdapter.notifyDataSetChanged();
                    //将最新数据保存到file中
                    FileUtils.saveFile(mContext, Constant.FILE_BANNER, JsonUtils.toJsonFromList(list));
                }
            }

            @Override
            public void loadBannerDataFailedListener() {
                if (mDmzjAdapter != null) {
                    mDmzjAdapter.changeAddMoreStatus(DmzjRecyclerAdapter.LOAD_MORE_COMPILE);
                }
            }
        });
    }

    /***
     * 拉取最新的动漫之家的数据
     */
    private void loadLatestData(int mpage, final boolean flag) {
        mAppHttpClient.requestDmzjBeanList("get_category_posts", "topfans", mpage, 14);
        mAppHttpClient.setOnLoadDmzjDataListener(new AppHttpClient.OnLoadDmzjDataListener() {

            @Override
            public void loadDmzjDataSuccessListener(DmzjBean bean) {
                totalPage = bean.pages;
                List<DmzjBean.PostsBean> list = bean.posts;
                if (flag) { // 加载更多
                    if (list.size() / 7 >= 1) {
                        list.add(7, null);
                    }
                    if (list.size() / 7 == 2) {
                        list.add(null);
                    }
                    mDmzjAdapter.addMoreItem(list, DmzjRecyclerAdapter.LOAD_MORE_COMPILE);

                } else { // 拉取最新
                    FileUtils.saveFile(mContext, Constant.FILE_MAIN_DATA, JsonUtils.toJsonFromList(list));
                    list.add(7, null);
                    list.add(null);
                    mDmzjAdapter.resetAdapter(list);//刷新界面数据
                    id_swiperefresh.setRefreshing(false);//刷新完成
                }
            }

            @Override
            public void loadDmzjDataFailedListener() {
                id_swiperefresh.setRefreshing(false);//刷新完成
                if (flag)
                    mDmzjAdapter.changeAddMoreStatus(DmzjRecyclerAdapter.LOAD_MORE_COMPILE);
            }
        });
    }


    @Override
    public void onRefresh() {
        refresh();
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
     * 刷新数据
     */
    public void refresh() {
        if (JudgeNetWork.isNetAvailable(mContext)) {
            if (id_layout_no_network.getVisibility() == View.VISIBLE) {
                id_layout_no_network.setVisibility(View.GONE);
            }
            page = 1;
            loadLatestData(page, false);
            if (mDmzjAdapter != null && mDmzjAdapter.mBannerBeanList == null) {
                loadBannerData();
            }
        } else {
            id_swiperefresh.setRefreshing(false);
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
//        mDmzjAdapter.refreshNightMode(1);
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
//        mDmzjAdapter.refreshNightMode(2);
    }

    /***
     * 搜索
     */
    private void searchDmzj() {
        String text = mSearchEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {
            if (Patterns.WEB_URL.matcher(text).matches()) {
                goToMainActivity(text);
            } else {
                goToMainActivity(Constant.SEARCH_URL + text);
                mSearchEditText.setText(null);
            }
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

    /***
     * 获取通知栏消息是否前往mainActivity页面
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
    protected void onResume() {
        super.onResume();
        DoAnalyticsManager.getInstance(mContext).pageEvent(FirebaseAnalytics.Event.APP_OPEN);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        //提示用户是否退出
        exit();
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
//            System.exit(0);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (mDmzjAdapter != null && mDmzjAdapter.mViewPagerManager != null) {
            mDmzjAdapter.mViewPagerManager.destroyViewPager();
        }
        super.onDestroy();
    }

}
