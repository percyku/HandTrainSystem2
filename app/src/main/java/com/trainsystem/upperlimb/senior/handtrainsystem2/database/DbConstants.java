package com.trainsystem.upperlimb.senior.handtrainsystem2.database;

import android.provider.BaseColumns;

public interface DbConstants extends BaseColumns {

    public static final String TABLE_SENIORUSER = "seniornumber";
    public static final String TABLE_SENIORSCORE = "seniorscore";


    public  static final String DATE="date";
    public  static final String USER="user";
    public  static final String GAME1="game1";
    public  static final String GAME2="game2";
    public  static final String GAME3="game3";
    public  static final String STATE="state";


    public static String creatTableNumber = "CREATE TABLE seniornumber" + "(" +
            "_id  	INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "date DATETIME NOT NULL , " +
            "user  TEXT )";

    public static String creatTableScore = "create table seniorscore(" +
            "_id  	INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "date DATETIME NOT NULL , " +
            "user	TEXT," +
            "game1	TEXT," +
            "game2	TEXT," +
            "game3	TEXT," +
            "state  TEXT )";


}
