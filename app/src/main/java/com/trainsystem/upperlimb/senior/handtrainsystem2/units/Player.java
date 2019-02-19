package com.trainsystem.upperlimb.senior.handtrainsystem2.units;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import com.trainsystem.upperlimb.senior.handtrainsystem2.Game;
import com.trainsystem.upperlimb.senior.handtrainsystem2.Game2;
import com.trainsystem.upperlimb.senior.handtrainsystem2.GamePanel;
import com.trainsystem.upperlimb.senior.handtrainsystem2.GamePanel2;


/**
 * Created by percyku on 2017/2/7.
 */

public class Player extends GameObject {
    //game basic elements
    private Bitmap spritesheet;
    private Animation animation = new Animation();
    private GamePanel gamePanel;
    private GamePanel2 gamePanel2;
    private int gameNumber;

    //gane environment
    private float dya;
    private int score;
    private boolean up;
    private boolean playing;
    private long startTime;
    private static int bounce;


    public Player(Bitmap res, int w, int h, int numFrames, GamePanel gamePanel, int gameNumber) {
        this.gamePanel = gamePanel;
        spritesheet = res;

        this.gameNumber = gameNumber;

        //player position
        x = gamePanel.WIDTH / 5;
        y = gamePanel.HEIGHT / 2;
        dy = 0;
        bounce = gamePanel.HEIGHT - h;

        score = 0;

        //player size
        height = h;
        width = w;

        Bitmap[] image = new Bitmap[numFrames];

        for (int i = 0; i < image.length; i++) {
            image[i] = Bitmap.createBitmap(spritesheet, i * width, 0, width, height);
        }

        animation.setFrames(image);
        animation.setDelay(10);
        startTime = System.nanoTime();

    }


    public Player(Bitmap res, int w, int h, int numFrames, GamePanel2 gamePanel2, int gameNumber) {
        this.gamePanel2 = gamePanel2;
        spritesheet = res;


        this.gameNumber = gameNumber;
        //player position
        x = gamePanel2.WIDTH / 4;
        y = gamePanel2.HEIGHT / 2;
        dy = 0;
        bounce = gamePanel2.HEIGHT - 187;

        score = 0;

        //player size
        height = h;
        width = w;

        Bitmap[] image = new Bitmap[numFrames];

        for (int i = 0; i < image.length; i++) {
            image[i] = Bitmap.createBitmap(spritesheet, i * width, 0, width, height);
        }

        animation.setFrames(image);
        animation.setDelay(10);
        startTime = System.nanoTime();

    }


    public void change(Bitmap res, int w, int h, int numFrames, GamePanel gamePanel) {
        height = h;
        width = w;

        Bitmap[] image = new Bitmap[numFrames];

        for (int i = 0; i < image.length; i++) {
            image[i] = Bitmap.createBitmap(spritesheet, i * width, 0, width, height);
            animation.setFrames(image);
            animation.setDelay(10);
        }


    }

    public void setUp(boolean b) {
        up = b;
    }

    public boolean getUp() {
        return up;
    }

    public void update() {
        long elapsed = (System.nanoTime() - startTime) / 1000000;
        if (elapsed > 100) {
            score++;
            startTime = System.nanoTime();
            Log.d("score", "" + score);
        }


        animation.update();


        if (gameNumber == 1) {
            firstGamePosition();

        } else {
            secondGamePosition();
        }


//        Test1();

    }


    private void firstGamePosition() {
        if (Game.finger[0] < 55) {
            dy -= 5.1f;
        } else {
            dy += getSpeedTimeDecrease();

        }


        if (dy > getMaxSpeed()) {
            // speed limit
            dy = getMaxSpeed();
        }


        y += dy;

        if (y >= bounce)
            y = bounce;
        if (y < 0)
            y = 0;

        dy = 0;

    }


    private void secondGamePosition(){
        int a=(int) Game2.aram[0];
        //Log.e("a",""+a);
        int position=50- a;
        //Log.e("position",""+position);
        if(position>120){
            position=120;
        }else if(position<0){
            position=0;
        }

        y=(((int)(bounce+25)*position/120));


        Log.e("bounce",""+y);
        if(y>=gamePanel.HEIGHT-60)
            y=bounce;
    }

    private void Test1() {
        if (up) {
            //dy = dya-=2.1f;
            dy -= 2.1f;
            //dy =dy* 2 / 3 + getSpeedTimeDecrease() / 2f ;
        } else {
            //dy = dya+=0.1f;
            // dy +=0.1f;
            dy += getSpeedTimeDecrease();
        }


        if (dy > getMaxSpeed()) {
            // speed limit
            dy = getMaxSpeed();
        }


        y += dy;

        if (y >= bounce)
            y = bounce;
        if (y < 0)
            y = 0;

        dy = 0;

    }


    public void draw(Canvas canvas) {
        canvas.drawBitmap(animation.getImage(), x, y, null);
    }

    public int getScore() {
        return score;
    }

    public boolean getPlaying() {
        return playing;
    }

    public void setPlaying(boolean b) {
        playing = b;
    }

    public void resetDY() {
        dya = 0;
    }

    public void resetScore() {
        score = 0;
    }


    protected int getSpeedTimeDecrease() {
        // 4 @ 720x1280 px
        //return gamePanel.getHeight() / 320;
        return 5;
    }

    protected int getMaxSpeed() {
        // 25 @ 720x1280 px
        //return gamePanel.getHeight() / 51.2f;
        return 30;
    }

    /**
     * Created by percyku on 2017/1/19.
     */


}