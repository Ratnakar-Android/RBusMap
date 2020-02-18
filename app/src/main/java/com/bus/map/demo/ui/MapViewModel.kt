package com.bus.map.demo.ui

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.content.Context
import android.net.ConnectivityManager
import com.bus.map.demo.R
import com.bus.map.demo.module.MapExceptions
import com.bus.map.demo.module.MapProviderImpl

class MapViewModel: ViewModel(){

    val SOURCE_AUTOCOMPLETE_REQUEST_CODE = 1001
    val DESTINATION_AUTOCOMPLETE_REQUEST_CODE = 1002
    var context: Context? = null
    var mapProviderImpl: MapProviderImpl? = null

    fun init(context: Context, mapProviderImpl: MapProviderImpl){
        this.context = context
        this.mapProviderImpl = mapProviderImpl
    }
    val previousSource: MutableLiveData<String>? by lazy {
        MutableLiveData<String>()
    }
    val previousDestination: MutableLiveData<String>? by lazy {
        MutableLiveData<String>()
    }

    val networkCheckFailed: MutableLiveData<Boolean>? by lazy {
        MutableLiveData<Boolean>()
    }
    val errorState: MutableLiveData<String>? by lazy {
        MutableLiveData<String>()
    }

    fun searchForSource(view: View){
        if(isOnline()){
            mapProviderImpl?.getGPlace(SOURCE_AUTOCOMPLETE_REQUEST_CODE)
        }else{
            networkCheckFailed?.postValue(false)
        }
    }

    fun searchForDestination(view: View){
        if(isOnline()) {
            mapProviderImpl?.getGPlace(DESTINATION_AUTOCOMPLETE_REQUEST_CODE)
        }else{
            networkCheckFailed?.postValue(false)
        }
    }
    fun saveClickListener(view: View){
        try {
            if(isOnline()){
                mapProviderImpl?.saveSelectedPath()
                errorState?.postValue(context?.getString(R.string.data_save_message))
            }else{
                networkCheckFailed?.postValue(false)
            }
            
        }catch (exception: MapExceptions){
            errorState?.postValue(exception.message)
        }
    }

    fun displayPreviousClickListener(view: View){
        mapProviderImpl?.renderPreviousPath()
        previousSource?.postValue(mapProviderImpl?.getPreviousSource())
        previousDestination?.postValue(mapProviderImpl?.getPreviousDestination())
    }

    fun isOnline(): Boolean {
        val conMgr =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = conMgr.activeNetworkInfo
        if (netInfo == null || !netInfo.isConnected || !netInfo.isAvailable) {
            return false
        }
        return true
    }

}