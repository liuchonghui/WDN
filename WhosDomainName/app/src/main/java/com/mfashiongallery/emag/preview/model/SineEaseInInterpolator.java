package com.mfashiongallery.emag.preview.model;

import android.view.animation.Interpolator;

/**
 * Created by liuchonghui on 16/6/2.
 */
public class SineEaseInInterpolator implements Interpolator {
    public SineEaseInInterpolator() {
    }

    public float getInterpolation(float t) {
        return -((float)Math.cos((double)t * 1.5707963267948966D)) + 1.0F;
    }
}