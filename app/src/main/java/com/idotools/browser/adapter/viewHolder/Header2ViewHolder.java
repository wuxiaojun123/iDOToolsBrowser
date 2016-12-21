package com.idotools.browser.adapter.viewHolder;

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

    @BindView(R.id.id_iv_image_first)
    public ImageView id_iv_image_first;
    @BindView(R.id.id_tv_text_first)
    public TextView id_tv_text_first;

    @BindView(R.id.id_iv_image_second)
    public ImageView id_iv_image_second;
    @BindView(R.id.id_tv_text_second)
    public TextView id_tv_text_second;

    @BindView(R.id.id_iv_image_third)
    public ImageView id_iv_image_third;
    @BindView(R.id.id_tv_text_third)
    public TextView id_tv_text_third;

    @BindView(R.id.id_iv_image_four)
    public ImageView id_iv_image_four;
    @BindView(R.id.id_tv_text_four)
    public TextView id_tv_text_four;

    @BindView(R.id.id_iv_image_five)
    public ImageView id_iv_image_five;
    @BindView(R.id.id_tv_text_five)
    public TextView id_tv_text_five;

    @BindView(R.id.id_iv_image_six)
    public ImageView id_iv_image_six;
    @BindView(R.id.id_tv_text_six)
    public TextView id_tv_text_six;


    public Header2ViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}