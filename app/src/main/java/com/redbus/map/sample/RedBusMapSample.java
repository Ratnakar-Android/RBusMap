package com.redbus.map.sample;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.maps.SupportMapFragment;
import com.redbus.map.module.RMapProvider;
import com.redbus.map.sample.listener.MyResultReceiver;
import com.redbus.map.sample.listener.etaUpdateReceiver;

public class RedBusMapSample extends AppCompatActivity implements MyResultReceiver.GetResultInterface{

    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    private boolean mLocationPermissionGranted  = false;
    FrameLayout mContainerID;
    TextView distance;
    TextView eta;
    MyResultReceiver myResultReceiver;
    RelativeLayout distanceLayout;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_layout);
        distanceLayout = (RelativeLayout) findViewById(R.id.distance_layout);
        distance = (TextView) findViewById(R.id.distance);
        eta = (TextView) findViewById(R.id.eta);

        myResultReceiver = new MyResultReceiver(new Handler());
        myResultReceiver.setReceiver(this);

       // mContainerID = (FrameLayout)findViewById(R.id.map_fragment_container) ;
        FragmentManager myFragmentManager = this.getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) myFragmentManager
                .findFragmentById(R.id.map);

        RMapProvider.getInstance(this).addMapToView(mapFragment);

       // startService(new Intent(this, RBBackgroundService.class));

        Intent intent = new Intent(this, RBBackgroundService.class);
        intent.putExtra("result",myResultReceiver);
        startService(intent);

        myResultReceiver = new MyResultReceiver(new Handler());
        myResultReceiver.setReceiver(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mLocationPermissionGranted){
            getLocationPermission();
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }


    @Override
    public void getResult(int resultCode, Bundle resultData) {
        if(resultData!=null){
            switch (resultCode){
                case 100:
                    distanceLayout.setVisibility(View.VISIBLE);
                    double  totalDistance = Double.valueOf(resultData.getString("distance")) / 1000;
                    double  totalDuration = Double.valueOf(resultData.getString("duration")) / 60;
                    distance.setText(String.valueOf(Math.round(totalDistance * 10) / 10.0 + " Km"));
                    eta.setText(String.valueOf(Math.round(totalDuration * 10) / 10.0 +" Minute"));
                    break;
            }
        }
    }
}
