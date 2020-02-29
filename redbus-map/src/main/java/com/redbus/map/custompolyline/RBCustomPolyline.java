package com.redbus.map.custompolyline;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.maps.model.SquareCap;
import com.redbus.map.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.android.gms.maps.model.JointType.ROUND;

public class RBCustomPolyline {

    private static RBCustomPolyline singleton = new RBCustomPolyline( );

    /* Static 'instance' method */
    public static RBCustomPolyline getInstance() {
        return singleton;
    }

    public RBCustomPolyline() {
    }

    private static final int PATTERN_DASH_LENGTH_PX = 10;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DOT);

    private static final int POLYLINE_STROKE_WIDTH_PX = 12;

    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int COLOR_WHITE_ARGB = 0xffffffff;
    private static final int COLOR_GREEN_ARGB = 0xff388E3C;
    private static final int COLOR_PURPLE_ARGB = 0xff81C784;
    private static final int COLOR_ORANGE_ARGB = 0xffF57F17;
    private static final int COLOR_BLUE_ARGB = 0xffF9A825;

    private static final int POLYGON_STROKE_WIDTH_PX = 8;


    Marker marker;
    float v;
    double lat, lng;
    Handler handler;
    LatLng startPosition, endPosition;
    int index, next;
    LatLng sydney;
    String destination;
    PolylineOptions polylineOptions, blackPolylineOptions;
    Polyline blackPolyline, greyPolyLine;


    // Create a stroke pattern of a gap followed by a dash.


    private void stylePolygon(Polygon polygon) {
        String type = "";
        // Get the data object stored with the polygon.
        if (polygon.getTag() != null) {
            type = polygon.getTag().toString();
        }

        List<PatternItem> pattern = null;
        int strokeColor = COLOR_BLACK_ARGB;
        int fillColor = COLOR_WHITE_ARGB;

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "alpha":
                // Apply a stroke pattern to render a dashed line, and define colors.
                pattern = PATTERN_POLYGON_ALPHA;
                strokeColor = COLOR_GREEN_ARGB;
                fillColor = COLOR_PURPLE_ARGB;
                break;
            case "beta":
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
               // pattern = PATTERN_POLYGON_BETA;
                strokeColor = COLOR_ORANGE_ARGB;
                fillColor = COLOR_BLUE_ARGB;
                break;
        }

        polygon.setStrokePattern(pattern);
        polygon.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
        polygon.setStrokeColor(strokeColor);
        polygon.setFillColor(fillColor);
    }

    private void stylePolyline(Polyline polyline) {
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "A":
                // Use a custom bitmap as the cap at the start of the line.
//                polyline.setStartCap(
//                        new CustomCap(
//                                BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow), 10));
                break;
            case "B":
                // Use a round cap at the start of the line.
                polyline.setStartCap(new RoundCap());
                break;
        }

        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_BLACK_ARGB);
        polyline.setJointType(JointType.ROUND);
    }



    public ArrayList<PolylineOptions> getDotPolyline(Context context, List<LatLng> polylinePoints, GoogleMap map){
       // Adjsuting the bounds

         LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latlong: polylinePoints) {
            builder.include(latlong);
        }

        LatLngBounds bounds = builder.build();
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
        map.animateCamera(mCameraUpdate);

        PolylineOptions  greyPolyOptions  = new PolylineOptions();
        greyPolyOptions.color(ContextCompat.getColor(context, R.color.colorPrimary1));
        greyPolyOptions.pattern(PATTERN_POLYGON_ALPHA);
        greyPolyOptions.startCap(new SquareCap());
        greyPolyOptions.endCap(new SquareCap());
        greyPolyOptions.jointType(JointType.ROUND);

        PolylineOptions blackPolyOptions  = new PolylineOptions();
        blackPolyOptions.color(ContextCompat.getColor(context, R.color.colorPrimary1));
        blackPolyOptions.pattern(PATTERN_POLYGON_ALPHA);
        blackPolyOptions.startCap(new SquareCap());
        blackPolyOptions.endCap(new SquareCap());
        blackPolyOptions.jointType(JointType.ROUND);

        ArrayList<PolylineOptions> options = new ArrayList<>();
        options.add(greyPolyOptions);
        options.add(blackPolyOptions);


        return options;

    }

    public PolylineOptions getDashPolyline(Context context){
        final int PATTERN_DASH_LENGTH_PX = 10;
        final int PATTERN_GAP_LENGTH_PX = 20;
        final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
        final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
        final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(ContextCompat.getColor(context, R.color.colorPrimary1));
        polyOptions.pattern(PATTERN_POLYGON_ALPHA);
        return polyOptions;
    }


    public void drawPolyLineAndAnimateCar(Location myCurrent, @NotNull Context context, @NotNull List<LatLng> polyLineList, @NotNull GoogleMap mMap) {


        //Adjusting bounds
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : polyLineList) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
        mMap.animateCamera(mCameraUpdate);

        polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.GRAY);
        polylineOptions.width(8);
        polylineOptions.startCap(new SquareCap());
        polylineOptions.endCap(new SquareCap());
        polylineOptions.jointType(ROUND);
        polylineOptions.addAll(polyLineList);
        greyPolyLine = mMap.addPolyline(polylineOptions);

        blackPolylineOptions = new PolylineOptions();
        blackPolylineOptions.width(8);
        blackPolylineOptions.color(Color.BLACK);
        blackPolylineOptions.startCap(new SquareCap());
        blackPolylineOptions.endCap(new SquareCap());
        blackPolylineOptions.jointType(ROUND);
        blackPolyline = mMap.addPolyline(blackPolylineOptions);

        mMap.addMarker(new MarkerOptions()
                .position(polyLineList.get(polyLineList.size() - 1)));

        ValueAnimator polylineAnimator = ValueAnimator.ofInt(0, 100);
        polylineAnimator.setDuration(2000);
        polylineAnimator.setInterpolator(new LinearInterpolator());
        polylineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                List<LatLng> points = greyPolyLine.getPoints();
                int percentValue = (int) valueAnimator.getAnimatedValue();
                int size = points.size();
                int newPoints = (int) (size * (percentValue / 100.0f));
                List<LatLng> p = points.subList(0, newPoints);
                blackPolyline.setPoints(p);
            }
        });
        polylineAnimator.start();
        LatLng myLatlng = new LatLng(myCurrent.getLatitude(),myCurrent.getLongitude());
        marker = mMap.addMarker(new MarkerOptions().position(myLatlng)
                .flat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car)));
        handler = new Handler();
        index = -1;
        next = 1;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (index < polyLineList.size() - 1) {
                    index++;
                    next = index + 1;
                }
                if (index < polyLineList.size() - 1) {
                    startPosition = polyLineList.get(index);
                    endPosition = polyLineList.get(next);
                }
