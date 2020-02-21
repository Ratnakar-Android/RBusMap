package com.redbus.map.module

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.redbus.map.R
import java.util.*

class PlacesUtil(var mContext: Context?)
{

    companion object {
        @JvmStatic
        @Volatile
        private var INSTANCE: PlacesUtil? = null

        @JvmStatic
        fun getInstance(context: Context?): PlacesUtil {
            return INSTANCE ?: synchronized(this) {
                PlacesUtil(context).also { INSTANCE = it }
            }
        }
    }

        fun getGPlace(context: Context, requestCode: Int) {
            initialisePlacesIfNot(context)
            var fields = getPlaceFields()
            var intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields
            ).build(context)
            (context as? AppCompatActivity)?.startActivityForResult(intent, requestCode)
        }

        private fun getPlaceFields() =
            Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

        fun initialisePlacesIfNot(context: Context){
            if (!Places.isInitialized()) {
                Places.initialize(context.getApplicationContext(), context.getString(R.string.google_maps_key))
            }
        }
}