package com.teamfegit.wheresmypoint.DatabasePackage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Eustace on 01-Nov-16.
 */

public class DbOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "whereispoint.db";
    private static final int DATABASE_VERSION = 4;


    public static final String TABLE_HISTORY = "history";
    public static final String HISTORY_POINT_NUMBER = "pointno";
    public static final String HISTORY_NUID = "nuid";
    public static final String HISTORY_TIME = "time";
    public static final String HISTORY_LOCATION = "location";
    public static final String HISTORY_IS_UPLOADED = "uploaded";

    public static final String TABLE_FRIENDS = "friends";
    public static final String FRIENDS_ID = "_id";
    public static final String FRIENDS_NAME = "fname";
    public static final String FRIENDS_NUMBER = "fnumber";

    public static final String TABLE_DRIVER = "drivers";
    public static final String DRIVER_NAME = "name";
    public static final String DRIVER_PHONENUMBER = "contactno";
    public static final String DRIVER_POINTNUMBER = "pointno";


    public static final String CREATE_HISTORY = "CREATE TABLE IF NOT EXISTS " + TABLE_HISTORY + " (" +
            HISTORY_NUID + " varchar," +
            HISTORY_POINT_NUMBER + " varchar," +
            HISTORY_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            HISTORY_LOCATION + " varchar, " +
            HISTORY_IS_UPLOADED + " integer )";


    public static final String CREATE_FRIENDS = "CREATE TABLE IF NOT EXISTS " + TABLE_FRIENDS + " (" +
            FRIENDS_ID + " Integer PRIMARY KEY AUTOINCREMENT, " +
            FRIENDS_NAME + " varchar, " +
            FRIENDS_NUMBER + " varchar )";

    public static final String CREATE_DRIVER = "CREATE TABLE IF NOT EXISTS " + TABLE_DRIVER + " (" +
            DRIVER_NAME + " varchar, " +
            DRIVER_PHONENUMBER + " varchar, " +
            DRIVER_POINTNUMBER + " integer)";


    public DbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_HISTORY);
        db.execSQL(CREATE_FRIENDS);
        db.execSQL(CREATE_DRIVER);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion == 1 && newVersion == 2) {
            String UPDATE_QUERY = "Alter table " + TABLE_HISTORY + " add column " + HISTORY_NUID;
            db.execSQL(UPDATE_QUERY);

        } else if (oldVersion == 2 && newVersion == 3) {
            db.execSQL(CREATE_FRIENDS);
        } else if (oldVersion == 3 && newVersion == 4) {
            db.execSQL(CREATE_DRIVER);
        }
    }
}
