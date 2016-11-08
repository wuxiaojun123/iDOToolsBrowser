package com.base.browser.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.base.browser.R;
import com.base.browser.adapter.HistoryRecyclerAdapter;
import com.base.browser.bean.CartoonDetailsBean;
import com.base.browser.manager.dialog.AlertDialog;
import com.base.browser.minterface.OnItemDeleteClickListener;
import com.base.browser.sqlite.SqliteManager;
import com.base.browser.utils.ActivitySlideAnim;
import com.base.browser.view.SideSlipRecyclerView;
import com.idotools.utils.LogUtils;
import com.idotools.utils.ToastUtils;

import java.util.List;


/**
 * Created by wuxiaojun on 16-10-9.
 */
public class HistoryActivity extends BaseActivity implements View.OnClickListener, OnItemDeleteClickListener {

    ImageButton id_iv_right;
    TextView id_tv_title;
    TextView id_tv_clean_cache;
    SideSlipRecyclerView recyclerView;
    private List<CartoonDetailsBean> list;
    private HistoryRecyclerAdapter mAdapter;
    private SqliteManager mSqliteManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initView();
        id_iv_right.setImageResource(R.mipmap.img_title_back);
        id_tv_title.setText(R.string.string_history);
        initData();
    }

    private void initView() {
        id_iv_right = (ImageButton) findViewById(R.id.id_iv_right);
        id_tv_title = (TextView) findViewById(R.id.id_tv_title);
        id_tv_clean_cache = (TextView) findViewById(R.id.id_tv_clean_cache);
        recyclerView = (SideSlipRecyclerView) findViewById(R.id.id_recycler_view);
        //设置事件
        id_iv_right.setOnClickListener(this);
        id_tv_clean_cache.setOnClickListener(this);
    }

    /***
     * 从历史记录中获取数据
     */
    private void initData() {
        SqliteManager manager = new SqliteManager(getApplicationContext());
        list = manager.selectAll();
        if (list != null && !list.isEmpty()) {
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mAdapter = new HistoryRecyclerAdapter(mContext, list);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setRecyclerOnItemClickListener(this);
        } else {
            LogUtils.e("没有数据");
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.id_iv_right) {
            finish();
            ActivitySlideAnim.slideOutAnim(this);

        } else if (id == R.id.id_tv_clean_cache) {//清除缓存
            cleanCache();
                /*if (mWebViewManager == null) {
                    mWebViewManager = ((MainActivity) mContext).getWebViewManager();
                }
                mWebViewManager.cleanCache();*/
        }
    }

    /***
     * 清除缓存
     *
     * @return
     */
    public void cleanCache() {
        new AlertDialog(HistoryActivity.this).builder().setTitle(R.string.string_prompt)
                .setMsg(R.string.string_confirm_clean_all_cache)
                .setPositiveButton(R.string.string_confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*if (mWebView != null) {
                            mWebView.clearCache(true);
                            ToastUtils.show(mContext, mContext.getResources().getString(R.string.string_clean_success));
                        }*/
                        ToastUtils.show(mContext, mContext.getResources().getString(R.string.string_clean_success));
                    }
                }).setNegativeButton(R.string.string_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).show();
    }

    @Override
    public void onItemClickListener(int position) {
        //打开url
        CartoonDetailsBean bean = list.get(position);
        if (bean != null) {
            Intent mIntent = new Intent(HistoryActivity.this, MainActivity.class);
            mIntent.putExtra("url", bean.url);
            startActivity(mIntent);
        }
    }

    /**
     * //删除数据库中的这条数据
     *
     * @param position
     */
    @Override
    public void deleteListener(int position) {
        if (list != null && (list.size() - 1) >= position) {
            CartoonDetailsBean bean = list.get(position);
            if (bean != null) {
                if (mSqliteManager == null) {
                    mSqliteManager = new SqliteManager(mContext);
                }
                mSqliteManager.delete(bean.url);
                list.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mSqliteManager != null) {
            mSqliteManager.closeData();
        }
        super.onDestroy();
    }
}
