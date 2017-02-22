package com.browser.dmzj.yinyangshi.manager.viewpager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.browser.dmzj.yinyangshi.R;
import com.browser.dmzj.yinyangshi.activity.BrowserActivity;
import com.browser.dmzj.yinyangshi.activity.DmzjActivity;
import com.browser.dmzj.yinyangshi.activity.DmzjHotActivity;
import com.browser.dmzj.yinyangshi.adapter.DmzjFragmentPagerAdapter;
import com.browser.dmzj.yinyangshi.adapter.viewHolder.Header2ViewHolder;
import com.browser.dmzj.yinyangshi.bean.DmzjBeanResp;
import com.browser.dmzj.yinyangshi.fragment.BaseFragment;
import com.browser.dmzj.yinyangshi.fragment.HotRecommendFragment;
import com.browser.dmzj.yinyangshi.utils.ActivitySlideAnim;
import com.browser.dmzj.yinyangshi.utils.Constant;
import com.browser.dmzj.yinyangshi.utils.DoAnalyticsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxiaojun on 17-1-12.
 */

public class FragmentViewPagerManger implements View.OnClickListener {

    private Header2ViewHolder mHeaderViewHolder2;
    private List<BaseFragment> fragmentList;
    private HotRecommendFragment mFirstFragment;
    private HotRecommendFragment mSecondFragment;
    private HotRecommendFragment mThirdFragment;
    private DmzjFragmentPagerAdapter mDmzjPagerAdapter;
    private FragmentManager mFragmentManager;
    private Context mContext;

    public FragmentViewPagerManger(Header2ViewHolder headerViewHolder2,
                                   FragmentManager mFragmentManager,
                                   Context context) {
        this.mHeaderViewHolder2 = headerViewHolder2;
        this.mFragmentManager = mFragmentManager;
        this.mContext = context;
    }

    /***
     * 初始化点击事件
     */
    public void initEvent() {
        mHeaderViewHolder2.tv_more.setOnClickListener(this);
        mHeaderViewHolder2.tv_main_secret.setOnClickListener(this);
        mHeaderViewHolder2.tv_main_tencent.setOnClickListener(this);
        mHeaderViewHolder2.tv_main_read.setOnClickListener(this);
        mHeaderViewHolder2.tv_main_book.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_more:
                mContext.startActivity(new Intent(mContext, DmzjHotActivity.class));
                ActivitySlideAnim.slideInAnim((DmzjActivity) mContext);

                DoAnalyticsManager.event(mContext, DoAnalyticsManager.DOT_KEY_MORE_CLICK);

                break;
            case R.id.tv_main_secret:

                startMainActivity(Constant.URL_COMICS_SECRET);
                DoAnalyticsManager.event(mContext, DoAnalyticsManager.DOT_KEY_COMICS_SECRET_CLICK);

                break;
            case R.id.tv_main_tencent:

                startMainActivity(Constant.URL_COMICS_TENCENT);
                DoAnalyticsManager.event(mContext, DoAnalyticsManager.DOT_KEY_COMICS_TENCENT_CLICK);

                break;
            case R.id.tv_main_read:

                startMainActivity(Constant.URL_COMICS_READ);
                DoAnalyticsManager.event(mContext, DoAnalyticsManager.DOT_KEY_COMICS_READ_CLICK);

                break;
            case R.id.tv_main_book:

                startMainActivity(Constant.URL_COMICS_BOOK);
                DoAnalyticsManager.event(mContext, DoAnalyticsManager.DOT_KEY_COMICS_BOOK_CLICK);

                break;
        }
    }

    private void startMainActivity(String url) {
        Intent intent = new Intent(mContext, BrowserActivity.class);
        intent.putExtra("url", url);
        mContext.startActivity(intent);
        ActivitySlideAnim.slideInAnim((DmzjActivity) mContext);
    }

    public void initFragment() {

        fragmentList = new ArrayList<>();
        mFirstFragment = new HotRecommendFragment();
        Bundle mBundle = new Bundle();
        mBundle.putBoolean("isShowAd", true);
        mFirstFragment.setArguments(mBundle);

        mSecondFragment = new HotRecommendFragment();
        mThirdFragment = new HotRecommendFragment();

        fragmentList.add(mFirstFragment);
        fragmentList.add(mSecondFragment);
        fragmentList.add(mThirdFragment);

        mHeaderViewHolder2.vp_fragment.setOffscreenPageLimit(3);

        initAdapter();
        initViewPagerChangeListener();

    }

    public void initAdapter() {
        mDmzjPagerAdapter = new DmzjFragmentPagerAdapter(mFragmentManager, fragmentList);
        mHeaderViewHolder2.vp_fragment.setAdapter(mDmzjPagerAdapter);
    }

    public void setFragmentDmzjBeanList(List<DmzjBeanResp.DmzjBean> list) {
        if (list != null && !list.isEmpty()) {

            List<DmzjBeanResp.DmzjBean> mDmzjList = new ArrayList<>(6);
            if (mDmzjList == null) {
                mDmzjList = new ArrayList<>();
            } else {
                mDmzjList.clear();
            }
            for (int i = 0; i < 6; i++) {
                if (list.get(i) != null) {
                    mDmzjList.add(list.get(i));
                }
            }
            mFirstFragment.setDmzjList(mDmzjList);

            if (mDmzjList == null) {
                mDmzjList = new ArrayList<>();
            } else {
                mDmzjList.clear();
            }
            for (int i = 6; i < 12; i++) {
                if (list.get(i) != null) {
                    mDmzjList.add(list.get(i));
                }
            }
            mSecondFragment.setDmzjList(mDmzjList);

            if (mDmzjList == null) {
                mDmzjList = new ArrayList<>();
            } else {
                mDmzjList.clear();
            }
            int size = list.size();
            for (int i = 12; i < size; i++) {
                if (list.get(i) != null) {
                    mDmzjList.add(list.get(i));
                }
            }
            mThirdFragment.setDmzjList(mDmzjList);
        }
    }

    private void initViewPagerChangeListener() {
        mHeaderViewHolder2.vp_fragment.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setSelectedDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    private void setSelectedDot(int position) {
        if (position == 0) {
            mHeaderViewHolder2.iv_fm_first.setImageResource(R.mipmap.choose_true);
            mHeaderViewHolder2.iv_fm_second.setImageResource(R.mipmap.choose_false);
            mHeaderViewHolder2.iv_fm_third.setImageResource(R.mipmap.choose_false);
        } else if (position == 1) {
            mHeaderViewHolder2.iv_fm_first.setImageResource(R.mipmap.choose_false);
            mHeaderViewHolder2.iv_fm_second.setImageResource(R.mipmap.choose_true);
            mHeaderViewHolder2.iv_fm_third.setImageResource(R.mipmap.choose_false);
        } else {
            mHeaderViewHolder2.iv_fm_first.setImageResource(R.mipmap.choose_false);
            mHeaderViewHolder2.iv_fm_second.setImageResource(R.mipmap.choose_false);
            mHeaderViewHolder2.iv_fm_third.setImageResource(R.mipmap.choose_true);
        }
    }


}
