
package com.mfashiongallery.emag.preview;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import com.mfashiongallery.emag.preview.model.CubicEaseInOutInterpolator;
import com.mfashiongallery.emag.preview.model.CubicEaseOutInterpolator;
import com.mfashiongallery.emag.preview.model.RecordType;
import com.mfashiongallery.emag.preview.model.WallpaperInfo;

import android.support.v4.view.ViewPager;
import android.support.v4.view.CustomViewPager;

import tool.whosdomainname.android.R;

@SuppressLint("NewApi")
public class LockWallpaperPreviewView extends FrameLayout {
    private CustomViewPager mViewPager;
    private LockWallpaperPreviewAdapter mAdapter;

    private LoadingContainer mLoadingView;
    private ActionMenus mActionMenus;
    private View mMask;

    private boolean mHasShowHint;

    ViewConfiguration mViewConfiguration;
    Context mContext;

    public LockWallpaperPreviewView(Context context) {
        super(context);
        mContext = context;
    }

    public LockWallpaperPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public LockWallpaperPreviewView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mViewConfiguration = ViewConfiguration.get(mContext);
        mViewPager = new CustomViewPager(mContext);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setMainView(this);
        addView(mViewPager, 0);

        mActionMenus = (ActionMenus) findViewById(R.id.menu);
        mActionMenus.setMainView(this);

