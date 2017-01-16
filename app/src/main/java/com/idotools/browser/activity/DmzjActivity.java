package com.idotools.browser.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.idotools.browser.R;
import com.idotools.browser.adapter.DmzjRecyclerAdapter;
import com.idotools.browser.bean.BannerResp;
import com.idotools.browser.bean.DmzjBeanResp;
import com.idotools.browser.manager.http.AppHttpClient;
import com.idotools.browser.minterface.OnItemClickListener;
import com.idotools.browser.minterface.OnLoadBannerDataListener;
import com.idotools.browser.minterface.OnLoadDmzjHotDataListener;
import com.idotools.browser.minterface.OnLoadDmzjUpdateDataListener;
import com.idotools.browser.utils.ActivitySlideAnim;
import com.idotools.browser.utils.Constant;
import com.idotools.browser.utils.DoAnalyticsManager;
import com.idotools.browser.utils.JsonUtils;
import com.idotools.browser.utils.WebAddress;
import com.idotools.browser.view.SearchEditTextView;
import com.idotools.utils.DeviceUtil;
import com.idotools.utils.FileUtils;
import com.idotools.utils.InputWindowUtils;
import com.idotools.utils.JudgeNetWork;
import com.idotools.utils.LogUtils;
import com.idotools.utils.SharedPreferencesHelper;
import com.idotools.utils.ToastUtils;
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

    private int page = 1;//当前页
    private int lastVisiblePosition;
    private List<DmzjBeanResp.DmzjBean> mDmzjBeanList;
    private AppHttpClient mAppHttpClient;//网络请求业务类
    private DmzjRecyclerAdapter mDmzjAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private List<BannerResp.BannerBean> mBannerBeanList;//banner list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PushManager.getInstance().initialize(getApplicationContext());
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
                    mDmzjAdapter.changeAddMoreStatus(DmzjRecyclerAdapter.LOAD_MORE_COMPILE);
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
                    mDmzjAdapter.addMoreItem(list, DmzjRecyclerAdapter.LOAD_MORE_COMPILE);
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
                    mDmzjAdapter.changeAddMoreStatus(DmzjRecyclerAdapter.LOAD_MORE_COMPILE);
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
        toogleNightMode();
        initRecycler();
        initScrollListener();
        id_swiperefresh.setColorSchemeResources(R.color.color_main_title);
        id_swiperefresh.setOnRefreshListener(this);
        mSearchEditText.setFrameLayout(id_fl_mask);
        mSearchEditText.setOnKeyListener(new SearchEditTextView.onKeyListener() {
            @Override
            public void onKey() {
                searchDmzj();
            }
        });
    }

    private void toogleNightMode() {
        boolean modeNight = SharedPreferencesHelper.getInstance(mContext).getBoolean(SharedPreferencesHelper.SP_KEY_MODE_NIGHT, false);
        if (!modeNight) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
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
                        mDmzjAdapter.changeAddMoreStatus(DmzjRecyclerAdapter.LOAD_MORE_LOADING);
                        page += 1;
                        loadUpdateData(page, true);
                    } else {
                        mDmzjAdapter.changeAddMoreStatus(DmzjRecyclerAdapter.LOAD_MORE_COMPILE);
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
                DoAnalyticsManager.event(mContext, DoAnalyticsManager.DOT_KEY_HISTORY_CLICK);

                break;
        }
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
