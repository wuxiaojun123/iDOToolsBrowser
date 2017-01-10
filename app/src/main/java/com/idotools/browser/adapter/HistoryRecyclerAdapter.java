package com.idotools.browser.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.idotools.browser.R;
import com.idotools.browser.adapter.viewHolder.HistoryAndRecordsViewHolder;
import com.idotools.browser.bean.CartoonDetailsBean;

import java.util.List;

/**
 * Created by wuxiaojun on 16-10-2.
 */
public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryAndRecordsViewHolder> {

    private Context mContext;
    private List<CartoonDetailsBean> list;


    public HistoryRecyclerAdapter(Context context, List<CartoonDetailsBean> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    public HistoryAndRecordsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_history, null);
        return new HistoryAndRecordsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryAndRecordsViewHolder holder, final int position) {
        CartoonDetailsBean bean = list.get(position);
        if (bean != null) {
            if (bean.img != null)
                Glide.with(mContext).load(bean.img).centerCrop().crossFade().into(holder.iv_img);
            holder.tv_title.setText(bean.title);
            holder.tv_laster_chapter.setText(bean.url);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