        mLoadingView = (LoadingContainer) findViewById(R.id.loading_container);
        mMask = findViewById(R.id.mask);
        mViewPager.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    return cannotMove();
                }
                return false;
            }
        });
        setOnPageChangeListener(null);
    }

    public void setAdapter(LockWallpaperPreviewAdapter adapter) {
        mAdapter = adapter;
        mAdapter.setViewPager(this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View view, float position) {
                int pageWidth = view.getWidth();
                View wallpaper = view.findViewById(R.id.player_pager_wallpaper);
                if (position < -1) { // [-Infinity,-1)
                    wallpaper.setTranslationX(0);
                    view.setTranslationX(0);
                } else if (position <= 1) { // [-1,1]
                    wallpaper.setTranslationX(pageWidth * getFactor(position));
                    view.setTranslationX(8 * position);
                } else { // (1,+Infinity]
                    wallpaper.setTranslationX(0);
                    view.setTranslationX(0);
                }
                if (!mHasShowHint || mInExit) {
                    return;
                }
                mAdapter.transformPage(view, position);
            }

            private float getFactor(float position) {
                return -position / 2;
            }
        });
        mActionMenus.updateView();
    }

    public void updateActionMenuView() {
        mActionMenus.updateView();
    }

    public interface OnPageChangeListener extends ViewPager.OnPageChangeListener {

    }
    public void setOnPageChangeListener(final OnPageChangeListener listener) {
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                mAdapter.recordEvent(position % mAdapter.getSize(), RecordType.EVENT_SHOW);
                mActionMenus.updateView();

                if (listener != null) {
                    listener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (listener != null) {
                    listener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (listener != null) {
                    listener.onPageScrollStateChanged(state);
                }
            }
        });
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)
                    || Intent.ACTION_SCREEN_OFF.equals(action)) {
                    ((Activity) getContext()).finish();
            }
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // receive broadcasts
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mContext.registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mContext.unregisterReceiver(mBroadcastReceiver);
    }

    private boolean cannotMove() {
        return mInExit || mLoadingView.isLoading() || fakeDraggin;
    }

    private boolean mInExit;
    public void startExitAnim() {
        mInExit = true;
        View view = mAdapter.getView(getCurrentItem());
        if (view != null) {
            final View clickArea = view.findViewById(R.id.player_pager_click_area);
            clickArea.animate().alpha(0).setDuration(500).setStartDelay(100).setListener(null).start();
        }
    }

    public void showHint() {
        mActionMenus.show(true);
        View view = mAdapter.getView(getCurrentItem());
        if (view != null) {
            final View clickArea = view.findViewById(R.id.player_pager_click_area);
            clickArea.animate().alpha(1).setDuration(500).setStartDelay(100)
                    .setListener(new AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    showScrollHint();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }
            }).start();
        } else {
            showScrollHint();
        }
    }

    private int mLastDragValue = 0;
    private void showScrollHint() {
        if (mInExit) {
            return;
        }
        mHasShowHint = true;
        ValueAnimator animator = ValueAnimator.ofInt(0,
                (int) (-30 * mContext.getResources().getDisplayMetrics().density));
        animator.setDuration(500);
        animator.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                mLastDragValue = 0;
                mViewPager.beginFakeDrag();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mViewPager.isFakeDragging()) {
                    mViewPager.endFakeDrag();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        animator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mViewPager.isFakeDragging()) {
                    int value = (Integer) animation.getAnimatedValue();
                    mViewPager.fakeDragBy(value - mLastDragValue);
                    mLastDragValue = value;
                }
            }
        });
        animator.start();
    }

    public int getViewPagerChildCount() {
        return mViewPager.getChildCount();
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    public int getCurrentItem() {
//        return mViewPager.getCurrentItem() % mAdapter.getSize();
        return mAdapter.getPositionInList(mViewPager.getCurrentItem());
    }

    public LockWallpaperPreviewAdapter getAdapter() {
        return mAdapter;
    }

    public int getAdapterVisibleChildCount() {
        return mAdapter.getVisibleSize();
    }

    public View getLoadingView() {
        return mLoadingView;
    }

    public void showMask() {
        mMask.animate().alpha(1).start();
    }

    public void hideMask() {
        mMask.animate().alpha(0).start();
    }

    public void toggleMenus() {
        mActionMenus.toggle();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(mViewPager.isFakeDragging()) {
            return true;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    public void startDislikeAnim() {
        if (!mHasShowHint || mInExit) {
            // 没开始或者已结束
            return;
        }
        if (mAdapter == null || mAdapter.getVisibleSize() <= 1) {
            // 再删除就没有数据
            return;
        }
        if (!mViewPager.isStateIdle()) {
            // viewpager还在滑动状态
            return;
        }
        int positionInViewPager = mViewPager.getCurrentItem();
        int positionInList = mAdapter.getPositionInList(positionInViewPager);
        mAdapter.cacheTargetView(positionInList);
        startFadeTurnPageAnim(positionInList, positionInViewPager, null);
        fakeScrollOnePage(positionInList, positionInViewPager, new PositionRunnable() {
            @Override
            public void run(int positionInList, int positionInViewPager) {
                WallpaperInfo nextInfo = removeItemByPosition(positionInList, positionInViewPager);
                if (mAdapter.isFirst(positionInList)) {
                    // 用户删除了第0个，即当前锁屏图，那么应用下一张作为锁屏图并通知用户已替换
                    mActionMenus.applyWallpaper(nextInfo);
                }
			}
		});
    }

    private void startFadeTurnPageAnim(final int positionInList, final int positionInViewPager,
                                       final PositionRunnable finallyToDo) {
        View view = mAdapter.getView(positionInList);
        if (view != null) {
            ObjectAnimator alphaAnimOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.1f);
            ObjectAnimator scaleXAnimOut = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f);
            ObjectAnimator scaleYAnimOut = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f);

            AnimatorSet animOut = new AnimatorSet();
            animOut.setInterpolator(new CubicEaseInOutInterpolator());
            animOut.play(alphaAnimOut).with(scaleXAnimOut).with(scaleYAnimOut);
            animOut.setDuration(500L);
            animOut.addListener(new AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (finallyToDo != null) {
                        finallyToDo.setData(positionInList, positionInViewPager);
                        finallyToDo.run();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            animOut.start();
        }
    }

    private int mLastFakeDragValue = 0;
    private boolean fakeDraggin = false;

    public void fakeScrollOnePage(final int positionInList, final int positionInViewPager,
                                  final PositionRunnable finallyToDo) {
        if (!mHasShowHint || mInExit || fakeDraggin) {
            // 没开始或者已结束
            return;
        }
        if (mAdapter == null || mAdapter.getVisibleSize() <= 1) {
            // 再删除就没有数据
            return;
        }
        if (!mViewPager.isStateIdle()) {
            // viewpager还在滑动状态
            return;
        }
        fakeDraggin = true;
//        DisplayMetrics display = Resources.getSystem().getDisplayMetrics();
//        int width = display.widthPixels;
        int clientWidth = mViewPager.getMeasuredWidth()
                - mViewPager.getPaddingLeft() - mViewPager.getPaddingRight();
        ValueAnimator animator = ValueAnimator.ofInt(0, -clientWidth);
        animator.setDuration(500L);
        animator.setStartDelay(333L);
        animator.setInterpolator(new CubicEaseOutInterpolator());
        animator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mLastFakeDragValue = 0;
                mViewPager.beginFakeDrag();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mViewPager.isFakeDragging()) {
                    mViewPager.endFakeDrag();

                    if (finallyToDo != null) {
                        finallyToDo.setData(positionInList, positionInViewPager);
                        finallyToDo.run();
                    }

                    fakeDraggin = false;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mViewPager.isFakeDragging()) {
                    int value = (Integer) animation.getAnimatedValue();
                    mViewPager.fakeDragBy(value - mLastFakeDragValue);
                    mLastFakeDragValue = value;
                }
            }
        });
        animator.start();
    }

    private WallpaperInfo removeItemByPosition(int positionInList, int positionInViewPager) {
//        int size = mAdapter.getSize();
//        final int loop = positionInViewPager / size;
        final int loop = mAdapter.countLoop(positionInViewPager);
        WallpaperInfo nextInfo = null;
        {   // 解决方案1:
            // 使用remove＋notify的方式去处理数据，但是处理之后，curItem的位置发生改变，
            // 需要手动计算期望的位置，所以有setCurrentItem调用，带来的问题就是重复刷新的问题
            // 后来用缓存View的方式解决
            mViewPager.resetCurrentItem(positionInViewPager - loop);
            nextInfo = mAdapter.removeWallpapaerItem(positionInList, positionInViewPager);
            mAdapter.notifyAdapterDataChanged();
        }
        {   // 解决方案2:
            // 使用getPageWith=0f的方式解决数据问题，curItem位置不发生变化
//            mAdapter.setPositionWidthToZero(positionInList);
//            mAdapter.notifyDataSetChanged();
        }
        return nextInfo;
    }

    public void declarConfirmFakeScroll() {
        if (!mHasShowHint || mInExit) {
            // 没开始或者已结束
            return;
        }
        if (mAdapter == null || mAdapter.getVisibleSize() <= 1) {
            // 再删除就没有数据
            return;
        }
        if (!mViewPager.isStateIdle()) {
            // viewpager还在滑动状态
            return;
        }
        int positionInViewPager = mViewPager.getCurrentItem();
        int positionInList = mAdapter.getPositionInList(positionInViewPager);
        fakeScrollOnePage(positionInList, positionInViewPager, new PositionRunnable() {
            @Override
            public void run(int positionInList, int positionInViewPager) {
                WallpaperInfo nextInfo = removeItemByPosition(positionInList, positionInViewPager);
                if (mAdapter.isFirst(positionInList)) {
                    // 用户删除了第0个，即当前锁屏图，那么应用下一张作为锁屏图并通知用户已替换
                    mActionMenus.applyWallpaper(nextInfo);
                }
            }
        });
    }
}
