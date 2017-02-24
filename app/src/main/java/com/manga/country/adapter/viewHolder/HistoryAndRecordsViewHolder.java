package com.manga.country.adapter.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.manga.country.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wuxiaojun on 16-12-1.
 */

public class HistoryAndRecordsViewHolder extends RecyclerView.ViewHolder  {

    @BindView(R.id.id_ll_layout)
    public LinearLayout ll_layout;
    @BindView(R.id.id_iv_img)
    public ImageView iv_img;
    @BindView(R.id.id_tv_title)
    public TextView tv_title;
    @BindView(R.id.id_tv_laster_chapter)
    public TextView tv_laster_chapter;
    @BindView(R.id.item_delete)
    public TextView deleteTextView;
    @BindView(R.id.ll_text)
    public LinearLayout ll_text;

    public HistoryAndRecordsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}
