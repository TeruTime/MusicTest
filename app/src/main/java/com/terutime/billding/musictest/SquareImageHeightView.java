package com.terutime.billding.musictest;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by drdc on 2015-07-30.
 */
public class SquareImageHeightView extends ImageView
{
    //Public constructors
    public SquareImageHeightView(Context context)
    {
        super(context);
    }
    public SquareImageHeightView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    public SquareImageHeightView(Context context, AttributeSet attrs, int defStyleRes)
    {
        super(context, attrs, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //Temp fix
        double scaledHeight = 0.5*getMeasuredHeight();
        int height = Integer.valueOf((int) Math.round(scaledHeight));
        int width = getMeasuredWidth();
        setMeasuredDimension(height, height);
    }
}
