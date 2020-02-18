package com.bus.map.demo.module

import `in`.redbus.android.rblogger.client.RedBusMapRepository
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.bus.map.demo.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.bus.map.demo.data.PlaceData
import java.util.ArrayList

class MarkerPolyLineUtil(var context: Context?)
{


    companion object {

        @JvmStatic
        @Volatile
        private var INSTANCE: MarkerPolyLineUtil? = null

        @JvmStatic
        fun getInstance(context: Context?): MarkerPolyLineUtil {
            return INSTANCE ?: synchronized(this) {
                MarkerPolyLineUtil(context).also { INSTANCE = it }
            }
        }

    }
        fun addMarkerToMap(googleMap: GoogleMap, place: PlaceData): Marker?{
            var marker: Marker?
            with(googleMap) {
                marker = addMarker(getMarkerOptions(place))
                marker?.showInfoWindow()
                animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(place.latitude!!, place.longitude!!), 15f))
            }
            return marker
        }
        fun getMarkerOptions(place: PlaceData):MarkerOptions{
            return MarkerOptions()
                .position(LatLng(place.latitude!!, place.longitude!!))
                .title(place?.name)
        }

        fun addPolylineToMap(context: Context, googleMap: GoogleMap,
                             mapsLatLongs: List<com.google.android.gms.maps.model.LatLng>){

            Handler(Looper.getMainLooper()).post {
                val newDecodedPath = ArrayList<LatLng>()

                // This loops through all the LatLng coordinates of ONE polyline.
                for (latLng in mapsLatLongs) {

                    newDecodedPath.add(LatLng(
                            latLng.latitude,
                            latLng.longitude
                    ))
                }

                var polyline = googleMap.addPolyline(PolylineOptions().addAll(newDecodedPath))
                polyline?.color = context.getResources().getColor(R.color.colorPrimary1);
                polyline?.width = context.resources.getDimension(R.dimen.selected_polyline_width)
                polyline?.zIndex = 1f
                polyline?.isClickable = true
            }
        }
    }
