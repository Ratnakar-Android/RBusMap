package com.redbus.map.batterystatus;

import com.google.android.gms.maps.model.MarkerOptions;

public interface DragLocationListener {
    void dragLocationUpdate(MarkerOptions markerOptions);
}
