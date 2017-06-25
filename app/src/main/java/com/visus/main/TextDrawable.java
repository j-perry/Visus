package com.visus.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by jonathanperry on 15/05/2017.
 *
 * http://phaleksandrov.blogspot.co.uk/2012/08/draw-text-android.html
 */
public class TextDrawable {

    private Canvas canvas;
    private String msg;
    private int x;
    private int y;
    private final int textSize = 180;

    public TextDrawable(Canvas canvas, String msg, int x, int y) {
        this.canvas = canvas;
        this.msg = msg;
        this.x = x;
        this.y = y;
    }

    public void display() {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(textSize);
        canvas.drawText(msg, x, y, paint);
    }

}
