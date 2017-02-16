package com.gp.browser.video.dance.adapter.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gp.browser.video.dance.view.DilatingDotsProgressBar;
import com.gp.browser.video.dance.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wuxiaojun on 16-11-29.
 */

public class FooterViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.id_ll_footer)
    public LinearLayout id_ll_footer;
    @BindView(R.id.id_tv_tips)
    public TextView id_tv_tips;
    @BindView(R.id.rcv_load_more)
    public DilatingDotsProgressBar progressBar;

    public FooterViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}