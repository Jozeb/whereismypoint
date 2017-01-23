package com.teamfegit.wheresmypoint;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.teamfegit.wheresmypoint.StructurePackage.HelperClass;
import com.teamfegit.wheresmypoint.StructurePackage.PredictionClass;

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
import java.util.ArrayList;

public class PointPredictionActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private String POINT_NUMBER;

    ArrayList<PredictionClass> arrayList;

    EditText text_hours;
    EditText text_mins;

    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_prediction);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        text_hours = (EditText) findViewById(R.id.text_hours);
        text_mins = (EditText) findViewById(R.id.text_mins);


        Bundle bundle = getIntent().getExtras();
        POINT_NUMBER = bundle.getString(HelperClass.INTENT_POINT_NUMBER);
        Log.d("Jozeb", "point number: " + POINT_NUMBER);
        new GetHistoryTask().execute(POINT_NUMBER);


        Button btn_predict = (Button) findViewById(R.id.btn_predict);
        btn_predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int hours = Integer.parseInt(text_hours.getText().toString());
                int mins = Integer.parseInt(text_mins.getText().toString());

                double score = (hours * 60) + mins;

                PredictionClass pmin = null;
                PredictionClass pmax = null;
                for (int i = 1; i < arrayList.size(); i++) {

                    if (arrayList.get(i).minutes > score) {
                        pmin = arrayList.get(i - 1);
                        pmax = arrayList.get(i);
                        break;
                    }

                }

                if(pmin ==null || pmax == null){
                    Toast.makeText(PointPredictionActivity.this, "No prediction for given time", Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] temp_loc1 = pmin.location.split(",");
                double x1 = Double.parseDouble(temp_loc1[0]);
                double y1 = Double.parseDouble(temp_loc1[1]);

                temp_loc1 = pmax.location.split(",");
                double x2 = Double.parseDouble(temp_loc1[0]);
                double y2 = Double.parseDouble(temp_loc1[1]);


                double DY = y2 - y1;
                double DX = x2 - x1;

                double rad = Math.atan2(DY, DX);

                Log.d("Jozeb", "rad: " + rad + " degree: " + Math.toDegrees(rad));

                double distance =  Math.sqrt(Math.pow(DX,2) + Math.pow(DY,2));
                Log.d("Jozeb", "distance: " + distance);
                Log.d("Jozeb", "distance: " + distance(x1,y1,x2,y2,"K"));


                double time = pmax.minutes - pmin.minutes;
                double velocity = 1000 / time;
                Log.d("Jozeb", "distance: " + 1000 + " time: " + time + " velocity: " + velocity);

                double new_distance = (velocity * score)/1000000000; // score is current time in minutes
                new_distance = Math.toDegrees(new_distance);
                Log.d("Jozeb","new distnace: " + new_distance);
                Log.d("Jozeb","new distnace: " + new_distance);
                Log.d("Jozeb", x1+","+y1);
                double new_x = x1 + (new_distance * Math.cos(rad));
                double new_y = y1 + (new_distance * Math.sin(rad));


                LatLng prev_location = new LatLng(x1, y1);

                mMap.addMarker(new MarkerOptions().position(prev_location)
                        .title("Last Known Location"));



                LatLng p_point_location = new LatLng(new_x, new_y);

                Log.d("Jozeb","gps: "+p_point_location.toString());


                //mMap.addMarker(new MarkerOptions().position(p_point_location).title("Predicted Location"));

                PointShowActivity.ResourceUtil resourceUtil = new PointShowActivity.ResourceUtil();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(p_point_location, 17));
                if (marker != null) {
                    marker.setPosition(p_point_location);
                } else {
                    marker = mMap.addMarker(new MarkerOptions().position(p_point_location)
                            .title("Predicted")
                            .icon(BitmapDescriptorFactory.fromBitmap(resourceUtil.getBitmap(PointPredictionActivity.this, R.drawable.ic_directions_bus_black_24dp))));

                }


            }
        });

    }


    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }

        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        // Add a marker in Sydney and move the camera

    }


    private class GetHistoryTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(HelperClass.HTTP_GET_HISTORY);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setConnectTimeout(1000 * 5); // Timeout of 5 second
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);


                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));


                String data = URLEncoder.encode("point_number", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");


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
                    arrayList = new ArrayList<>();

                    JSONArray jsonArray = jsonRootObject.optJSONArray("history");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int time_m = jsonObject.optInt("time_m");
                        String location = jsonObject.optString("location");
                        Log.d("Jozeb", time_m + " " + location);
                        arrayList.add(new PredictionClass(time_m, location));

                    }

                    Toast.makeText(PointPredictionActivity.this, "Prediction data ready", Toast.LENGTH_SHORT).show();


                } else {

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }


}
