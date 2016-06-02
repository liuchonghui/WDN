package com.mfashiongallery.emag.preview.model;

import android.view.animation.Interpolator;

/**
 * Created by liuchonghui on 16/6/2.
 */
public class CubicEaseInOutInterpolator implements Interpolator {
    public CubicEaseInOutInterpolator() {
    }

    public float getInterpolation(float t) {
        t *= 2.0F;
        if(t < 1.0F) {
            return 0.5F * t * t * t;
        } else {
            t -= 2.0F;
            return 0.5F * (t * t * t + 2.0F);
        }
    }
}
