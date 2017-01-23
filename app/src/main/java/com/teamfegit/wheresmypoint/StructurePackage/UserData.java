package com.teamfegit.wheresmypoint.StructurePackage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Eustace on 13-Oct-16.
 */

public class UserData {

    private String nuid;
    private String phoneno;
    private String pointno;
    private String homelocation;


    private static final String PREF_USER = "userpref";
    private static final String USER_NUID = "nuid";
    private static final String USER_PHONENUMBER = "usercellno";
    private static final String USER_POINTNUMBER = "pointnumber";
    private static final String USER_HOMELOCATION = "homelocation";
    private static final String USER_ISLOGGEDIN= "loggedin";

    public String getNuid() {
        return nuid;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public String getPointno() {
        return pointno;
    }

    public String getHomelocation() {
        return homelocation;
    }

    public UserData(Context context){

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE);
        nuid = sharedPreferences.getString(USER_NUID,"null");
        phoneno = sharedPreferences.getString(USER_PHONENUMBER,"null");
        pointno = sharedPreferences.getString(USER_POINTNUMBER,"null");
        homelocation = sharedPreferences.getString(USER_HOMELOCATION,"null");

        

    }



    public static void SetData(Context context, String nuid, String phoneno, String pointno, String homelocation) {

        Log.d("Jozeb","UserData -> SetData: Nuid: " + nuid);
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(USER_NUID, nuid);
        editor.putString(USER_PHONENUMBER, phoneno);
        editor.putString(USER_POINTNUMBER, pointno);
        editor.putString(USER_HOMELOCATION, homelocation);
        editor.putBoolean(USER_ISLOGGEDIN, true);

        editor.commit();
    }

    public static void clear(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static boolean isLoggedIn(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(USER_ISLOGGEDIN, false);
    }
}
