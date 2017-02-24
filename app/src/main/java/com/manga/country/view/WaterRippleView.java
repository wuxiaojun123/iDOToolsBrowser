package com.manga.country.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ImageView;

import com.manga.country.R;

/**
 * 水波纹效果
 * Created by wuxiaojun on 16-10-24.
 */
public class WaterRippleView extends ImageView {

    private static final int INVALIDATE_DURATION = 100; //每次刷新的时间间隔
    private static int DIFFUSE_GAP = 2;                  //扩散半径增量
    private static int TAP_TIMEOUT;                   //判断点击和长按的时间

    private int viewWidth;                            //控件宽度和高度
    private int viewHeight;
    private int pointX;                               //控件原点坐标（左上角）
    private int pointY;
    private int maxRadio;                             //扩散的最大半径
    private int shaderRadio;

    private Paint bottomPaint;                        //画笔
    private Paint colorPaint;

    private boolean isPushButton;                     //记录是否按钮被按下

    public WaterRippleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        TAP_TIMEOUT = ViewConfiguration.getLongPressTimeout();
    }

    /**
     * 初始化画笔资源
     */
    private void initPaint() {
        colorPaint = new Paint();
        bottomPaint = new Paint();
        colorPaint.setColor(getResources().getColor(R.color.color_about_good_text));
        bottomPaint.setColor(getResources().getColor(android.R.color.transparent));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.viewWidth = w;
        this.viewHeight = h;
    }

    private int eventX;
    private int eventY;
    private long downTime = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //只需要取一次时间
                if(downTime == 0){
                    downTime = SystemClock.elapsedRealtime();
                }
                eventX = (int)event.getX();
                eventY = (int)event.getY();
                //计算最大半径
                countMaxRadio();
                isPushButton = true;
                postInvalidateDelayed(INVALIDATE_DURATION);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(SystemClock.elapsedRealtime() - downTime < TAP_TIMEOUT){
                    DIFFUSE_GAP = 30;
                    postInvalidate();
                }else{
                    clearData();
                }
                break;
        }
        return true;
    }

    /**
     * 计算此时的最大半径
     */
    private void countMaxRadio() {
        if(viewWidth > viewHeight){
            if(eventX < viewWidth / 2){
                maxRadio = viewWidth - eventX;
            }else{
                maxRadio = viewWidth / 2 + eventX;
            }
        }else{
            if(eventY < viewHeight / 2){
                maxRadio = viewHeight - eventY;
            }else{
                maxRadio = viewHeight / 2 + eventY;
            }
        }
    }

    /**
     * 清理改变的数据（初始化数据）
     */
    private void clearData(){
        downTime = 0;
        DIFFUSE_GAP = 10;
        isPushButton = false;
        shaderRadio = 0;
        postInvalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if(!isPushButton) return; //如果按钮没有被按下则返回

        //绘制按下后的整个背景
        canvas.drawRect(pointX, pointY, pointX + viewWidth, pointY + viewHeight, bottomPaint);
        canvas.save();
        //绘制扩散圆形背景
        canvas.clipRect(pointX, pointY, pointX + viewWidth, pointY + viewHeight);
        canvas.drawCircle(eventX, eventY, shaderRadio, colorPaint);
        canvas.restore();
        //直到半径等于最大半径
        if(shaderRadio < maxRadio){
            postInvalidateDelayed(INVALIDATE_DURATION,
                    pointX, pointY, pointX + viewWidth, pointY + viewHeight);
            shaderRadio += DIFFUSE_GAP;
        }else{
            clearData();
        }
    }

}
