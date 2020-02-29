package com.redbus.map.module

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.animation.LinearInterpolator
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
    private lateinit var userCurrentLocation: Location
    lateinit var startPosition : LatLng
    lateinit var endPosition : LatLng
    var v : Float = 0.0f
    var lat : Double = 0.0
    var long : Double = 0.0


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
        this.userCurrentLocation = userCurrentLocation
        this.listener = listener
       // drawRoutingLineOnMap(polyline)

        RBCustomPolyline.getInstance().drawPolyLineAndAnimateCar(userCurrentLocation, context, polyline, mMap)
        Log.v(TAG, "Location update after 10 seconds")
        // Read the battery status
        RBusBatteryManager.getInstance(context).checkBatteryStatus()

    }

    private fun drawRoutingLineOnMap(polylineList: List<LatLng>) {

        var options  : ArrayList<PolylineOptions>  = RBCustomPolyline.getInstance().getDotPolyline(context, polylineList, mMap)


        var  greyPolyline = mMap.addPolyline(options.get(0).addAll(polylineList))
        greyPolyline.setWidth(context.getResources().getDimension(R.dimen.selected_polyline_width))


            var  blackPolyline = mMap.addPolyline(options.get(1).addAll(polylineList))
                 blackPolyline.setWidth(context.getResources().getDimension(R.dimen.selected_polyline_width))
                // polylines.add(blackPolyline)

        //polylines.add(blackPolyline)

        //Animator
        var  polylineAnimator : ValueAnimator = ValueAnimator.ofInt(0, 100);
        polylineAnimator.setDuration(2000)
        polylineAnimator.interpolator = LinearInterpolator()
        polylineAnimator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {

            override fun onAnimationUpdate(animation: ValueAnimator?) {
                var  points1 : List<LatLng>  = options.get(0).points
                var percentValue = animation?.getAnimatedValue()
                var size = points1.size
                var newPoints = size * ((percentValue as Int)/ 1000.0f)
                var points2 : List<LatLng> = points1.subList(0, newPoints.toInt())

                blackPolyline.points = points2

            }
        });

        polylineAnimator.start()

        //Car moving

        var myLatlng = LatLng(userCurrentLocation.latitude,userCurrentLocation.longitude);
        var marker : Marker =  mMap.addMarker(MarkerOptions().position(myLatlng))
        marker.isFlat = true
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.car))
        var index  = -1
        var next = 1
        var handler : Handler? = null

        handler?.postDelayed(object : Runnable{
            override fun run() {
                if (index < polylineList.size -1){
                    index++
                    next = index + 1
                }
                if (index < polylineList.size -1){
                    startPosition = polylineList.get(index)
                    endPosition = polylineList.get(next)
                }
                var  valueAnimator : ValueAnimator = ValueAnimator.ofFloat(0f, 1.0f)
                valueAnimator.setDuration(3000)
                valueAnimator.interpolator = LinearInterpolator()
                valueAnimator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {

                    override fun onAnimationUpdate(valueAnimator: ValueAnimator) {
                        v =   valueAnimator?.animatedFraction
                        lat = v * endPosition.longitude + (1-v) * startPosition?.longitude;
                        long = v * endPosition?.latitude + (1-v) * startPosition?.latitude;

                        var newPos : LatLng = LatLng(lat, long)
                        marker.position = newPos
                        marker.setAnchor(0.5f, 0.5f)
                        marker.rotation = getBearing(startPosition, newPos)
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.Builder()
                                .target(newPos)
                                .zoom(15.5f)
                                .build()))
                    }
                });
                valueAnimator.start()
                handler.postDelayed(this, 3000)
            }
        }, 3000)
    }

    private fun getBearing(startPosition: LatLng, newPos: LatLng): Float {

        val lat = Math.abs(startPosition.latitude - newPos.latitude)
        val lng = Math.abs(startPosition.longitude - newPos.longitude)

        if (startPosition.latitude < newPos.latitude && startPosition.longitude < newPos.longitude)
            return Math.toDegrees(Math.atan(lng / lat)).toFloat()
        else if (startPosition.latitude >= newPos.latitude && startPosition.longitude < newPos.longitude)
            return (90 - Math.toDegrees(Math.atan(lng / lat)) + 90).toFloat()
        else if (startPosition.latitude >= newPos.latitude && startPosition.longitude >= newPos.longitude)
            return Math.toDegrees(Math.atan(lng / lat) + 180).toFloat()
        else if (startPosition.latitude < newPos.latitude && startPosition.longitude >= newPos.longitude)
            return (90 - Math.toDegrees(Math.atan(lng / lat)) + 270).toFloat()

        return -1f
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.isTrafficEnabled = false
        mMap.isIndoorEnabled = false
        mMap.isBuildingsEnabled = false
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.setAllGesturesEnabled(true)
        mMap.uiSettings.isZoomGesturesEnabled = true

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
//        val markerOptions = MarkerOptions().position(mMap.cameraPosition.target)
//        if (listener != null){
//            for (line in polylines) {
//                line.remove()
//            }
//            listener!!.dragLocationUpdate(markerOptions)
//        }
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