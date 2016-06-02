package com.mfashiongallery.emag.preview.model;

import android.view.animation.Interpolator;

/**
 * Created by liuchonghui on 16/6/2.
 */
public class SineEaseInOutInterpolator implements Interpolator {
    public SineEaseInOutInterpolator() {
    }

    public float getInterpolation(float t) {
        return -0.5F * (float)(Math.cos(3.141592653589793D * (double)t) - 1.0D);
    }
}
