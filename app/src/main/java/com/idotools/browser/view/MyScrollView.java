package com.idotools.browser.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import com.idotools.browser.R;
import com.idotools.utils.LogUtils;
import com.idotools.utils.MetricsUtils;
import com.idotools.utils.MobileScreenUtils;

/**
 * 实现思路
 * 设置scrollview的高度，为固定的两个viewpager,
 * 当超出这个高度之后，就不拦截滑动事件(不让scrollview滑动)
 * <p>
 * Created by wuxiaojun on 17-1-12.
 */

public class MyScrollView extends ScrollView {

    private int totalHeight;

    public MyScrollView(Context context) {
        this(context, null);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        float dmzjVpHeight = context.getResources().getDimension(R.dimen.dimen_dmzj_vp_height);
        totalHeight = (int) (MetricsUtils.dipToPx(168 + 90) + dmzjVpHeight);//广告banner,
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //这里设置高度
        int height = MeasureSpec.getSize(heightMeasureSpec);
        LogUtils.e("高度是：" + height);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = super.onInterceptTouchEvent(ev);
        LogUtils.e("onTouchEvent：getScrollY=" + getScrollY() + "======" + result);
        if (getScrollY() > totalHeight) {
            result = false;
        }
        return result;
    }

    private int l;

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        this.l = l;
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }


}
