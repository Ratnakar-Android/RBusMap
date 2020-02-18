package com.bus.map.demo.module;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.bus.map.demo.data.DirectionData;
import com.bus.map.demo.data.PlaceData;
import com.bus.map.demo.data.PlacesDao;
import com.bus.map.demo.listener.OSRMRouteListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.LatLng;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import in.redbus.android.rblogger.client.RedBusMapRepository;

public class RedBusMapProvider implements MapProviderImpl, GoogleMap.OnPolylineClickListener, OSRMRouteListener, OnMapReadyCallback {

    FusedLocationProviderClient mFusedLocation;
    // we are requesting fuse to check Location after 10 seconds
    private static final int INTERVAL = 10000;
    private static final int FASTEST_INTERVAL = 10000;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private GoogleMap mGoogleMap;
    private PlaceData mSource;
    private PlaceData mDestination;
    private Marker mSourceMarker;
    private Marker mDestinationMarker;
    private ArrayList<DirectionData> directionsDataList;
    private DirectionData selectedDirectionData;
    private DataSnapshot previousData;
    private Context context;
    private ValueEventListener valueChangeListener;
    private PlacesDao placesDao = new PlacesDao();

    public RedBusMapProvider(Context context) {
        this.context = context;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mLocationRequest = new LocationRequest();
        LocationManager lm = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);

        boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gps_enabled){
            // buildAlertMessageNoGps()
        }
        this.mGoogleMap = googleMap;
        mGoogleMap.setOnPolylineClickListener(this);
        mGoogleMap.setMaxZoomPreference(16f);
        startLocationUpdates();
    }

    private void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest =  builder.build();
        SettingsClient settingsClient =  LocationServices.getSettingsClient(context);
        settingsClient.checkLocationSettings(locationSettingsRequest);
        mFusedLocation = LocationServices.getFusedLocationProviderClient(context);
        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.
                checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback,
                Looper.myLooper());

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            // do work here
            //locationResult.lastLocation;
            onLocationChanged(locationResult.getLastLocation());
        }
    };

    private void onLocationChanged(Location lastLocation) {

        mLastLocation = lastLocation;

        Log.v("Ratna","location is"+mLastLocation.getLatitude()+" Location "+mLastLocation.getLongitude());

        PlaceData placeData = placesDao.getPlaceData(mLastLocation);
        setSourceItem(placeData);
        if (mSourceMarker != null)
            mSourceMarker.remove();
        setupSourceMarker(placeData);

        setDestinationItemOnMap();


    }

    private void  setSourceItem(PlaceData source) {
        this.mSource = source;
    }

    private void setupSourceMarker(PlaceData place) {
        mSourceMarker = MarkerPolyLineUtil.getInstance(context).addMarkerToMap(mGoogleMap, place);
    }

    private void  setDestinationItemOnMap() {
       // desLocation.setLatitude(12.9116225);

       // PlaceData placeData = placesDao.getPlaceData(desLocation);
       // setDestinationItem(placeData);
        if (mDestinationMarker != null)
        mDestinationMarker.remove();
       // setupDestinationMarker(placeData);

        handleDirectionDrawing();

    }

    private void handleDirectionDrawing() {
            RedBusMapRepository.getInstance(context).callRedBusLoggerApi(this, this, mLastLocation);
    }


    private  void setDestinationItem(PlaceData destination) {
        this.mDestination = destination;
    }

    @Override
    public void onResult(JsonObject result) {
        String  encodedGeometry  = result.getAsJsonArray("routes").get(0).getAsJsonObject().get("geometry").getAsString();
        drawDirectionsOnMap(encodedGeometry.toString());
        zoomToRoute(mSource, mDestination);
    }

    private void zoomToRoute(PlaceData source,PlaceData destination) {

      //  var sourceLatLng =
      //          com.google.android.gms.maps.model.LatLng(source.getLatitude(), source.getLongitude())
//        var destinationLatLng = com.google.android.gms.maps.model.LatLng(
//            destination?.latitude!!, destination?.longitude!!
//        )

        //  var bounds = LatLngBounds.Builder()
        // bounds.include(sourceLatLng)
        // bounds.include(destinationLatLng)
        // var latlongBounds = bounds.build()
        //  var routeArea = 120

//        googleMap.animateCamera(
//            CameraUpdateFactory.newLatLngBounds(latlongBounds, routeArea),
//            600,
//            null
//        )
    }


    private void drawDirectionsOnMap(String encodedPath) {

        List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(encodedPath);
        List<com.google.android.gms.maps.model.LatLng> newDecodedPath = new ArrayList<>();

        // This loops through all the LatLng coordinates of ONE polyline.
        for (com.google.maps.model.LatLng latLng : decodedPath) {

            newDecodedPath.add(new com.google.android.gms.maps.model.LatLng(
                    latLng.lat,
                    latLng.lng
            ));
        }
        MarkerPolyLineUtil.getInstance(context).addPolylineToMap(context, mGoogleMap, newDecodedPath);
    }




    @Override
    public void addMapToView(int containerId) {

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();

        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                .add(containerId, mapFragment)
                .commit();

        MapsInitializer.initialize(context);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void removeMap() {

    }

    @Override
    public void getGPlace(int requestCode) {
        PlacesUtil.getInstance(context).getGPlace(context, requestCode);
    }

    @Override
    public void saveSelectedPath() throws MapExceptions {

    }



    private void setupDestinationMarker(PlaceData place) {
        mDestinationMarker = MarkerPolyLineUtil.getInstance(context).addMarkerToMap(mGoogleMap, place);
    }


    @Override
    public void renderPreviousPath() {

    }

    @Nullable
    @Override
    public String getPreviousSource() {
        return null;
    }

    @Nullable
    @Override
    public String getPreviousDestination() {
        return null;
    }



    @Override
    public void onFailure(Throwable e) {

    }

    @Override
    public void onPolylineClick(Polyline polyline) {

    }

//    override fun onPolylineClick(polyLine: Polyline?) {
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
//    }
}



