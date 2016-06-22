package com.mfashiongallery.emag.preview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.Toast;

import com.android.overlay.RunningEnvironment;
import com.google.gson.Gson;
import com.mfashiongallery.emag.preview.model.RecordType;
import com.mfashiongallery.emag.preview.model.SineEaseInInterpolator;
import com.mfashiongallery.emag.preview.model.SineEaseInOutInterpolator;
import com.mfashiongallery.emag.preview.model.WallpaperInfo;
import com.mfashiongallery.emag.utils.MiFGUtils;

import java.io.File;
import java.io.IOException;

import miui.graphics.BitmapFactory;
import tool.whosdomainname.android.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ActionMenus extends LinearLayout implements OnClickListener {
//    private static final String SNAPSHOT_DIR = Environment.getExternalStorageDirectory().getPath()
//            + "/DCIM/Screenshots/";
    private static final String SNAPSHOT_NAME = "lock_wallpaper.jpg";

    /**
     * Action to refresh lockscreen wallpaper
     */
    public static final String ACTION_REQUEST_LOCKSCREEN_WALLPAPER = "android.miui.REQUEST_LOCKSCREEN_WALLPAPER";


    private final static int SETTING = 0;
    private final static int LIKE = 1;
    private final static int DISLIKE = 2;
    private final static int SHARE = 3;
    private final static int APPLY = 4;
    private final static int ACTION_COUNT = 5;
    private final static int MOMENT = 0;
    private final static int WECHAT = 1;
    private final static int WEIBO = 2;
    private final static int QZONE = 3;
    private final static int QQ = 4;
    private final static int TAG_MOMENT = MOMENT + ACTION_COUNT;
    private final static int TAG_WECHAT = WECHAT + ACTION_COUNT;
    private final static int TAG_WEIBO = WEIBO + ACTION_COUNT;
    private final static int TAG_QZONE = QZONE + ACTION_COUNT;
    private final static int TAG_QQ = QQ + ACTION_COUNT;

    private int colorEnable;
    private int colorDisable;
    private View[] mActions = new View[ACTION_COUNT];
    private View[] mShareActions = new View[ACTION_COUNT];
    private boolean[] mShareAvailds = new boolean[ACTION_COUNT];
    private Animator[] mItemAnimIn = new Animator[ACTION_COUNT + 1];
    private Animator[] mItemAnimOut = new Animator[ACTION_COUNT + 1];
    private AnimatorSet mItemAnimSetIn = new AnimatorSet();
    private AnimatorSet mItemAnimSetOut = new AnimatorSet();

    private AnimatorSet mShareAnimSetIn = new AnimatorSet();
    private AnimatorSet mShareAnimSetOut = new AnimatorSet();
    private Animator[] mShareAnimIn = new Animator[ACTION_COUNT];
    private Animator[] mShareAnimOut = new Animator[ACTION_COUNT];

    private View mLine;

    private boolean mRegistered;
    private boolean mCanFinish;
    private boolean mNeedShowLoading;
    private LockWallpaperPreviewView mMainView;
    private Gson mGson;
    private Toast mToast;
    private String currentIdentify;
    private Context mContext;
    private int dp05;
    private int dp26;

    public ActionMenus(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        mContext = context;
        colorEnable = getResources().getColor(R.color.menu_item_text_color_enable);
        colorDisable = getResources().getColor(R.color.menu_item_text_color_disable);
        final float scale = getResources().getDisplayMetrics().density;
        int dp1 = (int) (scale + 0.5f);
        dp05 = (int) (dp1 * 0.5);
        dp26 = 26 * dp1;
    }

    boolean finishActivity = true;

    WallpaperBroadcastReceiver mWallpaperChangeReceiver = new WallpaperBroadcastReceiver();

    class WallpaperBroadcastReceiver extends BroadcastReceiver {
        WallpaperInfo info;

        public void setWallpaperInfoToRecord(WallpaperInfo info) {
            this.info = info;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            final boolean succeed = intent.getBooleanExtra("set_lock_wallpaper_result", true);
            Log.d("LiJianbo", "onR " + succeed);
            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    if (succeed) {
//                        ThemeResources.getLockWallpaperCache(getContext().getApplicationContext());
                    }
                    return new Boolean(succeed);
                }

                @Override
                protected void onPostExecute(final Boolean result) {
                    long delayDuration = 600L;
                    mMainView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (info != null && result != null && result.booleanValue()) {
                                recordWallpaperInfoApplied(info);
                            }

                            if (finishActivity) {
                                ((Activity) getContext()).finish();
                            } else {
                                mNeedShowLoading = false;
                                mMainView.getLoadingView().setVisibility(View.INVISIBLE);
                                show(true);

                                if (currentIdentify == null) { // info.key lost
                                    return;
                                }
                                if (mToast != null) {
                                    mToast.cancel();
                                }
                                mToast = Toast.makeText(getContext(), R.string.lockscreen_wallpaper_changed,
                                        Toast.LENGTH_SHORT);
                                mToast.show();
                                currentIdentify = null;
                            }
                        }
                    }, delayDuration);

                };
            }.execute();
        }
    };

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addTopLine();

        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        addView(relativeLayout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout container1 = new LinearLayout(getContext());
//        addView(container, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));

        mGson = new Gson();
        mActions[SETTING] = createImage(R.drawable.settings, SETTING, R.string.settings);
        mActions[LIKE] = createImage(R.drawable.like_enable, LIKE, R.string.like);
        mActions[DISLIKE] = createImage(R.drawable.dislike_selector, DISLIKE, R.string.dislike);
        mActions[SHARE] = createImage(R.drawable.share, SHARE, R.string.share);
        mActions[APPLY] = createImage(R.drawable.apply, APPLY, R.string.apply);

        LayoutParams lp = new LayoutParams(0, 0, 100);
        container1.addView(new Space(mContext), lp);
        for (int i = 0; i < ACTION_COUNT; ++i) {
            container1.addView(mActions[i], new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            container1.addView(new Space(mContext), lp);
        }
//        addView(new Space(mContext), lp);

        LinearLayout container2 = new LinearLayout(getContext());

        mShareActions[MOMENT] = createImage(R.drawable.moment_selector, TAG_MOMENT, -1);
        mShareActions[WECHAT] = createImage(R.drawable.wechat_selector, TAG_WECHAT, -1);
        mShareActions[WEIBO] = createImage(R.drawable.weibo_selector, TAG_WEIBO, -1);
        mShareActions[QZONE] = createImage(R.drawable.qzone_selector, TAG_QZONE, -1);
        mShareActions[QQ] = createImage(R.drawable.qq_selector, TAG_QQ, -1);

        for (int i = 0; i < ACTION_COUNT; ++i) {
            mShareActions[i].setAlpha(0);
        }

        LayoutParams lp2 = new LayoutParams(0, 0, 100);
        container2.addView(new Space(getContext()), lp2);
        for (int i = 0; i < ACTION_COUNT; ++i) {
            container2.addView(mShareActions[i], new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            container2.addView(new Space(getContext()), lp2);
        }

        relativeLayout.addView(container2, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        relativeLayout.addView(container1, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        initAnim();
        mItemAnimSetIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                for (int i = 0; i < ACTION_COUNT; ++i) {
                    mActions[i].setAlpha(0);
                }
                mLine.setAlpha(0);
                setVisibility(View.VISIBLE);
            }
        });
        mItemAnimSetOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(View.INVISIBLE);
                if (mCanFinish) {
                    ((Activity) getContext()).finish();
                } else if (mNeedShowLoading) {
                    mMainView.getLoadingView().setVisibility(View.VISIBLE);
                }
            }
        });

