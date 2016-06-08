
package com.mfashiongallery.emag.preview.model;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

public class WallpaperInfo {
    public String authority;
    public String key;
    public String wallpaperUri;
    public String title;
    public String titleColor;
    public String content;
    public String contentColor;
    public String packageName;
    public String landingPageUrl;
    public boolean supportLike;
    public boolean like;
    public String tag;
    public String cp;
    public int pos;
    public String ex;

    @Override
    public String toString() {
        return "WallpaperInfo [authority=" + authority + ", key=" + key + ", wallpaperUri=" + wallpaperUri + ", title="
                + title + ", content=" + content + ", packageName=" + packageName + ", landingPageUrl="
                + landingPageUrl + ", supportLike=" + supportLike + ", like=" + like + ", tag=" + tag + ", cp=" + cp
                + ", pos=" + pos + ", ex=" + ex + "]";
    }

    public Intent buildIntent() {
        if (!TextUtils.isEmpty(landingPageUrl)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(landingPageUrl));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (!TextUtils.isEmpty(packageName)) {
                intent.setPackage(packageName);
            }
            return intent;
        }
        return null;
    }
}
