package com.xando.chefsclub;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


public class ScrollChangingViewPager extends ViewPager {

    private boolean isPagingEnabled = true;

    public ScrollChangingViewPager(Context context) {
        super(context);
    }

    public ScrollChangingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return this.isPagingEnabled && super.performClick();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onInterceptTouchEvent(event);
    }

    public ScrollChangingViewPager setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;

        return this;
    }
}
