package com.learn.heddy.xyzreader.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by hyeryungpark on 3/3/17.
 */

public class MaxHeightLinearLayout extends LinearLayout {
    private static final int[] ATTRS = {
            android.R.attr.maxWidth
    };

    private int mMaxHeight = Integer.MAX_VALUE;

    public MaxHeightLinearLayout(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public MaxHeightLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public MaxHeightLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle, 0);
    }

    public MaxHeightLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        mMaxHeight = a.getLayoutDimension(0, Integer.MAX_VALUE);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int newSpecHeight = Math.min(MeasureSpec.getSize(heightMeasureSpec), mMaxHeight);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(newSpecHeight, MeasureSpec.getMode(heightMeasureSpec));
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }
}