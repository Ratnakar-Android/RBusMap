package com.redbus.map.module

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.redbus.map.R
import com.redbus.map.data.PlaceData
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

        fun getPolyLine(context: Context,
                             mapsLatLongs: List<com.google.android.gms.maps.model.LatLng>): ArrayList<LatLng> {

            val newDecodedPath = ArrayList<LatLng>()

            Handler(Looper.getMainLooper()).post {

                // This loops through all the LatLng coordinates of ONE polyline.
                for (latLng in mapsLatLongs) {

                    newDecodedPath.add(LatLng(
                            latLng.latitude,
                            latLng.longitude
                    ))
                }




            }
            return newDecodedPath
        }
    }
