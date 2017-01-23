package com.teamfegit.wheresmypoint;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crittercism.app.Crittercism;
import com.teamfegit.wheresmypoint.DatabasePackage.DbSource;
import com.teamfegit.wheresmypoint.ServicePackage.UpdateCurrentLocationService;
import com.teamfegit.wheresmypoint.StructurePackage.AppData;
import com.teamfegit.wheresmypoint.StructurePackage.DriverClass;
import com.teamfegit.wheresmypoint.StructurePackage.HelperClass;
import com.teamfegit.wheresmypoint.StructurePackage.UserData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends Activity {

    private static int RESULT_LOCATION_UPDATE = 123;
    private static int CURRENT_DRIVER_VERSION;
    private static int NEW_DRIVER_VERSION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Crittercism.initialize(getApplicationContext(), "febd3ac039b24d8499ccf721f0202ac000555300");
        final Spinner spin_pointnumber = (Spinner) findViewById(R.id.spin_pointnumber);


        TextView text_appVersion = (TextView) findViewById(R.id.text_appversion);
        text_appVersion.setText("V"+HelperClass.APP_VERSION);

        setUpBroadCastLocationUpdate();


        Button btn_go = (Button) findViewById(R.id.btn_go);
        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String point_number = (String) spin_pointnumber.getSelectedItem();
                Intent intent = new Intent(MainActivity.this, PointShowActivity.class);
                intent.putExtra(HelperClass.INTENT_POINT_NUMBER, point_number);
                startActivity(intent);
            }
        });

        Button btn_participate = (Button) findViewById(R.id.btn_participate);
        btn_participate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        Button btn_predict = (Button) findViewById(R.id.btn_predict);
        btn_predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String point_number = (String) spin_pointnumber.getSelectedItem();
                Intent intent = new Intent(MainActivity.this, PointPredictionActivity.class);
                intent.putExtra(HelperClass.INTENT_POINT_NUMBER, point_number);
                startActivity(intent);
            }
        });

        setUpAppUpdate();

    }

    public void setUpAppUpdate() {
        AppData appData = new AppData(this);
        CURRENT_DRIVER_VERSION = appData.getDRIVER_VERSION();
        new AppTask().execute();


    }


    public void setUpBroadCastLocationUpdate() {

        BroadcastReceiver LocationStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (HelperClass.BROADCAST_LOCATION_STATE.equals(intent.getAction())) {
                    turnOnLocation();


                }
            }
        };


        LocalBroadcastManager.getInstance(this).registerReceiver(LocationStateReceiver,
                new IntentFilter(HelperClass.BROADCAST_LOCATION_STATE));


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RESULT_LOCATION_UPDATE) {
            Log.e("Jozeb", "Result code: " + resultCode);
            //  if (resultCode ==RESULT_OK){
            Intent intent1 = new Intent(this, UpdateCurrentLocationService.class);
            startService(intent1);
            //  }


        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    public void turnOnLocation() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(800);// Vibrate for 800 milliseconds

            Toast.makeText(this, "Please turn on Location", Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), RESULT_LOCATION_UPDATE);


        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        onRestart();
    }

    @Override
    protected void onResume() {
        if (UserData.isLoggedIn(this)) {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            turnOnLocation();
            finish();

        } else {
            turnOnLocation();
        }
        super.onResume();
    }


    private class DriverTask extends AsyncTask<String, Void, String> {

        Context context;

        DriverTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(HelperClass.HTTP_GET_DRIVER);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setConnectTimeout(1000 * 5); // Timeout of 5 second
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);


                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String response = "";
                String line = "";

                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                Log.i("Jozeb", "Driver:" + response + "");
                return response;

            } catch (Exception e) {

                e.printStackTrace();

            }

            return "n";
        }

        @Override
        protected void onPostExecute(String jsonString) {

            JSONObject jsonRootObject;
            try {
                jsonRootObject = new JSONObject(jsonString);
                JSONArray jsonArray = jsonRootObject.optJSONArray(HelperClass.JSON_TAG_DRIVER);

                if (jsonRootObject.optInt("success") == 1) {

                    DbSource database = new DbSource(context);
                    database.deleteAllDriver();
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jobject = jsonArray.getJSONObject(i);
                        String name = jobject.optString("name");
                        int point_number = jobject.optInt("point_number");
                        String cellnumber = jobject.optString("cellnumber");

                        database.addDriver(new DriverClass(name, cellnumber, point_number));
                    }

                    database.close(); //Close database

                    SharedPreferences sharedPreferences = getSharedPreferences(HelperClass.PREF_FIRSTRUN, MODE_PRIVATE); //Change FirstRun shared preferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(HelperClass.PREF_FIRSTRUN, false);
                    editor.apply();
                    Toast.makeText(context, "Drivers Loaded", Toast.LENGTH_SHORT).show();

                    AppData.setDriverVersion(context, NEW_DRIVER_VERSION);
                    //  Log.d("jozeb", "adding user " + nuid);


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private class AppTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(HelperClass.HTTP_GET_UPDATE_INFO);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setConnectTimeout(1000 * 5); // Timeout of 5 second
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);


                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String response = "";
                String line = "";

                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                Log.i("Akhtar3", response + "");
                return response;

            } catch (Exception e) {

                e.printStackTrace();

            }

            return "n";
        }

        @Override
        protected void onPostExecute(String jsonString) {

            JSONObject jsonRootObject;
            try {
                jsonRootObject = new JSONObject(jsonString);

                int version = jsonRootObject.optInt("version");
                int driver_version = jsonRootObject.optInt("driver_version");
                long update_time = jsonRootObject.optLong("update_time");
                long update_distance = jsonRootObject.optLong("update_distance");


                if(version > HelperClass.APP_VERSION){
                    app_update_notification(456,"Where is my Point","Update available",MainActivity.this);
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(200);// Vibrate for 200 milliseconds
                    v.vibrate(100);// Vibrate for 100 milliseconds
                    v.vibrate(200);// Vibrate for 200 milliseconds

                }

                if (driver_version > CURRENT_DRIVER_VERSION) { //update driver information if they are outdated
                    NEW_DRIVER_VERSION = driver_version;
                    new DriverTask(MainActivity.this).execute();

                }

                AppData.setUpdatePeference(MainActivity.this, update_time, update_distance);



            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


        public void app_update_notification(int id, String title, String message, Context context) {
            Log.d("jozeb", "notifcation");
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

            String url = "https://drive.google.com/file/d/0B1aK1nbmkP_3LVFPV0dtTHdrc1E/view?usp=sharing";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            //startActivity(i);

            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,i,0);



            mBuilder.setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_directions_bus_black_24dp)
                    .setContentIntent(pendingIntent);


            Notification notification = mBuilder.build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(id, notification);
        }
    }


}
