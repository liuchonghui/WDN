package com.mfashiongallery.emag.preview;

/**
 * @author liuchonghui
 */
public abstract class PositionRunnable implements Runnable {
    int positionInList;
    int positionInViewPager;

    public void setData(int positionInList, int positionInViewPager) {
        this.positionInList = positionInList;
        this.positionInViewPager = positionInViewPager;
    }

    @Override
    public void run() {
        run(getPositionInList(), getPositionInViewPager());
    }

    protected abstract void run(int positionInList, int positionInViewPager);

    protected int getPositionInList() {
        return positionInList;
    }

    protected int getPositionInViewPager() {
        return positionInViewPager;
    }
}
