package com.mobileaviationtools.flightsimplannerpro.Route;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.Log;

import com.mobileaviationtools.flightsimplannerpro.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import us.ba3.me.Location;
import us.ba3.me.MapView;
import us.ba3.me.VectorMapInfo;
import us.ba3.me.markers.DynamicMarker;
import us.ba3.me.markers.DynamicMarkerMapInfo;
import us.ba3.me.styles.LineStyle;

/**
 * Created by Rob Verhoef on 28-6-2016.
 */
public class Route extends HashMap<String, Waypoint> {
    public Route(MapView mapView, String name, Context context)
    {
        this.mapView = mapView;
        this.name = name;
        this.markerHit = new MarkerHit(this.mapView, this);
        this.context = context;
    }

    private MapView mapView;
    private String name;
    private MarkerHit markerHit;
    private Context context;
    private Waypoint selectedWaypoint;

    private void removeRouteFromMap()
    {
        mapView.removeMap(name + "markers", true);
        mapView.removeMap(name + "lines", true);
    }

    private void drawRouteOnMap()
    {
        DynamicMarkerMapInfo mapInfo = new DynamicMarkerMapInfo();
        mapInfo.name = name + "markers";
        mapInfo.zOrder = 101;
        mapInfo.hitTestingEnabled = true;
        mapInfo.delegate = markerHit;
        mapView.addMapUsingMapInfo(mapInfo);


        VectorMapInfo vectorMapInfo = new VectorMapInfo();
        vectorMapInfo.name = name + "lines";
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

        Location[] waypoints = new Location[this.size()];

        int i = 0;

        for (Waypoint w: this.values()) {
            waypoints[i] = w.location;
            addMarker(w, R.drawable.bluedot);
            i++;
        }

        mapView.addDynamicLineToVectorMap(name+"lines", "route", waypoints, lineStyle);
    }

    public void SelectWaypoint(String name)
    {
        Waypoint w = this.get(name);
        if (w != null)
        {
            Log.i("Route", "Waypoint selected: " + name);
            resetSelectedWaypointMarker();
            setSelectedwaypointMarker(w);
        }
    }

    public void dragSelectedWaypoint(Location newLocation)
    {
        if (selectedWaypoint != null)
        {
            mapView.removeDynamicMarkerFromMap(this.name+"markers", selectedWaypoint.name);
            mapView.removeDynamicMarkerFromMap(this.name+"markers", selectedWaypoint.name + "s");
            selectedWaypoint.location = newLocation;
            selectedWaypoint.name = newLocation.longitude + "," + newLocation.latitude;
            addMarker(selectedWaypoint, R.drawable.bluedot);
            setSelectedwaypointMarker(selectedWaypoint);
        }
    }

    private void setSelectedwaypointMarker(Waypoint newSelectedwaypoint)
    {
        mapView.hideDynamicMarker(this.name+"markers", newSelectedwaypoint.name);
        mapView.showDynamicMarker(this.name+"markers", newSelectedwaypoint.name + "s");
        selectedWaypoint = newSelectedwaypoint;
    }

    private void resetSelectedWaypointMarker()
    {
        if (selectedWaypoint != null)
        {
            mapView.hideDynamicMarker(this.name+"markers", selectedWaypoint.name + "s");
            mapView.showDynamicMarker(this.name+"markers", selectedWaypoint.name);
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
        waypoint.marker.name = waypoint.name;
        waypoint.marker.setImage(getBitmapFromDrawable(drawable), false);
        waypoint.marker.anchorPoint = new PointF(16,16);
        waypoint.marker.location = waypoint.location;
        waypoint.selectedMarker = new DynamicMarker();
        waypoint.selectedMarker.name = waypoint.name + "s";
        waypoint.selectedMarker.setImage(getBitmapFromDrawable(R.drawable.greendot), false);
        waypoint.selectedMarker.anchorPoint = new PointF(16,16);
        waypoint.selectedMarker.location = waypoint.location;
        mapView.addDynamicMarkerToMap(this.name+"markers", waypoint.marker);
        mapView.addDynamicMarkerToMap(this.name+"markers", waypoint.selectedMarker);
        mapView.hideDynamicMarker(this.name+"markers", waypoint.name + "s");
    }

    public void AddWaypoint(String name, Location location)
    {
        Log.i("Route", "Adding waypoint: " + name);

        Waypoint waypoint = new Waypoint();
        waypoint.location = location;
        waypoint.name = name;
        this.put(waypoint.name, waypoint);
        // redraw route on map
        removeRouteFromMap();
        drawRouteOnMap();
    }

}
