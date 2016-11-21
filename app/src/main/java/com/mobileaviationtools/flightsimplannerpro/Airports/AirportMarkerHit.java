package com.mobileaviationtools.flightsimplannerpro.Airports;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;

import com.mobileaviationtools.flightsimplannerpro.InfoWindow;

import us.ba3.me.markers.DynamicMarkerMapDelegate;

/**
 * Created by Rob Verhoef on 31-10-2016.
 */

public class AirportMarkerHit implements DynamicMarkerMapDelegate {
    public AirportMarkerHit(Context context)
    {
        this.context = context;
    }

    private Context context;
    private String TAG = "AirportMarkerHit";

    @Override
    public void tapOnMarker(String mapName, String markerName, PointF screenPoint, PointF markerPoint) {
        Log.w(TAG,"Airport was tapped on" +
                " Name:" + markerName +
                " location:" + screenPoint.x + "," + screenPoint.y +
                " markerPoint:" + markerPoint.x + "," + markerPoint.y);// +
                //" location:" + location.longitude + "," + location.latitude);
        InfoWindow infoWindow = new InfoWindow(context);
        infoWindow.ShowInfoWindow(markerName);

    }
}
