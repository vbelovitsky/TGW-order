package com.java.boris.tgw;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TGWDB";

    public static final String TABLE_CATEGORY = "categories";
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "_name";
    public static final String KEY_VALUE = "_value";
    public static final String KEY_COLOR = "_color";

    private int[] categoryIds = {1, 2, 3, 4, 5};
    private String[] categoryNames = {"Здоровье", "Учеба", "Отношения", "Работа", "Хобби"};
    private int[] categoryValues = {5, 5, 5, 5, 5};
    private String[] categoryColors = {"#89EE1E", "#1EEEEE", "#E53FF8", "#817A83", "#FFEB3B"};



    public static final String TABLE_GOALS = "goals";
    public static final String KEY_GOALS_ID = "_goalid";
    public static final String KEY_GOALS_TEXT = "_goaltext";
    public static final String KEY_GOALS_COMPLETED = "_goalcompleted";
    public static final String KEY_GOALS_CATEGORY = "_category";


    public static final String CREATE_CATEGORY_TABLE = "create table " + TABLE_CATEGORY + "(" +
            KEY_ID + " integer primary key, " +
            KEY_NAME + " text, " +
            KEY_VALUE + " integer, " +
            KEY_COLOR + " text" + ")";

    public static final String CREATE_GOAL_TABLE = "create table " + TABLE_GOALS + "(" +
            KEY_GOALS_ID + " integer primary key, " +
            KEY_GOALS_TEXT + " text, " +
            KEY_GOALS_COMPLETED + " integer, " +
            KEY_GOALS_CATEGORY + " integer" + ")";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_GOAL_TABLE);


        ContentValues contentValues = new ContentValues();
        for(int i = 0; i < categoryIds.length; i++) {
            contentValues.clear();
            contentValues.put(KEY_ID, categoryIds[i]);
            contentValues.put(KEY_NAME, categoryNames[i]);
            contentValues.put(KEY_VALUE, categoryValues[i]);
            contentValues.put(KEY_COLOR, categoryColors[i]);
            db.insert(TABLE_CATEGORY, null, contentValues);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_CATEGORY);
        db.execSQL("drop table if exists " + TABLE_GOALS);

        onCreate(db);
    }
}
