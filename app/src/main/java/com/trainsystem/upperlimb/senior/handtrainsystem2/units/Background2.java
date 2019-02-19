package com.trainsystem.upperlimb.senior.handtrainsystem2.units;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.trainsystem.upperlimb.senior.handtrainsystem2.GamePanel2;

/**
 * Created by percyku on 2017/2/28.
 */

public class Background2 {

    private Bitmap image;
    private int x,y,dx;

    public Background2(Bitmap image){
        this.image=image;
        dx= GamePanel2.MOVESPEED;
    }


    public void update(){
        x+=dx;
        if(x<-GamePanel2.WIDTH){
            x=0;
        }
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(image, x, y,null);
        if(x<0)
        {
            canvas.drawBitmap(image, x+GamePanel2.WIDTH, y, null);
        }
    }
    public void setVector(int dx)
    {
        this.dx = dx;
    }
}
