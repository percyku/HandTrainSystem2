package com.trainsystem.upperlimb.senior.handtrainsystem2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.trainsystem.upperlimb.senior.handtrainsystem.R;
import com.trainsystem.upperlimb.senior.handtrainsystem2.database.DatabaseHelper;
import com.trainsystem.upperlimb.senior.handtrainsystem2.database.DbConstants;
import com.trainsystem.upperlimb.senior.handtrainsystem2.tools.BlueToothConnectFinal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.droidsonroids.gif.GifImageView;

import static com.trainsystem.upperlimb.senior.handtrainsystem2.tools.BlueToothConnectFinal.handlerState;


/**
 * Created by percyku on 2017/3/2.
 */

public class Game extends Activity {

    //Test=========
    // frame width
    private static final int FRAME_W = 85;
    // frame height
    private static final int FRAME_H = 121;
    // number of frames
    private static final int NB_FRAMES = 14;
    // nb of frames in x
    private static final int COUNT_X = 5;
    // nb of frames in y
    private static final int COUNT_Y = 3;
    // frame duration
    // we can slow animation by changing frame duration
    private static final int FRAME_DURATION = 200; // in ms !
    // scale factor for each frame
    private static final int SCALE_FACTOR = 5;

    //=================


    private static BlueToothConnectFinal leftBt, rightBt;
    private StringBuilder sb = new StringBuilder();
    private StringBuilder sb2 = new StringBuilder();

    private static String test;
    public static int[] finger = new int[2];
    public static double[] aram = new double[2];
    public static double[] elbow = new double[3];


    public static int number;

    private boolean showGameDialog = false;
    private boolean showGameDialog2 = false;


    private boolean mNumberState = false;
    private boolean mNumberState2 = false;


    private boolean mStartState = false;
    private boolean mStartState2 = false;


    private boolean mImputState = true;
    private boolean mImputState2 = true;


    private boolean introduction = false;

    private Timer timer01;
    public static boolean startflag = false;
    private int tsec = 0, csec = 0, cmin = 0;


    private MediaPlayer mediaPlayer;
    private GamePanel gamePanel;
    private ProgressDialog dialog;
    private TextToSpeech textToSpeech;


    DatabaseHelper manager;
    SQLiteDatabase db;
    Cursor c;

    public static int score1[] = {0, 0, 0, 0};
    public static int prescore1[] = {0, 0, 0, 0};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Log.d("手機銀幕大小為 ", "" + metrics.widthPixels + " X " + metrics.heightPixels);
//        gamePanel = new GamePanel(Game.this);
//        setContentView(gamePanel);
        manager = new DatabaseHelper(this, DbConstants.creatTableScore);
        checkDatabase();
        dialog = ProgressDialog.show(Game.this,
                "讀取中", "請等待3秒...", true);

        new Thread(new Runnable() {
            @Override
            public void run() {


                try {
                    //google speark initial
                    textToSpeech = new TextToSpeech(Game.this, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if (status != TextToSpeech.ERROR) {
                                textToSpeech.setLanguage(Locale.CHINESE);
                            }
                        }
                    });
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    dialog.cancel();
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mArduinoConnectDialog();
                    }
                });


            }


        }).start();

        //Timer acount initial
        timer01 = new Timer();
        timer01.schedule(task, 0, 1000);

        //bluetooth initial
        leftBt = new BlueToothConnectFinal(this, 1);
        rightBt = new BlueToothConnectFinal(this, 2);

//        final AlertDialog alertDialog = getAlertDialog("與手套連線", "連線中......");
//        alertDialog.show();


