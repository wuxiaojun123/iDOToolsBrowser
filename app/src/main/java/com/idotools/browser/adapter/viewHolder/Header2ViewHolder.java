package com.idotools.browser.adapter.viewHolder;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.idotools.browser.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wuxiaojun on 16-11-29.
 */

public class Header2ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_more)
    public TextView tv_more;
    @BindView(R.id.id_viewpager_fm)
    public ViewPager vp_fragment;
    @BindView(R.id.iv_fm_first)
    public ImageView iv_fm_first;
    @BindView(R.id.iv_fm_second)
    public ImageView iv_fm_second;
    @BindView(R.id.iv_fm_third)
    public ImageView iv_fm_third;

    public Header2ViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}