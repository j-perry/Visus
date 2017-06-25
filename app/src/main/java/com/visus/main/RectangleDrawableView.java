package com.visus.main;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.*;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by jonathanperry on 12/05/2017.
 */

public class RectangleDrawableView extends View {

    private ShapeDrawable mDrawable;

    public RectangleDrawableView(Context context, int width, int height) {
        super(context);
        int x = 10;
        int y = 10;

        //mDrawable = new ShapeDrawable(new OvalShape());
        mDrawable = new ShapeDrawable(new RectShape());
        mDrawable.getPaint().setColor(0xff74AC23);
//        mDrawable.setBounds(x, y, x + width, y + height);
        mDrawable.setBounds(0, 0, width, (height / 100 * 80));
    }

    protected void onDraw(Canvas canvas) {
        mDrawable.draw(canvas);
    }

}
