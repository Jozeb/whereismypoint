package com.teamfegit.wheresmypoint.ServicePackage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CurrentLocationReceiver extends BroadcastReceiver {
    public CurrentLocationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving

        Intent intent1 = new Intent(context,UpdateCurrentLocationService.class);
        context.startService(intent1);
        Log.d("Jozeb","CurrentLocationReceiver -> onReceive");
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
