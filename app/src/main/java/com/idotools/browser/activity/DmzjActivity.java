package com.idotools.browser.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.idotools.browser.R;
import com.idotools.browser.adapter.DmzjRecyclerAdapter;
import com.idotools.browser.bean.BannerResp;
import com.idotools.browser.bean.DmzjBean;
import com.idotools.browser.manager.http.AppHttpClient;
import com.idotools.browser.minterface.OnItemClickListener;
import com.idotools.browser.utils.ActivitySlideAnim;
import com.idotools.browser.utils.ActivityUtils;
import com.idotools.browser.utils.Constant;
import com.idotools.browser.utils.DoAnalyticsManager;
import com.idotools.utils.DeviceUtil;
import com.idotools.utils.SharedPreferencesHelper;
import com.idotools.utils.ToastUtils;
import com.igexin.sdk.PushManager;
import com.ta.utdid2.android.utils.NetworkUtils;

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
    EditText id_et_search;
    @BindView(R.id.id_iv_start_page)
    ImageView id_iv_start_page;
    @BindView(R.id.id_return_to_top)
    ImageView id_return_to_top;

    private LinearLayoutManager mLinearLayoutManager;
    private DmzjRecyclerAdapter mDmzjAdapter;
    private List<DmzjBean> mDmzjBeanList;
    private int lastVisiblePosition;
    private AppHttpClient mAppHttpClient;//网络请求业务类
    private int page = 1;//当前页

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PushManager.getInstance().initialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dmzj);
        ButterKnife.bind(this);
        startPageAnim();
        toogleNightMode();
        initView();
        initRecycler();
        initData();
        loadMore();
        getGeTuiMsg();
    }

    private void startPageAnim() {
        if (ActivityUtils.getSizie() <= 1) {
            id_iv_start_page.animate().alpha(0f).scaleX(3.0f).scaleY(3.0f).setDuration(1000).setStartDelay(2000).
                    setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            id_iv_start_page.setVisibility(View.GONE);
                        }
                    }).start();
        } else {
            id_iv_start_page.setVisibility(View.GONE);
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
                    if (NetworkUtils.isConnectInternet(mContext)) {
                        mDmzjAdapter.changeAddMoreStatus(DmzjRecyclerAdapter.LOAD_MORE_LOADING);
                        page += 1;
                        loadLatestData(page, true);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisiblePosition = mLinearLayoutManager.findLastVisibleItemPosition();
                if(lastVisiblePosition > 12){
                    id_return_to_top.setVisibility(View.VISIBLE);
                }else{
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
        id_swiperefresh.setRefreshing(true);
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
        loadLatestData(page, false);
        loadBannerData();
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
        mAppHttpClient.requestDmzjBeanList(Constant.DMZJ_TAG, Constant.DMZJ_TYPE_UPDATE, mpage, 15);
        mAppHttpClient.setOnLoadDmzjDataListener(new AppHttpClient.OnLoadDmzjDataListener() {
            @Override
            public void loadDmzjDataSuccessListener(List<DmzjBean> list) {
                if (flag) {
                    //加载更多
                    recyclerView.scrollToPosition(mDmzjBeanList.size() - 8);
                    mDmzjAdapter.addMoreItem(list, DmzjRecyclerAdapter.LOAD_MORE_COMPILE);
                } else {
                    //拉取最新
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
        page = 1;
        loadLatestData(page, false);
        if (mDmzjAdapter != null && mDmzjAdapter.mBannerBeanList == null) {
            loadBannerData();
        }
    }

    @OnClick({R.id.id_iv_go,R.id.id_return_to_top})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_iv_go:
                //前往mainActivity
                String text = id_et_search.getText().toString().trim();
                if (!TextUtils.isEmpty(text)) {
                    goToMainActivity("http://www.baidu.com/#wd=" + text);
                }
                break;
            case R.id.id_return_to_top:
                //返回顶部
                if(mDmzjAdapter != null && !mDmzjBeanList.isEmpty()){
                    recyclerView.scrollToPosition(0);
                }
                break;
        }
    }

    @Override
    public void onItemClickListener(String url) {
        goToMainActivity(url);
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
    protected void onResume() {
        super.onResume();
        DoAnalyticsManager.pageResume(this, "DmzjActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        DoAnalyticsManager.pagePause(this, "DmzjActivity");
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
        if (mDmzjAdapter != null) {
            mDmzjAdapter.mHandler.removeMessages(1);
            mDmzjAdapter.mHandler = null;
        }
        super.onDestroy();
    }


}
