package com.mobileaviationtools.flightsimplannerpro.Airports;

import android.graphics.PointF;
import android.util.Log;

import us.ba3.me.markers.DynamicMarkerMapDelegate;

/**
 * Created by Rob Verhoef on 31-10-2016.
 */

public class AirportMarkerHit implements DynamicMarkerMapDelegate {
    public AirportMarkerHit()
    {

    }

    private String TAG = "AirportMarkerHit";

    @Override
    public void tapOnMarker(String mapName, String markerName, PointF screenPoint, PointF markerPoint) {
        Log.w(TAG,"Marker was tapped on" +
                " Name:" + markerName +
                " location:" + screenPoint.x + "," + screenPoint.y +
                " markerPoint:" + markerPoint.x + "," + markerPoint.y);// +
                //" location:" + location.longitude + "," + location.latitude);
    }
}
