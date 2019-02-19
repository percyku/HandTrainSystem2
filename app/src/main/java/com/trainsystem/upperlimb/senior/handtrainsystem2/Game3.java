package com.trainsystem.upperlimb.senior.handtrainsystem2;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.droidsonroids.gif.GifImageView;

import static com.trainsystem.upperlimb.senior.handtrainsystem2.tools.BlueToothConnectFinal.handlerState;
import static com.trainsystem.upperlimb.senior.handtrainsystem2.tools.BlueToothConnectFinal.handlerState2;

public class Game3 extends AppCompatActivity {

    private static BlueToothConnectFinal leftBt, rightBt;
    private StringBuilder sb = new StringBuilder();
    private StringBuilder sb2 = new StringBuilder();

    private static String test;
    public static int[] finger = new int[2];
    public static double[] aram = new double[2];
    public static double[] elbow = new double[3];


    private static int number;

    private boolean showGameDialog = false;
    private boolean showGameDialog2 = false;


    private boolean mNumberState = false;
    private boolean mNumberState2 = false;


    private boolean mStartState = false;
    private boolean mStartState2 = false;


    private boolean mImputState = true;
    private boolean mImputState2 = true;


    private boolean introduction = false;


    public boolean handState[] = {false, false, false, false};

    private Timer timer01;
    public static boolean startflag = false;
    private int tsec = 0, csec = 0, cmin = 0;


    private MediaPlayer mediaPlayer;
    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundMap = new HashMap<>();

    private ProgressDialog dialog;
    private TextToSpeech textToSpeech;

    TextView txt_tur;
    GifImageView gif_tur;


    DatabaseHelper manager;
    SQLiteDatabase db;
    Cursor c;

    public static String score[] = {"null", "null", "null", "null"};
    public static String prescore[] = {"null", "null", "null", "null"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Log.d("手機銀幕大小為 ", "" + metrics.widthPixels + " X " + metrics.heightPixels);

        manager = new DatabaseHelper(this, DbConstants.creatTableScore);
        checkDatabase();
        dialog = ProgressDialog.show(Game3.this,
                "讀取中", "請等待3秒...", true);

        new Thread(new Runnable() {
            @Override
            public void run() {


                try {
                    //google speark initial
                    textToSpeech = new TextToSpeech(Game3.this, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if (status != TextToSpeech.ERROR) {
                                textToSpeech.setLanguage(Locale.CHINESE);
                            }
                        }
                    });
                    AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();

                    soundPool = new SoundPool.Builder()
                            .setAudioAttributes(audioAttributes)
                            .setMaxStreams(10)
                            .build();

                    soundMap.put(1, soundPool.load(getApplicationContext(), R.raw.pianoc1, 1));

                    soundMap.put(2, soundPool.load(getApplicationContext(), R.raw.pianod1, 1));

                    soundMap.put(3, soundPool.load(getApplicationContext(), R.raw.pianoe1, 1));

                    soundMap.put(4, soundPool.load(getApplicationContext(), R.raw.pianof1, 1));

                    soundMap.put(5, soundPool.load(getApplicationContext(), R.raw.pianog1, 1));

                    soundMap.put(6, soundPool.load(getApplicationContext(), R.raw.pianoa1, 1));

                    soundMap.put(7, soundPool.load(getApplicationContext(), R.raw.pianob1, 1));

                    soundMap.put(8, soundPool.load(getApplicationContext(), R.raw.pianoc2, 1));


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
                                if (mNumberState) {
                                    if (mStartState) {
//                                        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.gamebackground1);
//                                        mediaPlayer.setLooping(true);
//                                        mediaPlayer.start();
//                                        ttsTest("開始拉");


                                        mStartState = false;
                                        mImputState = false;
                                    }
                                    checkhandState();
                                } else {

                                    leftBt.mInput("2");

                                }
                            }


                        }
                        break;
                    //連線到 右手
                    case handlerState2:
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
//                                        ttsTest("開始拉");

                                        mStartState2 = false;
                                        mImputState2 = false;
                                    }

