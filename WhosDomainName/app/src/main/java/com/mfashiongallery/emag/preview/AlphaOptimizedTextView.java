
package com.mfashiongallery.emag.preview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class AlphaOptimizedTextView extends TextView
{
    public AlphaOptimizedTextView(Context context) {
        super(context);
    }

    public AlphaOptimizedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlphaOptimizedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }
}
