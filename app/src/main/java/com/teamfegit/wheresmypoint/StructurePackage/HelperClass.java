package com.teamfegit.wheresmypoint.StructurePackage;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.teamfegit.wheresmypoint.R;

/**
 * Created by Eustace on 17-Oct-16.
 */

public class HelperClass {

    public static final String JSON_TAG_DRIVER = "driver";
    private static String IP = "http://test-resolve.rhcloud.com/whereispoint/";
    public static final String HTTP_EDIT_PROFILE = IP + "edit_profile.php";
    public static String HTTP_LOGIN = IP + "db_login.php";
    public static String HTTP_REGISTER = IP + "signup.php";
    public static String HTTP_LOGOUT = IP + "logout.php";
    public static final String HTTP_GET_DRIVER = IP +"get_driver.php";
    public static final String HTTP_GET_UPDATE_INFO = IP +"get_update_info.php";
    public static String HTTP_UPDATE_CURRENT_LOCATION = IP + "update_current_location.php";
    public static String HTTP_CURRENT_POINT = IP + "get_current_point.php";
    public static String HTTP_UPLOAD_HISTORY = IP + "upload_history.php";
    public static String HTTP_GET_HISTORY = IP + "get_history.php";
    public static String JSON_TAG_USER = "user";

    public static final String BROADCAST_LOCATION_STATE = "com.teamfegit.whereispoint.LOCATIONSTATE";
    public static String PREF_FIRSTRUN = "firstrun";
    public static String INTENT_POINT_NUMBER = "pointno";


    public static final int APP_VERSION = 4;
    public static int DRIVER_VERSION = 0;
    public static int LOCATION_UPDATE_TIME = 1000*15;
    public static int LOCATION_UPDATE_DISTANCE = 1000;



    public static void notification(int id, String title, String message, Context context) {
        Log.d("jozeb", "notifcation");
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);


        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_directions_bus_black_24dp);

        //Intent resultIntent = new Intent(this, ViewEventsActivity.class);
        //PendingIntent resultPendingIntent = PendingIntent.getActivity(ContextAwareService.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //mBuilder.setContentIntent(resultPendingIntent);

        Notification notification = mBuilder.build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
    }
}
