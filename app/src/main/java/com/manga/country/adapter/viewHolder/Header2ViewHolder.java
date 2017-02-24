package com.manga.country.adapter.viewHolder;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.manga.country.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wuxiaojun on 16-11-29.
 */

public class Header2ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_main_secret)
    public TextView tv_main_secret;
    @BindView(R.id.tv_main_tencent)
    public TextView tv_main_tencent;
    @BindView(R.id.tv_main_read)
    public TextView tv_main_read;
    @BindView(R.id.tv_main_book)
    public TextView tv_main_book;
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

    @BindView(R.id.view_space1)
    public View view_space1;
    @BindView(R.id.view_space2)
    public View view_space2;
    @BindView(R.id.tv_adorable_station)
    public TextView tv_adorable_station;
    @BindView(R.id.tv_hot_recommend)
    public TextView tv_hot_recommend;
    @BindView(R.id.tv_new_update)
    public TextView tv_new_update;



    public Header2ViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}