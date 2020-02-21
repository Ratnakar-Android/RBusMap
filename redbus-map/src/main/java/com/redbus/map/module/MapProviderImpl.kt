package com.redbus.map.module

import com.google.android.gms.maps.SupportMapFragment

interface MapProviderImpl
{
    fun addMapToView(containerId: Int)
    fun removeMap()
    fun getGPlace(requestCode: Int)

}