
package com.mfashiongallery.emag.preview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.imageloadercompact.CompactImageView;
import com.android.imageloadercompact.ImageLoaderCompact;
import com.mfashiongallery.emag.preview.model.RecordType;
import com.mfashiongallery.emag.preview.model.WallpaperInfo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tool.whosdomainname.android.R;

@SuppressLint("NewApi")
public class LockWallpaperPreviewAdapter extends PagerAdapter {
    protected final static String LOG_TAG = "LockWallpaperPreviewAdapter";
    protected final static boolean DEBUG = LockWallpaperPreviewActivity.DEBUG;
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<WallpaperItem> mWallpaperItems;
    protected int mMaxPixels;
    protected LockWallpaperPreviewView mMainView;
    protected Handler mHandler = new H();
    protected final static int MSG_RECORD_EVENT = 100;

    protected class H extends Handler {
        public void handleMessage(Message m) {
            switch (m.what) {
                case MSG_RECORD_EVENT:
                    handleRecordEvent(m.arg1, m.arg2);
                    break;
            }
        }
    }

    public LockWallpaperPreviewAdapter(Context context, List<WallpaperInfo> wallpaperInfos) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mWallpaperItems = new ArrayList<WallpaperItem>();
        for(WallpaperInfo info :wallpaperInfos) {
            WallpaperItem item = new WallpaperItem();
            item.mInfo = info;
            mWallpaperItems.add(item);
        }
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        mMaxPixels = dm.widthPixels * dm.heightPixels;
    }

    public void recordEvent(int position, int event) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_RECORD_EVENT, position, event));
    }

    public void recordEvent(WallpaperInfo info, int event) {
        int position = -1;
        for (int i = 0; i < mWallpaperItems.size(); i++) {
            WallpaperItem item = mWallpaperItems.get(i);
            if (item != null && item.mInfo == info) {
                position = i;
                break;
            }
        }
        if (position >= 0) {
            mHandler.sendMessage(mHandler.obtainMessage(MSG_RECORD_EVENT, position, event));
        }
    }

    public void handleRecordEvent(int position, int event) {
        if (DEBUG) {
            Log.d(LOG_TAG, "handleRecordEvent position:" + position + " event:" + event);
        }
//        WallpaperInfo info = mWallpaperItems.get(position).mInfo;
        WallpaperInfo info = null;
        if (mWallpaperItems.size() > position && position >= 0) {
            info = mWallpaperItems.get(position).mInfo;
        }
        if (info == null) {
            return;
        }

        String providerInCharge = info.authority;
        if (TextUtils.isEmpty(providerInCharge) ) {
            return;
        }

        try {
            JSONObject jo = new JSONObject();
            jo.put("key", info.key);
            jo.put("event", event);
            String requestJson = jo.toString();
            handleRecordEvent(providerInCharge, requestJson);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleRecordEvent(String authority, String requestJson) {
        if (DEBUG) {
            Log.d(LOG_TAG, "handleRecordEvent requestJson:" + requestJson);
        }
//        try {
//            Bundle extras = new Bundle();
//            extras.putString(MiFGConstants.METHOD_REQUEST_JSON, requestJson);
//            Uri uri = Uri.parse("content://" + authority);
//            mContext.getContentResolver().call(uri, MiFGConstants.METHOD_RECORD_EVENT, null, extras);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public WallpaperInfo getWallpaperInfo(int position) {
        return mWallpaperItems.get(position).mInfo;
    }

    public View getView(int positionInList) {
//        return mWallpaperItems.get(position).mView;
        View view = null;
        WallpaperInfo info = mWallpaperItems.get(positionInList).mInfo;
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

    @Override
    public int getCount() {
        return mWallpaperItems.size() == 1 ? 1 : Integer.MAX_VALUE;
    }

    public int getSize() {
        return mWallpaperItems.size();
    }

    public int getVisibleSize() {
        int full = mWallpaperItems.size();
        int invisible = pagewidth.size();
        return Math.max(0, full - invisible);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
//        return view == ((WallpaperItem) object).mView;
        return view == object;
    }

    public int getPositionInList(int position) {
        return position % getSize();
    }

    protected WallpaperItem getPosItem(int pos) {
        return mWallpaperItems.get(pos);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final int pos = getPositionInList(position);
        final WallpaperItem item = getPosItem(pos);
        final WallpaperInfo info = item.mInfo;

//        ViewGroup view = null;
        ViewGroup viewgroup = null;
//        if (item.mView != null) {
//            view = (ViewGroup) item.mView;
//        }
        if (viewgroup == null) {
            viewgroup = (ViewGroup) mInflater.inflate(R.layout.draw_text_on_page_template, null);
            TextView title = (TextView) viewgroup.findViewById(R.id.player_pager_title);
            TextView content = (TextView) viewgroup.findViewById(R.id.player_pager_content);
            View clickArea = viewgroup.findViewById(R.id.player_pager_click_area);
            clickArea.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = info.buildIntent();
                    if (intent != null) {
                        try {
                            mContext.startActivity(intent);
                            recordEvent(pos, RecordType.EVENT_CLICK);
                            mContext.sendBroadcast(new Intent("xiaomi.intent.action.SHOW_SECURE_KEYGUARD"));
                            ((Activity) mMainView.getContext()).finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            Float v = pagewidth.get(pos);
            if (v == null || v.floatValue() != 0f) {
                CompactImageView image = (CompactImageView) viewgroup.findViewById(R.id.player_pager_wallpaper);
                if (pos == 0) {
//            File first_pic = ThemeResources.getSystem().getLockscreenWallpaper();
//            Picasso.with(mContext).load(first_pic).into(image);
//                    image.setImageDrawable(ThemeResources.getLockWallpaperCache(mContext));
                    ImageLoaderCompact.getInstance().displayImage(mContext, info.wallpaperUri, image);
                } else {
                    if (info != null) {
//                        Uri uri = Uri.parse(info.wallpaperUri);
//                        Picasso.with(mContext).load(uri).into(image);
                        ImageLoaderCompact.getInstance().displayImage(mContext, info.wallpaperUri, image);
                    }
//            loadBitmap(pos, image);
                }
                title.setText(info.title);
                content.setText(info.content + "." + position);
            }
        }

//        if (view != null && view.getChildAt(0) != null) {
//            View v = view.getChildAt(0);
//            view.removeViewAt(0);
//            viewgroup.removeViewAt(0);
//            viewgroup.addView(v, 0);
//        }
        if (useCache) {
            if (cachedView != null && cachedView.getChildAt(0) != null) {
                View v = cachedView.getChildAt(0);
                cachedView.removeViewAt(0);
                viewgroup.removeViewAt(0);
                viewgroup.addView(v, 0);
                cachedView = null;
                useCache = false;
            }
        }

        container.addView(viewgroup, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
//        item.mView = viewgroup;
        viewgroup.setTag(info);
        return viewgroup;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        WallpaperItem item = (WallpaperItem) object;
//        container.removeView(item.mView);
//        if (item != cachedItem) {
//            item.mView = null;
//        }
        View view = (View) object;
        container.removeView(view);
//        if (view.getTag() == cachedInfo) {
//            cachedView = (ViewGroup) view;
//        }
        view.setTag(null);
        object = null;
    }

    public boolean isLiked(int position) {
        return false;
    }

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        View clickArea = view.findViewById(R.id.player_pager_click_area);
        View title = view.findViewById(R.id.player_pager_title);
        View content = view.findViewById(R.id.player_pager_content);
        if (position < -1) { // [-Infinity,-1)
            title.setTranslationX(0);
            title.setAlpha(0);
            content.setTranslationX(0);
            content.setAlpha(0);
        } else if (position <= 1) { // [-1,1]
            clickArea.setAlpha(1);
            title.setTranslationX(pageWidth * position * 0.2f);
            title.setAlpha(getTitleFactor(1 - Math.abs(position)));
            content.setTranslationX(pageWidth * position * 0.1f);
            content.setAlpha(getContentFactor(1 - Math.abs(position)));
        } else { // (1,+Infinity]
            title.setTranslationX(0);
            title.setAlpha(0);
            content.setTranslationX(0);
            content.setAlpha(0);
        }
    }

    private float getTitleFactor(float position) {
        return position * position * position;
    }

    private float getContentFactor(float position) {
        return position;
    }

    public void setViewPager(LockWallpaperPreviewView lockWallpaperPreviewView) {
        mMainView = lockWallpaperPreviewView;
    }

    WallpaperInfo cachedInfo;
    ViewGroup cachedView;

    public void cacheTargetView(int positionInList) {
        WallpaperItem item = mWallpaperItems.get(positionInList);
        if (item != null) {
            int nextIndex = positionInList + 1;
            if (nextIndex >= getSize()) {
                nextIndex = 0;
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

    boolean useCache;

    public WallpaperInfo removeWallpapaerItem(int positionInList, int positionInViewPager) {
        WallpaperItem item = mWallpaperItems.get(positionInList);
        if (item != null) {
            int nextIndex = positionInList + 1;
            if (nextIndex >= getSize()) {
                nextIndex = 0;
            }
            cachedInfo = mWallpaperItems.get(nextIndex).mInfo;
            mWallpaperItems.remove(item);
        }
        useCache = true;
        return cachedInfo;
    }

    public int countLoop(int positionInViewPager) {
        int loop = positionInViewPager / getSize();
        return loop;
    }

    @Override
    public int getItemPosition(Object object) {
//        if (cachedItem != null) {
//            return POSITION_NONE;
//        }
//        WallpaperItem checkedItem = (WallpaperItem) object;
//        WallpaperItem curItem = mWallpaperItems.get(mMainView.getCurrentItem());
//        if (checkedItem == curItem) {
//            return POSITION_UNCHANGED;
//        }
//        return POSITION_NONE;
        return POSITION_NONE;
    }

    HashMap<Integer, Float> pagewidth = new HashMap<Integer, Float>();

    public void setPositionWidthToZero(int position) {
        pagewidth.put(position, 0f);
    }

    @Override
    public float getPageWidth(int position) {
        int pos = position % getSize();
        Float v = pagewidth.get(pos);
        if (v != null) {
            return v.floatValue();
        }
        return super.getPageWidth(position);
    }

    public void notifyAdapterDataChanged() {
        mMainView.updateActionMenuView();
        notifyDataSetChanged();
    }

    public boolean isFirst(int positionInList) {
        return 0 == positionInList;
    }

    public boolean canDislike(int positionInList) {
        return getSize() > 1;
    }
}
