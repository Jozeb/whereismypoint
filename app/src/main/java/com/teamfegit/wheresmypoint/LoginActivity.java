package com.teamfegit.wheresmypoint;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        final EditText edit_nuid = (EditText) findViewById(R.id.edit_nuid);
        final EditText edit_password = (EditText) findViewById(R.id.edit_password);

        final Button btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nuid = edit_nuid.getText().toString();
                String pass = edit_password.getText().toString();
                btn_login.setText("Logging in");
                btn_login.setEnabled(false);
                new LoginTask().execute(nuid, pass);
            }
        });

    }

    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(HelperClass.HTTP_LOGIN);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setConnectTimeout(1000*5); // Timeout of 5 second
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);


                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));


                String data = URLEncoder.encode("nuid", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                        URLEncoder.encode("pass", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8");


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
        protected void onPostExecute(String jsonString) {

            JSONObject jsonRootObject;
            try {
                jsonRootObject = new JSONObject(jsonString);
             //   JSONArray jsonArray = jsonRootObject.optJSONArray(HelperClass.JSON_TAG_USER);

                if (jsonRootObject.optInt("success") == 1) {
                    String nuid = jsonRootObject.optString("nuid");
                    String point_number = jsonRootObject.optString("point_number");
                    String phone_number = jsonRootObject.optString("phone_number");
                    String home_location = jsonRootObject.optString("home_location");

                    UserData.SetData(LoginActivity.this, nuid, phone_number, point_number, home_location);
                  //  Log.d("jozeb", "adding user " + nuid);

                    finish();
                }else{

                    Toast.makeText(LoginActivity.this, "ID or Passwords do not match", Toast.LENGTH_LONG).show();
                    final Button btn_login = (Button) findViewById(R.id.btn_login);
                    btn_login.setText("Login");
                    btn_login.setEnabled(true);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
