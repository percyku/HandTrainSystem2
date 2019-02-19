package com.trainsystem.upperlimb.senior.handtrainsystem2.tabfragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trainsystem.upperlimb.senior.handtrainsystem.R;
import com.trainsystem.upperlimb.senior.handtrainsystem2.database.DatabaseHelper;
import com.trainsystem.upperlimb.senior.handtrainsystem2.database.DbConstants;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Belal on 2/3/2016.
 */

public class Tab3 extends Fragment {

    private View mView;

    private TextView game_Left,game_Right,textView1;


    private String sb[];

    DatabaseHelper manager;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = new DatabaseHelper(getContext(), DbConstants.creatTableScore);

        getData();

    }

    private void getData() {

        SQLiteDatabase db = manager.getReadableDatabase();
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + DbConstants.TABLE_SENIORSCORE, null);
            c.moveToFirst();
            Calendar mCal = Calendar.getInstance();
            String dateformat = "yyyyMMdd";
            SimpleDateFormat df = new SimpleDateFormat(dateformat);
            String today = df.format(mCal.getTime());
            if (c.moveToFirst()) {
                do {


                    if(c.getString(c.getColumnIndex(DbConstants.STATE)).equals("true")&&c.getString(c.getColumnIndex(DbConstants.DATE)).equals(today)){

//                        test=c.getString(c.getColumnIndex(DbConstants.GAME1));
                        sb=c.getString(c.getColumnIndex(DbConstants.GAME3)).split(",");

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.tabgame3, container, false);
        textView1= (TextView) mView.findViewById(R.id.textView1);
        textView1.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"fonts/AdobeFanHeitiStd-Bold.otf"));
        game_Left= (TextView) mView.findViewById(R.id.game_left);
        game_Left.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"fonts/AdobeFanHeitiStd-Bold.otf"));
//        game_Left.setText("左手側面："+sb[0]+"度"+"，"+"正面："+sb[1]+"度");
        game_Right= (TextView) mView.findViewById(R.id.game_right);
        game_Right.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"fonts/AdobeFanHeitiStd-Bold.otf"));
//        game_Right.setText("右手側面："+sb[2]+"度"+"，"+"正面："+sb[3]+"度");


//        if (sb[0].equals("null") && sb[1].equals("null") && sb[1].equals("null") && sb[3].equals("null")) {
//            game_Left.setText("左手側面：" + "未挑戰"+ "，" + "正面：" + "未挑戰");
//            game_Right.setText("右手側面：" + "未挑戰"+ "，" + "正面：" +"未挑戰");
//        } else {
//            game_Left.setText("左手側面：" + sb[0]+"度"+"，" + "正面：" + sb[1]+"度");
//            game_Right.setText("左手側面：" + sb[2]+"度"+ "，" + "正面：" + sb[3]+"度");

            String left="";
            String right="";
            if(sb[0].equals("null")){
                left=left+"左手側面：" + "未挑戰";
            }else{
                left=left+"左手側面：" + sb[0]+"度";
            }
            if(sb[1].equals("null")){
                left=left+ "正面：" + "未挑戰";
            }else{
                left=left+ "正面：" + sb[1]+"度";
            }


            if(sb[2].equals("null")){
                right=right+"右手側面：" + "未挑戰";
            }else{
                right=right+"右手側面：" + sb[2]+"度";
            }
            if(sb[3].equals("null")){
                right=right+ "正面：" + "未挑戰";
            }else{
                right=right+ "正面：" + sb[3]+"度";
            }

            game_Left.setText(left);

            game_Right.setText(right);

//        }
        return mView;
    }


}
