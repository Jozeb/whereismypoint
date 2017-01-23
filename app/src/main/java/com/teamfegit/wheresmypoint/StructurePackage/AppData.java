package com.teamfegit.wheresmypoint.StructurePackage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Eustace on 11-Dec-16.
 */

public class AppData {
    private int DRIVER_VERSION;
    private long LOCATION_UPDATE_TIME;
    private long LOCATION_UPDATE_DISTANCE;

    private static final String PREF_APP = "apppref";
    private static final String PREF_DRIVER_VERSION = "name";
    private static final String PREF_LOCATION_UPDATE_TIME = "phoneno";
    private static final String PREF_LOCATION_UPDATE_DISTANCE = "pointnumber";


    public int getDRIVER_VERSION() {
        return DRIVER_VERSION;
    }

    public long getLOCATION_UPDATE_TIME() {
        return LOCATION_UPDATE_TIME;
    }

    public long getLOCATION_UPDATE_DISTANCE() {
        return LOCATION_UPDATE_DISTANCE;
    }

    public AppData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE);
        DRIVER_VERSION = sharedPreferences.getInt(PREF_DRIVER_VERSION,HelperClass.DRIVER_VERSION);
        LOCATION_UPDATE_TIME = sharedPreferences.getLong(PREF_LOCATION_UPDATE_TIME,HelperClass.LOCATION_UPDATE_TIME);
        LOCATION_UPDATE_DISTANCE = sharedPreferences.getLong(PREF_LOCATION_UPDATE_DISTANCE,HelperClass.LOCATION_UPDATE_DISTANCE);

    }

    public static void setData(Context context, int driver_version, long update_time, long update_distance){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(PREF_DRIVER_VERSION,driver_version);
        editor.putLong(PREF_LOCATION_UPDATE_TIME,update_time);
        editor.putLong(PREF_LOCATION_UPDATE_DISTANCE,update_distance);

        editor.apply();
    }

    public static void setDriverVersion(Context context, int driverVersion){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_DRIVER_VERSION,driverVersion);
        editor.apply();
    }

    public static void setUpdatePeference(Context context, long updateTime, long updateDistance){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(PREF_LOCATION_UPDATE_TIME,updateTime);
        editor.putLong(PREF_LOCATION_UPDATE_DISTANCE,updateDistance);
        editor.apply();
    }


    public static void clear(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}
