package com.redbus.map.module;

import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.redbus.map.R;
import com.redbus.map.batterystatus.RBusBatteryManager;
import com.redbus.map.custompolyline.RBCustomPolyline;
import com.redbus.map.data.PlaceData;
import com.redbus.map.data.PlacesDao;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.Arrays;
import java.util.List;


public class RedBusMapProvider<onRequestPermissionsResult> implements MapProviderImpl, GoogleMap.OnPolylineClickListener, OnMapReadyCallback,GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener {

    private GoogleMap mGoogleMap;
    private PlaceData mSource;
    private PlaceData mDestination;
    private Marker mSourceMarker;
    private Marker mDestinationMarker;
    private static Context mContext;
    private PlacesDao placesDao = new PlacesDao();
    private static final String TAG = "RedBusMapProvider";
    Location currentPosition;


    private static RedBusMapProvider singleton = new RedBusMapProvider();

    private RedBusMapProvider() {
    }

    /* Static 'instance' method */
    public static RedBusMapProvider getInstance(Context context) {
        mContext = context;
        return singleton;
    }

    // Call from client to draw marker
    public void setUserLocationMarker(Location mUserCurrentLocation) {
        PlaceData placeData = placesDao.getPlaceData(mUserCurrentLocation);
        setSourceItem(placeData);
        if (mSourceMarker != null)
            mSourceMarker.remove();
        // setupSourceMarker(placeData);
    }


    //Call from client after user location update
    public void onLocationChanged(List<LatLng> polyline, Location userCurrentLocation) {

        drawRoutingLineOnMap(polyline);
        Log.v(TAG, "Location update after 10 seconds");

        // Read the battery status
        RBusBatteryManager.getInstance(mContext).checkBatteryStatus();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LocationManager lm = (LocationManager) mContext
                .getSystemService(Context.LOCATION_SERVICE);

        boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gps_enabled) {
            // buildAlertMessageNoGps()
        }
        this.mGoogleMap = googleMap;
        mGoogleMap.setOnPolylineClickListener(this);
        mGoogleMap.setMaxZoomPreference(16f);
        mGoogleMap.setMyLocationEnabled(true);


        mGoogleMap.getUiSettings().setMapToolbarEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

        mGoogleMap.setOnCameraMoveStartedListener(this);
        mGoogleMap.setOnCameraIdleListener(this);
        mGoogleMap.setOnCameraMoveListener(this);

        //This method will get called very first time and only one times
        setCameraAtCurrent();
    }

    private void setCameraAtCurrent() {

        Log.v(TAG, "Camera launch");

        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        currentPosition = location;

        // set Circle around the marker
        //setCircularViewBehindMarker(location);

        if (location != null) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private void setCircularViewBehindMarker(Location location) {
        Circle circle = mGoogleMap.addCircle(new CircleOptions()
                .center(new LatLng(location.getLatitude(), location.getLongitude()))
                .radius(50)
                .strokeColor(Color.WHITE)
                .fillColor(Color.BLUE));
    }


    private void setSourceItem(PlaceData source) {
        this.mSource = source;
    }


    private void setupSourceMarker(PlaceData place) {
        mSourceMarker = MarkerPolyLineUtil.getInstance(mContext).addMarkerToMap(mGoogleMap, place);
    }


    private void drawRoutingLineOnMap(List<LatLng> PolylinePoints) {
        Polyline polyline = mGoogleMap.addPolyline(RBCustomPolyline.getInstance().getDotPolyline(mContext).addAll(PolylinePoints));
        polyline.setWidth(mContext.getResources().getDimension(R.dimen.selected_polyline_width));
    }


    public void addMapToView(SupportMapFragment mapFragment) {

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

        MapsInitializer.initialize(mContext);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void removeMap() {

    }

    @Override
    public void getGPlace(int requestCode) {
        PlacesUtil.getInstance(mContext).getGPlace(mContext, requestCode);
    }

    private void setupDestinationMarker(PlaceData place) {
        mGoogleMap.setMyLocationEnabled(true);
        //GeolocationMarker
        mDestinationMarker = MarkerPolyLineUtil.getInstance(mContext).addMarkerToMap(mGoogleMap, place);
    }


    @Override
    public void onPolylineClick(Polyline polyline) {
//        for (directionData in directionsDataList) {
//            if (directionData.polyline?.id == polyLine?.id) {
//                selectedDirectionData = directionData
//                MarkerPolyLineUtil.setSelectedPolyline(context, polyLine)
//                sourceMarker?.snippet =
//                        "Distance ${directionData.direction?.legs?.get(0)?.distance} Time ${directionData.direction?.legs?.get(
//                0
//                    )?.duration}"
//            sourceMarker?.showInfoWindow()
//        } else {
//            MarkerPolyLineUtil.setNonSelectedPolyline(context, directionData.polyline)
//        }
    }


    @Override
    public void addMapToView(int containerId) {

    }

    @Override
    public void onCameraIdle() {

    }

    @Override
    public void onCameraMoveCanceled() {

    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraMoveStarted(int i) {

      //  MarkerOptions().position(mMap.cameraPosition.target)

              // new  MarkerOptions().getPosition(mGoogleMap.getCameraPosition().target);

    }


}



