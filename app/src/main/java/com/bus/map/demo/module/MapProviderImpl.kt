package com.bus.map.demo.module

import com.google.android.libraries.places.api.model.Place

interface MapProviderImpl
{
    fun addMapToView(containerId: Int)
    fun removeMap()
   // fun setDestinationItemOnMap()
    fun getGPlace(requestCode: Int)

    @Throws(MapExceptions::class)
    fun saveSelectedPath()

    fun renderPreviousPath()
    fun getPreviousSource():String?
    fun getPreviousDestination():String?
}