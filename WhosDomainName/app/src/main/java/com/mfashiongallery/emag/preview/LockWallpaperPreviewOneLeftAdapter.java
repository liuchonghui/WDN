package com.mfashiongallery.emag.preview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.mfashiongallery.emag.preview.model.WallpaperInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuchonghui on 16/6/6.
 */
public class LockWallpaperPreviewOneLeftAdapter extends LockWallpaperPreviewAdapter {
    public LockWallpaperPreviewOneLeftAdapter(Context context, List<WallpaperInfo> wallpaperInfos) {
        super(context, wallpaperInfos);
        firstItem = null;
        mWallpaperItems.clear();
        if (wallpaperInfos != null) {
            if (wallpaperInfos.size() > 0) {
                WallpaperInfo firstInfo = wallpaperInfos.get(0);
                firstInfo.supportDislike = true;
                firstItem = new WallpaperItem();
                firstItem.mInfo = firstInfo;
            }
            if (wallpaperInfos.size() > 1) {
                WallpaperItem item = null;
                List<WallpaperItem> tails = new ArrayList<WallpaperItem>();
                for (int i = 1; i < wallpaperInfos.size(); i++) {
                    item = new WallpaperItem();
                    item.mInfo = wallpaperInfos.get(i);
                    tails.add(item);
                }
                mWallpaperItems.addAll(tails);
            }
        }
    }

    public int getVisibleSize() {
        int visibleSize = super.getVisibleSize();
        if (firstItem == null) {
            return visibleSize;
        } else {
            return visibleSize + 1;
        }
    }

    protected WallpaperItem firstItem = null;

    public int getPositionInList(int position) {
        int pos = 0;
        if (firstItem == null) {
            pos = position % getSize();
        } else {
            if (0 == position) {
                pos = -1;
            } else {
                pos = (position - 1) % getSize();
            }
        }
        return pos;
    }

    protected WallpaperItem getPosItem(int pos) {
        return pos < 0 ? firstItem : mWallpaperItems.get(pos);
    }

    public void cacheTargetView(int positionInList) {
        WallpaperInfo nextInfo = null;
        if (positionInList < 0) {
            nextInfo = mWallpaperItems.get(0).mInfo;
        } else {
            WallpaperItem item = mWallpaperItems.get(positionInList);
            int nextIndex = positionInList + 1;
            if (nextIndex >= getSize()) {
                nextIndex = 0;
            }
            nextInfo = mWallpaperItems.get(nextIndex).mInfo;
        }

        int count = mMainView.getViewPagerChildCount();
        for (int i = 0; i < count; i++) {
            View child = mMainView.getViewPager().getChildAt(i);
            if (child.getTag() == nextInfo) {
                cachedView = (ViewGroup) child;
                break;
            }
        }
    }

    public WallpaperInfo removeWallpapaerItem(int positionInList, int positionInViewPager) {
        WallpaperInfo nextInfo = null;
        if (positionInList < 0) {
            nextInfo = mWallpaperItems.get(0).mInfo;
            firstItem = null;
        } else {
            WallpaperItem item = mWallpaperItems.get(positionInList);
            int nextIndex = positionInList + 1;
            if (nextIndex >= getSize()) {
                nextIndex = 0;
            }
            nextInfo = mWallpaperItems.get(nextIndex).mInfo;
            mWallpaperItems.remove(item);
        }

        cachedInfo = nextInfo;
        useCache = true;
        return cachedInfo;
    }

    public int countLoop(int positionInViewPager) {
        int loop = 0;
        if (firstItem == null) {
            loop = positionInViewPager / getSize();
        } else {
            loop = (positionInViewPager - 1) / getSize();
        }
        return loop;
    }

    public View getView(int positionInList) {
        View view = null;
        WallpaperInfo info = null;
        if (positionInList < 0) {
            info = firstItem.mInfo;
        } else {
            info = mWallpaperItems.get(positionInList).mInfo;
        }

        int count = mMainView.getViewPagerChildCount();
        for (int i = 0; i < count; i++) {
            View child = mMainView.getViewPager().getChildAt(i);
            if (child.getTag() == info) {
                view = child;
                break;
            }
        }
        return view;
    }

    public boolean isFirst(int positionInList) {
        if (positionInList == -1) {
            return true;
        }
        if (firstItem == null) {
            return 0 == positionInList;
        } else {
            return -1 == positionInList;
        }
    }

    @Override
    public int getCount() {
        if (firstItem == null) {
            return mWallpaperItems.size() == 1 ? 1 : Integer.MAX_VALUE;
        } else {
            return mWallpaperItems.size() == 1 ? 2 : Integer.MAX_VALUE;
        }
    }

    public WallpaperInfo getWallpaperInfo(int positionInList) {
        WallpaperInfo info = null;
        if (positionInList < 0) {
            info = firstItem.mInfo;
        } else {
            info = mWallpaperItems.get(positionInList).mInfo;
            if (getSize() <= 1) {
               info.supportDislike = false;
            }
        }
        return info;
    }
}
