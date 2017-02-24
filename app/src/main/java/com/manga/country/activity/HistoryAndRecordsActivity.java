package com.manga.country.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.manga.country.R;
import com.manga.country.fragment.HistoryFragment;
import com.manga.country.fragment.RecordsFragment;
import com.manga.country.utils.ActivitySlideAnim;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wuxiaojun on 16-11-30.
 */

public class HistoryAndRecordsActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.id_iv_right)
    ImageButton id_iv_right;
    @BindView(R.id.id_tv_clean_cache)
    TextView id_tv_clean_cache;
    @BindView(R.id.id_tablayout)
    TabLayout tabLayout;
    @BindView(R.id.id_viewpager)
    ViewPager viewPager;
    MyFragmentPageAdapter mAdapter;
    Fragment[] fragments = new Fragment[2];
    Context mContext;

    private String deleteAllStr;
    private String cleanCacheStr;

    private HistoryFragment mHistoryFragment;
    private RecordsFragment mRecordsFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_records);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        initData();
        initEvent();
        deleteAllStr = getMString(R.string.string_delete_all);
        cleanCacheStr = getMString(R.string.string_clean_cache);
    }

    private void initEvent() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //判断当前选中的是历史记录还是收藏
                if (position == 0) {
                    id_tv_clean_cache.setText(cleanCacheStr);
                } else {
                    id_tv_clean_cache.setText(deleteAllStr);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initData() {
        mHistoryFragment = new HistoryFragment();
        mRecordsFragment = new RecordsFragment();
        fragments[0] = mHistoryFragment;
        fragments[1] = mRecordsFragment;
        mAdapter = new MyFragmentPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @OnClick({R.id.id_iv_right, R.id.id_tv_clean_cache})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_iv_right:
                finish();
                ActivitySlideAnim.slideOutAnim(HistoryAndRecordsActivity.this);

                break;
            case R.id.id_tv_clean_cache:
                String content = id_tv_clean_cache.getText().toString();
                if (cleanCacheStr.equals(content)) {
                    //清空缓存
                    mHistoryFragment.cleanCache();
                } else {
                    //全部删除收藏
                    mRecordsFragment.cleanCache();
                }
                break;
        }
    }


    private class MyFragmentPageAdapter extends FragmentPagerAdapter {

        public MyFragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 1:
                    return getMString(R.string.string_records);
                default:
                    return getMString(R.string.string_history);
            }
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivitySlideAnim.slideOutAnim(this);
    }

    /***
     * 根据resId获取字符串
     *
     * @param resId
     * @return
     */
    private String getMString(int resId) {
        return mContext.getString(resId);
    }

}
