package com.model.cjx.component;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by cjx on 2016/12/12.
 */
public class MyViewPager extends ViewPager {
    private float mDownMotionX;
    private float mDownMotionY;
    boolean canScroll = true;
    RotationView.CanScrollListener canScrollListener;

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCanScrollListener(RotationView.CanScrollListener listener) {
        canScrollListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (canScrollListener != null) {
                canScrollListener.closeScroll();
            }
            return super.onInterceptTouchEvent(ev);
        } else {
            if (!canScroll) {
                return false;
            } else {
                return super.onInterceptTouchEvent(ev);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        // 每次进行onTouch事件都记录当前的按下的坐标
        switch (arg0.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownMotionX = arg0.getX();
                mDownMotionY = arg0.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float xDiff = Math.abs(arg0.getX() - mDownMotionX);
                final float yDiff = Math.abs(arg0.getY() - mDownMotionY);

                if (xDiff > yDiff) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
        }
        return super.onTouchEvent(arg0);
    }

    public void setScroll(boolean scroll) {
        canScroll = scroll;
    }
}
