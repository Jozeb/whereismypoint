package com.teamfegit.wheresmypoint;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.teamfegit.wheresmypoint.AdapterPackage.ContactAdapter;
import com.teamfegit.wheresmypoint.DatabasePackage.DbSource;
import com.teamfegit.wheresmypoint.StructurePackage.DriverClass;
import com.teamfegit.wheresmypoint.StructurePackage.FriendsClass;
import com.teamfegit.wheresmypoint.StructurePackage.HelperClass;
import com.teamfegit.wheresmypoint.StructurePackage.UserClass;
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
import java.util.ArrayList;
import java.util.Collections;

import static com.teamfegit.wheresmypoint.StructurePackage.HelperClass.HTTP_CURRENT_POINT;

public class PointShowActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {

    private GoogleMap mMap = null;


    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    private String POINT_NUMBER;
    private ArrayList<UserClass> userClassArrayList;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10 * 1000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    Location mLocation = null;

    ArrayList<FriendsClass> list;
    DbSource database;
    DriverClass driver;

    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_show);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle bundle = getIntent().getExtras();
        POINT_NUMBER = bundle.getString(HelperClass.INTENT_POINT_NUMBER);
        Log.d("Jozeb", "point number: " + POINT_NUMBER);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Point #" + POINT_NUMBER);


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();
        mGoogleApiClient.connect();

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + driver.number));
                if (ActivityCompat.checkSelfPermission(PointShowActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(callIntent);
            }
        });


        if (UserData.isLoggedIn(this)) {
            database = new DbSource(this);
            list = database.getFriends();

            if (list.size() > 0) {

                TextView title = (TextView) findViewById(R.id.text_list);
                title.setVisibility(View.VISIBLE);
                ListView listView = (ListView) findViewById(R.id.listview);
                listView.setVisibility(View.VISIBLE);
                listView.setAdapter(new ContactAdapter(this, list));


                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + list.get(position).number));
                        if (ActivityCompat.checkSelfPermission(PointShowActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(callIntent);
                    }
                });

            }
        }
        setUpDriver();


    }

    public void setUpDriver() {

        TextView textVdrivername = (TextView) findViewById(R.id.text_drivername);
        TextView textVdrivernumber = (TextView) findViewById(R.id.text_drivernumber);
        if (database == null) {
            database = new DbSource(this);
        }
        driver = database.getDriver(Integer.parseInt(POINT_NUMBER));

        textVdrivername.setText(driver.name);
        textVdrivernumber.setText(driver.number);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
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
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        new FetchPointLocationTask().execute(POINT_NUMBER);
        Log.d("jozeb", "location updated");
    }

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
        //24.879273, 67.057669

    }

    @Override
    protected void onDestroy() {
        mGoogleApiClient.disconnect();

        if (database != null) {
            database.close();
        }
        super.onDestroy();
    }

    public class GoogleMatrixAPI extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
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

                Log.d("Jozeb", "Http Response: " + response);
                httpURLConnection.disconnect();

                return response;
            } catch (Exception e) {
                e.printStackTrace();
                return "networkerror";
            }


        }

        @Override
        protected void onPostExecute(String s) {

            SetUpData(s);
            TakeMedianofLocation();
            super.onPostExecute(s);
        }

        private void SetUpData(String response) {

            try {
                JSONObject jsonRootObject = new JSONObject(response);
                JSONArray jsonArray = jsonRootObject.getJSONArray("rows");

                JSONObject jsonObject = jsonArray.optJSONObject(0);
                JSONArray jsonArray1 = jsonObject.getJSONArray("elements");

                for (int i = 0; i < jsonArray1.length(); i++) {

                    JSONObject jsonObject1 = jsonArray1.getJSONObject(i);

                    JSONObject jsonObject2 = jsonObject1.getJSONObject("distance");
                    JSONObject jsonObject3 = jsonObject1.getJSONObject("duration");

                    double l_distance = jsonObject2.getDouble("value");
                    double l_time = jsonObject3.getDouble("value");


                    Log.i("testing:", "L_distance: " + l_distance / 1000);
                    Log.i("testing:", "L_time: " + l_time / 60);

                    userClassArrayList.get(i).l_distance = l_distance;
                    userClassArrayList.get(i).l_time = l_time;


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        private void TakeMedianofLocation() {

            ArrayList<Double> list_lat = new ArrayList<Double>();
            ArrayList<Double> list_lng = new ArrayList<Double>();

            for (UserClass userClass : userClassArrayList) {
                String[] location = userClass.currentlocation.split(",");
                double lat = Double.parseDouble(location[0]);
                double lng = Double.parseDouble(location[1]);

                list_lat.add(lat);
                list_lng.add(lng);

            }

            Collections.sort(list_lat);
            Collections.sort(list_lng);
            double final_lat = func_Median(list_lat);
            double final_lng = func_Median(list_lng);


            LatLng m_point_location = new LatLng(final_lat, final_lng);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(m_point_location, 17));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(m_point_location));


            ResourceUtil resourceUtil = new ResourceUtil();

            if (marker != null) {
//                marker.remove();
                marker.setPosition(m_point_location);
            } else {
                marker = mMap.addMarker(new MarkerOptions().position(m_point_location)
                        .title("Point")
                        .icon(BitmapDescriptorFactory.fromBitmap(resourceUtil.getBitmap(PointShowActivity.this, R.drawable.ic_directions_bus_black_24dp))));

            }


        }

        private double func_Median(ArrayList<Double> list) {

            int MIDDLE = list.size() / 2;
            return list.get(MIDDLE);
        }
    }

    public static class ResourceUtil {

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        private Bitmap getBitmap(VectorDrawable vectorDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
            return bitmap;
        }

        private Bitmap getBitmap(VectorDrawableCompat vectorDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
            return bitmap;
        }

        public Bitmap getBitmap(Context context, @DrawableRes int drawableResId) {
            Drawable drawable = ContextCompat.getDrawable(context, drawableResId);
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            } else if (drawable instanceof VectorDrawableCompat) {
                return getBitmap((VectorDrawableCompat) drawable);
            } else if (drawable instanceof VectorDrawable) {
                return getBitmap((VectorDrawable) drawable);
            } else {
                throw new IllegalArgumentException("Unsupported drawable type");
            }
        }
    }


    public class FetchPointLocationTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(HTTP_CURRENT_POINT);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
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
                Log.d("jozeb", response);


                httpURLConnection.disconnect();

                return response;
            } catch (Exception e) {
                e.printStackTrace();
                return "b";
            }
        }

        @Override
        protected void onPostExecute(String jsonString) {

            Log.i("Jozeb", "Json: " + jsonString);
            ArrayList<String> list = new ArrayList<String>();
            userClassArrayList = new ArrayList<UserClass>();

            try {
                JSONObject jsonRootObject = new JSONObject(jsonString);
                JSONArray jsonArray = jsonRootObject.optJSONArray("users");

                if (jsonRootObject.optInt("success") == 1) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String nuid = jsonObject.optString("nu_id");
                        String point_number = jsonObject.optString("point_number");
                        String phone_number = jsonObject.optString("phone_number");
                        String home_location = jsonObject.optString("home_location");
                        String current_location = jsonObject.optString("current_location");
                        list.add(current_location);
                        userClassArrayList.add(new UserClass(nuid, phone_number, point_number, home_location, current_location));
                    }

                    String destination = "";
                    for (String gps : list) {
                        destination += gps + "|";
                    }
                    destination = destination.substring(0, destination.length() - 1);
                    //System.out.println(destination);
                    Log.d("Jozeb", "destination: " + destination);
                    String origin = null;
                    if (mLocation != null) {
                        origin = mLocation.getLatitude() + "," + mLocation.getLongitude();
                        String GOOGLE_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + origin + "&destinations=" + destination + "&key=AIzaSyCrf51ezoiZdER8xPAD32UvY17QNLjpIoM";
                        new GoogleMatrixAPI().execute(GOOGLE_URL);
                        //new GoogleMatrixAPI().execute();


                    } else {
                        Log.d("Jozeb", "Location null. no Distance Api :'(");
                        Toast.makeText(PointShowActivity.this, "Location was not ready", Toast.LENGTH_SHORT).show();

                    }

                } else {

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
