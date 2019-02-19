package com.trainsystem.upperlimb.senior.handtrainsystem2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import com.trainsystem.upperlimb.senior.handtrainsystem.R;
import com.trainsystem.upperlimb.senior.handtrainsystem2.units.Background2;
import com.trainsystem.upperlimb.senior.handtrainsystem2.units.BotBorder2;
import com.trainsystem.upperlimb.senior.handtrainsystem2.units.Enemy2;
import com.trainsystem.upperlimb.senior.handtrainsystem2.units.Frog;
import com.trainsystem.upperlimb.senior.handtrainsystem2.units.GameObject;
import com.trainsystem.upperlimb.senior.handtrainsystem2.units.Player;
import com.trainsystem.upperlimb.senior.handtrainsystem2.units.Reward;
import com.trainsystem.upperlimb.senior.handtrainsystem2.units.Spider2;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by percyku on 2017/3/11.
 */

public class GamePanel2 extends SurfaceView implements SurfaceHolder.Callback {


    public static final int WIDTH = 1856;
    public static final int HEIGHT = 1007;

    public static boolean newGameCreated = false;


    public static int HEIGHT1 = HEIGHT - 100;//grass
    public static int HEIGHT2 = HEIGHT - 110;//plant
    public static int HEIGHT3 = -300;        //spider


    public static  int MOVESPEED = -5;
    private long missileStartTime;
    private int maxBorderHeight;
    private int minBorderHeight;

    //units
    public static Player player;
    private Background2 bg2;
    private ArrayList<Reward> rewards;
    private ArrayList<Enemy2> enemy2s;
    private ArrayList<BotBorder2> botborder2;
    private ArrayList<Spider2> spider2;

    private boolean enemyState = false;

    private ArrayList<Frog> frog;

    //background control
    private MainThread thread2;


    private SoundPool soundPool;
    HashMap<Integer, Integer> soundMap = new HashMap<>();


    public GamePanel2(Context context) {

        super(context);
        //add the callback to the surfaceholder to intercept events
        getHolder().addCallback(this);

        thread2 = new MainThread(getHolder(), this, 2);

        //make gamePanel focusable so it can handle events
        setFocusable(true);

    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.bee_1), 143, 126, 6, this, 2);
        bg2 = new Background2(BitmapFactory.decodeResource(getResources(), R.drawable.grassbg2_1));

        rewards = new ArrayList<Reward>();
        enemy2s = new ArrayList<Enemy2>();
        spider2 = new ArrayList<Spider2>();
        frog = new ArrayList<Frog>();
        thread2.setRunning(true);
        thread2.start();

        AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();


        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(10)
                .build();
        soundMap.put(1, soundPool.load(getContext(), R.raw.reward, 1));

        soundMap.put(2, soundPool.load(getContext(), R.raw.crash, 1));


    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        boolean retry = true;
        int counter = 0;
        while (retry && counter < 1000) {
            counter++;
            try {
                thread2.setRunning(false);
                thread2.join();

                retry = false;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        final float scaleFactorX = getWidth() / (WIDTH * 1.f);
        final float scaleFactorY = getHeight() / (HEIGHT * 1.f);
        if (canvas != null) {
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);

            bg2.draw(canvas);
            player.draw(canvas);


            for (Reward s : rewards) {
                s.draw(canvas);
            }

            for (Spider2 ss : spider2) {
                ss.draw(canvas);
            }
            for (Enemy2 e : enemy2s) {
                e.draw(canvas);
            }

            for (Frog fg : frog) {
                fg.draw(canvas);
            }


            canvas.restoreToCount(savedState);
        }
    }

    public void update() {

        if (player.getPlaying()) {
            bg2.update();
            player.update();
            this.getEnemy();


        }
        if (newGameCreated) {
            newGameCreated = false;
            newGame();
        }

    }

    //get reward & enemy elements
