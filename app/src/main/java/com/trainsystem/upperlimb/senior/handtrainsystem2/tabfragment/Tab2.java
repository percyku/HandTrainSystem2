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

public class Tab2 extends Fragment {


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
                        sb=c.getString(c.getColumnIndex(DbConstants.GAME2)).split(",");

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
        mView = inflater.inflate(R.layout.tabgame2, container, false);
        textView1= (TextView) mView.findViewById(R.id.textView1);
        textView1.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"fonts/AdobeFanHeitiStd-Bold.otf"));
        game_Left= (TextView) mView.findViewById(R.id.game_left);
        game_Left.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"fonts/AdobeFanHeitiStd-Bold.otf"));
        game_Right= (TextView) mView.findViewById(R.id.game_right);
        game_Right.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"fonts/AdobeFanHeitiStd-Bold.otf"));
//        game_Right.setText("右手得分："+sb[2]+"，"+"失誤："+sb[3]);
//        game_Left.setText("左手得分："+sb[0]+"，"+"失誤："+sb[1]);


        if (sb[0].equals("null") || sb[1].equals("null") || sb[1].equals("null") || sb[3].equals("null")) {
            game_Left.setText("左手得分：" + 0 + "，" + "失誤：" + 0);
            game_Right.setText("右手得分：" + 0 + "，" + "失誤：" + 0);
        } else {
            game_Left.setText("左手得分：" + sb[0] + "，" + "失誤：" + sb[1]);
            game_Right.setText("右手得分：" + sb[2] + "，" + "失誤：" + sb[3]);

        }

            return mView;
    }

}
