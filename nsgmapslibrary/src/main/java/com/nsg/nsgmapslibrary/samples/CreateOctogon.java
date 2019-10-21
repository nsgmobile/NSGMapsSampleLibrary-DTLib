package com.nsg.nsgmapslibrary.samples;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class CreateOctogon extends View {
    public CreateOctogon(Context context) {
        super(context);
    }

    public CreateOctogon(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CreateOctogon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path clipPath = new Path();
        clipPath.addPath(Octagon());
        canvas.clipPath(clipPath);
        canvas.drawColor(Color.GRAY);
        super.onDraw(canvas);
    }


    private Path Octagon(){
        Path p = new Path();
        float midX = getWidth()/2;
        float midY = getHeight()/2;
        p.moveTo(midX, midY);
        p.lineTo(midX+30, midY+12);
        p.lineTo(midX+12, midY+30);
        p.lineTo(midX-12, midY+30);
        p.lineTo(midX-30, midY+12);
        p.lineTo(midX-30, midY-12);
        p.lineTo(midX-12, midY-30);
        p.lineTo(midX+12, midY-30);
        p.lineTo(midX+30, midY-12);
        p.lineTo(midX+30, midY+12);
        return p;

    }
}
