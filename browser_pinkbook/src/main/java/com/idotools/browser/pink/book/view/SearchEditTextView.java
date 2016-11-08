package com.idotools.browser.pink.book.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.idotools.browser.pink.book.R;
import com.idotools.utils.InputWindowUtils;

/**
 * Created by wuxiaojun on 16-10-18.
 */
public class SearchEditTextView extends EditText implements TextWatcher, View.OnFocusChangeListener {

    private Drawable rightDrawable;


    public SearchEditTextView(Context context) {
        this(context, null);
    }

    public SearchEditTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        rightDrawable = getCompoundDrawables()[2];
        if (rightDrawable == null) {
            rightDrawable = getResources().getDrawable(R.mipmap.img_edit_remove);
        }
        //设置drawable的宽和高
        rightDrawable.setBounds(0, 0, rightDrawable.getIntrinsicWidth(), rightDrawable.getIntrinsicHeight());
        setFocusable(true);
        setFocusableInTouchMode(true);
        addTextChangedListener(this);
        setOnFocusChangeListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getX() > (getWidth() - getTotalPaddingRight()) && event.getX() < (getWidth() - getPaddingRight())) {
                setText(null);
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (!TextUtils.isEmpty(text)) {
            isVisibleRightDrawable(true);
        } else {
            isVisibleRightDrawable(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private void isVisibleRightDrawable(boolean visible) {
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], visible ? rightDrawable : null, getCompoundDrawables()[3]);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            setCursorVisible(true);
        } else {
            InputWindowUtils.closeInputWindow(v,getContext());
        }
    }

}
