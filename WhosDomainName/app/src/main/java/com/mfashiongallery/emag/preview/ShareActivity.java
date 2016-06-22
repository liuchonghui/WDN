package com.mfashiongallery.emag.preview;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.File;

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
        Intent intent = null;
        ComponentName componentName = null;
        if (extra.getShareComponent() != null) {
            componentName = ComponentName.unflattenFromString(
                    extra.getShareComponent());
        }
        if (componentName == null) {
            intent = new Intent(context, ShareActivity.class);
        } else {
            intent = new Intent();
            intent.setComponent(componentName);
        }
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
                Log.d("ACME", "Extra " + sharePlatform + ", " + shareTitle +
                        ", " + shareContent + ", " + shareUri);
                for (SharePlatform p : SharePlatform.values()) {
                    if (p.name().equalsIgnoreCase(sharePlatform)) {
                        new ShareAgent().doShare(this, p,
                                shareTitle, shareContent,
                                Uri.fromFile(new File(shareUri)));
                        break;
                    }
                }
            }
        }
        finish();
    }
}
