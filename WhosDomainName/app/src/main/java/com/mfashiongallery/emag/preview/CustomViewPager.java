package com.mfashiongallery.emag.preview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class CustomViewPager extends ViewPager {

    private float mInitialTouchY, mInitialTouchX;
    private ViewConfiguration mViewConfiguration;
    private LockWallpaperPreviewView mMainView;
    private boolean mTouchSlopEnable = true;

    public CustomViewPager(Context context) {
        super(context);
        mViewConfiguration = ViewConfiguration.get(context);
    }

    public void setMainView(LockWallpaperPreviewView view) {
        mMainView = view;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mTouchSlopEnable) {
            return false;
        }
        if (isFakeDragging()) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!mTouchSlopEnable) {
            mMainView.toggleMenus();
            return false;
        }
        if (isFakeDragging()) {
            return true;
        }
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mInitialTouchX = ev.getRawX();
            mInitialTouchY = ev.getRawY();
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            final int touchSlop = mViewConfiguration.getScaledTouchSlop();
            if (Math.abs(mInitialTouchX - ev.getRawX()) < touchSlop &&
                    Math.abs(mInitialTouchY - ev.getRawY()) < touchSlop) {
                mMainView.toggleMenus();
            }
        }
        return super.onTouchEvent(ev);
    }

    private enum State {
        IDLE,
        GOING_LEFT,
        GOING_RIGHT,
    }

    private State mState;
    private int oldPage;

    @Override
    protected void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mState == State.IDLE && positionOffset > 0) {
            oldPage = getCurrentItem();
            mState = position == oldPage ? State.GOING_RIGHT : State.GOING_LEFT;
        }
        boolean goingRight = position == oldPage;
        if (mState == State.GOING_RIGHT && !goingRight) {
            mState = State.GOING_LEFT;
        } else if (mState == State.GOING_LEFT && goingRight) {
            mState = State.GOING_RIGHT;
        }

        float effectOffset = isSmall(positionOffset) ? 0 : positionOffset;

        super.onPageScrolled(position, positionOffset, positionOffsetPixels);

        if (effectOffset == 0) {
            mState = State.IDLE;
        }
    }

    private boolean isSmall(float positionOffset) {
        return Math.abs(positionOffset) < 0.0001;
    }

    public boolean isStateIdle() {
        return State.IDLE == mState;
    }

    public State getState() {
        return mState;
    }

    public void setTouchSlopEnable(boolean enable) {
        mTouchSlopEnable = enable;
    }
}