//                if (index == 0) {
//                    BeginJourneyEvent beginJourneyEvent = new BeginJourneyEvent();
//                    beginJourneyEvent.setBeginLatLng(startPosition);
//                    JourneyEventBus.getInstance().setOnJourneyBegin(beginJourneyEvent);
//                }
//                if (index == polyLineList.size() - 1) {
//                    EndJourneyEvent endJourneyEvent = new EndJourneyEvent();
//                    endJourneyEvent.setEndJourneyLatLng(new LatLng(polyLineList.get(index).latitude,
//                            polyLineList.get(index).longitude));
//                    JourneyEventBus.getInstance().setOnJourneyEnd(endJourneyEvent);
//                }
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                valueAnimator.setDuration(3000);
                valueAnimator.setInterpolator(new LinearInterpolator());
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        v = valueAnimator.getAnimatedFraction();
                        lng = v * endPosition.longitude + (1 - v)
                                * startPosition.longitude;
                        lat = v * endPosition.latitude + (1 - v)
                                * startPosition.latitude;
                        LatLng newPos = new LatLng(lat, lng);
//                        CurrentJourneyEvent currentJourneyEvent = new CurrentJourneyEvent();
//                        currentJourneyEvent.setCurrentLatLng(newPos);
//                        JourneyEventBus.getInstance().setOnJourneyUpdate(currentJourneyEvent);
                        marker.setPosition(newPos);
                        marker.setAnchor(0.5f, 0.5f);
                        marker.setRotation(getBearing(startPosition, newPos));
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition
                                (new CameraPosition.Builder().target(newPos)
                                        .zoom(15.5f).build()));
                    }
                });
                valueAnimator.start();
                if (index != polyLineList.size() - 1) {
                    handler.postDelayed(this, 3000);
                }
            }
        }, 3000);
    }


    private float getBearing(LatLng startPosition, LatLng newPos){

        double lat  = Math.abs(startPosition.latitude - newPos.latitude);
        double lng  = Math.abs(startPosition.longitude - newPos.longitude);

        if (startPosition.latitude < newPos.latitude && startPosition.longitude < newPos.longitude)
            return (float) (Math.toDegrees(Math.atan(lng/lat)));

       else if (startPosition.latitude >= newPos.latitude && startPosition.longitude < newPos.longitude)
            return (float)((90 - Math.toDegrees(Math.atan(lng/lat)))+ 90);


       else if (startPosition.latitude >=  newPos.latitude && startPosition.longitude >= newPos.longitude)
            return (float) Math.toDegrees(Math.atan(lng/lat) + 180);


        else if (startPosition.latitude < newPos.latitude && startPosition.longitude >= newPos.longitude)
            return (float)((90 - Math.toDegrees(Math.atan(lng/lat)))+ 270);

    return -1;
    }
}
