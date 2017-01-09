package com.gp.browser.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.gp.browser.R;
import com.gp.utils.InputWindowUtils;

/**
 * Created by wuxiaojun on 16-10-18.
 */
public class SearchEditTextView extends EditText implements TextWatcher, View.OnFocusChangeListener, View.OnKeyListener {

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
        setOnKeyListener(this);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                if(monKeyListener != null){
                    monKeyListener.onKey();
                }
            }
        }
        return false;
    }

    private onKeyListener monKeyListener;

    public void setOnKeyListener(onKeyListener monKeyListener) {
        this.monKeyListener = monKeyListener;
    }

    public interface onKeyListener {
        void onKey();
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
            if (mFrameLayout != null && mFrameLayout.getVisibility() == View.GONE) {
                mFrameLayout.setVisibility(View.VISIBLE);
            }
        } else {
            if (mFrameLayout != null && mFrameLayout.getVisibility() == View.VISIBLE) {
                mFrameLayout.setVisibility(View.GONE);
            }
            InputWindowUtils.closeInputWindow(v, getContext());
        }
    }

    private FrameLayout mFrameLayout;

    public void setFrameLayout(FrameLayout frameLayout) {
        this.mFrameLayout = frameLayout;
        mFrameLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                v.requestFocus();

                InputWindowUtils.closeInputWindow(v, getContext());
                mFrameLayout.setVisibility(View.GONE);
            }
        });
    }

    public void backKey() {
        if (mFrameLayout != null && mFrameLayout.getVisibility() == View.VISIBLE) {
            mFrameLayout.performClick();
        }
    }

}
