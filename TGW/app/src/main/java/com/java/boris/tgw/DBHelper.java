package com.java.boris.tgw;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

// Класс для работы с базой данных
public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TGWDB";

    public static final String TABLE_CATEGORY = "categories";
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "_name";
    public static final String KEY_VALUE = "_value";
    public static final String KEY_COLOR = "_color";

    private int[] categoryIds = {1, 2, 3, 4, 5, 6};
    private String[] categoryNames = {"Любовь", "Благосостояние", "Хобби", "Друзья", "Работа", "Здоровье"};
    private int[] categoryValues = {5, 5, 5, 5, 5, 5};
    private String[] categoryColors = {"#E53FF8", "#1EEEEE", "#FFEB3B", "#FF8400", "#817A83", "#89EE1E"};



    public static final String TABLE_GOALS = "goals";
    public static final String KEY_GOALS_ID = "_goalid";
    public static final String KEY_GOALS_TEXT = "_goaltext";
    public static final String KEY_GOALS_COMPLETED = "_goalcompleted";
    public static final String KEY_GOALS_CATEGORY = "_category";

    public static final String TABLE_STATISTICS = "statistics";
    public static final String KEY_STATISTICS_ID = "_statisticsid";
    public static final String KEY_STATISTICS_TIME = "_statisticstime";
    public static final String KEY_STATISTICS_CATEGORIES = "_statisticscategories";
    public static final String KEY_STATISTICS_VALUES = "_statisticsvalues";
    public static final String KEY_STATISTICS_COLORS = "_statisticscolors";



    public static final String CREATE_CATEGORY_TABLE = "create table " + TABLE_CATEGORY + "(" +
            KEY_ID + " integer primary key, " +
            KEY_NAME + " text, " +
            KEY_VALUE + " integer, " +
            KEY_COLOR + " text" + ")";

    public static final String CREATE_GOAL_TABLE = "create table " + TABLE_GOALS + "(" +
            KEY_GOALS_ID + " integer primary key, " +
            KEY_GOALS_TEXT + " text, " +
            KEY_GOALS_COMPLETED + " integer, " +
            KEY_GOALS_CATEGORY + " integer, " +
            "foreign key(" + KEY_GOALS_CATEGORY + ") REFERENCES " + TABLE_CATEGORY + "(" + KEY_ID + ")" +
            ")";

    public static final String CREATE_STATISTICS_TABLE = "create table " + TABLE_STATISTICS + "(" +
            KEY_STATISTICS_ID + " integer primary key, " +
            KEY_STATISTICS_TIME + " text, " +
            KEY_STATISTICS_CATEGORIES + " text, " +
            KEY_STATISTICS_VALUES + " text, " +
            KEY_STATISTICS_COLORS + " text" + ")";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_GOAL_TABLE);
        db.execSQL(CREATE_STATISTICS_TABLE);


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
        db.execSQL("drop table if exists " + TABLE_STATISTICS);

        fillStatistics(db);

        onCreate(db);
    }

    // Метод для сброса статистики
    public void dropStatistics(SQLiteDatabase db){
        db.execSQL("drop table if exists " + TABLE_STATISTICS);
        db.execSQL(CREATE_STATISTICS_TABLE);

        fillStatistics(db);

    }

    // Заполняем статистику по умолчанию
    private void fillStatistics(SQLiteDatabase db){
        ContentValues contentValues = new ContentValues();
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm/ss");
        contentValues.put(DBHelper.KEY_STATISTICS_TIME, simpleDateFormat.format(date));
        contentValues.put(DBHelper.KEY_STATISTICS_CATEGORIES, joinArray(categoryNames));
        contentValues.put(DBHelper.KEY_STATISTICS_VALUES, joinArray(categoryValues));
        contentValues.put(DBHelper.KEY_STATISTICS_COLORS, joinArray(categoryColors));

        db.insert(DBHelper.TABLE_STATISTICS, null, contentValues);
    }

    private String joinArray(String[] array){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < array.length; i++){
            if(i != array.length - 1){
                stringBuilder.append(array[i]).append("!");
            }
            else{
                stringBuilder.append(array[i]);
            }
        }
        return stringBuilder.toString();
    }

    private String joinArray(int[] array){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < array.length; i++){
            if(i != array.length - 1){
                stringBuilder.append(array[i]).append("!");
            }
            else{
                stringBuilder.append(array[i]);
            }
        }
        return stringBuilder.toString();
    }
}
