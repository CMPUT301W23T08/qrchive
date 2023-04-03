package com.example.qrchive.Classes;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

/**
 * UniformSizeImageView is a custom ImageView that maintains a uniform size
 * by setting its height equal to its width. This class extends the AppCompatImageView and overrides the onMeasure method to achieve this behavior.
 */

public class UniformSizeImageView extends androidx.appcompat.widget.AppCompatImageView {

    public UniformSizeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Overrides the onMeasure method to set the height equal to the width.
     *
     * @param widthMeasureSpec  The width measurement specification.
     * @param heightMeasureSpec The height measurement specification.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        Drawable d = getDrawable();
        setMeasuredDimension(widthMeasureSpec,widthMeasureSpec);

    }
}
