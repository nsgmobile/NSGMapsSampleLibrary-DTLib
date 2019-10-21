package com.nsg.nsgmapslibrary.samples;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.gms.maps.model.Circle;

public class CreateCircle extends View {
    Paint paint = new Paint();
    public CreateCircle(Context context) {
        super(context);
    }

    public CreateCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CreateCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path clipPath = new Path();
        paint.setColor(Color.GREEN);
        // set your own position and radius
        canvas.drawCircle(20,20,10,paint);
        super.onDraw(canvas);
    }


}
