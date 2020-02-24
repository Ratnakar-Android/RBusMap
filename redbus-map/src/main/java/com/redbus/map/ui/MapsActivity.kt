//package com.redbus.map.ui
//
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.content.Intent
//import android.content.IntentSender
//import android.content.pm.PackageManager
//import android.location.Location
//import android.os.Build
//import android.os.Bundle
//import android.util.Log
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.google.android.gms.common.api.ResolvableApiException
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationRequest
//import com.google.android.gms.location.LocationServices
//import com.google.android.gms.location.LocationSettingsRequest
//import com.google.android.gms.maps.CameraUpdateFactory
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.OnMapReadyCallback
//import com.google.android.gms.maps.SupportMapFragment
//import com.google.android.gms.maps.model.LatLng
//import com.google.android.gms.maps.model.MarkerOptions
//import com.redbus.map.R
//import java.util.*
//
//class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener,
//        GoogleMap.OnCameraMoveListener,
//        GoogleMap.OnCameraMoveCanceledListener,
//        GoogleMap.OnCameraIdleListener{
//
//
//
//    private val REQUEST_CHECK_SETTINGS: Int=101;
//    private lateinit var mMap: GoogleMap
//    var PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=101;
//    private lateinit var fusedLocationClient: FusedLocationProviderClient;
//    private lateinit var lastLocation: Location;
//    private lateinit var locationRequest: LocationRequest;
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_map)
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        val mapFragment = supportFragmentManager
//                .findFragmentById(R.id.map) as SupportMapFragment
//
//
//        mapFragment.getMapAsync(this)
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//
//    }
//
//
//
//    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//
//        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(
//                            this.getApplicationContext(),
//                            android.Manifest.permission.ACCESS_FINE_LOCATION
//                    )
//                    == PackageManager.PERMISSION_GRANTED
//            ) {
//                mMap.isMyLocationEnabled = true;
//                mMap.uiSettings.isMapToolbarEnabled = true;
//                mMap.uiSettings.isMyLocationButtonEnabled = true;
//                checkLocationService();
//            } else {
//                ActivityCompat.requestPermissions(
//                        this,
//                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
//                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
//                );
//            }
//        }else
//        {
//            mMap.isMyLocationEnabled = true;
//            mMap.uiSettings.isMapToolbarEnabled = true;
//            mMap.uiSettings.isMyLocationButtonEnabled = true;
//            checkLocationService();
//        }
//
//        mMap.setOnCameraMoveStartedListener (this)
//        mMap.setOnCameraIdleListener (this)
//        mMap.setOnCameraMoveListener  (this)
//
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
//                        android.Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            mMap.isMyLocationEnabled=true;
//            mMap.uiSettings.isMapToolbarEnabled=true;
//            mMap.uiSettings.isMyLocationButtonEnabled=true;
//            checkLocationService();
//        }
//    }
//
//    fun fetchCurrentLocation() {
//        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
//            // Got last known location. In some rare situations this can be null.
//            // 3
//            if (location != null) {
//                lastLocation = location
//                val currentLatLng = LatLng(location.latitude, location.longitude)
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
//            }
//        }
//    }
//
//    fun checkLocationService() {
//
//        locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(10 * 1000);
//        locationRequest.setFastestInterval(2 * 1000);
//
//
//        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
//        // builder.setAlwaysShow(true);
//        val client = LocationServices.getSettingsClient(this)
//        val task = client.checkLocationSettings(builder.build())
//        task.addOnSuccessListener(this) {it->
//            it.locationSettingsStates;
//            fetchCurrentLocation();
//        }
//
//        task.addOnFailureListener(this) { e ->
//            if (e is ResolvableApiException) {
//                // Location settings are not satisfied, but this can be fixed
//                // by showing the user a dialog.
//                try {
//                    // Show the dialog by calling startResolutionForResult(),
//                    // and check the result in onActivityResult().
//                    e.startResolutionForResult(
//                            this@MapsActivity,
//                            REQUEST_CHECK_SETTINGS
//                    )
//                } catch (sendEx: IntentSender.SendIntentException) {
//                    // Ignore the error.
//                }
//
//            }
//        }
//    }
//    @SuppressLint("MissingSuperCall")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//
//        if (requestCode == REQUEST_CHECK_SETTINGS) {
//            if (resultCode == Activity.RESULT_OK) {
//                val result = data!!.getStringExtra("result")
//                fetchCurrentLocation();
//            }
//            else if (resultCode == Activity.RESULT_CANCELED) {
//                //Write your code if there's no result
//            }
//        }
//    }
//
//
//
//
//    override fun onCameraMoveStarted(p0: Int) {
//        Log.v("Onmove start","Onmove "+p0);
//        mMap.clear()
//    }
//
//    override fun onCameraMove() {
//
//        Log.v("Onmove ","Onmove ");
//    }
//
//    override fun onCameraMoveCanceled() {
//        Log.v("Onmove cancel","Onmove ");
//    }
//
//    override fun onCameraIdle() {
//        Log.v("Onmove Idle","Onmove ");
//        val markerOptions = MarkerOptions().position(mMap.cameraPosition.target)
//
//        mMap.addMarker(markerOptions)
//    }
//
//}