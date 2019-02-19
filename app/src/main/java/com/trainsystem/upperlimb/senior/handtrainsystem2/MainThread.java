package com.trainsystem.upperlimb.senior.handtrainsystem2;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by percyku on 2017/2/28.
 */

public class MainThread extends Thread {

    private int FPS = 30;
    private double averageFPS;
    private SurfaceHolder surfaceHolder;
    private GamePanel gamepanel;
    private GamePanel2 gamepanel2;

    private boolean running;
    private static Canvas canvas;
    int gameNumber;





    public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel, int gameNumber) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamepanel = gamePanel;
        this.gameNumber = gameNumber;
    }


    public MainThread(SurfaceHolder surfaceHolder, GamePanel2 gamePanel2, int gameNumber) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamepanel2 = gamePanel2;
        this.gameNumber = gameNumber;

    }

    @Override
    public void run() {
        super.run();
        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime = 0;
        long frameCount = 0;
        long targetTime = 1000 / FPS;

        while (running) {
            startTime = System.nanoTime();
            canvas = null;


            try {
                canvas = this.surfaceHolder.lockCanvas();
                if (gameNumber == 1) {
                    synchronized (canvas) {
                        this.gamepanel.update();
                        this.gamepanel.draw(canvas);
                    }
                } else {
                    synchronized (canvas) {
                        this.gamepanel2.update();
                        this.gamepanel2.draw(canvas);
                    }
                }

            } catch (Exception e) {
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


            }

            timeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - timeMillis;
            try {
                this.sleep(waitTime);
            } catch (Exception e) {
            }

            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if (frameCount == FPS) {
                averageFPS = 1000 / ((totalTime / frameCount) / 1000000);
                frameCount = 0;
                totalTime = 0;
                System.out.println(averageFPS);
            }


        }

    }

    public void setRunning(boolean b) {
        running = b;
    }

}
