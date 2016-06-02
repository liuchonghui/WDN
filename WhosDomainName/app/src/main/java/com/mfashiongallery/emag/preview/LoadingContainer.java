
package com.mfashiongallery.emag.preview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import tool.whosdomainname.android.R;

@SuppressLint("NewApi")
public class LoadingContainer extends FrameLayout {
    private static final float LOADING_ITEM_ALPHA_MIN = 0.5f;
    private static final float LOADING_ITEM_ALPHA_MAX = 0.9f;
    private static final int LOADING_ITEM_COUNT = 4;
    private static final long LOADING_DURATION = 250;

    private boolean mEnableLoadingAnim = false;

    private boolean mStopLoading = true;

    private ObjectAnimator[] mItemAnimIn = new ObjectAnimator[LOADING_ITEM_COUNT];
    private ObjectAnimator[] mItemAnimOut = new ObjectAnimator[LOADING_ITEM_COUNT];
    private View[] mLoadItems = new View[LOADING_ITEM_COUNT];
    private FrameLayout mLoadingView;

    public LoadingContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate() {
        mLoadingView = (FrameLayout) findViewById(R.id.item_container);
        mLoadItems[0] = findViewById(R.id.item1);
        mLoadItems[1] = findViewById(R.id.item2);
        mLoadItems[2] = findViewById(R.id.item3);
        mLoadItems[3] = findViewById(R.id.item4);

        for (int i = 0; i < LOADING_ITEM_COUNT; i++) {
            mItemAnimIn[i] = getItemAnimIn(mLoadItems[i]);
            mItemAnimOut[i] = getItemAnimOut(mLoadItems[i]);
        }
    }

    public void startLoadingAnim() {

        if (!mStopLoading) {
            return;
        }

        if ((View.VISIBLE == this.getVisibility()) && mEnableLoadingAnim) {
            mStopLoading = true;
            mLoadingView.setVisibility(View.VISIBLE);

            for (int i = 0; i < mLoadItems.length; i++) {
                mLoadItems[i].setAlpha(LOADING_ITEM_ALPHA_MIN);
            }

            mStopLoading = false;
            startLoading();
        }

    }

    public void stopLoadingAnim() {
        mStopLoading = true;
    }

    private void resetAlpha() {
        for (int i = 0; i < mLoadItems.length; i++) {
            mLoadItems[i].setAlpha(LOADING_ITEM_ALPHA_MAX);
        }
    }

    private void startLoading() {
        mItemAnimIn[0].addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mEnableLoadingAnim && !mStopLoading) {
                    mItemAnimOut[0].start();
                    mItemAnimIn[1].start();
                } else {
                    resetAlpha();
                }
            }
        });

        mItemAnimIn[1].addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mEnableLoadingAnim && !mStopLoading) {
                    mItemAnimOut[1].start();
                    mItemAnimIn[2].start();
                } else {
                    resetAlpha();
                }
            }
        });

        mItemAnimIn[2].addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mEnableLoadingAnim && !mStopLoading) {
                    mItemAnimOut[2].start();
                    mItemAnimIn[3].start();
                } else {
                    resetAlpha();
                }
            }
        });

        mItemAnimIn[3].addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mEnableLoadingAnim && !mStopLoading) {
                    mItemAnimOut[3].start();
                    mItemAnimIn[0].start();
                } else {
                    resetAlpha();
                }
            }
        });
        mItemAnimIn[0].start();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == View.VISIBLE) {
            startLoadingAnim();
        } else {
            stopLoadingAnim();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mEnableLoadingAnim = false;
        stopLoadingAnim();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mEnableLoadingAnim = true;
    }

    private ObjectAnimator getItemAnimOut(View v) {
        ObjectAnimator animOut = ObjectAnimator.ofFloat(v, "alpha", LOADING_ITEM_ALPHA_MAX, LOADING_ITEM_ALPHA_MIN);
        animOut.setDuration(LOADING_DURATION);
        return animOut;
    }

    private ObjectAnimator getItemAnimIn(View v) {
        ObjectAnimator animIn = ObjectAnimator.ofFloat(v, "alpha", LOADING_ITEM_ALPHA_MIN, LOADING_ITEM_ALPHA_MAX);
        animIn.setDuration(LOADING_DURATION);
        return animIn;
    }


    public boolean isLoading() {
        return View.VISIBLE == getVisibility();
    }
}