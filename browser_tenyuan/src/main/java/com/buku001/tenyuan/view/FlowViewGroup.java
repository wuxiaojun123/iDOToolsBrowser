package com.buku001.tenyuan.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxiaojun on 16-10-17.
 */
public class FlowViewGroup extends ViewGroup {

    //存放所有的view
    private List<List<View>> mAllViews = new ArrayList<>();
    //行高集合，是为了childview.mar
    private List<Integer> mLineHeights = new ArrayList<>();

    public FlowViewGroup(Context context) {
        this(context, null);
    }

    public FlowViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //总共的宽度和高度
        int width = 0;
        int height = 0;

        //每一行的宽度和高度
        int lineWidth = 0;
        int lineHeight = 0;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            //获取子类
            View childView = getChildAt(i);
            //测量childview的大小
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();

            int childWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            //判断是否需要换行
            if (lineWidth + childWidth > widthSize) {
                //将原来最大的宽度和现在这一行的宽度进行对比，为了对比出最大的宽度
                width = Math.max(width, lineWidth);
                //行宽重置为0
                lineWidth = childWidth;
                //行高需要叠加
                height += lineHeight;
                //重置行高
                lineHeight = childHeight;
            } else {
                //对比高度，获取最高的高度
                lineHeight = Math.max(lineHeight, childHeight);
                //宽度叠加
                lineWidth += childWidth;
            }
            //如果是最后一个
            if (i == childCount - 1) {
                //宽度：如果最后一个是某一行的最后一个，那么还需要比较行宽和原来最大宽度,因为可能最后一个行宽是大于原来的width的
                width = Math.max(width, lineWidth);
                //高度：如果最后一个view的高度比之前的都高，但是上边判断走的是else，那么height就没有进行叠加
                height += lineHeight;
            }
        }

        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : width, heightMode == MeasureSpec.EXACTLY ? heightSize : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeights.clear();
        //摆放位置
        int width = getMeasuredWidth();
        int lineHeight = 0;
        int lineWidth = 0;
        //先把每一行的所有view都存放到mAllViews的集合中
        List<View> mLineViews = new ArrayList<>();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
            //判断是否需要换行
            if (lineWidth + lp.leftMargin + lp.rightMargin + childView.getMeasuredWidth() > width) {
                mLineHeights.add(lineHeight);
                mAllViews.add(mLineViews);
                mLineViews = new ArrayList<>();
                lineWidth = 0;
                lineHeight = childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            }
            //得到每一行中最高的行高
            int childViewHeight = childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            lineHeight = Math.max(lineHeight, childViewHeight);
            //将宽度进行叠加
            lineWidth += childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            //将这一行的一个view添加到行view集合中
            mLineViews.add(childView);
        }
        //处理最后一行
        mAllViews.add(mLineViews);
        mLineHeights.add(lineHeight);

        int left = 0;
        int top = 0;

        int size = mAllViews.size();
        for (int i = 0; i < size; i++) {
            mLineViews = mAllViews.get(i);
            lineHeight = mLineHeights.get(i);

            int mLineViewSize = mLineViews.size();
            for (int i1 = 0; i1 < mLineViewSize; i1++) {
                View childView = mLineViews.get(i1);
                if (childView.getVisibility() == View.GONE) {
                    continue;
                }
                MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
                int childLeft = lp.leftMargin + left;
                int childTop = lp.topMargin + top;
                int childRight = childView.getMeasuredWidth() + childLeft;
                int childBottom = childView.getMeasuredHeight() + childTop;

                childView.layout(childLeft, childTop, childRight, childBottom);

                left += childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            }
            left = 0;
            top += lineHeight;
        }
    }


    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
