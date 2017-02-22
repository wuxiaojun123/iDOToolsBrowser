package com.browser.dmzj.yinyangshi.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.browser.dmzj.yinyangshi.fragment.BaseFragment;

import java.util.List;

/**
 * Created by wuxiaojun on 17-1-10.
 */

public class DmzjFragmentPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {

    public List<BaseFragment> list;
    private FragmentManager fm;

    public DmzjFragmentPagerAdapter(FragmentManager fm, List<BaseFragment> list) {
        super(fm);
        this.fm = fm;
        this.list = list;
    }


    @Override
    public Fragment getItem(int position) {

        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
