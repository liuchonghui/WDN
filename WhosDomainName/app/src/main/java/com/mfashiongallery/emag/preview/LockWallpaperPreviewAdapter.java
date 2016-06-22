
package com.mfashiongallery.emag.preview;

import android.annotation.TargetApi;
import android.content.Context;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
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

import com.mfashiongallery.emag.preview.model.RecordType;
import com.mfashiongallery.emag.preview.model.WallpaperInfo;
import com.mfashiongallery.emag.utils.MiFGUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tool.whosdomainname.android.R;


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
    protected boolean firstShown = true;

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

    class EventObj {
        int event;
        String identify;
        String authority;
        EventObj(int event, String identify, String authority) {
            this.event = event;
            this.identify = identify;
            this.authority = authority;
        }
    }

    public void recordEvent(int event, WallpaperInfo info) {
        if (info == null || info.key == null || info.authority == null) {
            return;
        }
        Message m = new Message();
        m.what = MSG_RECORD_EVENT;
        m.obj = new EventObj(event, info.key, info.authority);
        mHandler.sendMessage(m);
    }

    class H extends Handler {
        public void handleMessage(Message m) {
            if (MSG_RECORD_EVENT == m.what) {
                if (m.obj instanceof EventObj) {
                    EventObj obj = (EventObj) m.obj;
                    handleRecordEvent(obj.event, obj.identify, obj.authority);
                }
            }
        }
    }

    public void handleRecordEvent(int event, String identify, String authority) {
        if (DEBUG) {
            Log.d("ACME", "handleRecordEvent " + identify + ", " + event);
        }
        if (TextUtils.isEmpty(identify) || TextUtils.isEmpty(authority)) {
            return;
        }

        try {
            JSONObject jo = new JSONObject();
            jo.put("key", identify);
            jo.put("event", event);
            String requestJson = jo.toString();

//            Bundle extras = new Bundle();
//            extras.putString(MiFGConstants.METHOD_REQUEST_JSON, requestJson);
//            Uri uri = Uri.parse("content://" + authority);
//            mContext.getContentResolver().call(uri,
//                    MiFGConstants.METHOD_RECORD_EVENT, null, extras);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public WallpaperInfo getWallpaperInfo(int positionInList) {
        return mWallpaperItems.get(positionInList).mInfo;
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
//        final int pos = position % getSize();
//        final WallpaperItem item = mWallpaperItems.get(pos);
        final int pos = getPositionInList(position);
        final WallpaperItem item = getPosItem(pos);
        final WallpaperInfo info = item.mInfo;
        if (DEBUG) {
            Log.d("ACME", "new(" + position + ", " + pos + ") " + info.key);
        }
//        ViewGroup view = null;
        ViewGroup viewgroup = null;
//        if (item.mView != null) {
//            view = (ViewGroup) item.mView;
//        }
        if (viewgroup == null) {
            viewgroup = (ViewGroup) mInflater.inflate(R.layout.draw_text_on_page_template, null);
            TextView title = (TextView) viewgroup.findViewById(R.id.player_pager_title);
            TextView content = (TextView) viewgroup.findViewById(R.id.player_pager_content);
            TextView cp = (TextView) viewgroup.findViewById(R.id.player_pager_cp);
            View clickArea = viewgroup.findViewById(R.id.player_pager_click_area);
            clickArea.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = info.buildIntent();
                    if (intent != null) {
                        try {
                            mContext.startActivity(intent);
                            recordEvent(RecordType.EVENT_CLICK, info);
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
                ImageView image = (ImageView) viewgroup.findViewById(R.id.player_pager_wallpaper);
                if (position == 0 && firstShown) {
//            File first_pic = ThemeResources.getSystem().getLockscreenWallpaper();
//            Picasso.with(mContext).load(first_pic).into(image);
                    firstShown = false;
//                    image.setImageDrawable(ThemeResources.getLockWallpaperCache(mContext));
                    Uri uri = Uri.parse(info.wallpaperUri);
                    Picasso.with(mContext).load(uri).into(image);
                } else {
                    if (info != null && info.wallpaperUri != null) {
                        Uri uri = Uri.parse(info.wallpaperUri);
                        Picasso.with(mContext).load(uri).into(image);
                    } else {
//                        image.setImageDrawable(ThemeResources.getLockWallpaperCache(mContext));
                    }
//            loadBitmap(pos, image);
                }
                title.setText(info.title);
                content.setText(info.content);
                if (info.cp != null) {
                    cp.setText("©" + info.cp);
                }
                title.setTextColor(MiFGUtils.parseColor(info.titleColor, -1)); // white + alpha 100%
                content.setTextColor(MiFGUtils.parseColor(info.contentColor, -654311425)); // white + alpha 90%
                cp.setTextColor(MiFGUtils.parseColor(info.contentColor, 1509949439)); // white + alpha 35%
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
        if (DEBUG) {
            Log.d("ACME", "des(" + position + ") " + ((WallpaperInfo) view.getTag()).key);
        }
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        View clickArea = view.findViewById(R.id.player_pager_click_area);
        View title = view.findViewById(R.id.player_pager_title);
        View content = view.findViewById(R.id.player_pager_content);
        View cpArea = view.findViewById(R.id.player_pager_cp_area);
        View cp = view.findViewById(R.id.player_pager_cp);
        if (position < -1) { // [-Infinity,-1)
            title.setTranslationX(0);
            content.setTranslationX(0);
            cp.setTranslationX(0);
            title.setAlpha(0);
            content.setAlpha(0);
            cp.setAlpha(0);

        } else if (position <= 1) { // [-1,1]
            title.setTranslationX(pageWidth * position * 0.2f);
            content.setTranslationX(pageWidth * position * 0.1f);
            cp.setTranslationX(pageWidth * position * 0.1f);
            if (mMainView.isMenuShowing()) {
                clickArea.setAlpha(1);
                cpArea.setAlpha(0);
            } else {
                clickArea.setAlpha(0);
                cpArea.setAlpha(1);
            }
            title.setAlpha(getTitleFactor(1 - Math.abs(position)));
            content.setAlpha(getContentFactor(1 - Math.abs(position)));
            cp.setAlpha(getContentFactor(1 - Math.abs(position)));

        } else { // (1,+Infinity]
            title.setTranslationX(0);
            content.setTranslationX(0);
            cp.setTranslationX(0);
            title.setAlpha(0);
            content.setAlpha(0);
            cp.setAlpha(0);
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
