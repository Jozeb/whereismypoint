package com.teamfegit.wheresmypoint.ServicePackage;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class StopUpdateLocationService extends Service {
    public StopUpdateLocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {

        Log.d("Jozeb","StopUpdateLocationService -> onStartCommand");

        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, UpdateCurrentLocationService.class);
        PendingIntent alarmIntent = PendingIntent.getService(this, 0, intent, 0);

        alarmMgr.cancel(alarmIntent);
        alarmIntent.cancel();
        stopService(intent);

        return super.onStartCommand(i, flags, startId);
    }
}
