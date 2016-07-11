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
    public RouteVisuals(Context context, MapView mapView)
    {
        this.mapView = mapView;
        this.context = context;
        blueDot = BitmapFactory.decodeResource(context.getResources(), R.drawable.bluedot);
        greenDot = BitmapFactory.decodeResource(context.getResources(), R.drawable.greendot);
        this.markerHit = new MarkerHit(this.mapView, this);
    }

    public void setRoute(Route route)
    {
        this.route = route;
        this.clear();
        for(Waypoint w: this.route.waypoints)
        {
            this.put(w.UID, w);
        }
        removeRouteFromMap();
        drawRouteOnMap();
    }

    public void drawRoute()
    {
        if (this.route != null) {
            drawRouteOnMap();
            this.route.reloaded = false;
        }
    }

    private MapView mapView;
    private Context context;
    private Route route;
    private MarkerHit markerHit;
    public Waypoint selectedWaypoint;
    public Bitmap blueDot;
    public Bitmap greenDot;

    private void removeRouteFromMap()
    {
        mapView.removeMap("markers", true);
        mapView.removeMap("lines", true);
    }

    private void drawRouteOnMap()
    {

        DynamicMarkerMapInfo mapInfo = new DynamicMarkerMapInfo();
        mapInfo.name = "markers";
        mapInfo.zOrder = 101;
        mapInfo.hitTestingEnabled = true;
        mapInfo.delegate = markerHit;
        mapView.addMapUsingMapInfo(mapInfo);

        VectorMapInfo vectorMapInfo = new VectorMapInfo();
        vectorMapInfo.name = "lines";
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
            addMarker(w);
            i++;
        }

        mapView.addDynamicLineToVectorMap("lines", "route", waypoints, lineStyle);
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
            mapView.removeDynamicMarkerFromMap("markers", selectedWaypoint.UID.toString());
            mapView.removeDynamicMarkerFromMap("markers", selectedWaypoint.UID.toString() + "s");

            selectedWaypoint.location = newLocation;
            selectedWaypoint.name = newLocation.longitude + "," + newLocation.latitude;

            addMarker(selectedWaypoint);
            setSelectedwaypointMarker(selectedWaypoint);

            removeRouteFromMap();
            drawRouteOnMap();
        }
    }

    private void setSelectedwaypointMarker(Waypoint newSelectedwaypoint)
    {
        mapView.hideDynamicMarker("markers", newSelectedwaypoint.UID.toString());
        mapView.showDynamicMarker("markers", newSelectedwaypoint.UID.toString() + "s");
        selectedWaypoint = newSelectedwaypoint;
    }

    private void resetSelectedWaypointMarker()
    {
        if (selectedWaypoint != null)
        {
            mapView.hideDynamicMarker("markers", selectedWaypoint.UID.toString() + "s");
            mapView.showDynamicMarker("markers", selectedWaypoint.UID.toString());
            selectedWaypoint = null;
        }
    }


    private void addMarker(Waypoint waypoint)
    {
        waypoint.marker = new DynamicMarker();
        waypoint.marker.name = waypoint.UID.toString();
        waypoint.marker.setImage(greenDot, false);
        waypoint.marker.anchorPoint = new PointF(16,16);
        waypoint.marker.location = waypoint.location;
        waypoint.selectedMarker = new DynamicMarker();
        waypoint.selectedMarker.name = waypoint.UID.toString() + "s";
        waypoint.selectedMarker.setImage(blueDot, false);
        waypoint.selectedMarker.anchorPoint = new PointF(16,16);
        waypoint.selectedMarker.location = waypoint.location;
        mapView.addDynamicMarkerToMap("markers", waypoint.marker);
        mapView.addDynamicMarkerToMap("markers", waypoint.selectedMarker);
        mapView.showDynamicMarker("markers", waypoint.UID.toString());
        mapView.hideDynamicMarker("markers", waypoint.UID.toString() + "s");
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
        drawRouteOnMap();
    }

    public void markerTestje()
    {
        Location l = new Location();
        l.longitude = 5.6129;
        l.latitude = 52.302;

        DynamicMarker marker = new DynamicMarker();
        marker.name = "test";
        marker.setImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.bluedot), false);
        marker.anchorPoint = new PointF(16,16);
        marker.location = l;

        DynamicMarkerMapInfo mapInfo = new DynamicMarkerMapInfo();
        mapInfo.name = "markers";
        mapInfo.zOrder = 101;
        mapInfo.hitTestingEnabled = true;
        mapView.addMapUsingMapInfo(mapInfo);

        mapView.addDynamicMarkerToMap("markers", marker);
        mapView.showDynamicMarker("markers", "test");
    }

}
