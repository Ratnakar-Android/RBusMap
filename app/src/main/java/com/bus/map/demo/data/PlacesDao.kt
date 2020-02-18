package com.bus.map.demo.data

import android.location.Location

class PlacesDao
{
    fun getPlaceData(source: Location?): PlaceData{
        var placeData = PlaceData()
      // placeData.name = source.
        placeData.latitude = source?.latitude
        placeData.longitude = source?.longitude
       // placeData.id = source.id
        return placeData

    }
}