                                    checkhandState();
                                } else {

                                    rightBt.mInput("2");

                                }
                            }
                        }
                        break;
                }

                //遊戲畫面呈現
                if (showGameDialog && showGameDialog2) {
                    showGameDialog = false;
                    showGameDialog2 = false;

                    setContentView(R.layout.activity_game3);
                    txt_tur = (TextView) findViewById(R.id.txt_tur);
                    gif_tur = (GifImageView) findViewById(R.id.gif_tur);
//                                final AlertDialog alertDialog = getAlertDialog1("手臂遊戲", "請按確認開始");
//                                alertDialog.show();
//                    mGameDialog(R.string.dialog_game_title3, R.drawable.game3_l1);
                    mGameDialogChoiceHand(R.string.dialog_game_title3);

                    ttsTest(getResources().getString(R.string.dialog_game3_start_content));
                }
            }

        };
    }

    Calendar mCal = Calendar.getInstance();
    String dateformat = "yyyyMMdd";
    SimpleDateFormat df = new SimpleDateFormat(dateformat);

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
                            String[] a = c.getString(c.getColumnIndex(DbConstants.GAME3)).split(",");
                            Log.e("DbConstants.DATE", "select" + c.getString(c.getColumnIndex(DbConstants.GAME3)));
                            prescore[0] = a[0];
                            prescore[1] = a[1];
                            prescore[2] = a[2];
                            prescore[3] = a[3];

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

    private void updateDatabase() {
        db = manager.getWritableDatabase();

        String today = df.format(mCal.getTime());
        try {
            c = db.rawQuery(" SELECT * FROM  " + DbConstants.TABLE_SENIORSCORE, null);
            c.moveToFirst();
            if (c.moveToFirst()) {
                do {
                    String state = c.getString(c.getColumnIndex(DbConstants.STATE));
                    if (state.equals("true") && c.getString(c.getColumnIndex(DbConstants.DATE)).equals(today)) {
                        if (c.getString(c.getColumnIndex(DbConstants.DATE)).equals(today)) {

                            ContentValues values = new ContentValues();

                            if (number == 1) {


                                score[2] = prescore[2];
                                score[3] = prescore[3];
                            } else {
                                score[0] = prescore[0];
                                score[1] = prescore[1];

                            }
                            values.put(DbConstants.DATE, c.getString(c.getColumnIndex(DbConstants.DATE)));
                            values.put(DbConstants.USER, c.getString(c.getColumnIndex(DbConstants.USER)));
                            values.put(DbConstants.GAME1, c.getString(c.getColumnIndex(DbConstants.GAME1)));
                            values.put(DbConstants.GAME2, c.getString(c.getColumnIndex(DbConstants.GAME3)));
                            values.put(DbConstants.GAME3, score[0] + "," + score[1] + "," + score[2] + "," + score[3]);
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
        ttsTest("請點擊與手套連線進行遊戲");
        android.support.v7.app.AlertDialog.Builder mBuilder = new android.support.v7.app.AlertDialog.Builder(Game3.this);
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
//                leftBt.connectBt("20:15:12:01:17:74");

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
        android.support.v7.app.AlertDialog.Builder mBuilder = new android.support.v7.app.AlertDialog.Builder(Game3.this);
        View mView = getLayoutInflater().inflate(R.layout.custom_game_hand_choice, null);
        TextView title = (TextView) mView.findViewById(R.id.text_title);
        title.setText(titile);
        GifImageView gif_left = (GifImageView) mView.findViewById(R.id.git_left);
        gif_left.setImageResource(R.drawable.game2_l);
        GifImageView gif_right = (GifImageView) mView.findViewById(R.id.git_right);
        gif_right.setImageResource(R.drawable.game2_r);
        Button bt = (Button) mView.findViewById(R.id.btncancel);
        mBuilder.setView(mView);
        final android.support.v7.app.AlertDialog dialog = mBuilder.create();
        dialog.show();
        gif_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                number = 1;
                leftBt.mInput("2");
                startflag = true;
                mStartState = true;
                handState[0] = false;
                handState[1] = false;
                handState[2] = true;
                handState[3] = true;
                handNumber=0;

                mGameDialog(R.string.dialog_game_title3, R.drawable.game3_l1);
                dialog.dismiss();

            }
        });
        gif_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ttsTest("開始拉");
                number = 2;
                rightBt.mInput("2");
                mStartState2 = true;
                startflag = true;
                handState[0] = true;
                handState[1] = true;
                handState[2] = false;
                handState[3] = false;
                handNumber=2;
                mGameDialog(R.string.dialog_game_title3, R.drawable.game3_r1);
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


    private void mGameDialog(int bt1, int gif_image) {

        android.support.v7.app.AlertDialog.Builder mBuilder = new android.support.v7.app.AlertDialog.Builder(Game3.this);
        View mView = getLayoutInflater().inflate(R.layout.custom_gametutorial_dialog, null);
        TextView title = (TextView) mView.findViewById(R.id.text_title);
        final GifImageView gif = (GifImageView) mView.findViewById(R.id.gif_tutorial);
        Button mConnect = (Button) mView.findViewById(R.id.btnYes);
        Button mCancel = (Button) mView.findViewById(R.id.btnNo);
        title.setText(bt1);
        gif.setImageResource(gif_image);
        mConnect.setText(R.string.dialog_game_start);
        mCancel.setText(R.string.dialog_game_leave);

        mBuilder.setView(mView);

        final android.support.v7.app.AlertDialog dialog = mBuilder.create();
        dialog.show();

        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "Connect!", Toast.LENGTH_SHORT).show();

                // ttsTest("開始囉");
                if (handNumber == 0 || handNumber == 1) {
                    number = 1;
                    mStartState = true;
                    leftBt.mInput("2");

                } else {
                    number = 2;
                    mStartState2 = true;
                    rightBt.mInput("2");

                }

                position_1 = false;
                position_2 = false;
                position_3 = false;


                ttsTest("開始囉 請手臂與肩同寬靠牆停留五秒");
                txt_tur.setText("請手臂與肩同寬靠牆停留五秒");
                if (handNumber == 0) {

                    gif_tur.setImageResource(R.drawable.game3_ls1);

                } else if (handNumber == 1) {
                    gif_tur.setImageResource(R.drawable.game3_1_ls1);


                } else if (handNumber == 2) {
                    gif_tur.setImageResource(R.drawable.game3_rs1);


                } else if (handNumber == 3) {
                    gif_tur.setImageResource(R.drawable.game3_1_rs1);

                }

                startflag = true;


                dialog.dismiss();

            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Cancel!", Toast.LENGTH_SHORT).show();
                ttsTest("下次再挑戰喔");
                dialog.dismiss();
                finish();


            }
        });

    }

    private void mGameagainDialog(int bt1, int img) {

        android.support.v7.app.AlertDialog.Builder mBuilder = new android.support.v7.app.AlertDialog.Builder(Game3.this);
        View mView = getLayoutInflater().inflate(R.layout.custom_game_dialog, null);
        Button mConnect = (Button) mView.findViewById(R.id.btnYes);
        Button mCancel = (Button) mView.findViewById(R.id.btnNo);
        TextView title = (TextView) mView.findViewById(R.id.text_title);
        GifImageView gif_show = (GifImageView) mView.findViewById(R.id.git_show);
        gif_show.setImageResource(img);
        title.setText(bt1);
        mConnect.setText(R.string.dialog_game_restart);
        mCancel.setText(R.string.dialog_game_leave);

        mBuilder.setView(mView);

        final android.support.v7.app.AlertDialog dialog = mBuilder.create();
        dialog.show();

        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (handState[3] == true) {
                    handState[0] = false;
                    handState[1] = false;
                    handState[2] = false;
                    handState[3] = false;
                }


                if (handState[0] == false) {
                    ttsTest("左手請跟著畫面的動作進行遊戲");
                    mGameDialog(R.string.dialog_game_title3, R.drawable.game3_l1);
                } else if (handState[1] == false) {
                    ttsTest("左手請跟著畫面的動作進行遊戲");
                    mGameDialog(R.string.dialog_game_title3, R.drawable.game3_l2);

                } else if (handState[2] == false) {
                    ttsTest("右手請跟著畫面的動作進行遊戲");
                    mGameDialog(R.string.dialog_game_title3, R.drawable.game3_r1);

                } else if (handState[3] == false) {
                    ttsTest("右手請跟著畫面的動作進行遊戲");
                    mGameDialog(R.string.dialog_game_title3, R.drawable.game3_r2);

                }


                dialog.dismiss();

            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ttsTest("下次再挑戰喔");
                finish();
                dialog.dismiss();


            }
        });

    }


    private void checkGameState(String str) {

        if (str.length() >= 6) {
            String start = str;
            String end = str;
//            Log.e("Check Char1", "" + start.charAt(0));
//            Log.e("Check Char2", "" + end.charAt(end.length() - 1));
            if (start.charAt(0) == 'F' && end.charAt(end.length() - 1) == 'E' && str.indexOf('I') != -1 && str.indexOf('G') != -1) {
                if (isNumerisIntegent(str.substring(str.indexOf("I") + 1, str.indexOf("G"))) && isNumerisIntegent(str.substring(str.indexOf("G") + 1, str.indexOf("E")))) {
                    finger[0] = Integer.valueOf(str.substring(str.indexOf("I") + 1, str.indexOf("G")));
                    finger[1] = Integer.valueOf(str.substring(str.indexOf("G") + 1, str.indexOf("E")));
                    test = "";
                    test = "彎曲程度" + finger[0] + "\n";

                }
            }

            if (start.charAt(0) == 'F' && end.charAt(end.length() - 1) == 'E' && str.indexOf('X') != -1 && str.indexOf('Y') != -1) {
                if (str.substring(str.indexOf("X") + 1) != "Y" && str.substring(str.indexOf("Y") + 1) != "E") {
                    if (isNumerisDouble(str.substring(str.indexOf("X") + 1, str.indexOf("Y"))) && isNumerisDouble(str.substring(str.indexOf("Y") + 1, str.indexOf("E")))) {
                        aram[0] = Double.valueOf(str.substring(str.indexOf("X") + 1, str.indexOf("Y")));
                        aram[1] = Double.valueOf(str.substring(str.indexOf("Y") + 1, str.indexOf("E")));
                        test = "";
                        test = "x軸:" + aram[0] + "\n" + "y角加速度:" + aram[1] + "\n";
                        if (number == 1)
                            mNumberState = true;

                        if (number == 2)
                            mNumberState2 = true;

                    }

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

    //手部靠前等待三秒
    boolean position_1 = false;
    //開始爬升
    boolean position_2 = false;

    boolean position_3 = false;


    private double preAram;

    private int handNumber = 0;

    private void checkhandState() {


        if (!handState[handNumber]) {

            if (!position_1) {
                if (aram[0] < 30 && aram[0] > -10) {
                    if (tsec == 4) {
                        position_1 = true;
                        tsec = 0;

                        txt_tur.setText("請向上爬升到手臂覺得不舒服的時候停下");
                        if (handNumber == 0) {

                            gif_tur.setImageResource(R.drawable.game3_l1);

                        } else if (handNumber == 1) {
                            gif_tur.setImageResource(R.drawable.game3_l2);


                        } else if (handNumber == 2) {
                            gif_tur.setImageResource(R.drawable.game3_r1);


                        } else if (handNumber == 3) {
                            gif_tur.setImageResource(R.drawable.game3_r2);

                        }
                    }
                } else {
                    tsec = 0;
                }


            } else {
                if (!position_2) {
                    if (aram[0] > 10 && aram[0] < 20) {
                        preAram = aram[0];
                        position_2 = true;
                        soundPool.play(soundMap.get(1), 1, 1, 0, 0, 1.5F);
                    } else if (aram[0] > 20 && aram[0] < 30) {
                        preAram = aram[0];
                        position_2 = true;
                        soundPool.play(soundMap.get(2), 1, 1, 0, 0, 1.5F);
                    } else if (aram[0] > 30 && aram[0] < 40) {
                        preAram = aram[0];
                        position_2 = true;
                        soundPool.play(soundMap.get(3), 1, 1, 0, 0, 1.5F);
                    } else if (aram[0] > 40 && aram[0] < 50) {
                        preAram = aram[0];
                        position_2 = true;
                        soundPool.play(soundMap.get(4), 1, 1, 0, 0, 1.5F);
                    } else if (aram[0] > 50 && aram[0] < 60) {
                        preAram = aram[0];
                        position_2 = true;
                        soundPool.play(soundMap.get(5), 1, 1, 0, 0, 1.5F);
                    } else if (aram[0] > 60 && aram[0] < 70) {
                        preAram = aram[0];
                        position_2 = true;
                        soundPool.play(soundMap.get(6), 1, 1, 0, 0, 1.5F);
                    } else if (aram[0] > 70 && aram[0] < 80) {
                        preAram = aram[0];
                        position_2 = true;
                        soundPool.play(soundMap.get(7), 1, 1, 0, 0, 1.5F);
                    } else if (aram[0] > 80 && aram[0] < 90) {
                        preAram = aram[0];
                        position_2 = true;
                        soundPool.play(soundMap.get(8), 1, 1, 0, 0, 1.5F);
                    } else if (aram[0] > 90 && aram[0] < 100) {
                        preAram = aram[0];
                        position_2 = true;
                        ttsTest("到極限囉ㄤ");
                    }


                } else {

                    if (aram[0] - preAram > 20 || aram[0] - preAram < -20) {
                        position_2 = false;
                        tsec = 0;
                        if (position_3) {
                            ttsTest("哎呀  太可惜了  挑戰失敗 還要再挑戰嗎");
                            tsec = 0;
                            startflag = false;
                            position_1 = false;
                            position_2 = false;
                            position_3 = false;
                            mediaPlayer.stop();

                            mGameagainDialog(R.string.dialog_game_defeat, R.drawable.gif_record);

                        }
                    } else {

                    }
                }


            }


        }


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
                        case 4:
                            if (!position_1) {
                                ttsTest("非常棒啊 請向上爬升 到手臂覺得不舒服的時候停下");
                            }
                            break;

                        case 7:
                            if (!position_3) {
                                ttsTest("很好喔！ 開始放音樂囉！ 二十秒後才可以放下");
                                position_3 = true;

                                txt_tur.setText("二十秒後才可以放下");
                                if (handNumber == 0) {

                                    gif_tur.setImageResource(R.drawable.game3_l4);

                                } else if (handNumber == 1) {
                                    gif_tur.setImageResource(R.drawable.game3_1_l4);


                                } else if (handNumber == 2) {
                                    gif_tur.setImageResource(R.drawable.game3_r4);


                                } else if (handNumber == 3) {
                                    gif_tur.setImageResource(R.drawable.game3_1_r4);

                                }


                                tsec = 0;
                                mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.gamebackground1);
                                mediaPlayer.setLooping(true);
                                mediaPlayer.start();
                            }


                            break;
                        case 20:
                            score[handNumber] = String.valueOf((int) aram[0]);
                            position_1 = false;
                            position_2 = false;
                            position_3 = false;
                            mediaPlayer.stop();
                            handState[handNumber] = true;
                            handNumber++;


                            if (handNumber == 1) {
                                ttsTest("您成功拉 可以慢慢放下了 換姿勢囉 請跟著畫面姿勢移動");
                                tsec = 0;
                                startflag = false;
                                mGameDialog(R.string.dialog_game_title3, R.drawable.game3_l2);
                            }
                            if (handNumber == 2) {
                                ttsTest("您成功拉 可以慢慢放下了 換右手囉 請跟著畫面姿勢移動");
                                tsec = 0;
                                startflag = false;
                                mGameDialog(R.string.dialog_game_title3, R.drawable.game3_r1);
                            }
                            if (handNumber == 3) {
                                ttsTest("您成功拉 可以慢慢放下了 換姿勢囉 請跟著畫面姿勢移動");
                                tsec = 0;
                                startflag = false;
                                mGameDialog(R.string.dialog_game_title3, R.drawable.game3_r2);
                            }
                            if (handNumber == 4) {
                                ttsTest("太厲害了！ 今天的你已經完成全部的伸展運動拉 右手可以慢慢放下了 還須要再次挑戰嗎");
                                tsec = 0;
                                startflag = false;
                                handNumber = 0;
                                updateDatabase();
//                                mGameagainDialog(R.string.dialog_game_restart, R.drawable.gif_record);
                                mGameDialogChoiceHand(R.string.dialog_game_restart);
                            }
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
