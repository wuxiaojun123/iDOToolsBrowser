package com.idotools.browser.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.idotools.browser.adapter.HistoryRecyclerAdapter;
import com.idotools.browser.adapter.viewHolder.HistoryAndRecordsViewHolder;
import com.idotools.browser.minterface.OnItemDeleteClickListener;
import com.idotools.utils.LogUtils;

/**
 * Created by wuxiaojun on 16-10-9.
 */
public class SideSlipRecyclerView extends RecyclerView {

    private Context mContext;
    //上一次触摸点的x,y轴位置
    private int lastX;
    private int lastY;
    //当前触摸item的位置
    private int mCurrentItemPosition;
    //当前item的布局
    private LinearLayout mCurrentItemLayout;
    //删除按钮
    private TextView mdeleteTextView;
    //最大的滑动距离(删除按钮的宽度)
    private int mMaxScrollDis;

    //是否在垂直滑动列表
    private boolean isDragging;
    //item是否跟随手指移动
    private boolean isItemMoving;
    //item是否开始自动滑动
    private boolean isStartScroll;

    //状态 0 关闭  1 将要关闭  2 将要打开  3　打开
    private int state;
    //检测手指在滑动过程中的速度
    private VelocityTracker mVelocityTracker;
    //滑动的类
    private Scroller mScroller;
    //点击和删除回调事件
    private OnItemDeleteClickListener mItemClickListener;


    public SideSlipRecyclerView(Context context) {
        this(context, null);
    }

    public SideSlipRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SideSlipRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        mVelocityTracker = VelocityTracker.obtain();
        mScroller = new Scroller(context, new LinearInterpolator());
    }

    private long pressTime;

    /***
     * @param e
     * @return true 表示自己消费不再往下传递,自己处理 false 表示不管不处理
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        getParent().requestDisallowInterceptTouchEvent(true);

        mVelocityTracker.addMovement(e);
        int x = (int) e.getX();
        int y = (int) e.getY();
        int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                pressTime = System.currentTimeMillis();
                if (state == 0) {
                    View view = findChildViewUnder(x, y);
                    if (view == null) {
                        return false;
                    }
                    //获取viewHolder
                    HistoryAndRecordsViewHolder viewHolder = (HistoryAndRecordsViewHolder) getChildViewHolder(view);
                    mCurrentItemLayout = viewHolder.ll_layout;
                    mCurrentItemPosition = viewHolder.getAdapterPosition();
                    mdeleteTextView = viewHolder.deleteTextView;
                    mMaxScrollDis = mdeleteTextView.getWidth();
                    //设置点击事件
                    mdeleteTextView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCurrentItemLayout.scrollTo(0, 0);
                            //重置状态
                            state = 0;
                            if (mItemClickListener != null) {
                                mItemClickListener.deleteListener(mCurrentItemPosition);
                            }
                        }
                    });
                } else if (state == 3) {
                    //正处于关闭状态
                    mScroller.startScroll(mCurrentItemLayout.getScrollX(), 0, -mMaxScrollDis, 0, 200);
                    invalidate();
                    state = 0;
                    return false;
                } else {
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //滑动，根据滑动距离
                int dx = lastX - x;
                int dy = lastY - y;
                if (mCurrentItemLayout != null) {
                    int scrollX = mCurrentItemLayout.getScrollX();
                    if (Math.abs(dx) > Math.abs(dy)) {
                        isItemMoving = true;
                        if (scrollX + dx <= 0) {
//                            mCurrentItemLayout.scrollTo(0, 0);return true;//这里需要研究一下
                            getParent().requestDisallowInterceptTouchEvent(false);
                            return false;
                        } else if (scrollX + dx >= mMaxScrollDis) {
                            mCurrentItemLayout.scrollTo(mMaxScrollDis, 0);
                            return true;
                        }
                        mCurrentItemLayout.scrollBy(dx, 0);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:

                int upScrollX = mCurrentItemLayout.getScrollX();

                long currentTime = System.currentTimeMillis();
                if ((currentTime - pressTime) < 100) {
                    if (!isDragging && !isItemMoving && mItemClickListener != null) {
                        mItemClickListener.onItemClickListener(mCurrentItemPosition);
                    }
                }

                isItemMoving = false;

                if (mCurrentItemLayout != null) {

                    mVelocityTracker.computeCurrentVelocity(1000);//计算手指滑动的速度
                    int xVelocity = (int) mVelocityTracker.getXVelocity();//x方向的速度（向左为负）
                    int yVelocity = (int) mVelocityTracker.getYVelocity();//y方向的速度

                    int deltaX = 0;

                    if (Math.abs(xVelocity) > 100 && Math.abs(xVelocity) > Math.abs(yVelocity)) {
                        if (xVelocity < -100) {//左滑速度小于100,显示删除按钮
                            deltaX = mMaxScrollDis - upScrollX;
                            state = 2;
                        } else if (xVelocity > 100) {//右滑速度大于100,隐藏删除按钮
                            deltaX = -upScrollX;
                            state = 1;
                        }
                    } else {
                        if (upScrollX > mMaxScrollDis / 2) {//item的左滑距离大于删除按钮的一半，显示删除按钮
                            deltaX = mMaxScrollDis - upScrollX;
                            state = 2;
                        } else {//item的左滑距离小于删除按钮的一半．隐藏删除按钮
                            deltaX = -upScrollX;
                        }
                    }
                    mScroller.startScroll(upScrollX, 0, deltaX, 0, 200);
                    invalidate();
                    isStartScroll = true;

                    mVelocityTracker.clear();

                }
                break;
        }
        lastX = x;
        lastY = y;
        return super.onTouchEvent(e);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mCurrentItemLayout.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        } else if (isStartScroll) {
            isStartScroll = false;
            if (state == 1) {
                state = 0;
            }
            if (state == 2) {
                state = 3;
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        mVelocityTracker.recycle();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        isDragging = state == SCROLL_STATE_DRAGGING;
    }

    public void setRecyclerOnItemClickListener(OnItemDeleteClickListener listener) {
        this.mItemClickListener = listener;
    }


}
