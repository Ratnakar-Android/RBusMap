package `in`.redbus.android.rblogger.client

import android.content.Context
import android.util.Log
import com.google.gson.JsonObject
import retrofit2.Call
import android.location.Location
import com.redbus.map.sample.UserClient
import com.redbus.map.sample.listener.OSRMRouteListener
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
     fun callOsrmRouteApi(listener: OSRMRouteListener, handleErrorCode: OSRMRouteListener, lastLocation: Location) {
         this.mLastLocation  = lastLocation

         var app = mContext!!.applicationContext as UserClient

         Log.v("Ratnakar api call", "callRedBusLoggerApi");

         var url   = "https://routing.openstreetmap.de/routed-car/route/v1/driving/"


         var source : String  = mLastLocation!!.longitude.toString()+","+mLastLocation!!.latitude.toString()

         Log.v("Source", "source is "+ mLastLocation!!.longitude.toString() +" "+mLastLocation!!.latitude);


         var destination = "77.680981,12.954519"

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