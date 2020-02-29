package com.redbus.map.module;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.redbus.map.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class RMapCustomMarkerProvider implements GoogleMap.OnMarkerClickListener {

    private static Context mContext;
    private static final String TAG  = "RMapCustomMarke";
    private static final LatLng ram = new LatLng(12.968037, 77.652099);
    private static final LatLng shyam = new LatLng(12.968665, 77.645243 );
    private static final LatLng kiran = new LatLng(12.962009, 77.649696);
    private static final LatLng jai = new LatLng(12.960357, 77.666438);
    private static final LatLng ravi = new LatLng(12.970669, 77.659054);

    private GoogleMap mMap;


    private static RMapCustomMarkerProvider singleton = new RMapCustomMarkerProvider( );

    /* Static 'instance' method */
    public static RMapCustomMarkerProvider getInstance(Context context) {
        mContext = context;
        return singleton;
    }

    public RMapCustomMarkerProvider() {
    }

    /** Called when the map is ready. */
    public void createMarker(GoogleMap map){
        mMap = map;
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.addMarker(new MarkerOptions().position(ram).
                        icon(BitmapDescriptorFactory.fromBitmap(
                                createCustomMarker(mContext, R.drawable.ram,"+1")))).setTitle("Location of John");
                mMap.addMarker(new MarkerOptions().position(shyam).
                        icon(BitmapDescriptorFactory.fromBitmap(
                                createCustomMarker(mContext,R.drawable.shyam,"+2")))).setTitle("Location of Ratnakar");
                mMap.addMarker(new MarkerOptions().position(kiran).
                        icon(BitmapDescriptorFactory.fromBitmap(
                                createCustomMarker(mContext ,R.drawable.kiran,"+3")))).setTitle("Location of Ajit");
                mMap.addMarker(new MarkerOptions().position(jai).
                        icon(BitmapDescriptorFactory.fromBitmap(
                                createCustomMarker(mContext,R.drawable.jai,"+5")))).setTitle("Location of Ram");
                mMap.addMarker(new MarkerOptions().position(ravi).
                        icon(BitmapDescriptorFactory.fromBitmap(
                                createCustomMarker(mContext,R.drawable.ravi,"+6")))).setTitle("Location of Arif");
            }
        });
        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        // Retrieve the data from the marker.
        String markerName = (String) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (markerName != null) {
            Log.v(TAG, "Marker name "+markerName);
        }
        return false;
    }

    public static Bitmap createCustomMarker(Context context, @DrawableRes int resource, String _name) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);
        markerImage.setImageResource(resource);
        TextView txt_name = (TextView)marker.findViewById(R.id.name);
        txt_name.setText(_name);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);
        return bitmap;
    }
}
