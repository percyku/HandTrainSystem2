package com.trainsystem.upperlimb.senior.handtrainsystem2;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import com.trainsystem.upperlimb.senior.handtrainsystem.R;
import com.trainsystem.upperlimb.senior.handtrainsystem2.units.Background;
import com.trainsystem.upperlimb.senior.handtrainsystem2.units.Enemy2;
import com.trainsystem.upperlimb.senior.handtrainsystem2.units.GameObject;
import com.trainsystem.upperlimb.senior.handtrainsystem2.units.Player;
import com.trainsystem.upperlimb.senior.handtrainsystem2.units.Reward;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by percyku on 2017/2/28.
 */

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {


    public static final int WIDTH = 1861;
    public static final int HEIGHT = 1013;

    public static boolean newGameCreated = false;


    public static final int MOVESPEED = -5;
    public static final int HEIGHT1 = 264;

    private long missileStartTime;

    private ArrayList<Enemy2> enemy2s;
    private ArrayList<Reward> rewards;

    private int maxBorderHeight;
    private int minBorderHeight;
    private Random rand = new Random();


    //units
    public static Player player;
    private Background bg;

    public MainThread thread2;


    private SoundPool soundPool;
    HashMap<Integer, Integer> soundMap = new HashMap<>();

    private MediaPlayer mediaPlayer;







    public GamePanel(Context context) {
        super(context);
        getHolder().addCallback(this);
        thread2 = new MainThread(getHolder(), this, 1);

        setFocusable(true);

    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {


        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.butterfly1), 140, 114, 5, this, 1);

        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.grassbg1_1));

        enemy2s = new ArrayList<Enemy2>();
        rewards = new ArrayList<Reward>();
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
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {


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
    public boolean onTouchEvent(MotionEvent event) {


        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!player.getPlaying()) {
                player.change(BitmapFactory.decodeResource(getResources(), R.drawable.butterfly1), 140, 114, 5, this);
                player.setPlaying(true);
                player.setUp(true);
                Game.startflag = true;
                newGameCreated=true;
//                mediaPlayer.start();
            } else {
                player.change(BitmapFactory.decodeResource(getResources(), R.drawable.butterfly1), 140, 114, 5, this);

                player.setUp(true);

            }
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            player.change(BitmapFactory.decodeResource(getResources(), R.drawable.butterfly1), 140, 114, 5, this);
            player.setUp(false);

            return true;

        }
        return super.onTouchEvent(event);
    }


    static int position;

    int position_h;

    private void getEnemy() {
        long missileElapsed = (System.nanoTime() - missileStartTime) / 1000000;
//        int position=(int)(minBorderHeight+(rand.nextDouble()*(maxBorderHeight-minBorderHeight)));
//        int position= (int) (Math.random() * (maxBorderHeight - minBorderHeight + 1) + minBorderHeight);
        position = (int) (Math.random() * (maxBorderHeight - minBorderHeight + 1) + minBorderHeight);
        if (missileElapsed > (2000 - player.getScore() / 4)) {
            if (rewards.size() < 1) {
                int resX = 126, resY = 118, resF = 1, resS = (int) (Math.random() * 10) + 1;
                position = (int) (Math.random() * (maxBorderHeight - minBorderHeight + 1) + minBorderHeight);
                Bitmap res = BitmapFactory.decodeResource(getResources(), R.drawable.flower);
                rewards.add(new Reward(res, WIDTH + 10, position, resX, resY, resS, resF));
                position_h = position + 118;
                Log.e("position", "" + position);
                Log.e("position_h", "" + position_h);

            }
            if (rewards.size() == 1) {
                if (enemy2s.size() < 5) {
                    int res_h = position_h;
                    Bitmap positon_res = BitmapFactory.decodeResource(getResources(), R.drawable.enemy_bird_01);
                    int positon_resX = 180;
                    int positon_resY = 136;
                    int positon_resS = (int) (Math.random() * 10) + 30;
                    int positon_resF = 7;
                    //bird
                    if (position_h < 300) {
//                        positon_res = BitmapFactory.decodeResource(getResources(), R.drawable.enemy_bird_01);
//                        positon_resX = 180;
//                        positon_resY = 136;
                        positon_res = BitmapFactory.decodeResource(getResources(), R.drawable.enemy_bird_01_1);
                        positon_resX = 191;
                        positon_resY = 152;

                        positon_resS = (int) (Math.random() * 9) + 10;
                        positon_resF = 7;
                        if (320 - position_h > 100)
                            res_h = res_h + 100;
                        else
                            res_h = res_h - 258;
                    }
                    //drangonfly
                    if (position_h < 600 && position_h > 300) {
//                        positon_res = BitmapFactory.decodeResource(getResources(), R.drawable.enemy_dragonfly_01);
//                        positon_resX = 169;
//                        positon_resY = 112;
                        positon_res = BitmapFactory.decodeResource(getResources(), R.drawable.enemy_bird_02_1);
                        positon_resX = 191;
                        positon_resY = 153;
                        positon_resS = (int) (Math.random() * 9) + 10;
                        positon_resF = 7;

                        if (620 - position_h > 100)
                            res_h = res_h + 100;
                        else
                            res_h = res_h - 258;
                    }
                    //bee
                    if (position_h > 620 && position_h < 950) {
                        positon_res = BitmapFactory.decodeResource(getResources(), R.drawable.enemy_bee_01);
                        positon_resX = 139;
                        positon_resY = 147;
                        positon_resS = (int) (Math.random() * 9) + 10;
                        positon_resF = 7;

                        if (950 - position_h > 100)
                            res_h = res_h + 100;
                        else
                            res_h = res_h - 258;
                    }
                    Log.e("res_h", "" + res_h);

                    enemy2s.add(new Enemy2(positon_res, WIDTH + 300, res_h, positon_resX, positon_resY, positon_resS, positon_resF));


                }
            }

            missileStartTime = System.nanoTime();

        }

        for (int i = 0; i < enemy2s.size(); i++) {
            //update missile
            enemy2s.get(i).update();

            if (collision(enemy2s.get(i), player)) {
                enemy2s.remove(i);
                soundPool.play(soundMap.get(2), 1, 1, 0, 0, 1.5F);
                player.setPlaying(true);
                if(Game.number==1)
                    Game.score1[1]++;
                else
                    Game.score1[3]++;
                break;
            }
            //remove missile if it is way off the screen
            if (enemy2s.get(i).getX() < -100) {
                enemy2s.remove(i);
                break;
            }
        }


        for (int i = 0; i < rewards.size(); i++) {
            //update missile
            rewards.get(i).update();

            if (collision(rewards.get(i), player)) {
                rewards.remove(i);
                soundPool.play(soundMap.get(1), 1, 1, 0, 0, 1.5F);
                player.setPlaying(true);
                if(Game.number==1)
                    Game.score1[0]++;
                else
                    Game.score1[2]++;
                break;
            }
            if (rewards.get(i).getX() < -10) {
                rewards.remove(i);
//                int resX = 126, resY = 118, resF = 1, resS = (int) (Math.random() * 10) + 1;
//                Bitmap res = BitmapFactory.decodeResource(getResources(), R.drawable.flower);
//                rewards.add(new Reward(res, WIDTH + 10, position, resX, resY, resS, resF));
                break;
            }
        }

    }

    protected void update() {

        if (player.getPlaying()) {

            bg.update();
            player.update();
            this.getEnemy();

        }
        if (newGameCreated) {
            newGame();
            newGameCreated = false;
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

            bg.draw(canvas);
            player.draw(canvas);

            for (Reward s : rewards) {
                s.draw(canvas);
            }


            for (Enemy2 e : enemy2s) {
                e.draw(canvas);
            }


            canvas.restoreToCount(savedState);

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

        minBorderHeight = 0;
        maxBorderHeight = 850;

        player.resetDY();
        player.resetScore();
        player.setY(HEIGHT / 2);


    }

    public void setStart(Boolean b) {
        player.setPlaying(b);
    }


    private void mEnemyTest() {
        long missileElapsed = (System.nanoTime() - missileStartTime) / 1000000;
        if (missileElapsed > (2000 - player.getScore() / 4)) {
            if (rewards.size() < 1) {
                int resX = 126, resY = 118, resF = 1, resS = (int) (Math.random() * 10) + 1;
                int position = (int) (rand.nextDouble() * (HEIGHT - (maxBorderHeight * 2)) + maxBorderHeight);
                Bitmap res = BitmapFactory.decodeResource(getResources(), R.drawable.flower);
                rewards.add(new Reward(res, WIDTH + 10, position, resX, resY, resS, resF));
            }
            if (enemy2s.size() < 3) {
                int resX = 180, resY = 136, resF = 7, resS = (int) (Math.random() * 10) + 10;
                int position = (int) (rand.nextDouble() * (HEIGHT - (maxBorderHeight * 2)) + maxBorderHeight);

                Bitmap res = BitmapFactory.decodeResource(getResources(), R.drawable.enemy_bird_01);

                if (position < HEIGHT / 3) {
                    res = BitmapFactory.decodeResource(getResources(), R.drawable.enemy_bird_01);
                    resX = 180;
                    resY = 136;
                    resS = (int) (Math.random() * 10) + 30;
                    resF = 7;
                }
                if (position > HEIGHT / 3 && position < HEIGHT * 2 / 3) {
                    res = BitmapFactory.decodeResource(getResources(), R.drawable.enemy_dragonfly_01);
                    resX = 169;
                    resY = 112;
                    resS = (int) (Math.random() * 10) + 20;
                    resF = 7;

                }
                if (position > HEIGHT * 2 / 3) {
                    res = BitmapFactory.decodeResource(getResources(), R.drawable.enemy_bee_01);
                    resX = 139;
                    resY = 147;
                    resS = (int) (Math.random() * 10) + 10;
                    resF = 7;
                    position = position - 139;
                }


                enemy2s.add(new Enemy2(res, WIDTH + 10, position, resX, resY, resS, resF));


            }

            //reset timer
            missileStartTime = System.nanoTime();

        }

        for (int i = 0; i < enemy2s.size(); i++) {
            //update missile
            enemy2s.get(i).update();

            if (collision(enemy2s.get(i), player)) {
                enemy2s.remove(i);
                player.setPlaying(true);
                break;
            }
            //remove missile if it is way off the screen
            if (enemy2s.get(i).getX() < -100) {
                enemy2s.remove(i);
                break;
            }
        }
        for (int i = 0; i < rewards.size(); i++) {
            //update missile
            rewards.get(i).update();

            if (collision(rewards.get(i), player)) {
                rewards.remove(i);
                player.setPlaying(true);
                break;
            }
            if (rewards.get(i).getX() < -100) {
                rewards.remove(i);
                break;
            }
        }


    }

}
