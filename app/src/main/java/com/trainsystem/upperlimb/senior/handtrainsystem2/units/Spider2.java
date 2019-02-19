package com.trainsystem.upperlimb.senior.handtrainsystem2.units;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.trainsystem.upperlimb.senior.handtrainsystem2.GamePanel2;


/**
 * Created by percyku on 2017/3/1.
 */

public class Spider2 extends GameObject {
    private Bitmap image;
    public Spider2(Bitmap res, int x, int y){

        height=501;
        width =122;

        this.x = x;
        this.y = y;
        dx = GamePanel2.MOVESPEED;

        image = Bitmap.createBitmap(res, 0, 0, width, height);

    }


    public void update()
    {
        x +=dx;

    }
    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(image, x, y, null);

    }
}
