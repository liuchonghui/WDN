package com.mfashiongallery.emag.preview.model;

import android.view.animation.Interpolator;

/**
 * Created by liuchonghui on 16/6/2.
 */
public class CubicEaseOutInterpolator implements Interpolator {
    public CubicEaseOutInterpolator() {
    }

    public float getInterpolation(float t) {
        --t;
        return t * t * t + 1.0F;
    }
}