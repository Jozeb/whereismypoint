package com.teamfegit.wheresmypoint.ServicePackage;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.teamfegit.wheresmypoint.DatabasePackage.DbSource;
import com.teamfegit.wheresmypoint.StructurePackage.HelperClass;
import com.teamfegit.wheresmypoint.StructurePackage.HistoryClass;

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
import java.util.ArrayList;
import java.util.List;

public class UploadHistoryService extends Service {

    DbSource database;
    ArrayList<HistoryClass> list;
    private static int ERROR = 1;
    private static int SUCCESS = 0;

    public UploadHistoryService() {
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("Jozeb", "UploadHistoryService: onStartCommad");
        database = new DbSource(this);
        list = database.get_history_toupload();
        Log.d("Jozeb", "List size: " + list.size());

        if (isOnline(this)) {

            Log.d("Jozeb", "UploadHistoryService: onStartCommand -> Connected to Internet");
            new UploadHistoryTask().execute();
        } else {
            //HelperClass.notification(3,"Where is my Point","Cannot upload today's history. Make sure you are connected to Internet",this);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());

    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public class UploadHistoryTask extends AsyncTask<Void, Void, String> {

        public int uploadHistory(int start, int length) {
            JSONArray jsonArray = new JSONArray();
            JSONObject pointObject = new JSONObject();

            try {

                List<HistoryClass> divisionList = list.subList(start, start + length);
                start += length;

                for (HistoryClass hc : divisionList) {

                    JSONObject pointEntry = new JSONObject();
                    pointEntry.put("nuid", hc.nuid);
                    pointEntry.put("pointno", hc.pointno);
                    pointEntry.put("location", hc.location);
                    pointEntry.put("time", hc.time);

                    jsonArray.put(pointEntry);

                }

                pointObject.put("points", jsonArray);
                Log.d("Jozeb", "Json: " + pointObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                return ERROR;
            }


            try {
                URL url = new URL(HelperClass.HTTP_UPLOAD_HISTORY);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);


                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));


                //String data = URLEncoder.encode("Shop", "UTF-8") + "=" + URLEncoder.encode(shopObject.toString(), "UTF-8");


                bufferedWriter.write(pointObject.toString());
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

                Log.d("Jozeb", "Http Response: " + response);


                httpURLConnection.disconnect();
                //return response;


            } catch (Exception e) {
                e.printStackTrace();
                return ERROR;

            }

            return SUCCESS;

        }

        @Override
        protected String doInBackground(Void... params) {

            int MAXSIZE = list.size();
            int startIterator = 0;

            boolean flag_EVERYTHINGISGOOD = true;
            while (MAXSIZE - startIterator >= 45) {

                if (uploadHistory(startIterator, 45) == ERROR) {
                    flag_EVERYTHINGISGOOD = false;
                }
                startIterator += 45;

            }

            if (MAXSIZE - startIterator > 0) {
                if (uploadHistory(startIterator, MAXSIZE - startIterator) == ERROR) {
                    flag_EVERYTHINGISGOOD = false;
                }
            }

            if (flag_EVERYTHINGISGOOD) {
                database.delete_locations();
            }
            return "success";
        }
    }

}
