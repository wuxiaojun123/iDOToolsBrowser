package com.gp.browser.video.lol.activity;

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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.base.browser.activity.BaseActivity;
import com.base.browser.activity.HistoryAndRecordsActivity;
import com.base.browser.activity.MainActivity;
import com.gp.browser.video.lol.bean.BannerResp;
import com.base.browser.minterface.OnItemClickListener;
import com.base.browser.utils.ActivitySlideAnim;
import com.base.browser.view.SearchEditTextView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.gp.browser.video.lol.adapter.DmzjRecyclerAdapter;
import com.gp.browser.video.lol.bean.DmzjBean;
import com.gp.browser.video.lol.manager.http.AppHttpClient;
import com.gp.browser.video.lol.utils.DoAnalyticsManager;
import com.gp.browser.video.lol.utils.JsonUtils;
import com.gp.browser.video.lol.utils.Constant;
import com.gp.browser.video.lol.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dmzj);
        ButterKnife.bind(this);
        setSwipeBackEnable(false);


        toogleNightMode();
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

    private void toogleNightMode() {
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
        if (flag) {
            mAppHttpClient.requestDmzjBeanList("get_category_posts", "mvideo", mpage, 14);
        } else {
            mAppHttpClient.requestDmzjBeanList("get_category_posts", "mvideo", mpage, 10);
        }
        mAppHttpClient.setOnLoadDmzjDataListener(new AppHttpClient.OnLoadDmzjDataListener() {

            @Override
            public void loadDmzjDataSuccessListener(DmzjBean bean) {
                totalPage = bean.pages;
                if (flag) {//加载更多
                    /*if (bean.posts.size() / 5 >= 1) {
                        bean.posts.add(5, null);
                    }*/
                    mDmzjAdapter.addMoreItem(bean.posts, DmzjRecyclerAdapter.LOAD_MORE_COMPILE);

                } else {//拉取最新
                    FileUtils.saveFile(mContext, Constant.FILE_MAIN_DATA, JsonUtils.toJsonFromList(bean.posts));
//                    bean.posts.add(7, null);
                    mDmzjAdapter.resetAdapter(bean.posts);//刷新界面数据
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

    @OnClick({R.id.id_iv_go, R.id.id_return_to_top, R.id.id_iv_history})
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
        }
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
            System.exit(0);
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
