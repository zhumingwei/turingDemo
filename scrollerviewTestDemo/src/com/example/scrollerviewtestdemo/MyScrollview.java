package com.example.scrollerviewtestdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class MyScrollview extends ScrollView {

	private OnBorderListener onBorderListener;
    private OnTouchListener  onBorderTouchListener;
    private View             contentView;

    public MyScrollview(Context context){
        super(context);
    }

    public MyScrollview(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public MyScrollview(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    public void setOnBorderListener(final OnBorderListener onBorderListener) {
        this.onBorderListener = onBorderListener;
        if (onBorderListener == null) {
            this.onBorderTouchListener = null;
            return;
        }

        if (contentView == null) {
            contentView = getChildAt(0);
        }
        this.onBorderTouchListener = new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        doOnBorderListener();
                        break;
                }
                return false;
            }

        };
        super.setOnTouchListener(onBorderTouchListener);
    }

    /**
     * OnBorderListener, Called when scroll to top or bottom
     * 
     * @author gxwu@lewatek.com 2013-5-22
     */
    public static interface OnBorderListener {

        /**
         * Called when scroll to bottom
         */
        public void onBottom();

        /**
         * Called when scroll to top
         */
        public void onTop();
    }

    @Override
    public void setOnTouchListener(final OnTouchListener l) {
        OnTouchListener onTouchListener = new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (onBorderTouchListener != null) {
                    onBorderTouchListener.onTouch(v, event);
                }
                return l.onTouch(v, event);
            }

        };
        super.setOnTouchListener(onTouchListener);
    }

    private void doOnBorderListener() {
        if (contentView != null && contentView.getMeasuredHeight() <= getScrollY() + getHeight()) {
            if (onBorderListener != null) {
                onBorderListener.onBottom();
            }
        } else if (getScrollY() == 0) {
            if (onBorderListener != null) {
                onBorderListener.onTop();
            }
        }
    }
}
