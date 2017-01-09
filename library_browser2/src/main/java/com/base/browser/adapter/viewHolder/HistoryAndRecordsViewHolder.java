package com.base.browser.adapter.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.browser.R;


/**
 * Created by wuxiaojun on 16-12-1.
 */

public class HistoryAndRecordsViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout ll_layout;
    public ImageView iv_img;
    public TextView tv_title;
    public TextView tv_laster_chapter;
    public TextView deleteTextView;
    public LinearLayout ll_text;

    public HistoryAndRecordsViewHolder(View itemView) {
        super(itemView);
        ll_layout = (LinearLayout) itemView.findViewById(R.id.id_ll_layout);
        iv_img = (ImageView) itemView.findViewById(R.id.id_iv_img);
        tv_title = (TextView) itemView.findViewById(R.id.id_tv_title);
        tv_laster_chapter = (TextView) itemView.findViewById(R.id.id_tv_laster_chapter);
        deleteTextView = (TextView) itemView.findViewById(R.id.item_delete);
        ll_text = (LinearLayout) itemView.findViewById(R.id.ll_text);
    }

}