//        initShareAnim();
        mShareAnimSetIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mActions[SHARE].setTag(TAG_QZONE);
                mActions[SHARE].setVisibility(View.INVISIBLE);
                mShareActions[QZONE].setVisibility(View.VISIBLE);
                mMainView.setTouchSlopEnable(false);
            }
        });
        mShareAnimSetOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mActions[SHARE].setTag(SHARE);
                mActions[SHARE].setVisibility(View.VISIBLE);
                mShareActions[QZONE].setVisibility(View.INVISIBLE);
                mMainView.setTouchSlopEnable(true);
            }
        });
    }

    private void initAnim() {
        for (int i = 0; i < ACTION_COUNT; ++i) {
            mItemAnimIn[i] = getItemAnimIn(mActions[i]);
//            mItemAnimIn[i].setStartDelay(i * 50);
            mItemAnimIn[i].setStartDelay(50);
            mItemAnimOut[i] = getItemAnimOut(mActions[i]);
//            mItemAnimOut[i].setStartDelay(i * 50);
            mItemAnimOut[i].setStartDelay(50);
        }

        mItemAnimIn[ACTION_COUNT] = getLineAnimIn();
        mItemAnimIn[ACTION_COUNT].setStartDelay(50);
        mItemAnimOut[ACTION_COUNT] = getLineAnimOut();
        mItemAnimOut[ACTION_COUNT].setStartDelay(50);

        mItemAnimSetIn.playTogether(mItemAnimIn);
        mItemAnimSetOut.playTogether(mItemAnimOut);
    }

    public void setShareAvailds(boolean... shareAvailds) {
        if (shareAvailds != null && shareAvailds.length == ACTION_COUNT) {
            for (int i = 0; i < ACTION_COUNT; i++) {
                mShareAvailds[i] = shareAvailds[i];
            }
        }
    }

    boolean shareAnimInited = false;

    private void initShareAnim() {
        for (int i = 0; i < ACTION_COUNT; i++) {
            mShareAnimIn[i] = getAnimIn(i);
            mShareAnimOut[i] = getAnimOut(i);
//            mShareActions[i].setEnabled(mShareAvailds[i]);
            mShareActions[i].findViewById(R.id.menu_item_image).setEnabled(mShareAvailds[i]);
        }

        mShareAnimSetIn.playTogether(mShareAnimIn);
        mShareAnimSetOut.playTogether(mShareAnimOut);
    }

    public void setMainView(LockWallpaperPreviewView view) {
        mMainView = view;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void addTopLine() {
        mLine = new View(getContext());
        mLine.setBackground(new ColorDrawable(getResources().getColor(R.color.menu_item_top_line_color)));
        LayoutParams lineLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lineLp.height = dp05;
        lineLp.leftMargin = dp26;
        lineLp.rightMargin = dp26;
        addView(mLine, lineLp);
    }

    private View createImage(int imageId, int tag, int textId) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.menu_item2, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.menu_item_image);
        imageView.setImageResource(imageId);
