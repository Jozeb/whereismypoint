package com.teamfegit.wheresmypoint;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.teamfegit.wheresmypoint.StructurePackage.HelperClass;
import com.teamfegit.wheresmypoint.StructurePackage.UserData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import static com.teamfegit.wheresmypoint.R.id.btn_register;
import static com.teamfegit.wheresmypoint.R.id.edit_nuid;

public class EditProfileActivity extends Activity {

    private static final int REQUEST_CODE_YOUR = 123;
    TextView txtv_addLocation;
    Button btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        final UserData userData = new UserData(this);

        TextView textView = (TextView) findViewById(edit_nuid);
        textView.setText(userData.getNuid());


        final EditText edit_pass = (EditText) findViewById(R.id.edit_password);
        final EditText edit_repass = (EditText) findViewById(R.id.edit_re_password);
        final EditText edit_phonenumber = (EditText) findViewById(R.id.edit_phonenumber);
        final Spinner spin_pointnumber = (Spinner) findViewById(R.id.spin_pointnumber);
        txtv_addLocation = (TextView) findViewById(R.id.text_home_location);


        edit_phonenumber.setText(userData.getPhoneno());
        spin_pointnumber.setSelection(Integer.parseInt(userData.getPointno()) - 1);
        txtv_addLocation.setText(userData.getHomelocation());

        txtv_addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, MapsActivity.class);
                startActivityForResult(intent, REQUEST_CODE_YOUR);
            }
        });


        btn_register = (Button) findViewById(R.id.btn_save);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String pass = edit_pass.getText().toString();
                String repass = edit_repass.getText().toString();
                String phonenumber = edit_phonenumber.getText().toString();
                String point_number = (String) spin_pointnumber.getSelectedItem();
                String home_location = txtv_addLocation.getText().toString();


                if (pass.compareTo(repass) != 0) {
                    edit_repass.setError("Password Mismatch");
                    return;
                }


                if (phonenumber.length() != 11) {
					 if(phonenumber.length()!=0){
						edit_phonenumber.setError("Should be 11 characters long");
                    return;
                    }
                    
                    return;
                }
                if (pass.length() < 3) {
                    edit_pass.setError("Minimum Password length is 4");
                    return;
                }


                btn_register.setText("Wait");
                btn_register.setEnabled(false);
                new EditProfileTask().execute(userData.getNuid(), pass, phonenumber, point_number, home_location);

            }
        });


    }

    private class EditProfileTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(HelperClass.HTTP_EDIT_PROFILE);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);


                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));


                String data = URLEncoder.encode("nuid", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") + "&" +
                        URLEncoder.encode("phone_number", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8") + "&" +
                        URLEncoder.encode("point_number", "UTF-8") + "=" + URLEncoder.encode(params[3], "UTF-8") + "&" +
                        URLEncoder.encode("home_location", "UTF-8") + "=" + URLEncoder.encode(params[4], "UTF-8");


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
            if (s.compareTo("n") == 0) {
                btn_register.setEnabled(true);
                btn_register.setText("Retry");
                Toast.makeText(EditProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(EditProfileActivity.this, "Profile Updated!", Toast.LENGTH_SHORT).show();
				
				//Todo: Update UserData
                finish();
            }
            super.onPostExecute(s);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_YOUR) {

            if (resultCode == Activity.RESULT_OK) {

                String latitude = data.getStringExtra("lat");
                String longitude = data.getStringExtra("lng");
                String GPS = latitude + "," + longitude;
                txtv_addLocation.setText(GPS);
                //           isLocationReady = true;
            }

        }
    }
}
