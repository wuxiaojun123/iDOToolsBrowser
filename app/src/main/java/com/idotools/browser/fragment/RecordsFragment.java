package com.idotools.browser.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.idotools.browser.R;
import com.idotools.browser.activity.MainActivity;
import com.idotools.browser.adapter.RecordsRecyclerAdapter;
import com.idotools.browser.bean.RecordsBean;
import com.idotools.browser.manager.dialog.AlertDialog;
import com.idotools.browser.minterface.OnItemDeleteClickListener;
import com.idotools.browser.sqlite.RecordsSqliteManager;
import com.idotools.browser.utils.ActivitySlideAnim;
import com.idotools.browser.view.SideSlipRecyclerView;
import com.idotools.utils.LogUtils;
import com.idotools.utils.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wuxiaojun on 16-11-30.
 */

public class RecordsFragment extends Fragment implements View.OnClickListener, OnItemDeleteClickListener {

    @BindView(R.id.id_recycler_view)
    SideSlipRecyclerView recyclerView;
    private List<RecordsBean> list;
    private RecordsRecyclerAdapter mAdapter;
    private RecordsSqliteManager mSqliteManager;
    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_history, null);
        ButterKnife.bind(this, view);

        initData();

        return view;
    }

    /**
     * 从收藏中获取数据
     */
    private void initData() {
        mSqliteManager = new RecordsSqliteManager(mContext);
        list = mSqliteManager.selectAll();
        if (list != null && !list.isEmpty()) {
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mAdapter = new RecordsRecyclerAdapter(mContext, list);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setRecyclerOnItemClickListener(this);
        }
    }

    //    @OnClick({R.id.id_tv_clean_cache})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            /*case R.id.id_tv_clean_cache:
                //清除缓存
                cleanCache();

                break;*/
        }
    }


    public void cleanCache() {
        if (list == null || list.isEmpty()) {
            ToastUtils.show(mContext, getString(R.string.string_no_records));
            return;
        }
        new AlertDialog(getActivity()).builder()
                .setMsg(R.string.string_confirm_clean_all_records)
                .setPositiveButton(R.string.string_confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //删除所有历史记录
                        if (list != null && !list.isEmpty()) {
                            if (mSqliteManager == null) {
                                mSqliteManager = new RecordsSqliteManager(mContext);
                            }
                            mSqliteManager.deleteAll();
                            list.clear();
                            mAdapter.notifyDataSetChanged();
                        }
                        ToastUtils.show(mContext, mContext.getResources().getString(R.string.string_clean_success));
                    }
                })
                .setNegativeButton(R.string.string_cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .show();
    }

    @Override
    public void onItemClickListener(int position) {
        //打开url
        RecordsBean bean = list.get(position);
        if (bean != null) {
            Intent mIntent = new Intent(getActivity(), MainActivity.class);
            mIntent.putExtra("url", bean.url);
            startActivity(mIntent);

            ActivitySlideAnim.slideOutAnim(getActivity());
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
            RecordsBean bean = list.get(position);
            if (bean != null) {
                mSqliteManager.delete(bean.url);
                list.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDestroyView() {
        if (mSqliteManager != null) {
            mSqliteManager.closeData();
        }
        super.onDestroyView();
    }

}
