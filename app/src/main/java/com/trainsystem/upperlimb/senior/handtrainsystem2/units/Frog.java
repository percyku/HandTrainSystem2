package com.trainsystem.upperlimb.senior.handtrainsystem2.units;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.trainsystem.upperlimb.senior.handtrainsystem2.GamePanel2;


/**
 * Created by percyku on 2017/3/13.
 */

public class Frog extends GameObject{

    private Bitmap image;

    public Frog(Bitmap res, int x, int y){


        height=105;
        width =111;
//        height=143;
//        width =125;
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
