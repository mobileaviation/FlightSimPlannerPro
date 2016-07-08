package com.mobileaviationtools.flightsimplannerpro.Route;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.Log;
import android.widget.Toast;

import com.mobileaviationtools.flightsimplannerpro.R;

import java.util.HashMap;

import us.ba3.me.Location;
import us.ba3.me.MapView;
import us.ba3.me.VectorMapInfo;
import us.ba3.me.markers.DynamicMarker;
import us.ba3.me.markers.DynamicMarkerMapInfo;
import us.ba3.me.styles.LineStyle;

/**
 * Created by Rob Verhoef on 8-7-2016.
 */
public class RouteVisuals extends HashMap<Integer, Waypoint> {
    public RouteVisuals(Context context, MapView mapView, Route route)
    {
        this.mapView = mapView;
        this.context = context;
        this.route = route;
        this.markerHit = new MarkerHit(this.mapView, this);
    }

    private MapView mapView;
    private Context context;
    private Route route;
    private MarkerHit markerHit;
    public Waypoint selectedWaypoint;

    private void removeRouteFromMap()
    {
        mapView.removeMap(route.name + "markers", true);
        mapView.removeMap(route.name + "lines", true);
    }

    private void drawRouteOnMap(Boolean createMarkermap)
    {

        DynamicMarkerMapInfo mapInfo = new DynamicMarkerMapInfo();
        mapInfo.name = route.name + "markers";
        mapInfo.zOrder = 101;
        mapInfo.hitTestingEnabled = true;
        mapInfo.delegate = markerHit;
        mapView.addMapUsingMapInfo(mapInfo);

        VectorMapInfo vectorMapInfo = new VectorMapInfo();
        vectorMapInfo.name = route.name + "lines";
        vectorMapInfo.zOrder = 100;
        vectorMapInfo.alpha = 0.75f;
        //vectorMapInfo.vectorMapDelegate = lineSegmentHit;
        mapView.addMapUsingMapInfo(vectorMapInfo);

        //Create a style for the route line
        LineStyle lineStyle = new LineStyle();
        lineStyle.outlineColor = Color.DKGRAY;
        lineStyle.outlineWidth = 2;
        lineStyle.strokeColor = Color.GREEN;
        lineStyle.strokeWidth = 10;

        Location[] waypoints = new Location[route.waypoints.size()];

        int i = 0;

        for (Waypoint w: route.waypoints) {
            waypoints[i] = w.location;
            addMarker(w, R.drawable.bluedot);
            i++;
        }

        mapView.addDynamicLineToVectorMap(route.name+"lines", "route", waypoints, lineStyle);
    }

    public void SelectWaypoint(String selectedId) {
        Integer s = Integer.parseInt(selectedId);

        Toast toast = Toast.makeText(context, "Waypoint Selected!!", Toast.LENGTH_SHORT);
        toast.show();

        Waypoint w = this.get(s);
        if (w != null) {
            Log.i("Route", "Waypoint selected: " + selectedId);
            resetSelectedWaypointMarker();
            setSelectedwaypointMarker(w);
        }
    }

    public void UnSelectWaypoint()
    {
        resetSelectedWaypointMarker();
    }

    public void dragSelectedWaypoint(Location newLocation)
    {
        if (selectedWaypoint != null)
        {
            //mapView.removeMap(name + "lines", true);
            mapView.removeDynamicMarkerFromMap(route.name+"markers", selectedWaypoint.UID.toString());
            mapView.removeDynamicMarkerFromMap(route.name+"markers", selectedWaypoint.UID.toString() + "s");

            selectedWaypoint.location = newLocation;
            selectedWaypoint.name = newLocation.longitude + "," + newLocation.latitude;

            addMarker(selectedWaypoint, R.drawable.bluedot);
            setSelectedwaypointMarker(selectedWaypoint);

            removeRouteFromMap();
            drawRouteOnMap(false);
        }
    }

    private void setSelectedwaypointMarker(Waypoint newSelectedwaypoint)
    {
        mapView.hideDynamicMarker(route.name+"markers", newSelectedwaypoint.UID.toString());
        mapView.showDynamicMarker(route.name+"markers", newSelectedwaypoint.UID.toString() + "s");
        selectedWaypoint = newSelectedwaypoint;
    }

    private void resetSelectedWaypointMarker()
    {
        if (selectedWaypoint != null)
        {
            mapView.hideDynamicMarker(route.name+"markers", selectedWaypoint.UID.toString() + "s");
            mapView.showDynamicMarker(route.name+"markers", selectedWaypoint.UID.toString());
            selectedWaypoint = null;
        }
    }

    private Bitmap getBitmapFromDrawable(Integer name)
    {
        return BitmapFactory.decodeResource(context.getResources(), name);
    }

    private void addMarker(Waypoint waypoint, Integer drawable)
    {
        waypoint.marker = new DynamicMarker();
        waypoint.marker.name = waypoint.UID.toString();
        waypoint.marker.setImage(getBitmapFromDrawable(drawable), false);
        waypoint.marker.anchorPoint = new PointF(16,16);
        waypoint.marker.location = waypoint.location;
        waypoint.selectedMarker = new DynamicMarker();
        waypoint.selectedMarker.name = waypoint.UID.toString() + "s";
        waypoint.selectedMarker.setImage(getBitmapFromDrawable(R.drawable.greendot), false);
        waypoint.selectedMarker.anchorPoint = new PointF(16,16);
        waypoint.selectedMarker.location = waypoint.location;
        mapView.addDynamicMarkerToMap(route.name+"markers", waypoint.marker);
        mapView.addDynamicMarkerToMap(route.name+"markers", waypoint.selectedMarker);
        mapView.showDynamicMarker(route.name+"markers", waypoint.UID.toString());
        mapView.hideDynamicMarker(route.name+"markers", waypoint.UID.toString() + "s");
    }

    public void AddWaypoint(String name, Location location)
    {
        Log.i("Route", "Adding waypoint: " + name);

        Toast toast = Toast.makeText(context, "Waypoint Added!!", Toast.LENGTH_SHORT);
        toast.show();

        Waypoint waypoint = new Waypoint();
        waypoint.location = location;
        waypoint.name = name;

        route.InsertWaypoint(waypoint);
        this.put(waypoint.UID, waypoint);

        selectedWaypoint = waypoint;
        // redraw route on map
        removeRouteFromMap();
        drawRouteOnMap(true);
    }

}
