package com.mfashiongallery.emag.preview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mfashiongallery.emag.preview.model.RecordType;
import com.mfashiongallery.emag.preview.model.SineEaseInInterpolator;
import com.mfashiongallery.emag.preview.model.SineEaseInOutInterpolator;
import com.mfashiongallery.emag.preview.model.WallpaperInfo;

import java.io.File;
import java.io.IOException;

import tool.whosdomainname.android.R;

@SuppressLint("NewApi")
public class ActionMenus extends LinearLayout implements OnClickListener {
    private static final String SNAPSHOT_DIR = Environment.getExternalStorageDirectory().getPath()
            + "/DCIM/Screenshots/";
    private static final String SNAPSHOT_NAME = "lock_wallpaper.jpg";

    /**
     * Action to refresh lockscreen wallpaper
     */
    public static final String ACTION_REQUEST_LOCKSCREEN_WALLPAPER = "android.miui.REQUEST_LOCKSCREEN_WALLPAPER";
    private Gson mGson;

    private final static int SETTING = 0;
    private final static int LIKE = 1;
    private final static int DISLIKE = 2;
    private final static int SHARE = 3;
    private final static int APPLY = 4;
    private final static int ACTION_COUNT = 5;

    private int colorEnable;
    private int colorDisable;
    private View[] mActions = new View[ACTION_COUNT];
    private Animator[] mItemAnimIn = new Animator[ACTION_COUNT];
    private Animator[] mItemAnimOut = new Animator[ACTION_COUNT];
    private AnimatorSet mItemAnimSetIn = new AnimatorSet();
    private AnimatorSet mItemAnimSetOut = new AnimatorSet();

    private boolean mRegistered;
    private boolean mCanFinish;
    private boolean mNeedShowLoading;
    private LockWallpaperPreviewView mMainView;

    private Context mContext;

    public ActionMenus(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mGson = new Gson();
        colorEnable = getResources().getColor(R.color.menu_item_text_color_enable);
        colorDisable = getResources().getColor(R.color.menu_item_text_color_disable);
    }

    boolean finishActivity = true;

