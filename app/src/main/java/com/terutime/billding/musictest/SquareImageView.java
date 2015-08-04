package com.terutime.billding.musictest;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by drdc on 2015-07-30.
 */
public class SquareImageView extends ImageView
{
    //Public constructors
    public SquareImageView(Context context)
    {
        super(context);
    }
    public SquareImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    public SquareImageView(Context context, AttributeSet attrs, int defStyleRes)
    {
        super(context, attrs, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }
}
