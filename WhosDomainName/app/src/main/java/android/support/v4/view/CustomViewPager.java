package android.support.v4.view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.mfashiongallery.emag.preview.LockWallpaperPreviewView;

/**
 * Created by liuchonghui on 16/6/3.
 */
public class CustomViewPager extends ViewPager {

    private float mInitialTouchY, mInitialTouchX;
    private ViewConfiguration mViewConfiguration;
    private LockWallpaperPreviewView mMainView;

    public CustomViewPager(Context context) {
        super(context);
        mViewConfiguration = ViewConfiguration.get(context);
    }

    public void setMainView(LockWallpaperPreviewView view) {
        mMainView = view;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isFakeDragging()) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
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

    @Override
    ItemInfo infoForPosition(int position) {
        ItemInfo info = super.infoForPosition(position);
        if (clipPopulate) {
            clipPopulate = false;
            if (info != null && info.scrolling) {
                info.scrolling = false;
            }
            ItemInfo ii = null;
            int startPos = position + 1;
            int endPos = position + getOffscreenPageLimit();
            for (int i = startPos; i <= endPos; i++) {
                ii = super.infoForPosition(i);
                if (ii != null && ii.scrolling) {
                    ii.scrolling = false;
                }
            }
            startPos = position - 1;
            endPos = position - getOffscreenPageLimit();
            for (int i = startPos; i >= endPos; i--) {
                ii = super.infoForPosition(i);
                if (ii != null && ii.scrolling) {
                    ii.scrolling = false;
                }
            }
        }
        return info;
    }

//    @Override
//    public void setCurrentItem(int item, boolean smoothScroll) {
//        clipPopulate = true;
//        super.setCurrentItem(item, smoothScroll);
//        clipPopulate = false;
//    }

    public void resetCurrentItem(int newItem) {
        clipPopulate = true;
        setCurrentItem(newItem, false);
    }

    boolean clipPopulate;

//    @Override
//    void populate(int newCurrentItem) {
////        clipPopulate = true;
//        super.populate(newCurrentItem);
////        clipPopulate = false;
//    }

//    @Override
//    ItemInfo addNewItem(int position, int index) {
//        ItemInfo info = super.addNewItem(position, index);
//        info.scrolling = false;
//        return info;
//    }
}
