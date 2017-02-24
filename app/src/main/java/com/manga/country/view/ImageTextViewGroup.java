package com.manga.country.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.manga.country.R;
import com.idotools.utils.MetricsUtils;
import com.idotools.utils.MobileScreenUtils;

/**
 * Created by wuxiaojun on 16-10-2.
 */
public class ImageTextViewGroup extends LinearLayout {

    private int width;
    private int height;
    public ImageView id_iv_image;
    public TextView id_tv_text;

    private String text;
    private int srcResID;

    public ImageTextViewGroup(Context context) {
        this(context, null);
    }

    public ImageTextViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageTextViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        width = MobileScreenUtils.getScreenWidth(context) / 4;
        height = MetricsUtils.dipToPx(90);
        LayoutInflater.from(context).inflate(R.layout.layout_image_text_view, this);
        //获取自定义属性
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.declare_image_text_viewgroup, 0, 0);
        text = typedArray.getString(R.styleable.declare_image_text_viewgroup_image_text_viewgroup_text);
        srcResID = typedArray.getResourceId(R.styleable.declare_image_text_viewgroup_image_text_viewgroup_src, 0);

        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        id_iv_image = (ImageView) findViewById(R.id.id_iv_image);
        id_tv_text = (TextView) findViewById(R.id.id_tv_text);

        id_iv_image.setImageResource(srcResID);
        id_tv_text.setText(text);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    public void setImageResource(int resId){
        id_iv_image.setImageResource(resId);
    }

    public void setTextResource(int resId){
        id_tv_text.setText(resId);
    }

}
