package `in`.redbus.android.rblogger.client

import android.content.Context
import android.util.Log
import com.bus.map.demo.UserClient
import com.bus.map.demo.listener.OSRMRouteListener
import com.google.gson.JsonObject
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import android.R.id
import android.R.attr.data
import android.location.Location
import com.bus.map.demo.module.OSRMApi
import retrofit2.Callback
import retrofit2.Response


class RedBusMapRepository(var mContext: Context?)  {
    private val TAG = "RedBusMapRepository"
    private var mLastLocation: Location? = null
    companion object {
        @JvmStatic
        @Volatile
        private var INSTANCE: RedBusMapRepository? = null

        @JvmStatic
        fun getInstance(context: Context?): RedBusMapRepository {
            return INSTANCE ?: synchronized(this) {
                RedBusMapRepository(context).also { INSTANCE = it }
            }
        }
    }
     fun callRedBusLoggerApi(listener: OSRMRouteListener, handleErrorCode: OSRMRouteListener, lastLocation: Location) {
         this.mLastLocation  = lastLocation
         var app = mContext!!.applicationContext as UserClient

         var url   = "https://routing.openstreetmap.de/routed-car/route/v1/driving/"


         var source : String  = mLastLocation!!.longitude.toString()+","+mLastLocation!!.latitude.toString()
         var destination = "77.697376,12.986923"
         url += source
         url += ";"
         url += destination

         Log.d("Ratnakar", "Complete url is "+url.toString())

         val call = app.osrmApi.getOSRMRouteData(url)
         call.enqueue(object : Callback<JsonObject>{
             override fun onFailure(call: Call<JsonObject>, t: Throwable) {
             }
             override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                 if (response != null) {
                    listener.onResult(response.body())
                }
             }
         })
     }

}