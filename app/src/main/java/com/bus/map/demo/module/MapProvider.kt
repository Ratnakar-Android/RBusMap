//package com.bus.map.demo.module
//
//import `in`.redbus.android.rblogger.client.RedBusMapRepository
//import android.Manifest
//import android.content.Context
//import android.location.Location
//import androidx.appcompat.app.AppCompatActivity
//import com.google.android.gms.maps.*
//import com.google.android.gms.maps.model.*
//import com.google.android.libraries.places.api.model.Place
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//import com.google.maps.internal.PolylineEncoding
//import com.google.maps.model.DirectionsResult
//import com.google.maps.model.DirectionsRoute
//import com.bus.map.demo.data.DirectionData
//import com.bus.map.demo.data.PathDetails
//import com.bus.map.demo.data.PlaceData
//import com.bus.map.demo.data.PlacesDao
//import com.bus.map.demo.module.MapConstants.rootNode
//import com.bus.map.demo.module.MapConstants.routeInfoNode
//import com.bus.map.demo.module.MapExceptions.Companion.insufficientPathDataToSave
//import android.content.pm.PackageManager
//import android.location.LocationManager
//import android.os.Build
//import android.os.Looper
//import android.provider.Settings
//import android.util.Log
//import android.webkit.GeolocationPermissions
//import android.widget.Toast
//import androidx.appcompat.app.AlertDialog
//import androidx.core.app.ActivityCompat
//import com.bus.map.demo.R
//import com.bus.map.demo.listener.OSRMRouteListener
//import com.google.android.gms.location.*
//import com.google.android.gms.maps.SupportMapFragment
//import com.google.android.gms.tasks.OnSuccessListener
//import com.google.gson.JsonObject
//
//
//class MapProvider(context: Context) : MapProviderImpl, OnMapReadyCallback,
//    GoogleMap.OnPolylineClickListener, OSRMRouteListener {
//
//
//
//    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
//    private val INTERVAL: Long = 200000
//    private val FASTEST_INTERVAL: Long = 10000
//    lateinit var mLastLocation: Location
//    internal lateinit var mLocationRequest: LocationRequest
//    private val REQUEST_PERMISSION_LOCATION = 10
//
//    lateinit var googleMap: GoogleMap
//    var source: PlaceData? = null
//    var destination: PlaceData? = null
//
//
//    var sourceMarker: Marker? = null
//    var destinationMarker: Marker? = null
//    var directionsDataList = arrayListOf<DirectionData>()
//    var selectedDirectionData: DirectionData? = null
//
//    var previousData: DataSnapshot? = null
//
//
//    var context: Context
//    var valueChangeListener: ValueEventListener? = null
//
//    var placesDao = PlacesDao()
//    var mapRepository = MapRepository()
//
//    init {
//        this.context = context
//
//        initValueChangeListener()
//        var databaseRef = FirebaseDatabase.getInstance().getReference(rootNode)
//        databaseRef.addValueEventListener(valueChangeListener!!)
//    }
//
//
//
//    override fun onMapReady(googleMap: GoogleMap?) {
//        mLocationRequest = LocationRequest()
//
//        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            // buildAlertMessageNoGps()
//        }
//        this.googleMap = googleMap ?: return
//        this.googleMap.setOnPolylineClickListener(this)
//       // googleMap.setMinZoomPreference(8.0f);
//        googleMap.setMaxZoomPreference(16f);
//        startLocationUpdates()
//
//    }
//
//
//
//    protected fun startLocationUpdates() {
//
//        // Create the location request to start receiving updates
//
//        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        mLocationRequest!!.setInterval(INTERVAL)
//        mLocationRequest!!.setFastestInterval(FASTEST_INTERVAL)
//
//        // Create LocationSettingsRequest object using location request
//        val builder = LocationSettingsRequest.Builder()
//        builder.addLocationRequest(mLocationRequest!!)
//        val locationSettingsRequest = builder.build()
//
//        val settingsClient = LocationServices.getSettingsClient(context)
//        settingsClient.checkLocationSettings(locationSettingsRequest)
//
//        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
//        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return
//        }
//        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback,
//                Looper.myLooper())
//    }
//
//
//    private val mLocationCallback = object : LocationCallback() {
//        override fun onLocationResult(locationResult: LocationResult) {
//            // do work here
//            locationResult.lastLocation
//            onLocationChanged(locationResult.lastLocation)
//        }
//    }
//
//    fun onLocationChanged(location: Location) {
//        mLastLocation = location
//
//        Log.v("Ratna","location is"+location.latitude+" Location "+location.longitude);
//
//        var placeData = placesDao.getPlaceData(mLastLocation)
//        setSourceItem(placeData)
//        sourceMarker?.remove()
//        setupSourceMarker(placeData)
//        if (destination != null) {
//            handleDirectionDrawing()
//        }else{
//            setDestinationItemOnMap();
//        }
//        // You can now create a LatLng Object for use with maps
//    }
//
//    fun initValueChangeListener() {
//        valueChangeListener = object : ValueEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                previousData = dataSnapshot
//            }
//        }
//    }
//
//    override fun addMapToView(containerId: Int) {
//
//        val mTransaction =
//            (context as? AppCompatActivity)?.supportFragmentManager?.beginTransaction()
//        val mapFragment = SupportMapFragment.newInstance()
//        mTransaction?.add(containerId, mapFragment)
//        mTransaction?.commit()
//
//        MapsInitializer.initialize(context)
//        mapFragment?.getMapAsync(this)
//    }
//
//    override fun removeMap() {
//
//    }
//
//    fun setSourceItem(source: PlaceData) {
//        this.source = source
//    }
//
//
//    fun setDestinationItem(destination: PlaceData) {
//        this.destination = destination
//    }
//
//    fun setDestinationItemOnMap() {
//        var  desLocation  :  Location? = null
//
//       // 77.6388622,12.9116225
//
//        desLocation?.latitude = 77.6388622;
//        desLocation?.latitude = 12.9116225;
//
//        var placeData = placesDao.getPlaceData(desLocation)
//        setDestinationItem(placeData)
//        destinationMarker?.remove()
//       // setupDestinationMarker(placeData)
//
//        handleDirectionDrawing()
//
//    }
//
//
//    fun handleDirectionDrawing() {
//        removeOlderRouteInfo()
//
//        val instance = RedBusMapRepository.getInstance(context)
//        instance.callRedBusLoggerApi(this, this)
//
//       // new RedBusMapRepository(getActivity()).callRedBusLoggerApi(this, this);
//
////        var directionResult = mapRepository.getDirections(
////            source, destination, context.getString(R.string.google_maps_key)
////        )
//
//
//
//
//
//
//    }
//
//    override fun onResult(result: JsonObject?) {
//      var  encodedGeometry  = result?.getAsJsonArray("routes")?.get(0)?.asJsonObject?.get("geometry")?.asString
//        drawDirectionsOnMap(encodedGeometry.toString())
//        zoomToRoute(source!!, destination!!)
//    }
//
//    override fun onFailure(e: Throwable?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//
//    fun drawDirectionsOnMap(encodedPath: String ) {
//        //for (route in directionResult.routes) {
//            var mapsLatLongs = getPathLatLngFromEncodedPath(encodedPath)
//
//            drawPolyLine(mapsLatLongs)
//           // var mapLatLngCords = getPathLatLngCords(route.overviewPolyline.encodedPath)
//           // addDirectionTodirectionList(route, polyline, mapLatLngCords)
//      //  }
//    }
//
//    fun addDirectionTodirectionList(
//        route: DirectionsRoute?,
//        polyline: Polyline?,
//        mapLatLngCords: List<PathDetails.PathLatLng>?
//    ) {
//
//        var directionData = DirectionData()
//        directionData.direction = route
//        directionData.polyline = polyline
//        directionData.pathLatLng = mapLatLngCords
//        directionsDataList.add(directionData)
//    }
//
//    fun getPathLatLngFromEncodedPath(encodedPath: String): List<com.google.android.gms.maps.model.LatLng> {
//        var pathsLatLong = PolylineEncoding.decode(encodedPath)
//
//        var mapsLatLongs = arrayListOf<com.google.android.gms.maps.model.LatLng>()
//
//        for (latLong in pathsLatLong) mapsLatLongs.add(
//            com.google.android.gms.maps.model.LatLng(
//                latLong.lat,
//                latLong.lng
//            )
//        )
//        return mapsLatLongs
//    }
//
//    fun getPolyPathWithCords(pathCords: List<PathDetails.PathLatLng>): List<com.google.android.gms.maps.model.LatLng> {
//
//        var mapsLatLongs = arrayListOf<com.google.android.gms.maps.model.LatLng>()
//        for (latLong in pathCords) mapsLatLongs.add(
//            com.google.android.gms.maps.model.LatLng(
//                latLong.latitude!!,
//                latLong.longitude!!
//            )
//        )
//        return mapsLatLongs
//    }
//
//    fun getPathLatLngCords(encodedPath: String): List<PathDetails.PathLatLng> {
//        var pathsLatLong = PolylineEncoding.decode(encodedPath)
//        var mapsLatLongs = arrayListOf<PathDetails.PathLatLng>()
//        for (latLong in pathsLatLong) {
//            var pathLatLng = PathDetails.PathLatLng()
//            pathLatLng.latitude = latLong.lat
//            pathLatLng.longitude = latLong.lng
//            mapsLatLongs.add(pathLatLng)
//        }
//        return mapsLatLongs
//    }
//
////
////    fun drawPolyLine(mapsLatLongs: List<com.google.android.gms.maps.model.LatLng>) {
////         MarkerPolyLineUtil.addPolylineToMap(context, googleMap, mapsLatLongs)
////    }
////
////    private fun setupSourceMarker(place: PlaceData) {
////        sourceMarker = MarkerPolyLineUtil.addMarkerToMap(googleMap, place)
////    }
////
////    private fun setupDestinationMarker(place: PlaceData) {
////        destinationMarker = MarkerPolyLineUtil.addMarkerToMap(googleMap, place)
////    }
//
//    override fun saveSelectedPath() {
//        if (source != null && destination != null && selectedDirectionData != null) {
//            mapRepository.saveDirectionInfo(source!!, destination!!, selectedDirectionData!!)
//        } else {
//            throw MapExceptions(insufficientPathDataToSave)
//        }
//    }
//
//    override fun renderPreviousPath() {
//        if (previousData != null) {
//            sourceMarker?.remove()
//            destinationMarker?.remove()
//
//            removeOlderRouteInfo()
//
//            var rootNode = previousData!!.child(routeInfoNode).getValue(PathDetails::class.java)!!
//            source = rootNode.source
//           // setupSourceMarker(source!!)
//
//            destination = rootNode.destination
//            setupDestinationMarker(destination!!)
//
//            var polylineCords = getPolyPathWithCords(rootNode.route!!)
//             drawPolyLine(polylineCords)
//           // MarkerPolyLineUtil.setSelectedPolyline(context, polyLine)
//            zoomToRoute(source!!, destination!!)
//
//            var directionData = DirectionData()
//           // directionData.polyline = polyLine
//            directionsDataList.add(directionData)
//
//            sourceMarker?.snippet =
//                "Distance ${rootNode.distance} Time ${rootNode.duration}"
//            sourceMarker?.showInfoWindow()
//        }
//    }
//
//    override fun getPreviousSource(): String? {
//        return previousData?.child(routeInfoNode)?.getValue(PathDetails::class.java)?.source?.name
//    }
//
//    override fun getPreviousDestination(): String? {
//        return previousData?.child(routeInfoNode)?.getValue(PathDetails::class.java)?.destination?.name
//    }
//
//
////    override fun onPolylineClick(polyLine: Polyline?) {
////        for (directionData in directionsDataList) {
////            if (directionData.polyline?.id == polyLine?.id) {
////                selectedDirectionData = directionData
////                MarkerPolyLineUtil.setSelectedPolyline(context, polyLine)
////                sourceMarker?.snippet =
////                    "Distance ${directionData.direction?.legs?.get(0)?.distance} Time ${directionData.direction?.legs?.get(
////                        0
////                    )?.duration}"
////                sourceMarker?.showInfoWindow()
////            } else {
////                MarkerPolyLineUtil.setNonSelectedPolyline(context, directionData.polyline)
////            }
////        }
////    }
////
////    override fun getGPlace(requestCode: Int) {
////        PlacesUtil.getGPlace(context, requestCode)
////    }
//
//    fun removeOlderRouteInfo() {
//        for (directionData in directionsDataList) {
//            directionData.polyline?.remove()
//        }
//        directionsDataList.clear()
//        selectedDirectionData = null
//    }
//
//    fun zoomToRoute(source: PlaceData, destination: PlaceData) {
//
//        var sourceLatLng =
//            com.google.android.gms.maps.model.LatLng(source?.latitude!!, source?.longitude!!)
////        var destinationLatLng = com.google.android.gms.maps.model.LatLng(
////            destination?.latitude!!, destination?.longitude!!
////        )
//
//      //  var bounds = LatLngBounds.Builder()
//       // bounds.include(sourceLatLng)
//       // bounds.include(destinationLatLng)
//       // var latlongBounds = bounds.build()
//      //  var routeArea = 120
//
////        googleMap.animateCamera(
////            CameraUpdateFactory.newLatLngBounds(latlongBounds, routeArea),
////            600,
////            null
////        )
//    }
//
////    fun checkPermissionForLocation(context: Context): Boolean {
////        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////
////            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
////                    PackageManager.PERMISSION_GRANTED){
////                true
////            }else{
////                // Show the permission request
////                ActivityCompat.requestPermissions(context, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
////                        REQUEST_PERMISSION_LOCATION)
////                false
////            }
////        } else {
////            true
////        }
////    }
//
//
//}