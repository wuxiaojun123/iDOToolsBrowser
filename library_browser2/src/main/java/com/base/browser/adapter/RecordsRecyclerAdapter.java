package com.base.browser.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.base.browser.R;
import com.base.browser.adapter.viewHolder.HistoryAndRecordsViewHolder;
import com.base.browser.bean.RecordsBean;
import com.base.browser.utils.GlideUtils;

import java.util.List;

/**
 * Created by wuxiaojun on 16-10-2.
 */
public class RecordsRecyclerAdapter extends RecyclerView.Adapter<HistoryAndRecordsViewHolder> {

    private Context mContext;
    private List<RecordsBean> list;


    public RecordsRecyclerAdapter(Context context, List<RecordsBean> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    public HistoryAndRecordsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_records, null);
        return new HistoryAndRecordsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryAndRecordsViewHolder holder, final int position) {
        RecordsBean bean = list.get(position);
        if (bean != null) {
            if (bean.imgUrl != null) {
                if (!"null".equals(bean.imgUrl)) {
                    GlideUtils.loadGIFImage(mContext, bean.imgUrl, holder.iv_img);
                }
            }
            holder.tv_title.setText(bean.title);
            holder.tv_laster_chapter.setText(bean.url);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
