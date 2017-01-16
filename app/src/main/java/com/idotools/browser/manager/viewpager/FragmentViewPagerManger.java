package com.idotools.browser.manager.viewpager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.idotools.browser.R;
import com.idotools.browser.activity.DmzjActivity;
import com.idotools.browser.activity.DmzjHotActivity;
import com.idotools.browser.adapter.DmzjFragmentPagerAdapter;
import com.idotools.browser.bean.DmzjBeanResp;
import com.idotools.browser.fragment.BaseFragment;
import com.idotools.browser.fragment.HotRecommendFragment;
import com.idotools.browser.utils.ActivitySlideAnim;
import com.idotools.browser.utils.DoAnalyticsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxiaojun on 17-1-12.
 */

public class FragmentViewPagerManger {

    //    TextView tv_more;
    ViewPager vp_fragment;
    ImageView iv_fm_first;
    ImageView iv_fm_second;
    ImageView iv_fm_third;

    private List<BaseFragment> fragmentList;
    private HotRecommendFragment mFirstFragment;
    private HotRecommendFragment mSecondFragment;
    private HotRecommendFragment mThirdFragment;
    private DmzjFragmentPagerAdapter mDmzjPagerAdapter;
    private FragmentManager mFragmentManager;
    private Context mContext;

    public FragmentViewPagerManger(ViewPager vp_fragment, ImageView iv_fm_first,
                                   ImageView iv_fm_second, ImageView iv_fm_third,
                                   FragmentManager mFragmentManager, Context context) {
        this.vp_fragment = vp_fragment;
        this.iv_fm_first = iv_fm_first;
        this.iv_fm_second = iv_fm_second;
        this.iv_fm_third = iv_fm_third;
        this.mFragmentManager = mFragmentManager;
        this.mContext = context;
    }

    public void setTextMoreClickListener(TextView textView, final Context context) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //更多
                context.startActivity(new Intent(context, DmzjHotActivity.class));
                ActivitySlideAnim.slideInAnim((DmzjActivity) context);

                DoAnalyticsManager.event(mContext, DoAnalyticsManager.DOT_KEY_MORE_CLICK);
            }
        });
    }

    public void initFragment() {
        fragmentList = null;
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

        vp_fragment.setOffscreenPageLimit(3);

        mDmzjPagerAdapter = null;
        mDmzjPagerAdapter = new DmzjFragmentPagerAdapter(mFragmentManager, fragmentList);
        vp_fragment.setAdapter(mDmzjPagerAdapter);
        initViewPagerChangeListener();


    }

    public void setFragmentDmzjBeanList(List<DmzjBeanResp.DmzjBean> list) {
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

    private void initViewPagerChangeListener() {
        vp_fragment.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
            iv_fm_first.setImageResource(R.mipmap.choose_true);
            iv_fm_second.setImageResource(R.mipmap.choose_false);
            iv_fm_third.setImageResource(R.mipmap.choose_false);
        } else if (position == 1) {
            iv_fm_first.setImageResource(R.mipmap.choose_false);
            iv_fm_second.setImageResource(R.mipmap.choose_true);
            iv_fm_third.setImageResource(R.mipmap.choose_false);
        } else {
            iv_fm_first.setImageResource(R.mipmap.choose_false);
            iv_fm_second.setImageResource(R.mipmap.choose_false);
            iv_fm_third.setImageResource(R.mipmap.choose_true);
        }
    }


    public void refreshAdapter() {
        if (mDmzjPagerAdapter != null) {
            mDmzjPagerAdapter.notifyDataSetChanged();
        }
    }

}