//    int position = (int) (Math.random() * (maxBorderHeight - minBorderHeight + 1) + minBorderHeight);
    int position = 500;

    int position_h;
    int preposition = HEIGHT / 2;
    int positon_resS;
    int enemy = 0;

    public static  boolean checkSpeed=false;
    private void getEnemy() {

        long missileElapsed = (System.nanoTime() - missileStartTime) / 1000000;
        if (missileElapsed > (2000 - player.getScore() / 4)) {
            if (rewards.size() < 1 && Game2.tsec < 95) {

                int resX = 107, resY = 101, resF = 1,resS = (int) (Math.random() * 10) + 1;


                //check previous posistion_height
                //spider height
                if (preposition < 200) {
                    position = (int) (Math.random() * (maxBorderHeight - 300 + 1) + 300);
                }
                //bird height
                if (preposition > 200 && preposition < 700) {
                    position = (int) (Math.random() * (maxBorderHeight - minBorderHeight + 1) + minBorderHeight);
                }
                //frog height
                if (preposition > 700) {
                    position = (int) (Math.random() * (650 - minBorderHeight + 1) + minBorderHeight);
                }
                preposition = position;
//                preposition = position_h;

                Bitmap res = BitmapFactory.decodeResource(getResources(), R.drawable.flower_1);
                position_h = position + 127;
                positon_resS = (int) (Math.random() * 10) + 10;
                rewards.add(new Reward(res, WIDTH, position, resX, resY, resS, resF));
                rewards.add(new Reward(res, WIDTH + 100, position, resX, resY, resS, resF));
                rewards.add(new Reward(res, WIDTH + 200, position, resX, resY, resS, resF));
                rewards.add(new Reward(res, WIDTH + 300, position, resX, resY, resS, resF));
                rewards.add(new Reward(res, WIDTH + 400, position, resX, resY, resS, resF));
                rewards.add(new Reward(res, WIDTH + 500, position, resX, resY, resS, resF));


                Log.e("position", "" + position);
                Log.e("position_h", "" + position_h);


                if (rewards.size() > 5) {

                    if (position < 200) {
                        enemy = 1;
                    }

                    if (position > 200 && position < 700) {
                        enemy = 2;
                    }

                    if (position > 700) {
                        enemy = 3;

                    }


                    switch (enemy) {
                        case 1:
                            int HEIGHT1 = -(int) (Math.random() * (350 - 200 + 1) + 200);
                            if (spider2.size() == 0) {
                                spider2.add(new Spider2(BitmapFactory.decodeResource(getResources(), R.drawable.spider), WIDTH + 400, HEIGHT1));
                                HEIGHT1 = -(int) (Math.random() * (350 - 200 + 1) + 200);
                                spider2.add(new Spider2(BitmapFactory.decodeResource(getResources(), R.drawable.spider), WIDTH + 400 + 150, HEIGHT1));
                                HEIGHT1 = -(int) (Math.random() * (350 - 200 + 1) + 200);
                                spider2.add(new Spider2(BitmapFactory.decodeResource(getResources(), R.drawable.spider), WIDTH + 400 + 300, HEIGHT1));
                                HEIGHT1 = -(int) (Math.random() * (350 - 200 + 1) + 200);
                                spider2.add(new Spider2(BitmapFactory.decodeResource(getResources(), R.drawable.spider), WIDTH + 400 + 450, HEIGHT1));
                                HEIGHT1 = -(int) (Math.random() * (350 - 200 + 1) + 200);
                                spider2.add(new Spider2(BitmapFactory.decodeResource(getResources(), R.drawable.spider), WIDTH + 400 + 600, HEIGHT1));
                                HEIGHT1 = -(int) (Math.random() * (350 - 200 + 1) + 200);
                                spider2.add(new Spider2(BitmapFactory.decodeResource(getResources(), R.drawable.spider), WIDTH + 400 + 750, HEIGHT1));
                            }
                            break;
                        case 2:
                            if (enemy2s.size() < 3) {
                                int HEIGHT2 = 500;
                                if (700 - position_h < 200) {
                                    HEIGHT2 = position - 200;
                                } else if (200 - position < 200) {
                                    HEIGHT2 = position_h + 100;

                                } else if (position_h < 500 && position > 300) {
                                    if (position - 300 > 500 - position_h)
                                        HEIGHT2 = position - 200;
                                    else
                                        HEIGHT2 = position_h + 100;
                                }

                                for (int i = 0; i < 3; i++) {
                                    enemy2s.add(new Enemy2(BitmapFactory.decodeResource(getResources(), R.drawable.enemy_bird_02_1), WIDTH + i * 300, HEIGHT2, 191, 153, positon_resS, 7));

                                }
                            }
                            break;
                        case 3:
                            int HEIGHT3 = (int) (Math.random() * (900 - 800 + 1) + 800);
                            if (frog.size() == 0) {
                                frog.add(new Frog(BitmapFactory.decodeResource(getResources(), R.drawable.frog1), WIDTH + 400, HEIGHT3));
                                HEIGHT3 = (int) (Math.random() * (900 - 800 + 1) + 800);
                                frog.add(new Frog(BitmapFactory.decodeResource(getResources(), R.drawable.frog1), WIDTH + 400 + 150, HEIGHT3));
                                HEIGHT3 = (int) (Math.random() * (900 - 800 + 1) + 800);
                                frog.add(new Frog(BitmapFactory.decodeResource(getResources(), R.drawable.frog1), WIDTH + 400 + 300, HEIGHT3));
                                HEIGHT3 = (int) (Math.random() * (900 - 800 + 1) + 800);
                                frog.add(new Frog(BitmapFactory.decodeResource(getResources(), R.drawable.frog1), WIDTH + 400 + 450, HEIGHT3));
                                HEIGHT3 = (int) (Math.random() * (900 - 800 + 1) + 800);
                                frog.add(new Frog(BitmapFactory.decodeResource(getResources(), R.drawable.frog1), WIDTH + 400 + 600, HEIGHT3));
                                HEIGHT3 = (int) (Math.random() * (900 - 800 + 1) + 800);
                                frog.add(new Frog(BitmapFactory.decodeResource(getResources(), R.drawable.frog1), WIDTH + 400 + 750, HEIGHT3));


                            }


                            break;
                    }
                }
            }


            for (int i = 0; i < rewards.size(); i++) {
                //update missile
                rewards.get(i).update();

                if (collision(rewards.get(i), player)) {
                    rewards.remove(i);
                    player.setPlaying(true);
                    soundPool.play(soundMap.get(1), 1, 1, 0, 0, 1.5F);
                    if (Game2.number == 1)
                        Game2.score2[0]++;
                    else
                        Game2.score2[2]++;

                    break;
                }
                if (rewards.get(i).getX() < 0) {
                    rewards.remove(i);

                    break;
                }
            }

            for (int i = 0; i < spider2.size(); i++) {
                //update missile
                spider2.get(i).update();

                if (collision(spider2.get(i), player)) {
                    spider2.remove(i);
                    player.setPlaying(true);
                    soundPool.play(soundMap.get(2), 1, 1, 0, 0, 1.5F);
                    if (Game2.number == 1)
                        Game2.score2[1]++;
                    else
                        Game2.score2[3]++;
                    break;
                }
                if (spider2.get(i).getX() < 0) {
                    spider2.remove(i);

                    break;
                }
            }

            for (int i = 0; i < frog.size(); i++) {
                //update missile
                frog.get(i).update();

                if (collision(frog.get(i), player)) {
                    frog.remove(i);
                    player.setPlaying(true);
                    soundPool.play(soundMap.get(2), 1, 1, 0, 0, 1.5F);

                    if (Game2.number == 1)
                        Game2.score2[1]++;
                    else
                        Game2.score2[3]++;
                    break;
                }
                if (frog.get(i).getX() < 0) {
                    frog.remove(i);

                    break;
                }
            }

            for (int i = 0; i < enemy2s.size(); i++) {
                //update missile
                enemy2s.get(i).update();

                if (collision(enemy2s.get(i), player)) {
                    enemy2s.remove(i);
                    player.setPlaying(true);
                    soundPool.play(soundMap.get(2), 1, 1, 0, 0, 1.5F);
                    if (Game2.number == 1)
                        Game2.score2[1]++;
                    else
                        Game2.score2[3]++;
                    break;
                }
                //remove missile if it is way off the screen
                if (enemy2s.get(i).getX() < -100) {
                    enemy2s.remove(i);
                    break;
                }
            }
        }

    }

    public boolean collision(GameObject a, GameObject b) {
        if (Rect.intersects(a.getRectangle(), b.getRectangle())) {
            return true;
        }
        return false;
    }

    public void newGame() {
        rewards.clear();
        enemy2s.clear();
        frog.clear();
        spider2.clear();
        minBorderHeight = 0;
        maxBorderHeight = 850;

        player.resetDY();
        player.resetScore();
        player.setY(HEIGHT / 2);


    }

    public void setStart(Boolean b) {
        player.setPlaying(b);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {


        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!player.getPlaying()) {
                player.setPlaying(true);
                player.setUp(true);
                Game2.startflag = true;
                newGameCreated = true;

            } else {
                player.setUp(true);
            }
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            player.setUp(false);
            return true;

        }
        return super.onTouchEvent(event);
    }


}