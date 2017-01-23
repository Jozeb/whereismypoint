package com.teamfegit.wheresmypoint.DatabasePackage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.teamfegit.wheresmypoint.StructurePackage.DriverClass;
import com.teamfegit.wheresmypoint.StructurePackage.FriendsClass;
import com.teamfegit.wheresmypoint.StructurePackage.HistoryClass;

import java.util.ArrayList;

/**
 * Created by Eustace on 01-Nov-16.
 */

public class DbSource {

    private SQLiteOpenHelper dbopenhelper;
    private SQLiteDatabase database;
    //private Context context;

    private String[] COLUMN_HISTORY = {
            DbOpenHelper.HISTORY_NUID,
            DbOpenHelper.HISTORY_POINT_NUMBER,
            DbOpenHelper.HISTORY_LOCATION,
            DbOpenHelper.HISTORY_TIME

    };

    private String[] COLUMN_FRIEND = {
            DbOpenHelper.FRIENDS_ID,
            DbOpenHelper.FRIENDS_NAME,
            DbOpenHelper.FRIENDS_NUMBER

    };

    private String[] COLUMN_DRIVER = {
            DbOpenHelper.DRIVER_NAME,
            DbOpenHelper.DRIVER_POINTNUMBER,
            DbOpenHelper.DRIVER_PHONENUMBER

    };

    public DbSource(Context context) {
        //this.context = context;
        dbopenhelper = new DbOpenHelper(context);

        open();
    }

    public void open() {
        database = dbopenhelper.getWritableDatabase();
    }

    public void close() {
        database.close();
    }

    public void add_current_location(String nuid, String pointno, String location) {
        ContentValues values = new ContentValues();
        values.put(DbOpenHelper.HISTORY_NUID, nuid);
        values.put(DbOpenHelper.HISTORY_POINT_NUMBER, pointno);
        values.put(DbOpenHelper.HISTORY_LOCATION, location);
        values.put(DbOpenHelper.HISTORY_IS_UPLOADED, 0);

        database.insert(DbOpenHelper.TABLE_HISTORY, null, values);

    }

    public void delete_locations() {
        database.delete(DbOpenHelper.TABLE_HISTORY, null, null);

    }

    public ArrayList<HistoryClass> get_history_toupload() {
        Cursor c = database.query(DbOpenHelper.TABLE_HISTORY, COLUMN_HISTORY, DbOpenHelper.HISTORY_IS_UPLOADED + " = 0", null, null, null, null);
        ArrayList<HistoryClass> toRet = new ArrayList<>();
        while (c.moveToNext()) {
            String nuid = c.getString(c.getColumnIndexOrThrow(DbOpenHelper.HISTORY_NUID));
            String pointnumber = c.getString(c.getColumnIndexOrThrow(DbOpenHelper.HISTORY_POINT_NUMBER));
            String time = c.getString(c.getColumnIndexOrThrow(DbOpenHelper.HISTORY_TIME));
            String location = c.getString(c.getColumnIndexOrThrow(DbOpenHelper.HISTORY_LOCATION));

            toRet.add(new HistoryClass(nuid, pointnumber, time, location));
        }
        c.close();

        return toRet;
    }

    public void addFriend(FriendsClass friend) {
        ContentValues values = new ContentValues();
        values.put(DbOpenHelper.FRIENDS_NAME, friend.name);
        values.put(DbOpenHelper.FRIENDS_NUMBER, friend.number);
        database.insert(DbOpenHelper.TABLE_FRIENDS, null, values);

    }

    public ArrayList<FriendsClass> getFriends() {

        Cursor c = database.query(DbOpenHelper.TABLE_FRIENDS, COLUMN_FRIEND, null, null, null, null, null);

        ArrayList<FriendsClass> toRet = new ArrayList<>();
        while (c.moveToNext()) {
            String name = c.getString(c.getColumnIndexOrThrow(DbOpenHelper.FRIENDS_NAME));
            String number = c.getString(c.getColumnIndexOrThrow(DbOpenHelper.FRIENDS_NUMBER));
            String id = c.getString(c.getColumnIndexOrThrow(DbOpenHelper.FRIENDS_ID));


            toRet.add(new FriendsClass(name, number, id));
        }
        c.close();

        return toRet;

    }

    public void clearFriends() {
        database.delete(DbOpenHelper.TABLE_FRIENDS, null, null);

    }

    public void deleteFriendwithID(String id) {
        database.delete(DbOpenHelper.TABLE_FRIENDS, DbOpenHelper.FRIENDS_ID + " =" + id, null);

    }

    public void deleteAllDriver(){
        database.delete(DbOpenHelper.TABLE_DRIVER, null, null);
    }

    public void addDriver(DriverClass driverClass) {

        ContentValues values = new ContentValues();
        values.put(DbOpenHelper.DRIVER_NAME, driverClass.name);
        values.put(DbOpenHelper.DRIVER_PHONENUMBER, driverClass.number);
        values.put(DbOpenHelper.DRIVER_POINTNUMBER, driverClass.pointno);

        database.insert(DbOpenHelper.TABLE_DRIVER, null, values);
    }

    public DriverClass getDriver(int pointnumber) {
        Cursor c = database.query(DbOpenHelper.TABLE_DRIVER, COLUMN_DRIVER, DbOpenHelper.DRIVER_POINTNUMBER + " = " + pointnumber, null, null, null, null);

        if (c.moveToNext()) {
            String name = c.getString(c.getColumnIndexOrThrow(DbOpenHelper.DRIVER_NAME));
            String cellnumber = c.getString(c.getColumnIndexOrThrow(DbOpenHelper.DRIVER_PHONENUMBER));
            c.close();
            return new DriverClass(name, cellnumber, pointnumber);

        }

        return null;
    }


}
