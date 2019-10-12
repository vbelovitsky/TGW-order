package com.java.boris.tgw;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TGWDB";

    public static final String CATEGORY_TABLE = "categories";
    public static final String GOAL_TABLE = "goals";

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "_name";
    public static final String KEY_VALUE = "_value";
    public static final String KEY_COLOR = "_color";



    public DBHelper(Context context, String name, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CATEGORY_TABLE + "(" + KEY_ID + " integer primary key, " +
                KEY_NAME + " text, " +
                KEY_VALUE + " integer, " +
                KEY_COLOR + " string" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + CATEGORY_TABLE);

        onCreate(db);
    }
}
