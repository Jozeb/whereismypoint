package com.teamfegit.wheresmypoint;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.teamfegit.wheresmypoint.DatabasePackage.DbSource;
import com.teamfegit.wheresmypoint.ServicePackage.UpdateCurrentLocationService;
import com.teamfegit.wheresmypoint.ServicePackage.UploadHistoryService;
import com.teamfegit.wheresmypoint.StructurePackage.HelperClass;
import com.teamfegit.wheresmypoint.StructurePackage.UserData;

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

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        LinearLayout linear_back = (LinearLayout) findViewById(R.id.linear_back);
        linear_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button btn_logout = (Button) findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserData userData = new UserData(SettingsActivity.this);
                new LogoutTask().execute(userData.getNuid());


            }
        });

        Button btn_friends = (Button) findViewById(R.id.btn_friends);
        btn_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, FriendsActivity.class);
                startActivity(intent);

            }
        });

        Button btn_editProfile = (Button) findViewById(R.id.btn_edit_profile);
        btn_editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, EditProfileActivity.class);
                startActivity(intent);

            }
        });


    }

    private class LogoutTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(HelperClass.HTTP_LOGOUT);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setConnectTimeout(1000 * 5); // Timeout of 5 second
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);


                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));


                String data = URLEncoder.encode("nuid", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");


                bufferedWriter.write(data);
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
        protected void onPostExecute(String s) {

            if (s.compareTo(" success") == 0) {

                DbSource database = new DbSource(SettingsActivity.this);
                database.clearFriends();
                database.close();

                UserData.clear(SettingsActivity.this);
                finish();

                Intent stopServiceIntent = new Intent(SettingsActivity.this, UpdateCurrentLocationService.class);
                stopService(stopServiceIntent);
                stopServiceIntent = new Intent(SettingsActivity.this, UploadHistoryService.class);
                stopService(stopServiceIntent);

                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);


            } else {
                Toast.makeText(SettingsActivity.this, "Network Error Occured", Toast.LENGTH_SHORT).show();
            }


        }
    }


}
