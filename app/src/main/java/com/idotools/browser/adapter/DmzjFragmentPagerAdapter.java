package com.idotools.browser.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.idotools.browser.fragment.BaseFragment;

import java.util.List;

/**
 * Created by wuxiaojun on 17-1-10.
 */

public class DmzjFragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    public List<BaseFragment> list;

    public DmzjFragmentPagerAdapter(FragmentManager fm, List<BaseFragment> list) {
        super(fm);
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


}
