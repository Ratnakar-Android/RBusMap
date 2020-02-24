package com.redbus.map.module

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.redbus.map.R
import com.redbus.map.batterystatus.DragLocationListener
import com.redbus.map.batterystatus.RBusBatteryManager
import com.redbus.map.custompolyline.RBCustomPolyline
import com.google.android.gms.maps.model.Polyline





class RMapProvider (var context: Context ) :  OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener,
GoogleMap.OnCameraMoveListener,
GoogleMap.OnCameraMoveCanceledListener,
GoogleMap.OnCameraIdleListener {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient;
    var PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=101;
    private val TAG = "RedBusMapProvider"
    private var listener : DragLocationListener? = null
    val polylines = ArrayList<Polyline>()


    companion object {
        @JvmStatic
        @Volatile
        private var INSTANCE: RMapProvider? = null

        @JvmStatic
        fun getInstance(context: Context): RMapProvider {
            return INSTANCE ?: synchronized(this) {
                RMapProvider(context).also { INSTANCE = it }
            }
        }
    }

    //Call from client after user location update
    fun onLocationChanged(polyline: MutableList<LatLng>, userCurrentLocation: Location, listener : DragLocationListener) {
        this.listener = listener
        drawRoutingLineOnMap(polyline)
        Log.v(TAG, "Location update after 10 seconds")
        // Read the battery status
        RBusBatteryManager.getInstance(context).checkBatteryStatus()

    }

    private fun drawRoutingLineOnMap(PolylinePoints: List<LatLng>) {
            var  polyline = mMap.addPolyline(RBCustomPolyline.getInstance().getDotPolyline(context).addAll(PolylinePoints))
            polyline.setWidth(context.getResources().getDimension(R.dimen.selected_polyline_width))
            polylines.add(polyline)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        RMapCustomMarkerProvider.getInstance(context).createMarker(mMap);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    == PackageManager.PERMISSION_GRANTED
            ) {
                mMap.isMyLocationEnabled = true;
                mMap.uiSettings.isMapToolbarEnabled = true;
                mMap.uiSettings.isMyLocationButtonEnabled = true;
            } else {
                ActivityCompat.requestPermissions(
                        context.applicationContext as Activity,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                );
            }
        }else
        {
            mMap.isMyLocationEnabled = true;
            mMap.uiSettings.isMapToolbarEnabled = true;
            mMap.uiSettings.isMyLocationButtonEnabled = true;
        }

        mMap.setOnCameraMoveStartedListener (this)
        mMap.setOnCameraIdleListener (this)
        mMap.setMaxZoomPreference(16f)
        mMap.setOnCameraMoveListener  (this)

        fetchCurrentLocation()

    }

    fun fetchCurrentLocation() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient!!.lastLocation
                .addOnSuccessListener { location: Location? ->
                    val currentLatLng = LatLng(location!!.latitude, location!!.longitude)
                    Log.v("Location is","Ratnakar latitude is "+location?.latitude + "Ratnakar latitude is "+ location?.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f))
                    val cameraPosition = CameraPosition.Builder()
                            .target(LatLng(location.latitude, location.longitude))      // Sets the center of the map to location user
                            .zoom(17f)                   // Sets the zoom
                            .bearing(90f)                // Sets the orientation of the camera to east
                            .tilt(30f)                   // Sets the tilt of the camera to 30 degrees
                            .build()                   // Creates a CameraPosition from the builder
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                }
    }

    override fun onCameraMoveStarted(p0: Int) {
    }

    override fun onCameraMove() {
    }

    override fun onCameraMoveCanceled() {
    }

    override fun onCameraIdle() {
        val markerOptions = MarkerOptions().position(mMap.cameraPosition.target)
        if (listener != null){
            for (line in polylines) {
                line.remove()
            }
            listener!!.dragLocationUpdate(markerOptions)
        }
    }

    fun addMapToView(mapFragment: SupportMapFragment) {

        //        FragmentManager myFragmentManager = ((FragmentActivity)get).getSupportFragmentManager();
        //        SupportMapFragment  mapFragment = (SupportMapFragment) myFragmentManager
        //                .findFragmentById(R.id.google_map);

        //        myFragmentManager.beginTransaction()
        //                .add(containerId, mapFragment)
        //                .commit();

        //        map = mapFragment.getMap();
        //
        //        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        //
        //        ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
        //                .add(containerId, mapFragment)


        //double curlat=currentPosition.getLatitude();
        //double curlon=currentPosition.getLongitude();
        //LatLng currentpos=new LatLng(curlat, curlon);


        //      Marker  marker = mGoogleMap.addMarker(new MarkerOptions().position(currentpos)
        //                .title("Draggable Marker")
        //                .snippet("Long press and move the marker if needed.")
        //                .draggable(true)
        //                .icon(mContext.getResources().getDrawable(R.id.));
        //
        //
        //        mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
        //
        //            @Override
        //            public void onMarkerDrag(Marker arg0) {
        //                // TODO Auto-generated method stub
        //                Log.d("Marker", "Dragging");
        //            }
        //
        //            @Override
        //            public void onMarkerDragEnd(Marker arg0) {
        //                // TODO Auto-generated method stub
        //                LatLng markerLocation = marker.getPosition();
        //                //Toast.makeText(MainActivity.this, markerLocation.toString(), Toast.LENGTH_LONG).show();
        //                Log.d("Marker", "finished");
        //            }
        //
        //            @Override
        //            public void onMarkerDragStart(Marker arg0) {
        //                // TODO Auto-generated method stub
        //                Log.d("Marker", "Started");
        //
        //            }
        //        });
        //                .commit();

        MapsInitializer.initialize(context)
        mapFragment.getMapAsync(this)

    }
}