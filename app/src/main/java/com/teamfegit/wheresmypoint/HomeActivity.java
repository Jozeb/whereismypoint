package com.teamfegit.wheresmypoint;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.teamfegit.wheresmypoint.ServicePackage.CurrentLocationReceiver;
import com.teamfegit.wheresmypoint.ServicePackage.HistoryUploadReceiver;
import com.teamfegit.wheresmypoint.ServicePackage.StopCurrentLocationReceiver;
import com.teamfegit.wheresmypoint.ServicePackage.StopUpdateLocationService;
import com.teamfegit.wheresmypoint.ServicePackage.UpdateCurrentLocationService;
import com.teamfegit.wheresmypoint.ServicePackage.UploadHistoryService;
import com.teamfegit.wheresmypoint.StructurePackage.AppData;
import com.teamfegit.wheresmypoint.StructurePackage.HelperClass;
import com.teamfegit.wheresmypoint.StructurePackage.UserData;

import java.util.Calendar;

public class HomeActivity extends Activity {
    private AlarmManager alarmMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SetUpPointUpdatingService();
        SetUpHistoryUpdateService();


        TextView text_appVersion = (TextView) findViewById(R.id.text_appversion);
        text_appVersion.setText("V"+HelperClass.APP_VERSION);


        final Button btn_go = (Button) findViewById(R.id.btn_go);
        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_go.setText("going...");
                UserData userData = new UserData(HomeActivity.this);

                Intent intent = new Intent(HomeActivity.this, PointShowActivity.class);
                intent.putExtra(HelperClass.INTENT_POINT_NUMBER, userData.getPointno());
                startActivity(intent);
            }
        });

        LinearLayout lin_settings = (LinearLayout) findViewById(R.id.lin_settings);
        lin_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(i);

            }
        });


    }

    @Override
    protected void onResume() {

        final Button btn_go = (Button) findViewById(R.id.btn_go);
        btn_go.setText("Go!");
        super.onResume();
    }

    public void SetUpPointUpdatingService() {

        alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(this, CurrentLocationReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() - System.currentTimeMillis() < 0) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }


        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);


        calendar.set(Calendar.HOUR_OF_DAY, 8);

        Intent cancelAlarmIntent = new Intent(this, StopCurrentLocationReceiver.class);
        PendingIntent pendingCancelAlarmIntent = PendingIntent.getBroadcast(this, 0, cancelAlarmIntent, 0);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingCancelAlarmIntent);
        //alarmMgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingCancelAlarmIntent);


    }

    public void SetUpHistoryUpdateService() {

        Intent intent = new Intent(this, HistoryUploadReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() - System.currentTimeMillis() < 0) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);


    }
}
