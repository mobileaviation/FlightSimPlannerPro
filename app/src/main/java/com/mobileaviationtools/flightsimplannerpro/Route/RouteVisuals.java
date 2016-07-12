package com.mobileaviationtools.flightsimplannerpro.Route;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.Log;
import android.widget.Toast;

import com.mobileaviationtools.flightsimplannerpro.R;

import java.util.ArrayList;
import java.util.HashMap;

import us.ba3.me.Location;
import us.ba3.me.MapView;
import us.ba3.me.VectorMapInfo;
import us.ba3.me.markers.DynamicMarker;
import us.ba3.me.markers.DynamicMarkerMapInfo;
import us.ba3.me.markers.MarkerRotationType;
import us.ba3.me.styles.LineStyle;

/**
 * Created by Rob Verhoef on 8-7-2016.
 */
public class RouteVisuals extends HashMap<Integer, Waypoint> {
    public RouteVisuals(Context context, MapView mapView)
    {
        dragging = false;
        this.mapView = mapView;
        this.context = context;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        blueDot = BitmapFactory.decodeResource(context.getResources(), R.drawable.bluedot, options);
        greenDot = BitmapFactory.decodeResource(context.getResources(), R.drawable.greendot, options);
        crossHair = BitmapFactory.decodeResource(context.getResources(), R.drawable.crosshair, options);
        this.mapView.addCachedImage("bluedot", blueDot, true);
        this.mapView.addCachedImage("greendot", greenDot, true);
        this.mapView.addCachedImage("crosshair", crossHair, true);
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
    public Bitmap crossHair;

    private void removeRouteFromMap()
    {
        mapView.removeMap("markers", true);
        mapView.removeMap("lines", true);
    }

    private void setMarkerAndLinesMaps(String markerName, String lineName, Boolean setMarkerHit)
    {
        DynamicMarkerMapInfo mapInfo = new DynamicMarkerMapInfo();
        mapInfo.name = markerName;
        mapInfo.zOrder = 101;
        mapInfo.hitTestingEnabled = setMarkerHit;
        if (setMarkerHit) mapInfo.delegate = markerHit;
        mapView.addMapUsingMapInfo(mapInfo);

        VectorMapInfo vectorMapInfo = new VectorMapInfo();
        vectorMapInfo.name = lineName;
        vectorMapInfo.zOrder = 100;
        vectorMapInfo.alpha = 0.75f;
        //vectorMapInfo.vectorMapDelegate = lineSegmentHit;
        mapView.addMapUsingMapInfo(vectorMapInfo);
        mapView.setTesselationThresholdForMap(lineName, 10);


    }

    private void drawRouteOnMap()
    {
        setMarkerAndLinesMaps("markers", "lines", true);

        Location[] waypoints = new Location[route.waypoints.size()];

        int i = 0;

        for (Waypoint w: route.waypoints) {
            waypoints[i] = w.location;
            addMarker(w);
            i++;
        }

        //Create a style for the route line
        LineStyle lineStyle = new LineStyle();
        lineStyle.outlineColor = Color.DKGRAY;
        lineStyle.outlineWidth = 2;
        lineStyle.strokeColor = Color.GREEN;
        lineStyle.strokeWidth = 10;

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
        if (dragging) {
            dragging = false;
            resetSelectedWaypointMarker();
            mapView.removeMap("draglines", true);
            removeRouteFromMap();
            drawRouteOnMap();
        }
    }

    public void dragSelectedWaypoint1(Location newLocation)
    {
        if (selectedWaypoint != null)
        {
            //mapView.removeMap(name + "lines", true);
            mapView.removeDynamicMarkerFromMap("markers", selectedWaypoint.UID.toString());

            selectedWaypoint.location = newLocation;
            selectedWaypoint.name = newLocation.longitude + "," + newLocation.latitude;

            addMarker(selectedWaypoint);
            setSelectedwaypointMarker(selectedWaypoint);

            removeRouteFromMap();
            drawRouteOnMap();
        }
    }

    private boolean dragging;
    public void dragSelectedWaypoint(Location newLocation)
    {
        Integer i = route.waypoints.indexOf(selectedWaypoint);
        Waypoint beforeWaypoint = (i==0) ? null : route.waypoints.get(i-1);
        Waypoint afterWaypoint = (i==route.waypoints.size()-1) ? null : route.waypoints.get(i+1);
        dragging = true;

        mapView.setDynamicMarkerLocation("markers", selectedWaypoint.UID.toString(), newLocation, 0);
        selectedWaypoint.location = newLocation;

        mapView.removeMap("draglines", false);
        VectorMapInfo vectorMapInfo = new VectorMapInfo();
        vectorMapInfo.name = "draglines";
        vectorMapInfo.zOrder = 100;
        vectorMapInfo.alpha = 0.75f;
        mapView.addMapUsingMapInfo(vectorMapInfo);


        // get before and after waypoint
        Location[] waypoints = new Location[3];
        waypoints[0] = (beforeWaypoint==null) ? null : beforeWaypoint.location;
        waypoints[1] = newLocation;
        waypoints[2] = (afterWaypoint==null) ? null : afterWaypoint.location;
        ArrayList<Location> locations = new ArrayList<>();

        for (Integer ii = 0; ii<3; ii++)
        {
            if (waypoints[ii]!=null) {
                locations.add(waypoints[ii]);
            }
        }

        LineStyle lineStyle = new LineStyle();
        lineStyle.outlineColor = Color.DKGRAY;
        lineStyle.outlineWidth = 2;
        lineStyle.strokeColor = Color.BLUE;
        lineStyle.strokeWidth = 10;

        mapView.addDynamicLineToVectorMap("draglines", "draglines", locations.toArray(new Location[locations.size()]), lineStyle);

    }

    private void setSelectedwaypointMarker(Waypoint newSelectedwaypoint)
    {
        mapView.setDynamicMarkerImage("markers", newSelectedwaypoint.UID.toString(), blueDot);
        selectedWaypoint = newSelectedwaypoint;
    }

    private void resetSelectedWaypointMarker()
    {
        if (selectedWaypoint != null)
        {
            mapView.setDynamicMarkerImage("markers", selectedWaypoint.UID.toString(), greenDot);
            selectedWaypoint = null;
        }
    }


    private void addMarker(Waypoint waypoint)
    {
        waypoint.marker = new DynamicMarker();
        waypoint.marker.name = waypoint.UID.toString();
        waypoint.marker.location = waypoint.location;
        waypoint.marker.anchorPoint = new PointF(16,16);
        //waypoint.marker.rotation = 45;
        //waypoint.marker.rotationType = MarkerRotationType.kMarkerRotationTrueNorthAligned;
        //waypoint.marker.setImage(greenDot, false);
        waypoint.marker.setImage("greendot");

        //waypoint.marker.offset = new PointF(16,16);

        mapView.addDynamicMarkerToMap("markers", waypoint.marker);
        //mapView.showDynamicMarker("markers", waypoint.UID.toString());
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
        marker.setImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.greendot), false);
        marker.anchorPoint = new PointF(16,16);
        marker.hitTestSize = new PointF(64,64);
        marker.location = l;

        DynamicMarkerMapInfo mapInfo = new DynamicMarkerMapInfo();
        mapInfo.name = "markers";
        mapInfo.zOrder = 101;
        mapInfo.hitTestingEnabled = true;
        mapView.addMapUsingMapInfo(mapInfo);

        mapView.addDynamicMarkerToMap("markers", marker);
        //mapView.showDynamicMarker("markers", "test");
    }

}
