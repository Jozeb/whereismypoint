package com.teamfegit.wheresmypoint.ServicePackage;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.teamfegit.wheresmypoint.DatabasePackage.DbSource;
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

public class UpdateCurrentLocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    String NUID;
    String POINT_NUMBER;

    DbSource database;

    Location mLastLocation;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000*15;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS;

    public UpdateCurrentLocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (database != null) {
            database.close();
        }
        mGoogleApiClient.disconnect();
        Log.d("Jozeb", "Service Destroyed!");
        HelperClass.notification(0, "Where is Point", "Service Closed", this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        //if (UserData.isLoggedIn(this)) {

        UserData userData = new UserData(this);
        NUID = userData.getNuid();
        Log.d("Jozeb", "NUID: " + NUID);
        POINT_NUMBER = userData.getPointno();

        database = new DbSource(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        //  }


        if (turnOnLocation()) {
            createLocationRequest();
            mGoogleApiClient.connect();
            HelperClass.notification(0, "Where is Point", "Service Started. Make your location is turned on", this);

        } else {

        }

        Log.d("Jozeb", " onStartCommand");
        return START_STICKY;
    }

    public boolean turnOnLocation() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(HelperClass.BROADCAST_LOCATION_STATE));
            return false;
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    }


    @Override
    public void onLocationChanged(Location location) {

        if(mLastLocation == null){ //If it is first time, DO NOT PROCEED
            mLastLocation = location;

        }else if (mLastLocation.distanceTo(location)<1000){ //If it is not first time, and the distance is less than 1km, DONOT proceed
            return;
        }else{ // The distance is greater than 1km, so proceed
            mLastLocation = location;
        }


        String s_location = location.getLatitude() + "," + location.getLongitude();
        Log.d("Jozeb", "UpdateCurrentLocation -> onLocationChanged -> locationChanged: " + s_location);
        database.add_current_location(NUID, POINT_NUMBER, s_location);
        new UpdateCurrentLocationTask().execute(s_location);


    }

    private class UpdateCurrentLocationTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(HelperClass.HTTP_UPDATE_CURRENT_LOCATION);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);


                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));


                String data = URLEncoder.encode("nuid", "UTF-8") + "=" + URLEncoder.encode(NUID, "UTF-8") + "&" +
                        URLEncoder.encode("current_location", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");


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
                // Log.d("Jozeb", "Response: " + response);
                return response;

            } catch (Exception e) {

                e.printStackTrace();

            }

            return "n";
        }


    }


}
