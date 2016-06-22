package com.mfashiongallery.emag.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;

import com.android.overlay.RunningEnvironment;

import java.io.File;

/**
 * Created by lijianbo1 on 15-12-9.
 */
public class MiFGUtils {

    public static String getSharePictureCachePath() {
        String path = null;
        Context context = RunningEnvironment.getInstance().getApplicationContext();
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(dir == null) {
            path = context.getFilesDir() + "/share_cache";
        } else {
            path = dir.getAbsolutePath();
        }
        return path;
    }

    public static int parseColor(String colorString) {
        return parseColor(colorString, -1); // -1 for white
    }

    public static int parseColor(String colorString, int defaultValue) {
        if (colorString == null || colorString.length() == 0) {
            return defaultValue;
        }
        return Color.parseColor(colorString);
    }

    public static int parseColor(String colorString, String defaultValue) {
        int color = 0;
        String colorValue = colorString;
        if (colorString == null || colorString.length() == 0) {
            colorValue = defaultValue;
        }
        color = Color.parseColor(colorValue);
        return color;
    }

}
