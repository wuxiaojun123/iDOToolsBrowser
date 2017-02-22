package com.browser.dmzj.yinyangshi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.browser.activity.BaseActivity;
import com.idotools.utils.JudgeNetWork;
import com.browser.dmzj.yinyangshi.R;
import com.browser.dmzj.yinyangshi.adapter.DmzjHotRecyclerAdapter;
import com.browser.dmzj.yinyangshi.bean.DmzjBeanResp;
import com.browser.dmzj.yinyangshi.manager.http.AppHttpClient;
import com.browser.dmzj.yinyangshi.minterface.OnItemClickListener;
import com.browser.dmzj.yinyangshi.minterface.OnLoadDmzjHotDataListener;
import com.browser.dmzj.yinyangshi.utils.ActivitySlideAnim;
import com.browser.dmzj.yinyangshi.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wuxiaojun on 17-1-11.
 */

public class DmzjHotActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, OnItemClickListener {

    @BindView(R.id.id_iv_right)
    ImageView id_iv_right;
    @BindView(R.id.id_tv_title)
    TextView id_tv_title;

    @BindView(R.id.id_swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.id_return_to_top)
    ImageView id_return_to_top;

    @BindView(R.id.id_layout_no_network)
    LinearLayout layout_no_network;

    private int page = 1;//当前页
    private int lastVisiblePosition;
    private AppHttpClient mAppHttpClient;//网络请求业务类
    private DmzjHotRecyclerAdapter mDmzjAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private List<DmzjBeanResp.DmzjBean> mDmzjBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dmzj_hot);
        ButterKnife.bind(this);

        id_iv_right.setImageResource(R.mipmap.img_title_back);
        id_tv_title.setText(R.string.string_hot_recommend);

        initView();
        initData();
    }

    private void initView() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        mDmzjBeanList = new ArrayList<>();
        mDmzjAdapter = new DmzjHotRecyclerAdapter(mContext, mDmzjBeanList);
        mDmzjAdapter.setFooterView(LayoutInflater.from(mContext).inflate(R.layout.layout_footer, recyclerView, false));
        recyclerView.setAdapter(mDmzjAdapter);
        mDmzjAdapter.setOnItemClickListener(this);
        //设置swipe的样式
        swipeRefreshLayout.setColorSchemeResources(R.color.color_main_title);
        swipeRefreshLayout.setOnRefreshListener(this);
        initScrollListener();
    }

    private void initData() {
        mAppHttpClient = new AppHttpClient();
        if (JudgeNetWork.isNetAvailable(mContext)) {
            layout_no_network.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(true);
            page = 1;
            loadHotData(1, false);
        } else {
            layout_no_network.setVisibility(View.VISIBLE);
        }
    }


    /***
     * 拉取热门的动漫之家的数据
     *
     * @param page 页数
     * @param flag true表示获取最新的 false表示加载更多
     */
    private void loadHotData(int page, final boolean flag) {
        mAppHttpClient.requestDmzjHotBeanList(page, 8);
        mAppHttpClient.setOnLoadDmzjHotDataListener(new OnLoadDmzjHotDataListener() {
            @Override
            public void loadDmzjDataSuccessListener(DmzjBeanResp resp) {
                //拉取热门的数据
                List<DmzjBeanResp.DmzjBean> list = resp.comics;
                if (list.size() / 4 >= 1) {
                    list.add(4, null);
                }
                if (list.size() / 4 == 2) {
                    list.add(null);
                }
                if (flag) { // 加载更多
                    mDmzjAdapter.addMoreItem(list, Constant.LOAD_MORE_COMPILE);
                } else { // 拉取最新
                    mDmzjAdapter.resetAdapter(list);//刷新界面数据
                }
                swipeRefreshLayout.setRefreshing(false);//刷新完成
            }

            @Override
            public void loadDmzjDataFailedListener() {
                swipeRefreshLayout.setRefreshing(false);//刷新完成
            }
        });
    }

    /***
     * 添加滑动事件的监听，以便可以出现加载更多
     */
    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && (lastVisiblePosition + 1) == mDmzjAdapter.getItemCount()) {
                    if (JudgeNetWork.isNetAvailable(mContext)) {
                        mDmzjAdapter.changeAddMoreStatus(Constant.LOAD_MORE_LOADING);
                        page += 1;
                        loadHotData(page, true);
                    } else {
                        mDmzjAdapter.changeAddMoreStatus(Constant.LOAD_MORE_COMPILE);
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

    @OnClick({R.id.id_iv_right, R.id.id_return_to_top})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_iv_right:
                finish();
                ActivitySlideAnim.slideOutAnim(DmzjHotActivity.this);

                break;
            case R.id.id_return_to_top:
                //返回顶部
                if (mDmzjAdapter != null && !mDmzjBeanList.isEmpty()) {
                    recyclerView.scrollToPosition(0);
                }

                break;
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        loadHotData(page, false);
    }


    @Override
    public void onItemClickListener(String url, String imgUrl, String title) {
        Intent mIntent = new Intent(DmzjHotActivity.this, BrowserActivity.class);
        mIntent.putExtra("url", url);
        mIntent.putExtra("imgUrl", imgUrl);
        mIntent.putExtra("title", title);
        startActivity(mIntent);
        ActivitySlideAnim.slideInAnim(DmzjHotActivity.this);
    }


}