    private BroadcastReceiver mWallpaperChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final boolean succeed = intent.getBooleanExtra("set_lock_wallpaper_result", true);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    if (succeed) {
//                        ThemeResources.getLockWallpaperCache(getContext().getApplicationContext());
                    }
                    try {
                        Thread.sleep(1333L);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    mMainView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (finishActivity) {
                                ((Activity) getContext()).finish();
                            } else {
                                mNeedShowLoading = false;
                                mMainView.getLoadingView().setVisibility(View.INVISIBLE);
                                show(true);
                                Toast.makeText(getContext(), R.string.lockscreen_wallpaper_changed, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, 400);

                };
            }.execute();
        }
    };

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mActions[SETTING] = createImage(R.drawable.settings, SETTING, R.string.settings);
        mActions[LIKE] = createImage(R.drawable.like_enable, LIKE, R.string.like);
        mActions[DISLIKE] = createImage(R.drawable.dislike_selector, DISLIKE, R.string.delete);
        mActions[SHARE] = createImage(R.drawable.share, SHARE, R.string.share);
        mActions[APPLY] = createImage(R.drawable.apply, APPLY, R.string.apply);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, 0, 100);
        addView(new Space(mContext), lp);
        for (int i = 0; i < ACTION_COUNT; ++i) {
            addView(mActions[i], new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            addView(new Space(mContext), lp);
        }
//        addView(new Space(mContext), lp);

        initAnim();
        mItemAnimSetIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                for (int i = 0; i < ACTION_COUNT; ++i) {
                    mActions[i].setAlpha(0);
                }
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
    }

    private void initAnim() {
        for (int i = 0; i < ACTION_COUNT; ++i) {
            mItemAnimIn[i] = getItemAnimIn(mActions[i]);
            mItemAnimIn[i].setStartDelay(i * 50);
            mItemAnimOut[i] = getItemAnimOut(mActions[i]);
            mItemAnimOut[i].setStartDelay(i * 50);
        }
        mItemAnimSetIn.playTogether(mItemAnimIn);
        mItemAnimSetOut.playTogether(mItemAnimOut);
    }

    public void setMainView(LockWallpaperPreviewView view) {
        mMainView = view;
    }

    private View createImage(int imageId, int tag, int textId) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.menu_item, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.menu_item_image);
        imageView.setImageResource(imageId);
        TextView textView = (TextView) view.findViewById(R.id.menu_item_text);
        textView.setText(textId);
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
                onShare();
                break;
            case APPLY:
                onApply();
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
            mMainView.getAdapter().recordEvent(currentItem, RecordType.EVENT_DISLIKE);
        }
    }

    private void onApply() {
        show(false);
        mMainView.startExitAnim();
        int currentItem = mMainView.getCurrentItem();
        WallpaperInfo info = mMainView.getAdapter().getWallpaperInfo(currentItem);
        if (TextUtils.isEmpty(info.key)) {
            mCanFinish = true;
        } else {
            try {
                mMainView.getAdapter().recordEvent(currentItem, RecordType.EVENT_APPLY);
                Intent intent = new Intent(ACTION_REQUEST_LOCKSCREEN_WALLPAPER);
                intent.putExtra("wallpaperInfo", mGson.toJson(info));
                if (currentItem == 0) {
                    mCanFinish = true;
                } else {
                    intent.putExtra("apply", true);
                    mNeedShowLoading = true;
                    finishActivity = true;
                    if (!mRegistered) {
                        IntentFilter wallpaperChangeIntentFilter = new IntentFilter();
//                        wallpaperChangeIntentFilter.addAction(SystemIntent.ACTION_SET_KEYGUARD_WALLPAPER);
                        getContext().registerReceiver(mWallpaperChangeReceiver, wallpaperChangeIntentFilter);
                        mRegistered = true;
                    }
                }
                getContext().sendBroadcast(intent);
            } catch (Exception e) {
                mCanFinish = true;
            }
        }
    }

    public void applyWallpaper(WallpaperInfo info) {
        if (info == null || info.key == null) {
            return;
        }
        show(false);
        Intent intent = new Intent(ACTION_REQUEST_LOCKSCREEN_WALLPAPER);
        intent.putExtra("wallpaperInfo", mGson.toJson(info));
        intent.putExtra("apply", true);
        mCanFinish = false;
        mNeedShowLoading = true;
        finishActivity = false;
        if (!mRegistered) {
            IntentFilter wallpaperChangeIntentFilter = new IntentFilter();
            wallpaperChangeIntentFilter.addAction(ACTION_REQUEST_LOCKSCREEN_WALLPAPER);
            getContext().registerReceiver(mWallpaperChangeReceiver, wallpaperChangeIntentFilter);
            mRegistered = true;
        }
        getContext().sendBroadcast(intent);
        try {
            mMainView.getAdapter().recordEvent(info, RecordType.EVENT_APPLY);
        } catch (Exception e) {
        }
    }

    private void onShare() {
        int curItem = mMainView.getCurrentItem();
        try {
            View view = mMainView.getAdapter().getView(curItem);
            if (view != null) {
                Bitmap bitmap = convertViewToBitmap(view, view.getWidth(), view.getHeight());
                show(false);
                mMainView.startExitAnim();
            }
        } catch (Exception e) {
        }
        try {
            mMainView.getAdapter().recordEvent(curItem, RecordType.EVENT_SHARE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap convertViewToBitmap(View view, int bitmapWidth, int bitmapHeight) {
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        return bitmap;
    }

    private void onLike(View v) {
        try {
            int currentItem = mMainView.getCurrentItem();
            WallpaperInfo info = mMainView.getAdapter().getWallpaperInfo(mMainView.getCurrentItem());
            boolean isLiked = info.like;
            isLiked = !isLiked;
            final ImageView image = (ImageView) v.findViewById(R.id.menu_item_image);
            image.setImageResource(isLiked ? R.drawable.liked : R.drawable.like_enable);
            final TextView text = (TextView) v.findViewById(R.id.menu_item_text);
            text.setText(isLiked ? R.string.liked : R.string.like);
            info.like = isLiked;
            mMainView.getAdapter().recordEvent(currentItem,
                    isLiked ? RecordType.EVENT_LIKE : RecordType.EVENT_CANCEL_LIKE);
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
            if (currentItem == 0) {
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
        ((Activity) getContext()).finish();
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
        final TextView text = (TextView) mActions[LIKE].findViewById(R.id.menu_item_text);
        text.setText(isLiked ? R.string.liked : R.string.like);
//        mLikeView.setTransitionAlpha(info.supportLike ? 1f : 0.5f);//todo 需要自己实现
//        mLikeView.setAlpha(info.supportLike ? 1f : 0.5f);
        image.setEnabled(info.supportLike);
        text.setTextColor(info.supportLike ? colorEnable : colorDisable);
        mActions[LIKE].setEnabled(info.supportLike);
    }

    private void updateDislikeView() {
        final ImageView image = (ImageView) mActions[DISLIKE].findViewById(R.id.menu_item_image);
        final TextView text = (TextView) mActions[DISLIKE].findViewById(R.id.menu_item_text);
        boolean enable = mMainView.getAdapter().canDislike(mMainView.getCurrentItem());
        image.setEnabled(enable);
        text.setTextColor(enable ? colorEnable : colorDisable);
        mActions[DISLIKE].setEnabled(enable);
    }

    /**
     * Use updateDislikeView for instead.
     * @param enable
     */
    @Deprecated
    private void setDislikeViewEnable(boolean enable) {
        final ImageView image = (ImageView) mActions[DISLIKE].findViewById(R.id.menu_item_image);
        final TextView text = (TextView) mActions[DISLIKE].findViewById(R.id.menu_item_text);
        image.setEnabled(enable);
        text.setTextColor(enable ? colorEnable : colorDisable);
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

    private boolean mShow = false;

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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mRegistered) {
            getContext().unregisterReceiver(mWallpaperChangeReceiver);
            mRegistered = false;
        }
    }

    public void toggle() {
        show(!mShow);
        if (mShow) {
            mMainView.showMask();
        } else {
            mMainView.hideMask();
        }
    }
}