//        mArduinoConnectDialog();


        leftBt.bluetoothIn = new Handler() {

            public void handleMessage(Message msg) {

                switch (msg.what) {

                    case handlerState:

                        byte[] readBuf = (byte[]) msg.obj;
                        String strIcom = new String(readBuf, 0, msg.arg1);
                        sb.append(strIcom);
                        int endOfLineIndex = sb.indexOf("\r\n");

                        //連線到 左手
                        if (endOfLineIndex >= 0) {
                            //開啟連遊戲畫面
                            if (mImputState) {
                                showGameDialog = true;
                                mImputState = false;
                            }
                            if (number == 1) {
                                String sbprint = sb.substring(0, endOfLineIndex);
                                sb.delete(0, sb.length());
                                checkGameState(sbprint.toString().trim());
                                Log.e("test:", "" + sbprint);
                                //判斷左手手套狀態是正確的
                                if (mNumberState == true) {
                                    if (mStartState) {


                                        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.gamebackground1);
                                        mediaPlayer.setLooping(true);
                                        mediaPlayer.start();
                                        ttsTest("開始拉");
                                        GamePanel.newGameCreated = true;
                                        gamePanel.setStart(mStartState);
//                                    Timer timer01 =new Timer();
//                                    timer01.schedule(task, 0,1000);
                                        mStartState = false;
                                        mImputState = false;

                                    }

                                    if (finger[0] < 60)
                                        gamePanel.player.change(BitmapFactory.decodeResource(getResources(), R.drawable.butterfly1), 140, 114, 5, gamePanel);
                                    else
                                        gamePanel.player.change(BitmapFactory.decodeResource(getResources(), R.drawable.butterfly1), 140, 114, 1, gamePanel);
                                } else {

                                    leftBt.mInput("1");

                                }
                            }


                        }
                        break;
                    //連線到 右手
                    case BlueToothConnectFinal.handlerState2:
                        byte[] readBuf2 = (byte[]) msg.obj;
                        String strIcom2 = new String(readBuf2, 0, msg.arg1);
                        sb2.append(strIcom2);
                        int endOfLineIndex2 = sb2.indexOf("\r\n");
                        if (endOfLineIndex2 >= 0) {
                            //開啟連遊戲畫面
                            if (mImputState2) {
                                showGameDialog2 = true;
                                mImputState2 = false;
                            }


                            if (number == 2) {
                                String sbprint2 = sb2.substring(0, endOfLineIndex2);
                                sb2.delete(0, sb2.length());
                                checkGameState(sbprint2.toString().trim());
                                Log.e("test2:", "" + sbprint2);
                                //判斷右手手套狀態是正確的
                                if (mNumberState2) {
                                    if (mStartState2) {
                                        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.gamebackground1);
                                        mediaPlayer.setLooping(true);
                                        mediaPlayer.start();
                                        ttsTest("開始拉");
                                        GamePanel.newGameCreated = true;
                                        gamePanel.setStart(mStartState2);
//                                    Timer timer01 =new Timer();
//                                    timer01.schedule(task, 0,1000);
                                        mStartState2 = false;
                                        mImputState2 = false;
                                    }

                                    if (finger[0] < 60)
                                        gamePanel.player.change(BitmapFactory.decodeResource(getResources(), R.drawable.butterfly1), 140, 114, 5, gamePanel);
                                    else
                                        gamePanel.player.change(BitmapFactory.decodeResource(getResources(), R.drawable.butterfly1), 140, 114, 1, gamePanel);
                                } else {

                                    rightBt.mInput("1");

                                }
                            }


                        }
                        break;
                }

                //遊戲畫面呈現
                if (showGameDialog && showGameDialog2) {
                    showGameDialog = false;
                    showGameDialog2 = false;
                    gamePanel = new GamePanel(Game.this);
                    setContentView(gamePanel);
//                                final AlertDialog alertDialog = getAlertDialog1("手臂遊戲", "請按確認開始");
//                                alertDialog.show();
//                    mGameDialog(R.string.dialog_game_start);
                    mGameDialogChoiceHand(R.string.dialog_game_start);
                    ttsTest(getResources().getString(R.string.dialog_game_start_content));
                }


            }

        };


    }


    private void updateDatabase() {
        db = manager.getWritableDatabase();
        Calendar mCal = Calendar.getInstance();
        String dateformat = "yyyyMMdd";
        SimpleDateFormat df = new SimpleDateFormat(dateformat);
        String today = df.format(mCal.getTime());
        try {
            c = db.rawQuery(" SELECT * FROM  " + DbConstants.TABLE_SENIORSCORE, null);
            c.moveToFirst();
            if (c.moveToFirst()) {
                do {
                    String state = c.getString(c.getColumnIndex(DbConstants.STATE));
                    if (state.equals("true") && c.getString(c.getColumnIndex(DbConstants.DATE)).equals(today)) {
                        if (c.getString(c.getColumnIndex(DbConstants.DATE)).equals(today)) {
                            Log.e("DbConstants.DATE", "update" + today);
                            ContentValues values = new ContentValues();
                            if(number==1){

                                score1[0]=score1[0] * 10;
                                score1[1]=score1[1];
                                score1[2]=prescore1[2];
                                score1[3]=prescore1[3];
                            }else{
                                score1[0]=prescore1[0] ;
                                score1[1]=prescore1[1];
                                score1[2]=score1[2]* 10;
                                score1[3]=score1[3];
                            }



                            values.put(DbConstants.DATE, c.getString(c.getColumnIndex(DbConstants.DATE)));
                            values.put(DbConstants.USER, c.getString(c.getColumnIndex(DbConstants.USER)));
                            values.put(DbConstants.GAME1, "" + score1[0] + "," + score1[1] + "," + score1[2] + "," + score1[3]);
                            values.put(DbConstants.GAME2, c.getString(c.getColumnIndex(DbConstants.GAME2)));
                            values.put(DbConstants.GAME3, c.getString(c.getColumnIndex(DbConstants.GAME3)));
                            values.put(DbConstants.STATE, c.getString(c.getColumnIndex(DbConstants.STATE)));
                            db.update(DbConstants.TABLE_SENIORSCORE, values, "_id=" + c.getInt(0), null);
                        }


                    }

                } while (c.moveToNext());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (db.isOpen())
                db.close();
        }
    }

    private void checkDatabase() {
        db = manager.getWritableDatabase();
        Calendar mCal = Calendar.getInstance();
        String dateformat = "yyyyMMdd";
        SimpleDateFormat df = new SimpleDateFormat(dateformat);
        String today = df.format(mCal.getTime());
        try {
            c = db.rawQuery(" SELECT * FROM  " + DbConstants.TABLE_SENIORSCORE, null);
            c.moveToFirst();
            if (c.moveToFirst()) {
                do {
                    String state = c.getString(c.getColumnIndex(DbConstants.STATE));
                    if (state.equals("true") && c.getString(c.getColumnIndex(DbConstants.DATE)).equals(today)) {
                        if (c.getString(c.getColumnIndex(DbConstants.DATE)).equals(today)) {
                            Log.e("DbConstants.DATE", "select" + today);
                            String[] a = c.getString(c.getColumnIndex(DbConstants.GAME1)).split(",");
                            Log.e("DbConstants.DATE", "select" + c.getString(c.getColumnIndex(DbConstants.GAME1)));
                            prescore1[0] = Integer.valueOf(a[0]);
                            prescore1[1] = Integer.valueOf(a[1]);
                            prescore1[2] = Integer.valueOf(a[2]);
                            prescore1[3] = Integer.valueOf(a[3]);

                        }


                    }

                } while (c.moveToNext());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (db.isOpen())
                db.close();
        }
    }


    private void checkGameState(String str) {

        if (str.length() <= 9 && str.length() >= 6) {
            String start = str;
            String end = str;
            Log.e("Check Char1", "" + start.charAt(0));
            Log.e("Check Char2", "" + end.charAt(end.length() - 1));
            Log.e("Check Char3", "" + str);
            if (start.charAt(0) == 'F' && end.charAt(end.length() - 1) == 'E' && str.indexOf('I') != -1 && str.indexOf('G') != -1) {
                Log.e("Check Char4", str.substring(str.indexOf("I") + 1, str.indexOf("G")));
                Log.e("Check Char5", str.substring(str.indexOf("G") + 1, str.indexOf("E")));
                if (str.substring(str.indexOf("I") + 1) != "G" && str.substring(str.indexOf("G") + 1) != "E") {
                    if (isNumerisIntegent(str.substring(str.indexOf("I") + 1, str.indexOf("G"))) && isNumerisIntegent(str.substring(str.indexOf("G") + 1, str.indexOf("E")))) {
                        finger[0] = Integer.valueOf(str.substring(str.indexOf("I") + 1, str.indexOf("G")));
                        finger[1] = Integer.valueOf(str.substring(str.indexOf("G") + 1, str.indexOf("E")));
                        test = "";
                        test = "彎曲程度" + finger[0] + "\n";
                        if (number == 1)
                            mNumberState = true;

                        if (number == 2)
                            mNumberState2 = true;
                    }
                }

            }

            if (start.charAt(0) == 'F' && end.charAt(end.length() - 1) == 'E' && str.indexOf('X') != -1 && str.indexOf('Y') != -1) {

                if (isNumerisDouble(str.substring(str.indexOf("X") + 1, str.indexOf("Y"))) && isNumerisDouble(str.substring(str.indexOf("Y") + 1, str.indexOf("E")))) {
                    aram[0] = Double.valueOf(str.substring(str.indexOf("X") + 1, str.indexOf("Y")));
                    aram[1] = Double.valueOf(str.substring(str.indexOf("Y") + 1, str.indexOf("E")));
                    test = "";
                    test = "x軸:" + aram[0] + "\n" + "y角加速度:" + aram[1] + "\n";
                }

            }


            if (start.charAt(0) == 'F' && end.charAt(end.length() - 1) == 'E' && str.indexOf('X') != -1 && str.indexOf('Y') != -1 && str.indexOf('Z') != -1) {

                if (isNumerisDouble(str.substring(str.indexOf("X") + 1, str.indexOf("Y"))) && isNumerisDouble(str.substring(str.indexOf("Y") + 1, str.indexOf("Z"))) && isNumerisDouble(str.substring(str.indexOf("Z") + 1, str.indexOf("E")))) {
                    elbow[0] = Double.valueOf(str.substring(str.indexOf("X") + 1, str.indexOf("Y")));
                    elbow[1] = Double.valueOf(str.substring(str.indexOf("Y") + 1, str.indexOf("Z")));
                    elbow[2] = Double.valueOf(str.substring(str.indexOf("Y") + 1, str.indexOf("Z")));
                    test = "";
                    test = "數值一：" + elbow[0] + "\n" + "數值二:" + elbow[1] + "\n" + "數值三:" + elbow[2] + "\n";

                }
            }
        }
    }

    public boolean isNumerisIntegent(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public boolean isNumerisDouble(String str) {
        Pattern pattern = Pattern.compile("^(-?\\d+)(\\.\\d+)?$");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    private void ttsTest(String str) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(str, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            textToSpeech.speak(str, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void mArduinoConnectDialog() {
        ttsTest("請點擊連線與手套連線進遊戲");
        android.support.v7.app.AlertDialog.Builder mBuilder = new android.support.v7.app.AlertDialog.Builder(Game.this);
        View mView = getLayoutInflater().inflate(R.layout.custom_arduino_dialog, null);
        Button mConnect = (Button) mView.findViewById(R.id.btnYes);
        Button mCancel = (Button) mView.findViewById(R.id.btnNo);

        TextView title = (TextView) mView.findViewById(R.id.text_title);
//        TextView content = (TextView) mView.findViewById(R.id.text_content);
        title.setText(R.string.dialog_arduino_title);
//        content.setText(R.string.dialog_arduino_content);

        mConnect.setText(R.string.dialog_arduino_start);
        mCancel.setText(R.string.dialog_arduino_leave);

        mBuilder.setView(mView);

        final android.support.v7.app.AlertDialog dialog = mBuilder.create();
        dialog.show();


        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Connect!", Toast.LENGTH_SHORT).show();
                leftBt.connectBt("98:D3:32:30:6F:03");
                rightBt.connectBt("98:D3:34:90:8D:C6");
                ttsTest("連線中");
                dialog.dismiss();

            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Cancel!", Toast.LENGTH_SHORT).show();
                ttsTest("記得還要再來玩喔");

                dialog.dismiss();
                finish();

            }
        });
    }

    private void mGameDialogChoiceHand(int titile) {
        android.support.v7.app.AlertDialog.Builder mBuilder = new android.support.v7.app.AlertDialog.Builder(Game.this);
        View mView = getLayoutInflater().inflate(R.layout.custom_game_hand_choice, null);
        TextView title = (TextView) mView.findViewById(R.id.text_title);
        title.setText(titile);
        GifImageView gif_left = (GifImageView) mView.findViewById(R.id.git_left);
        gif_left.setImageResource(R.drawable.game_l);
        GifImageView gif_right = (GifImageView) mView.findViewById(R.id.git_right);
        gif_right.setImageResource(R.drawable.game_r);
        Button bt = (Button) mView.findViewById(R.id.btncancel);
        mBuilder.setView(mView);
        final android.support.v7.app.AlertDialog dialog = mBuilder.create();
        dialog.show();
        gif_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                number = 1;
                leftBt.mInput("1");
                startflag = true;
                mStartState = true;
                dialog.dismiss();

            }
        });
        gif_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ttsTest("開始拉");
                number = 2;
                rightBt.mInput("1");
                mStartState2 = true;
                startflag = true;
                dialog.dismiss();

            }
        });
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Cancel!", Toast.LENGTH_SHORT).show();
                ttsTest("下次再挑戰喔");
                finish();
                dialog.dismiss();
            }
        });

    }

    private void mGameDialog(int bt1) {

        android.support.v7.app.AlertDialog.Builder mBuilder = new android.support.v7.app.AlertDialog.Builder(Game.this);
        View mView = getLayoutInflater().inflate(R.layout.custom_game_dialog, null);
        Button mConnect = (Button) mView.findViewById(R.id.btnYes);
        Button mCancel = (Button) mView.findViewById(R.id.btnNo);
        GifImageView gif = (GifImageView) mView.findViewById(R.id.git_show);
        gif.setImageResource(R.drawable.game_l);
        TextView title = (TextView) mView.findViewById(R.id.text_title);
        title.setText(R.string.dialog_game_title1);
        mConnect.setText(bt1);
        mCancel.setText(R.string.dialog_game_leave);

        mBuilder.setView(mView);

        final android.support.v7.app.AlertDialog dialog = mBuilder.create();
        dialog.show();

        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Connect!", Toast.LENGTH_SHORT).show();
                number = 1;
                leftBt.mInput("1");
                startflag = true;
                mStartState = true;
                score1[0] = 0;
                score1[1] = 0;
                score1[2] = 0;
                score1[3] = 0;
//                mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.gamebackground1);
//                mediaPlayer.setLooping(true);
//                mediaPlayer.start();
//                ttsTest("開始拉");
//                GamePanel.newGameCreated=true;
                dialog.dismiss();

            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Cancel!", Toast.LENGTH_SHORT).show();
                ttsTest("下次再挑戰喔");
                finish();
                dialog.dismiss();


            }
        });

    }

    private void mChangeHandDialog() {
        ttsTest("換手拉");
        android.support.v7.app.AlertDialog.Builder mBuilder = new android.support.v7.app.AlertDialog.Builder(Game.this);
        View mView = getLayoutInflater().inflate(R.layout.custom_gametutorial_dialog, null);
        Button mConnect = (Button) mView.findViewById(R.id.btnYes);
        Button mCancel = (Button) mView.findViewById(R.id.btnNo);

        TextView title = (TextView) mView.findViewById(R.id.text_title);
        GifImageView gif = (GifImageView) mView.findViewById(R.id.gif_tutorial);
        gif.setImageResource(R.drawable.game_r);
//        TextView content = (TextView) mView.findViewById(R.id.text_content);
        title.setText(R.string.dialog_arduino_title);
//        content.setText(R.string.dialog_arduino_content);
        mConnect.setText("換手拉");
        mCancel.setText(R.string.dialog_arduino_leave);

        mBuilder.setView(mView);

        final android.support.v7.app.AlertDialog dialog = mBuilder.create();
        dialog.show();


        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ttsTest("開始拉");
                number = 2;
                rightBt.mInput("1");
                mStartState2 = true;
                startflag = true;
                dialog.dismiss();

            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Cancel!", Toast.LENGTH_SHORT).show();
                ttsTest("下次再挑戰喔");
                finish();

                dialog.dismiss();

            }
        });

    }


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    csec = tsec % 60;
                    cmin = tsec / 60;
                    String s = "";
                    if (cmin < 10) {
                        s = "0" + cmin;
                    } else {
                        s = "" + cmin;
                    }
                    if (csec < 10) {
                        s = s + ":0" + csec;
                    } else {
                        s = s + ":" + csec;
                    }
                    switch (tsec) {
//                        case 40:
//                            GamePanel.player.setPlaying(false);
//                            mediaPlayer.pause();
//                            startflag = false;
//                            mChangeHandDialog();
//                            break;
//                        case 41:
//                            GamePanel.player.setPlaying(true);
//                            mediaPlayer.start();
//                            break;
                        case 70:
                            ttsTest("快結束囉");
                            break;
                        case 80:
                            ttsTest("結束囉 還要挑戰嗎");

                            mediaPlayer.stop();
                            gamePanel.setStart(false);
                            updateDatabase();
                            mStartState = false;
                            mStartState2 = false;
                            mGameDialogChoiceHand(R.string.dialog_game_restart);
//                            mGameDialog(R.string.dialog_game_restart);
                            startflag = false;
                            tsec = 0;
                            break;

                    }


//                    s字串為00:00格式
                    Log.e("time", "" + s);
                    break;
            }
        }
    };

    private TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (startflag) {
                //如果startflag是true則每秒tsec+1
                tsec++;
                Message message = new Message();

                //傳送訊息1
                message.what = 1;
                handler.sendMessage(message);
            }
        }

    };

    @Override
    protected void onStart() {
        if (introduction) {
            ttsTest("請連線手套，或是離開遊戲");
            introduction = false;
        }
        super.onStart();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();

        }

        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }

        }


        super.onPause();
    }


    @Override
    protected void onStop() {
        startflag = false;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        tsec = 0;
        task.cancel();

        if (leftBt.checkArduinoConnectState())
            leftBt.closeConnectBt(blReceiver);


        if (rightBt.checkArduinoConnectState())
            rightBt.closeConnectBt(blReceiver);

        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        }

        super.onDestroy();
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list


            }
        }
    };


}