//        TextView textView = (TextView) view.findViewById(R.id.menu_item_text);
//        textView.setText(textId);
        view.setTag(tag);
        view.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        requestDisallowInterceptTouchEvent(true);
        Integer mode = (Integer) v.getTag();
        switch (mode) {
            case LIKE:
                onLike(v);
                break;
            case SETTING:
                onSetting();
                break;
            case DISLIKE:
                onDislike();
                break;
            case SHARE:
                onShare(v);
                break;
            case APPLY:
                onApply();
                break;
            case TAG_MOMENT:
                if (mShareAvailds[MOMENT]) {
                    onShare(Platform.WECHAT_MOMENT);
                } else {
                    Toast.makeText(getContext(), "请安装微信", Toast.LENGTH_SHORT).show();
                }
                break;
            case TAG_WECHAT:
                if (mShareAvailds[WECHAT]) {
                    onShare(Platform.WECHAT);
                } else {
                    Toast.makeText(getContext(), "请安装微信", Toast.LENGTH_SHORT).show();
                }
                break;
            case TAG_WEIBO:
                if (mShareAvailds[WEIBO]) {
                    onShare(Platform.WEIBO);
                } else {
                    Toast.makeText(getContext(), "请安装微博", Toast.LENGTH_SHORT).show();
                }
                break;
            case TAG_QZONE:
//                if (!mShareActions[QZONE].isEnabled()) {
//                    onShare(v);
//                    return;
//                }
                if (mShareAvailds[QZONE]) {
                    onShare(Platform.QZONE);
                } else {
                    Toast.makeText(getContext(), "请安装QZone", Toast.LENGTH_SHORT).show();
                }
                break;
            case TAG_QQ:
                if (mShareAvailds[QQ]) {
                    onShare(Platform.QQ);
                } else {
                    Toast.makeText(getContext(), "请安装QQ", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void onDislike() {
        // 'Delete' button clicked
//        show(false);
        mMainView.startDislikeAnim();

        int currentItem = mMainView.getCurrentItem();
        WallpaperInfo info = mMainView.getAdapter().getWallpaperInfo(currentItem);

        if (TextUtils.isEmpty(info.key)) {
            mCanFinish = true;
        } else {
//            mMainView.getAdapter().recordEvent(currentItem, RecordType.EVENT_DISLIKE);
            mMainView.getAdapter().recordEvent(RecordType.EVENT_DISLIKE, info);
        }
    }

    private void onApply() {
        show(false);
        mMainView.startExitAnim();
        int currentItem = mMainView.getCurrentItem();
        WallpaperInfo info = mMainView.getAdapter().getWallpaperInfo(currentItem);
        if (TextUtils.isEmpty(info.key)) {
            mCanFinish = true;
            currentIdentify = null;
        } else {
            try {
//                Intent intent = new Intent(ACTION_REQUEST_LOCKSCREEN_WALLPAPER);
//                intent.putExtra("wallpaperInfo", mGson.toJson(info));
////                if (currentItem == 0) {
//                if (mMainView.getAdapter().isFirst(currentItem)) {
//                    mCanFinish = true;
//                } else {
//                    intent.putExtra("apply", true);
//                    mNeedShowLoading = true;
//                    finishActivity = true;
//                    mWallpaperChangeReceiver.setWallpaperInfoToRecord(info);
//                    if (!mRegistered) {
//                        IntentFilter wallpaperChangeIntentFilter = new IntentFilter();
//                        wallpaperChangeIntentFilter.addAction(SystemIntent.ACTION_SET_KEYGUARD_WALLPAPER);
//                        getContext().registerReceiver(mWallpaperChangeReceiver, wallpaperChangeIntentFilter);
//                        mRegistered = true;
//                    }
//                }
//                currentIdentify = info.key;
//                Log.d("LiJianbo", "apply " + currentIdentify);
//                getContext().sendBroadcast(intent);
            } catch (Exception e) {
                mCanFinish = true;
            }
        }
    }

    public void applyWallpaper(WallpaperInfo info) {
        if (info == null || info.key == null || info.key.length() == 0) {
            return;
        }
        show(false);
//        Intent intent = new Intent(ACTION_REQUEST_LOCKSCREEN_WALLPAPER);
//        intent.putExtra("wallpaperInfo", mGson.toJson(info));
//        intent.putExtra("apply", true);
//        mCanFinish = false;
//        mNeedShowLoading = true;
//        finishActivity = false;
//        mWallpaperChangeReceiver.setWallpaperInfoToRecord(info);
//        if (!mRegistered) {
//            IntentFilter wallpaperChangeIntentFilter = new IntentFilter();
//            wallpaperChangeIntentFilter.addAction(SystemIntent.ACTION_SET_KEYGUARD_WALLPAPER);
//            getContext().registerReceiver(mWallpaperChangeReceiver, wallpaperChangeIntentFilter);
//            mRegistered = true;
//        }
//        currentIdentify = info.key;
//        Log.d("LiJianbo", "apply " + currentIdentify);
//        getContext().sendBroadcast(intent);
    }

    private void recordWallpaperInfoApplied(WallpaperInfo info) {
        try {
            mMainView.getAdapter().recordEvent(RecordType.EVENT_APPLY, info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    enum Platform {
        WECHAT_MOMENT,
        WECHAT,
        WEIBO,
        QZONE,
        QQ,
    }

    private void onShare(Platform platform) {
        if (platform == null) {
            return;
        }
        int curItem = mMainView.getCurrentItem();
        WallpaperInfo info = mMainView.getAdapter().getWallpaperInfo(curItem);
        try {
//            View view = mMainView.getAdapter().getView(curItem);
            View view = mMainView.getViewPager().getCurrentView();
            if (view == null) {
                view = mMainView.getAdapter().getView(curItem);
            }
            if (view != null) {
                view.findViewById(R.id.player_pager_topline).setVisibility(View.VISIBLE);
                view.findViewById(R.id.player_pager_from).setVisibility(View.VISIBLE);

                Bitmap bitmap = convertViewToBitmap(view, view.getWidth(), view.getHeight());
                SavePicTask task = new SavePicTask();
                task.setPlatform(platform);
                task.setWallpaperInfo(info);
                task.setBitmap(bitmap);
                task.execute();
                show(false);
                mMainView.startExitAnim();
            }
        } catch (Exception e) {
        }
        try {
//            mMainView.getAdapter().recordEvent(curItem, RecordType.EVENT_SHARE);
            mMainView.getAdapter().recordEvent(RecordType.EVENT_SHARE, info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onShare(View view) {
        if (!shareAnimInited) {
            initShareAnim();
            shareAnimInited = true;
        }
        toggleShare();
    }

    public static Bitmap convertViewToBitmap(View view, int bitmapWidth, int bitmapHeight) {
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        return bitmap;
    }

    private class SavePicTask extends AsyncTask<Void, Void, Boolean> {
        private Bitmap mBitmap = null;
        private Platform platform = null;
        private WallpaperInfo info = null;

        public void setPlatform(Platform platform) {
            this.platform = platform;
        }

        public void setWallpaperInfo(WallpaperInfo info) {
            this.info = info;
        }

        public void setBitmap(Bitmap b) {
            mBitmap = b;
        }

        public boolean saveBitmapToPNG(Bitmap b, String dir, String name) {
            String path = new File(dir, name).getAbsolutePath();
            File destDir = new File(dir);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            if (null == b) {
                return false;
            }
            try {
                return BitmapFactory.saveToFile(b, path);
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            mNeedShowLoading = true;
            File root = new File(MiFGUtils.getSharePictureCachePath());
            if(!root.exists()) {
                root.mkdirs();
//                FileUtils.chmod(MiFGUtils.getSharePictureCachePath(), 0755);
            }
            return saveBitmapToPNG(mBitmap, MiFGUtils.getSharePictureCachePath(), SNAPSHOT_NAME);
        }

        protected void onPostExecute(final Boolean result) {
            if (((Activity) getContext()).isFinishing()) {
                return;
            }
            if (result) {
                try {
                    Uri imageUri = Uri.fromFile(new File(MiFGUtils.getSharePictureCachePath(), SNAPSHOT_NAME));
//                    final Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                    shareIntent.setType("image/*");
//                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
//                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    mContext.startActivity(Intent.createChooser(shareIntent,
//                            mContext.getString(R.string.share)));
                    PreviewExtra extra = new PreviewExtra();
                    extra.setSharePlatform(platform.name());
                    extra.setShareTitle(info.title);
                    extra.setShareContent(info.content);
                    extra.setShareUri(imageUri.getEncodedPath());
                    Intent intent = ShareActivity.createIntent(getContext(), extra);
                    ((Activity) getContext()).startActivity(intent);
//                    if (Platform.WEIBO == platform) {
//                        onShareWeibo(info, imageUri);
//                    } else if (Platform.WECHAT == platform) {
//                        onShareWeChat(info, imageUri);
//                    } else if (Platform.WECHAT_MOMENT == platform) {
//                        onShareWeChatMoment(info, imageUri);
//                    } else if (Platform.QQ == platform) {
//                        onShareQQ(info, imageUri);
//                    } else if (Platform.QZONE == platform) {
//                        onShareQzone(info, imageUri);
//                    } else {
//                        throw new IllegalStateException("unknown platform type!");
//                    }
                    getContext().sendBroadcast(new Intent("xiaomi.intent.action.SHOW_SECURE_KEYGUARD"));
                    ((Activity) getContext()).finish();
                } catch (Exception e) {
                }
            }
            mNeedShowLoading = false;
        }
    }

    private void onLike(View v) {
        try {
            int currentItem = mMainView.getCurrentItem();
            WallpaperInfo info = mMainView.getAdapter().getWallpaperInfo(mMainView.getCurrentItem());
            boolean isLiked = info.like;
            isLiked = !isLiked;
            final ImageView image = (ImageView) v.findViewById(R.id.menu_item_image);
            image.setImageResource(isLiked ? R.drawable.liked : R.drawable.like_enable);
//            final TextView text = (TextView) v.findViewById(R.id.menu_item_text);
//            text.setText(isLiked ? R.string.liked : R.string.like);
            info.like = isLiked;
//            mMainView.getAdapter().recordEvent(currentItem,
//                    isLiked ? RecordType.EVENT_LIKE : RecordType.EVENT_CANCEL_LIKE);
            mMainView.getAdapter().recordEvent(isLiked ? RecordType.EVENT_LIKE : RecordType.EVENT_CANCEL_LIKE, info);
            AnimationSet animationSet = new AnimationSet(false);
            Animation animation1 = new ScaleAnimation(1f, 1.1f, 1f, 1.1f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation1.setInterpolator(new SineEaseInInterpolator());
            animation1.setDuration(100);
            Animation animation2 = new ScaleAnimation(1.1f, 0.9f, 1.1f, 0.9f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation2.setInterpolator(new SineEaseInOutInterpolator());
            animation2.setDuration(200);
            animation2.setStartOffset(100);
            Animation animation3 = new ScaleAnimation(0.9f, 1f, 0.9f, 1f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation3.setInterpolator(new SineEaseInOutInterpolator());
            animation3.setDuration(200);
            animation3.setStartOffset(300);
            animationSet.addAnimation(animation1);
            animationSet.addAnimation(animation2);
            animationSet.addAnimation(animation3);
            image.startAnimation(animationSet);
            if (mMainView.getAdapter().isFirst(currentItem)) {
                Intent intent = new Intent(ACTION_REQUEST_LOCKSCREEN_WALLPAPER);
                intent.putExtra("wallpaperInfo", mGson.toJson(info));
                getContext().sendBroadcast(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onSetting() {
//        getContext().sendBroadcast(new Intent("xiaomi.intent.action.SHOW_SECURE_KEYGUARD"));
//        ComponentName cn = new ComponentName(getContext(), SSettingActivity.class);
//        Intent intent = new Intent();
//        intent.setComponent(cn);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        try {
//            getContext().startActivity(intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        ((Activity) getContext()).finish();
    }

    public void updateView() {
        updateLikeView();
//        setDislikeViewEnable(mMainView.getAdapterVisibleChildCount() > 1);
        updateDislikeView();
    }

    private void updateLikeView() {
        final ImageView image = (ImageView) mActions[LIKE].findViewById(R.id.menu_item_image);
        WallpaperInfo info = mMainView.getAdapter().getWallpaperInfo(mMainView.getCurrentItem());
        boolean isLiked = info.like;
//        image.setImageResource(isLiked ? R.drawable.liked : R.drawable.like);
        image.setImageResource(isLiked ? R.drawable.liked : R.drawable.like_selector);
//        final TextView text = (TextView) mActions[LIKE].findViewById(R.id.menu_item_text);
//        text.setText(isLiked ? R.string.liked : R.string.like);
//        mLikeView.setTransitionAlpha(info.supportLike ? 1f : 0.5f);//todo 需要自己实现
//        mLikeView.setAlpha(info.supportLike ? 1f : 0.5f);
//        image.setEnabled(info.supportLike);
//        text.setTextColor(info.supportLike ? colorEnable : colorDisable);
        mActions[LIKE].setEnabled(info.supportLike);
    }

    /**
     * Use updateDislikeView for instead.
     * @param enable
     */
    @Deprecated
    private void setDislikeViewEnable(boolean enable) {
//        final ImageView image = (ImageView) mActions[DISLIKE].findViewById(R.id.menu_item_image);
//        final TextView text = (TextView) mActions[DISLIKE].findViewById(R.id.menu_item_text);
//        image.setEnabled(enable);
//        text.setTextColor(enable ? colorEnable : colorDisable);
        mActions[DISLIKE].setEnabled(enable);
    }

    private void updateDislikeView() {
        final ImageView image = (ImageView) mActions[DISLIKE].findViewById(R.id.menu_item_image);
//        final TextView text = (TextView) mActions[DISLIKE].findViewById(R.id.menu_item_text);
        boolean enable = mMainView.getAdapter().canDislike(mMainView.getCurrentItem());
        image.setEnabled(enable);
//        text.setTextColor(enable ? colorEnable : colorDisable);
        mActions[DISLIKE].setEnabled(enable);
    }

    private Animator getItemAnimOut(View v) {
        ObjectAnimator alphaAnimOut = ObjectAnimator.ofFloat(v, "alpha", 1f, 0);
        ObjectAnimator scaleXAnimOut = ObjectAnimator.ofFloat(v, "scaleX", 1f, 0.9f);
        ObjectAnimator scaleYAnimOut = ObjectAnimator.ofFloat(v, "scaleY", 1f, 0.9f);

        AnimatorSet animOut = new AnimatorSet();
        animOut.play(alphaAnimOut).with(scaleXAnimOut).with(scaleYAnimOut);
        animOut.setDuration(250);
        return animOut;
    }

    private Animator getItemAnimIn(View v) {
        ObjectAnimator alphaAnimIn = ObjectAnimator.ofFloat(v, "alpha", 0, 1f);
        ObjectAnimator scaleXAnimIn = ObjectAnimator.ofFloat(v, "scaleX", 0.9f, 1f);
        ObjectAnimator scaleYAnimIn = ObjectAnimator.ofFloat(v, "scaleY", 0.9f, 1f);

        AnimatorSet animIn = new AnimatorSet();
        animIn.play(alphaAnimIn).with(scaleXAnimIn).with(scaleYAnimIn);
        animIn.setDuration(250);
        return animIn;
    }

    private Animator getLineAnimOut() {
        ObjectAnimator alphaAnimOut = ObjectAnimator.ofFloat(mLine, "alpha", 1f, 0);
        AnimatorSet animOut = new AnimatorSet();
        animOut.play(alphaAnimOut);
        animOut.setDuration(250);
        return animOut;
    }

    private Animator getLineAnimIn() {
        ObjectAnimator alphaAnimIn = ObjectAnimator.ofFloat(mLine, "alpha", 0, 1f);
        AnimatorSet animIn = new AnimatorSet();
        animIn.play(alphaAnimIn);
        animIn.setDuration(250);
        return animIn;
    }

    private boolean mShow = false;

    public boolean isShowing() {
        return mShow;
    }

    public void show(boolean show) {
        if (mShow != show) {
            mShow = show;
            mItemAnimSetIn.end();
            mItemAnimSetOut.end();
            if (show) {
                mItemAnimSetIn.start();
            } else {
                mItemAnimSetOut.start();
            }
        }
    }

    private boolean mShowShare = false;

    public void showShare(boolean show) {
        if (mShowShare != show) {
            mShowShare = show;
            mShareAnimSetIn.end();
            mShareAnimSetOut.end();
            if (show) {
                mShareAnimSetIn.start();
            } else {
                mShareAnimSetOut.start();
            }
        }
    }

    public void toggleShare() {
        showShare(!mShowShare);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mRegistered) {
            getContext().unregisterReceiver(mWallpaperChangeReceiver);
            mRegistered = false;
        }
    }

    public void toggle() {
        if (mShowShare) {
            toggleShare();
            return;
        }
        show(!mShow);
        if (mShow) {
            mMainView.showMask();
            mMainView.showTextArea();
            mMainView.hideTextCpArea();
        } else {
            mMainView.hideMask();
            mMainView.hideTextArea();
            mMainView.showTextCpArea();
        }
    }

    private Animator getAnimIn(int i) {
        float shareTranslate = 0;
        float editTranslate = 0;
        if (i < SHARE) {
            int[] location = new int[2];
            mActions[SHARE].getLocationOnScreen(location);
            int zero = location[0];
            mActions[i].getLocationOnScreen(location);
            int pos = location[0];
            shareTranslate = Math.abs(pos - zero);
            editTranslate = -shareTranslate * (i + 1);
        } else if (i > SHARE) {
            int[] location = new int[2];
            mActions[SHARE].getLocationOnScreen(location);
            int zero = location[0];
            mActions[i].getLocationOnScreen(location);
            int pos = location[0];
            editTranslate = Math.abs(pos - zero);
            shareTranslate = -editTranslate;

        } else {
            shareTranslate = 0;
            editTranslate = 0;
        }
        ObjectAnimator translateAnimIn1 = ObjectAnimator.ofFloat(mShareActions[i], "translationX", shareTranslate, 0);
        ObjectAnimator translateAnimIn2 = ObjectAnimator.ofFloat(mActions[i], "translationX", 0, editTranslate);
        ObjectAnimator alphaAnimIn1 = ObjectAnimator.ofFloat(mShareActions[i], "alpha", 0f, 1f);
        ObjectAnimator alphaAnimIn2 = ObjectAnimator.ofFloat(mActions[i], "alpha", 1f, 0f);
        AnimatorSet animIn = new AnimatorSet();
        animIn.play(translateAnimIn1)
                .with(alphaAnimIn1)
                .with(alphaAnimIn2)
                .with(translateAnimIn2);
        animIn.setDuration(300L);
        return animIn;
    }

    private Animator getAnimOut(int i) {
        float shareTranslate = 0;
        float editTranslate = 0;
        if (i < SHARE) {
            int[] location = new int[2];
            mActions[SHARE].getLocationOnScreen(location);
            int zero = location[0];
            mActions[i].getLocationOnScreen(location);
            int pos = location[0];
            shareTranslate = Math.abs(pos - zero);
            editTranslate = -shareTranslate * (i + 1);
        } else if (i > SHARE) {
            int[] location = new int[2];
            mActions[SHARE].getLocationOnScreen(location);
            int zero = location[0];
            mActions[i].getLocationOnScreen(location);
            int pos = location[0];
            editTranslate = Math.abs(pos - zero);
            shareTranslate = -editTranslate;

        } else {
            shareTranslate = 0;
            editTranslate = 0;
        }
        ObjectAnimator translateAnimOut1 = ObjectAnimator.ofFloat(mShareActions[i], "translationX", 0, shareTranslate);
        ObjectAnimator translateAnimOut2 = ObjectAnimator.ofFloat(mActions[i], "translationX", editTranslate, 0);
        ObjectAnimator alphaAnimOut1 = ObjectAnimator.ofFloat(mShareActions[i], "alpha", 1f, 0f);
        ObjectAnimator alphaAnimOut2 = ObjectAnimator.ofFloat(mActions[i], "alpha", 0f, 1f);
        AnimatorSet animOut = new AnimatorSet();
        animOut.play(translateAnimOut1)
                .with(alphaAnimOut1)
                .with(alphaAnimOut2)
                .with(translateAnimOut2);
        animOut.setDuration(300L);
        return animOut;
    }

    protected void onShareWeibo(WallpaperInfo info, Uri imageUri) {
        if (info == null || imageUri == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (info.title != null && info.content != null) {
            sb.append(info.title);
            sb.append("\n");
            sb.append(info.content);
        }
        Intent shareIntent=new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setComponent(new ComponentName("com.sina.weibo",
                "com.sina.weibo.composerinde.ComposerDispatchActivity"));
        shareIntent.addCategory(Intent.CATEGORY_DEFAULT);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(shareIntent);
    }

    protected void onShareWeChat(WallpaperInfo info, Uri imageUri) {
        if (info == null || imageUri == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (info.title != null && info.content != null) {
            sb.append(info.title);
            sb.append("\n");
            sb.append(info.content);
        }
        Intent shareIntent=new Intent(Intent.ACTION_SEND);
//        shareIntent.putExtra("Kdescription", sb.toString()); // Useless
//        shareIntent.putExtra(Intent.EXTRA_SUBJECT, sb.toString()); // Useless
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setComponent(new ComponentName("com.tencent.mm",
                "com.tencent.mm.ui.tools.ShareImgUI"));
        shareIntent.addCategory(Intent.CATEGORY_DEFAULT);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        shareIntent.putExtra("Kdescription", sb.toString());
        mContext.startActivity(shareIntent);
    }

    protected void onShareWeChatMoment(WallpaperInfo info, Uri imageUri) {
        if (info == null || imageUri == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (info.title != null && info.content != null) {
            sb.append(info.title);
            sb.append("\n");
            sb.append(info.content);
        }
        Intent shareIntent=new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra("Kdescription", sb.toString());
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setComponent(new ComponentName("com.tencent.mm",
                "com.tencent.mm.ui.tools.ShareToTimeLineUI"));
//        shareIntent.addCategory(Intent.CATEGORY_DEFAULT);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(shareIntent);
    }

    protected void onShareQQ(WallpaperInfo info, Uri imageUri) {
        if (info == null || imageUri == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (info.title != null && info.content != null) {
            sb.append(info.title);
            sb.append("\n");
            sb.append(info.content);
        }
        Intent shareIntent=new Intent(Intent.ACTION_SEND);
//        shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString()); // Useless
//        shareIntent.putExtra("summary", sb.toString()); // Useless
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setComponent(new ComponentName("com.tencent.mobileqq",
                "com.tencent.mobileqq.activity.JumpActivity"));
        shareIntent.addCategory(Intent.CATEGORY_DEFAULT); // Useless
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(shareIntent);
    }

    protected void onShareQzone(WallpaperInfo info, Uri imageUri) {
        if (info == null || imageUri == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (info.title != null && info.content != null) {
            sb.append(info.title);
            sb.append("\n");
            sb.append(info.content);
        }
        Intent shareIntent=new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString()); // Useless
//        shareIntent.putExtra("summary", sb.toString()); // Useless
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
//        shareIntent.setComponent(new ComponentName("com.tencent.mobileqq",
//                "com.tencent.mobileqq.activity.qlinkJumpActivity"));
        shareIntent.setComponent(new ComponentName("com.qzone",
                "com.qzonex.module.operation.ui.QZonePublishMoodActivity"));
        shareIntent.addCategory(Intent.CATEGORY_DEFAULT); // Useless
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(shareIntent);
    }
}
