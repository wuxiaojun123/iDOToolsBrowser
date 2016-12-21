package com.idotools.browser.adapter.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idotools.browser.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wuxiaojun on 16-12-20.
 */

public class DmzjViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.id_iv_img)
    public ImageView id_iv_img;
    @BindView(R.id.id_tv_title)
    public TextView id_tv_title;
    @BindView(R.id.id_tv_tag)
    public TextView id_tv_tag;
    @BindView(R.id.id_tv_description)
    public TextView id_tv_description;
    @BindView(R.id.id_ll_item)
    public LinearLayout id_ll_item;

    public DmzjViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}