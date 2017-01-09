package com.gp.browser.activity;

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

import com.gp.browser.R;
import com.gp.browser.adapter.DmzjRecyclerAdapter;
import com.gp.browser.bean.BannerResp;
import com.gp.browser.bean.DmzjBean;
import com.gp.browser.manager.http.AppHttpClient;
import com.gp.browser.minterface.OnItemClickListener;
import com.gp.browser.utils.ActivitySlideAnim;
import com.gp.browser.utils.Constant;
import com.gp.browser.utils.JsonUtils;
import com.gp.browser.view.SearchEditTextView;
import com.gp.utils.DeviceUtil;
import com.gp.utils.FileUtils;
import com.gp.utils.JudgeNetWork;
import com.gp.utils.SharedPreferencesHelper;
import com.gp.utils.ToastUtils;
import com.igexin.sdk.PushManager;

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
    @BindView(R.id.id_fl_mask)
    FrameLayout id_fl_mask;

    private int page = 1;//当前页
    private int lastVisiblePosition;
    private List<DmzjBean> mDmzjBeanList;
    private AppHttpClient mAppHttpClient;//网络请求业务类
    private DmzjRecyclerAdapter mDmzjAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PushManager.getInstance().initialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dmzj);
        setSwipeBackEnable(false);
        ButterKnife.bind(this);

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
                List<DmzjBean> list = JsonUtils.fromJsonArray(mainData, DmzjBean.class);
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
                        mDmzjAdapter.changeAddMoreStatus(DmzjRecyclerAdapter.LOAD_MORE_LOADING);
                        page += 1;
                        loadLatestData(page, true);
                    } else {
                        mDmzjAdapter.changeAddMoreStatus(DmzjRecyclerAdapter.LOAD_MORE_COMPILE);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisiblePosition = mLinearLayoutManager.findLastVisibleItemPosition();
                if (lastVisiblePosition > 8) {
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
            if (mDmzjAdapter.mBannerBeanList == null)
                id_layout_no_network.setVisibility(View.VISIBLE);
        }
    }

    /***
     * 加载banner数据
     */
    private void loadBannerData() {
        //browser00 DeviceUtil.getVersionCode(mContext)
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
            mAppHttpClient.requestDmzjBeanList(Constant.DMZJ_TAG, Constant.DMZJ_TYPE_UPDATE, mpage, 14);
        } else {
            mAppHttpClient.requestDmzjBeanList(Constant.DMZJ_TAG, Constant.DMZJ_TYPE_UPDATE, mpage, 10);
        }
        mAppHttpClient.setOnLoadDmzjDataListener(new AppHttpClient.OnLoadDmzjDataListener() {
            @Override
            public void loadDmzjDataSuccessListener(List<DmzjBean> list) {
                if (flag) {
                    if (list.size() / 7 >= 1) {
                        list.add(7, null);
                    }
                    if (list.size() / 7 == 2) {
                        list.add(null);
                    }
                    //加载更多
                    mDmzjAdapter.addMoreItem(list, DmzjRecyclerAdapter.LOAD_MORE_COMPILE);
                } else {
                    //拉取最新
                    FileUtils.saveFile(mContext, Constant.FILE_MAIN_DATA, JsonUtils.toJsonFromList(list));
                    list.add(7, null);
                    mDmzjAdapter.resetAdapter(list);//刷新界面数据
                }
                id_swiperefresh.setRefreshing(false);//刷新完成
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
                if (Constant.VERSION_COUNTRY_GP) {
                    goToMainActivity(Constant.SEARCH_URL_DMZJ + text + ".html");
                } else {
                    goToMainActivity(Constant.SEARCH_URL_BAIDU + text);
                }
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
        if (mDmzjAdapter != null && mDmzjAdapter.mViewPagerManager != null) {
            mDmzjAdapter.mViewPagerManager.destroyViewPager();
        }
        super.onDestroy();
    }


}
