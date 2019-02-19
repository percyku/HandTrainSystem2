package com.trainsystem.upperlimb.senior.handtrainsystem2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by percyku on 2017/5/4.
 */

public class DatabaseHelper extends SQLiteOpenHelper {


    private static String dbName = "SeniorDB";

    private static int version = 1;


    private Context context;

    private String table;


    public DatabaseHelper(Context context,String table) {
        super(context, dbName, null, version);
        this.context=context;
        this.table=table;

    }



    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(creatTableNumber);
        Log.e("OnCreate","1");
        db.execSQL(table);
        Log.e("OnCreate","2");
        Toast.makeText(this.context,"table created",Toast.LENGTH_LONG).show();
        Log.e("OnCreate","3");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

//        db.execSQL("DROP TABLE IF EXISTS seniornumber")
        Log.e("onUpgrade","4");
        db.execSQL("DROP TABLE IF EXISTS seniorscore");
        Log.e("onUpgrade","5");
        onCreate(db);
        Log.e("onUpgrade","6");

    }
}
