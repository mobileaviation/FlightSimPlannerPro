package com.mobileaviationtools.flightsimplannerpro.Route;

import android.graphics.PointF;
import android.util.Log;

import us.ba3.me.ConvertPointCallback;
import us.ba3.me.Location;
import us.ba3.me.MapView;
import us.ba3.me.markers.DynamicMarkerMapDelegate;

/**
 * Created by Rob Verhoef on 28-6-2016.
 */
public class MarkerHit implements DynamicMarkerMapDelegate {
    public MarkerHit(MapView mapView, Route route)
    {
        this.mapView = mapView;
        this.route = route;
    }

    private MapView mapView;
    private Route route;

    @Override
    public void tapOnMarker(String mapName, final String markerName, final PointF screenPoint, final PointF markerPoint) {
        mapView.getLocationForPoint(new PointF(screenPoint.x, screenPoint.y), new ConvertPointCallback() {
            @Override
            public void convertComplete(Location location) {
                Log.w("Route","Marker was tapped on" +
                        " Name:" + markerName +
                        " location:" + screenPoint.x + "," + screenPoint.y +
                        " markerPoint:" + markerPoint.x + "," + markerPoint.y +
                        " location:" + location.longitude + "," + location.latitude);


                route.SelectWaypoint(markerName);

            }
        });

    }

//    private OnWaypointSelected onWaypointSelected = null;
//
//    public void setOnWaypointSelected(final OnWaypointSelected waypointSelected) {
//        onWaypointSelected = waypointSelected;
//    }
//    public interface OnWaypointSelected{
//        public void OnWaypoint(Waypoint waypoint);
//    };
}