package com.redbus.map.custompolyline;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.redbus.map.R;

import java.util.Arrays;
import java.util.List;

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

    PolylineOptions polyOptions;

    public PolylineOptions getDotPolyline(Context context){

        polyOptions  = new PolylineOptions();
        polyOptions.color(ContextCompat.getColor(context, R.color.colorPrimary1));
        polyOptions.pattern(PATTERN_POLYGON_ALPHA);
        return polyOptions;

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




}
