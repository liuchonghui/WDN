package com.mfashiongallery.emag.preview;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * 分享调用页
 *
 * @author liu_chonghui
 *
 * <activity
 *  android:name=".preview.ShareActivity"
 *  android:excludeFromRecents="true"
 *  android:label="ShareActivity"
 *  android:launchMode="singleInstance"
 *  android:noHistory="true"
 *  android:stateNotNeeded="true"
 *  android:taskAffinity=""
 *  android:theme="@android:style/Theme.NoDisplay" />
 */
public class ShareActivity extends Activity {
    public static Intent createIntent(Context context, PreviewExtra extra) {
        Log.d("ACME", "createIntent ShareActivity");
        Intent intent = new Intent(context, ShareActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION
                | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("extra", extra);
        return intent;
    }

    PreviewExtra mExtra;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d("ACME", "onCreate ShareActivity");
        if (getIntent() != null) {
            try {
                mExtra = (PreviewExtra) getIntent().getParcelableExtra("extra");
            } catch (Exception e) {
                mExtra = null;
            }
            if (null != mExtra) {
                String sharePlatform = mExtra.getSharePlatform();
                String shareTitle = mExtra.getShareTitle();
                String shareContent = mExtra.getShareContent();
                String shareUri = mExtra.getShareUri();
                for (ActionMenus.Platform p : ActionMenus.Platform.values()) {
                    if (p.name().equalsIgnoreCase(sharePlatform)) {
                        doShare(p, shareTitle, shareContent, Uri.parse(shareUri));
                        break;
                    }
                }
            }
        }
        finish();
    }

    protected void doShare(ActionMenus.Platform platform, String title, String content, Uri uri) {
        Log.d("ACME", "doShare " + platform + ", " + content + ", " + uri.getEncodedPath());
        if (ActionMenus.Platform.WECHAT_MOMENT == platform) {
            onShareWeChatMoment(title, content, uri);
        } else if (ActionMenus.Platform.WECHAT == platform) {
            onShareWeChat(title, content, uri);
        } else if (ActionMenus.Platform.WEIBO == platform) {
            onShareWeibo(title, content, uri);
        } else if (ActionMenus.Platform.QZONE == platform) {
            onShareQzone(title, content, uri);
        } else if (ActionMenus.Platform.QQ == platform) {
            onShareQQ(title, content, uri);
        }
     }

    protected void onShareWeibo(String title, String content, Uri imageUri) {
        if (imageUri == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (title != null && content != null) {
            sb.append(title);
            sb.append("\n");
            sb.append(content);
        }
        Intent shareIntent=new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setComponent(new ComponentName("com.sina.weibo",
                "com.sina.weibo.composerinde.ComposerDispatchActivity"));
        shareIntent.addCategory(Intent.CATEGORY_DEFAULT);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(shareIntent);
    }

    protected void onShareWeChat(String title, String content, Uri imageUri) {
        if (imageUri == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (title != null && content != null) {
            sb.append(title);
            sb.append("\n");
            sb.append(content);
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
        shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        startActivity(shareIntent);
    }

    protected void onShareWeChatMoment(String title, String content, Uri imageUri) {
        if (imageUri == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (title != null && content != null) {
            sb.append(title);
            sb.append("\n");
            sb.append(content);
        }
        Intent shareIntent=new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra("Kdescription", sb.toString());
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setComponent(new ComponentName("com.tencent.mm",
                "com.tencent.mm.ui.tools.ShareToTimeLineUI"));
//        shareIntent.addCategory(Intent.CATEGORY_DEFAULT);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(shareIntent);
    }

    protected void onShareQQ(String title, String content, Uri imageUri) {
        if (imageUri == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (title != null && content != null) {
            sb.append(title);
            sb.append("\n");
            sb.append(content);
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
        startActivity(shareIntent);
    }

    protected void onShareQzone(String title, String content, Uri imageUri) {
        if (imageUri == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (title != null && content != null) {
            sb.append(title);
            sb.append("\n");
            sb.append(content);
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
        startActivity(shareIntent);
    }
}
