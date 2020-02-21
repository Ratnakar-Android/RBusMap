package com.redbus.map.sample;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.LatLng;
import com.redbus.map.module.RMapProvider;
import com.redbus.map.batterystatus.DragLocationListener;
import com.redbus.map.sample.listener.OSRMRouteListener;

import java.util.ArrayList;
import java.util.List;

import in.redbus.android.rblogger.client.RedBusMapRepository;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

public class RBBackgroundService extends Service implements OSRMRouteListener {
    private static final int NOTIF_ID = 101;
    private static final String NOTIF_CHANNEL_ID = "Channel_Id";

    private Location mUserCurrentLocation;
    FusedLocationProviderClient mFusedLocation;
    // we are requesting fuse to check Location after 10 seconds
    private static final int INTERVAL = 20000;
    private static final int FASTEST_INTERVAL = 20000;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        startLocationUpdates();

        startForeground();

        return super.onStartCommand(intent, flags, startId);
    }

    private void startForeground() {
        Intent notificationIntent = new Intent(this, RedBusMapSample.class);
        String channelId = "";
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId =  createNotificationChannel("my_service", "My Background Service");
        }

        startForeground(NOTIF_ID, new NotificationCompat.Builder(this,
                channelId) // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(R.drawable.taxi_icon)
                .setPriority(PRIORITY_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service is running background")
                .setContentIntent(pendingIntent)
                .build());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String  createNotificationChannel(String channelId, String channelName){
        NotificationChannel channel = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);

        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.createNotificationChannel(channel);
        return channelId;
    }


    private void startLocationUpdates() {
        LocationRequest mLocationRequest = new LocationRequest();
        // Create the location request to start receiving updates
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest =  builder.build();
        SettingsClient settingsClient =  LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback,
                Looper.myLooper());

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            mUserCurrentLocation =  locationResult.getLastLocation();
            //RMapProvider.getInstance(RBBackgroundService.this).setUserLocationMarker(mUserCurrentLocation);
            RedBusMapRepository.getInstance(RBBackgroundService.this).callOsrmRouteApi(RBBackgroundService.this, RBBackgroundService.this, mUserCurrentLocation);
        }
    };

    @Override
    public void onResult(JsonObject result) {
        String  encodedGeometry  = result.getAsJsonArray("routes").get(0).getAsJsonObject().get("geometry").getAsString();
        getPolyLine(encodedGeometry);
    }

    @Override
    public void onFailure(Throwable e) {

    }

    private void getPolyLine(String encodedPath) {

        List<LatLng> decodedPath = PolylineEncoding.decode(encodedPath);
        List<com.google.android.gms.maps.model.LatLng> newDecodedPath = new ArrayList<>();

        // This loops through all the LatLng coordinates of ONE polyline.
        for (com.google.maps.model.LatLng latLng : decodedPath) {

            newDecodedPath.add(new com.google.android.gms.maps.model.LatLng(
                    latLng.lat,
                    latLng.lng
            ));
        }
        RMapProvider.getInstance(this).onLocationChanged(newDecodedPath, mUserCurrentLocation, new LocationDargListener() );
    }

    class LocationDargListener implements DragLocationListener{
        @Override
        public void dragLocationUpdate(MarkerOptions markerOptions) {

            Location location = new Location("");
            location.setLongitude(markerOptions.getPosition().longitude);
            location.setLatitude(markerOptions.getPosition().latitude);

            RedBusMapRepository.getInstance(RBBackgroundService.this).callOsrmRouteApi(RBBackgroundService.this
                    , RBBackgroundService.this, location);

        }
    }




}
