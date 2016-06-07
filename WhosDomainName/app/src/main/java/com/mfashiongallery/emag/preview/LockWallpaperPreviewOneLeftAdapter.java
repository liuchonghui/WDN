package com.mfashiongallery.emag.preview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.mfashiongallery.emag.preview.model.WallpaperInfo;

import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * Created by liuchonghui on 16/6/6.
 */
public class LockWallpaperPreviewOneLeftAdapter extends LockWallpaperPreviewAdapter {
    public LockWallpaperPreviewOneLeftAdapter(Context context, List<WallpaperInfo> wallpaperInfos) {
        super(context, wallpaperInfos);
    }

    boolean zeroExist = true;

    public int getPositionInList(int position) {
        int lSize = getSize() - 1;
        if (lSize == 0) {
            return 0;
        }
        int lop = position / lSize;
        int pos = position % lSize;
        if (pos == 0 && lop > 0) {
            pos = lSize;
        }
        return pos;
    }

    public void cacheTargetView(int positionInList) {
        WallpaperItem item = mWallpaperItems.get(positionInList);
        if (item != null) {
            int nextIndex;
            if (positionInList == 0) {
                nextIndex = 1;
            } else if (positionInList == getSize() - 1) {
                if (mWallpaperItems.get(0).mInfo.key == null) {
                    nextIndex = 0;
                } else {
                    nextIndex = 1;
                }
            } else {
                nextIndex = positionInList + 1;
            }
            WallpaperInfo nextInfo = mWallpaperItems.get(nextIndex).mInfo;
            int count = mMainView.getViewPagerChildCount();
            for (int i = 0; i < count; i++) {
                View child = mMainView.getViewPager().getChildAt(i);
                if (child.getTag() == nextInfo) {
                    cachedView = (ViewGroup) child;
                    break;
                }
            }
        }
    }

    public WallpaperInfo removeWallpapaerItem(int positionInList, int positionInViewPager) {
        WallpaperItem item = mWallpaperItems.get(positionInList);
        if (item != null) {
            int nextIndex;
            if (positionInList == 0) {
                nextIndex = 1;
            } else if (positionInList == getSize() - 1) {
                if (mWallpaperItems.get(0).mInfo.key == null) {
                    nextIndex = 0;
                } else {
                    nextIndex = 1;
                }
            } else {
                nextIndex = positionInList + 1;
            }
            cachedInfo = mWallpaperItems.get(nextIndex).mInfo;
            mWallpaperItems.remove(item);
        }
        useCache = true;
        return cachedInfo;
    }

    public int countLoop(int positionInViewPager) {
        int lSize = getSize() - 1;
        if (lSize == 0) {
            return 0;
        }
        if (positionInViewPager == lSize) {
            return 0;
        }
        int loop = positionInViewPager / lSize;
        return loop;
    }

}
