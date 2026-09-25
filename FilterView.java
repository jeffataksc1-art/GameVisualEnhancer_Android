package com.gamevisualenhancer.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

public class FilterView extends View {
    private int tintColor = Color.TRANSPARENT;

    public FilterView(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public void setTint(int color) {
        tintColor = color;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(tintColor);
    }
}
