package com.mfashiongallery.emag.preview;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * @author liuchonghui
 */
public class ShareAgent {

    protected void doShare(Context context, SharePlatform platform,
                           String title, String content, Uri uri) {
        Log.d("ACME", "doShare " + platform + ", " + content + ", " + uri.getEncodedPath());
        if (SharePlatform.WECHAT_MOMENT == platform) {
            onShareWeChatMoment(context, title, content, uri);
        } else if (SharePlatform.WECHAT == platform) {
            onShareWeChat(context, title, content, uri);
        } else if (SharePlatform.WEIBO == platform) {
            onShareWeibo(context, title, content, uri);
        } else if (SharePlatform.QZONE == platform) {
            onShareQzone(context, title, content, uri);
        } else if (SharePlatform.QQ == platform) {
            onShareQQ(context, title, content, uri);
        }
    }

    protected void onShareWeibo(Context context, String title, String content, Uri imageUri) {
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
//        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        shareIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(shareIntent);
    }

    protected void onShareWeChat(Context context, String title, String content, Uri imageUri) {
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
//        shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(shareIntent);
    }

    protected void onShareWeChatMoment(Context context, String title, String content, Uri imageUri) {
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
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(shareIntent);
    }

    protected void onShareQQ(Context context, String title, String content, Uri imageUri) {
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
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(shareIntent);
    }

    protected void onShareQzone(Context context, String title, String content, Uri imageUri) {
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
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(shareIntent);
    }
}
