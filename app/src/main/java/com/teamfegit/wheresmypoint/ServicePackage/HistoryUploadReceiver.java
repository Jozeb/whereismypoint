package com.teamfegit.wheresmypoint.ServicePackage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class HistoryUploadReceiver extends BroadcastReceiver {
    public HistoryUploadReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Intent intent1 = new Intent(context,UploadHistoryService.class);
        context.startService(intent1);
        Log.d("Jozeb","HistoryUploadReceiver -> onReceive");
    }
}
