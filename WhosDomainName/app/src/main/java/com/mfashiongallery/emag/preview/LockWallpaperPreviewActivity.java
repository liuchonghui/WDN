
package com.mfashiongallery.emag.preview;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.android.overlay.ApplicationUncaughtHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mfashiongallery.emag.preview.model.PicEnum;
import com.mfashiongallery.emag.preview.model.WallpaperInfo;

import java.util.ArrayList;
import java.util.List;

import tool.whosdomainname.activity.BaseFragmentActivity;
import tool.whosdomainname.android.R;

public class LockWallpaperPreviewActivity extends BaseFragmentActivity {
    private final static String LOG_TAG = "LockWallpaperPreview";
    public final static boolean DEBUG = Log.isLoggable(LOG_TAG, Log.DEBUG);
    private LockWallpaperPreviewView mMainView;
    List<WallpaperInfo> mWallpaperInfos = new ArrayList<WallpaperInfo>();

    private Gson mGson;
    long mShowTime;
    boolean mShowingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setCustomizedTheme(miui.R.style.Theme_Dark_NoTitle);
//        setTheme(R.style.Theme_LockWallpaperPreviewTheme);
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ApplicationUncaughtHandler(this));
        setContentView(R.layout.preview_wallpaper_activity);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mGson = new Gson();
        Intent intent = getIntent();
        mShowTime = intent.getLongExtra("showTime", 0);
        String currentWallpaperInfo = intent.getStringExtra("currentWallpaperInfo");
        String wallpaperInfos = intent.getStringExtra("wallpaperInfos");
        String adWallpaperInfos = intent.getStringExtra("adWallpaperInfos");
        final String dialogComponent = intent.getStringExtra("dialogComponent");
        if (DEBUG) {
            Log.d(LOG_TAG, mShowTime + " time");
            Log.d(LOG_TAG, "current: " + currentWallpaperInfo);
            Log.d(LOG_TAG, "wallpapers: " + wallpaperInfos);
            Log.d(LOG_TAG, "ads: " + adWallpaperInfos);
            Log.d(LOG_TAG, "dialogComponent: " + dialogComponent);
        }
        WallpaperInfo current = mGson.fromJson(currentWallpaperInfo, WallpaperInfo.class);
        List<WallpaperInfo> wallpapers = mGson.fromJson(wallpaperInfos, new TypeToken<List<WallpaperInfo>>() {
        }.getType());
        List<WallpaperInfo> ads = mGson.fromJson(adWallpaperInfos, new TypeToken<List<WallpaperInfo>>() {
        }.getType());
        mWallpaperInfos.clear();
        if (current == null) {
            // 说明当前壁纸使用的是用户自设的壁纸
            current = new WallpaperInfo();
            mWallpaperInfos.add(current); // 添加用户自设壁纸到首位

        } else {
            // 说明当前壁纸使用的是由我们app设定的壁纸
            boolean foundCurrent = false;
            WallpaperInfo first = null;
            if (wallpapers != null && wallpapers.size() > 0) {
                first = wallpapers.get(0);
            }
            if (first != null && first.key != null
                    && first.key.equalsIgnoreCase(current.key)) {
                foundCurrent = true;
            }
            if (!foundCurrent) {
                mWallpaperInfos.add(current); // 添加用户自设壁纸到首位
            }
        }
        if (wallpapers != null) {
            mWallpaperInfos.addAll(wallpapers);
        }
        if (ads != null) {
            for (int i = 0; i < ads.size(); ++i) {
                WallpaperInfo info = ads.get(i);
                int position = info.pos;
                if (position <= 0 || position > mWallpaperInfos.size()) {
                    position = mWallpaperInfos.size();
                }
                mWallpaperInfos.add(position, info);
            }
        }

        List<WallpaperInfo> tests = new ArrayList<WallpaperInfo>();
        WallpaperInfo info = null;
        for (int i = 0; i < PicEnum.values().length; i++) {
            info = new WallpaperInfo();
            info.key = String.valueOf(i);
            info.supportLike = true;
            info.title = String.valueOf(i) + "." + PicEnum.getTitle(i);
            info.content = String.valueOf(i) + "." + PicEnum.getContent(i);
            info.wallpaperUri = PicEnum.get(i);
            tests.add(info);
        }
        mWallpaperInfos.clear();
        mWallpaperInfos.addAll(tests);

        mMainView = (LockWallpaperPreviewView) findViewById(R.id.view_pager);
        LockWallpaperPreviewAdapter adapter = new LockWallpaperPreviewOneLeftAdapter(getApplicationContext(), mWallpaperInfos);
        mMainView.setAdapter(adapter);
        mMainView.postDelayed(new Runnable() {
            @Override
            public void run() {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                if (!TextUtils.isEmpty(dialogComponent)) {
                    ComponentName component = ComponentName.unflattenFromString(dialogComponent);
                    if (component != null) {
                        Intent intent = new Intent();
                        intent.setComponent(component);
//                        intent.putExtra(MiFGConstants.EXTRA_START_ACTIVITY_WHEN_LOCKED, true);
                        try {
                            startActivity(intent);
                            mShowingDialog = true;
                        } catch (Exception ex) {
                            Log.e(LOG_TAG, "start activity failed.", ex);
                        }
                    }
                }
                mMainView.showHint();
            }
        }, mShowTime - System.currentTimeMillis());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mShowingDialog) {
            mShowingDialog = false;
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        }
    }

    int resumeCount = 0;
    boolean workingStateWhenFirstResume = false;

    @Override
    protected void onResume() {
        super.onResume();
        mMainView.postDelayed(new Runnable() {
            @Override
            public void run() {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            }
        }, mShowTime - System.currentTimeMillis());

//        if (0 == resumeCount) {
//            workingStateWhenFirstResume =
//                    PushLockscreenManager.getInstantce().isLockscreenMagazineWorking();
//        } else if (1 == resumeCount) {
//            boolean currentWorkingState =
//                    PushLockscreenManager.getInstantce().isLockscreenMagazineWorking();
//            if (!workingStateWhenFirstResume && currentWorkingState) {
//                onDeclarConfirmed();
//            }
//        }
        resumeCount++;
    }

    @Override
    protected void onDestroy() {
//        ThemeResources.clearLockWallpaperCache();
        super.onDestroy();
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

    protected void onDeclarConfirmed() {
        if (mMainView != null) {
            mMainView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMainView.declarConfirmFakeScroll();
                }
            }, 50L);
        }
    }
}
