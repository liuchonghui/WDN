package com.mfashiongallery.emag.preview;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author liuchonghui
 */
public class PreviewExtra implements Parcelable {
    public PreviewExtra() {
        super();
    }

    String sharePlatform;
    String shareTitle;
    String shareContent;
    String shareUri;

    String shareComponent;

    public String getSharePlatform() {
        return sharePlatform;
    }

    public void setSharePlatform(String sharePlatform) {
        this.sharePlatform = sharePlatform;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public String getShareContent() {
        return shareContent;
    }

    public void setShareContent(String shareContent) {
        this.shareContent = shareContent;
    }

    public String getShareUri() {
        return shareUri;
    }

    public void setShareUri(String shareUri) {
        this.shareUri = shareUri;
    }

    public String getShareComponent() {
        return shareComponent;
    }

    public void setShareComponent(String shareComponent) {
        this.shareComponent = shareComponent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public PreviewExtra(Parcel in) {
        this.sharePlatform = in.readString();
        this.shareTitle = in.readString();
        this.shareContent = in.readString();
        this.shareUri = in.readString();

        this.shareComponent = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sharePlatform);
        dest.writeString(this.shareTitle);
        dest.writeString(this.shareContent);
        dest.writeString(this.shareUri);

        dest.writeString(this.shareComponent);
    }

    public static final Creator<PreviewExtra> CREATOR = new Creator<PreviewExtra>() {

        @Override
        public PreviewExtra createFromParcel(Parcel arg0) {
            return new PreviewExtra(arg0);
        }

        @Override
        public PreviewExtra[] newArray(int arg0) {
            return new PreviewExtra[arg0];
        }
    };
}
