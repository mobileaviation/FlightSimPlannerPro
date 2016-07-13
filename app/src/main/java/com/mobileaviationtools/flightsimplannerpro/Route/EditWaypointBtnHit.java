package com.mobileaviationtools.flightsimplannerpro.Route;

import android.graphics.PointF;
import android.util.Log;

import us.ba3.me.ConvertPointCallback;
import us.ba3.me.Location;
import us.ba3.me.MapView;
import us.ba3.me.markers.DynamicMarkerMapDelegate;

/**
 * Created by Rob Verhoef on 13-7-2016.
 */
public class EditWaypointBtnHit implements DynamicMarkerMapDelegate {
    public EditWaypointBtnHit(MapView mapView, RouteVisuals route)
    {
        this.mapView = mapView;
        this.route = route;
    }

    private MapView mapView;
    private RouteVisuals route;

    @Override
    public void tapOnMarker(String mapName, final String markerName, final PointF screenPoint, final PointF markerPoint) {
        mapView.getLocationForPoint(new PointF(screenPoint.x, screenPoint.y), new ConvertPointCallback() {
            @Override
            public void convertComplete(Location location) {

            }
        });

        Log.i("EditWaypointBtnHit", "Hit marker: " + markerName);

        if (markerName.equals("addWaypointBtn")){
            route.insertWaypoint();
        }
        if (markerName.equals("removeWaypointBtn")){
            route.removeWaypoint();
        }
    }
}

