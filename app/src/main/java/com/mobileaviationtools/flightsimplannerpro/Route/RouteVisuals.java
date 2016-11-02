package com.mobileaviationtools.flightsimplannerpro.Route;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.Log;
import android.widget.Toast;

import com.mobileaviationtools.flightsimplannerpro.Helpers;
import com.mobileaviationtools.flightsimplannerpro.R;

import java.util.ArrayList;
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
        dragging = false;
        this.mapView = mapView;
        this.context = context;
        //BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inScaled = false;
        blueDot = BitmapFactory.decodeResource(context.getResources(), R.drawable.bluedot);
        greenDot = BitmapFactory.decodeResource(context.getResources(), R.drawable.greendot);
        addWaypointBtn = BitmapFactory.decodeResource(context.getResources(), R.drawable.add_waypoint_btn);
        removeWaypointBtn = BitmapFactory.decodeResource(context.getResources(), R.drawable.remove_waypoint_btn);

        this.mapView.addCachedImage("bluedot", blueDot, true);
        this.mapView.addCachedImage("greendot", greenDot, true);
        this.mapView.addCachedImage("addwaypoint", addWaypointBtn, true);
        this.mapView.addCachedImage("removewaypoint", removeWaypointBtn, true);
        this.routePointHit = new RoutePointHit(this.mapView, this);
        this.editWaypointBtnHit = new EditWaypointBtnHit(this.mapView, this);
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
    private RoutePointHit routePointHit;
    private EditWaypointBtnHit editWaypointBtnHit;
    public Waypoint selectedWaypoint;
    public Bitmap blueDot;
    public Bitmap greenDot;
    public Bitmap addWaypointBtn;
    public Bitmap removeWaypointBtn;
    public final String TAG = "RouteVisuals";

    private void removeRouteFromMap()
    {
        mapView.removeMap("markers", true);
        mapView.removeMap("lines", true);
        mapView.removeMap("buttons", true);
    }

    private void setMarkerAndLinesMaps(String markerName, String lineName, Boolean setMarkerHit)
    {
        DynamicMarkerMapInfo mapInfo = new DynamicMarkerMapInfo();
        mapInfo.name = markerName;
        mapInfo.zOrder = 141;
        mapInfo.alpha = 0;
        mapInfo.hitTestingEnabled = setMarkerHit;
        if (setMarkerHit) mapInfo.delegate = routePointHit;
        mapView.addMapUsingMapInfo(mapInfo);

        DynamicMarkerMapInfo buttonsMapInfo = new DynamicMarkerMapInfo();
        buttonsMapInfo.name = "buttons";
        buttonsMapInfo.zOrder = 140;
        buttonsMapInfo.alpha = 0;
        buttonsMapInfo.hitTestingEnabled = setMarkerHit;
        if (setMarkerHit) buttonsMapInfo.delegate = editWaypointBtnHit;
        mapView.addMapUsingMapInfo(buttonsMapInfo);

        VectorMapInfo vectorMapInfo = new VectorMapInfo();
        vectorMapInfo.name = lineName;
        vectorMapInfo.zOrder = 130;
        vectorMapInfo.alpha = 0.75f;
        //vectorMapInfo.vectorMapDelegate = lineSegmentHit;
        mapView.addMapUsingMapInfo(vectorMapInfo);

        addWaypointEditBtns();
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
        lineStyle.outlineWidth = Helpers.convertDpToPixel(2, this.context);;
        lineStyle.strokeColor = Color.GREEN;
        lineStyle.strokeWidth = Helpers.convertDpToPixel(10, this.context);;

        mapView.addDynamicLineToVectorMap("lines", "route", waypoints, lineStyle);
    }

    public void SelectWaypoint(String selectedId) {
        Integer s = Integer.parseInt(selectedId);
        Waypoint w = this.get(s);

        if (selectedWaypoint != null)
        {
            if (selectedWaypoint.UID == w.UID)
            {
                resetSelectedWaypointMarker();
                return;
            }
        }

        if (w != null) {
            Log.i(TAG, "Waypoint selected: " + selectedId);
            resetSelectedWaypointMarker();
            setSelectedwaypointMarker(w);
        }
    }

    public void UnSelectWaypoint()
    {
        if (dragging) {
            route.UpdateWaypointsData();
            route.UpdateWaypointsDatabase();
            dragging = false;
            resetSelectedWaypointMarker();
            mapView.removeMap("draglines", true);
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
        vectorMapInfo.zOrder = 101;
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
        lineStyle.outlineWidth = Helpers.convertDpToPixel(2, this.context);
        lineStyle.strokeColor = Color.BLUE;
        lineStyle.strokeWidth = Helpers.convertDpToPixel(10, this.context);

        mapView.addDynamicLineToVectorMap("draglines", "draglines", locations.toArray(new Location[locations.size()]), lineStyle);

    }

    private void setSelectedwaypointMarker(Waypoint newSelectedwaypoint)
    {
        mapView.setDynamicMarkerImage("markers", newSelectedwaypoint.UID.toString(), blueDot);
        selectedWaypoint = newSelectedwaypoint;

        setWaypointEditBtns(selectedWaypoint.location);
    }

    private void setWaypointEditBtns(Location location)
    {
        mapView.setDynamicMarkerLocation("buttons", "addWaypointBtn", location, 0);
        mapView.setDynamicMarkerLocation("buttons", "removeWaypointBtn", location, 0);
        mapView.showDynamicMarker("buttons", "addWaypointBtn");
        mapView.showDynamicMarker("buttons", "removeWaypointBtn");
    }

    private void addWaypointEditBtns()
    {
        DynamicMarker addWaypointMarker = new DynamicMarker();
        addWaypointMarker.setImage("addwaypoint");
        addWaypointMarker.name = "addWaypointBtn";
        addWaypointMarker.anchorPoint = new PointF(Helpers.convertDpToPixel(-25, context),
                Helpers.convertDpToPixel(60, context));
        addWaypointMarker.location = new Location(1, 1);
        mapView.addDynamicMarkerToMap("buttons",  addWaypointMarker);
        mapView.hideDynamicMarker("buttons", "addWaypointBtn");

        DynamicMarker removeWaypointMarker = new DynamicMarker();
        removeWaypointMarker.setImage("removewaypoint");
        removeWaypointMarker.name = "removeWaypointBtn";
        removeWaypointMarker.anchorPoint = new PointF(Helpers.convertDpToPixel(-25, context),
                Helpers.convertDpToPixel(-10, context));
        removeWaypointMarker.location = new Location(1, 1);
        mapView.addDynamicMarkerToMap("buttons",  removeWaypointMarker);
        mapView.hideDynamicMarker("buttons", "removeWaypointBtn");
    }

    private void resetSelectedWaypointMarker()
    {
        if (selectedWaypoint != null)
        {
            mapView.setDynamicMarkerImage("markers", selectedWaypoint.UID.toString(), greenDot);
            mapView.hideDynamicMarker("buttons", "addWaypointBtn");
            mapView.hideDynamicMarker("buttons", "removeWaypointBtn");
            
            selectedWaypoint = null;
        }
    }


    private void addMarker(Waypoint waypoint)
    {
        waypoint.marker = new DynamicMarker();
        waypoint.marker.name = waypoint.UID.toString();
        waypoint.marker.location = waypoint.location;
        waypoint.marker.anchorPoint = new PointF(Helpers.convertDpToPixel(16, this.context),
                Helpers.convertDpToPixel(16, this.context));
        //waypoint.marker.rotation = 45;
        //waypoint.marker.rotationType = MarkerRotationType.kMarkerRotationTrueNorthAligned;
        //waypoint.marker.setImage(greenDot, false);
        waypoint.marker.setImage("greendot");

        //waypoint.marker.offset = new PointF(16,16);

        mapView.addDynamicMarkerToMap("markers", waypoint.marker);
        //mapView.showDynamicMarker("markers", waypoint.UID.toString());
    }

//    public void AddWaypoint(String name, Location location)
//    {
//        Log.i("Route", "Adding waypoint: " + name);
//
//        Toast toast = Toast.makeText(context, "Waypoint Added!!", Toast.LENGTH_SHORT);
//        toast.show();
//
//        Waypoint waypoint = new Waypoint();
//        waypoint.location = location;
//        waypoint.name = name;
//
//        route.InsertWaypoint(waypoint);
//        this.put(waypoint.UID, waypoint);
//
//        selectedWaypoint = waypoint;
//        // redraw route on map
//        removeRouteFromMap();
//        drawRouteOnMap();
//    }

    public void removeWaypoint()
    {
        if (selectedWaypoint != null)
        {
            route.DeleteWaypointFromDatabase(selectedWaypoint);

            route.waypoints.remove(selectedWaypoint);
            mapView.removeDynamicMarkerFromMap("markers", selectedWaypoint.UID.toString());

            selectedWaypoint = null;

            route.UpdateWaypointsData();

            removeRouteFromMap();
            drawRouteOnMap();

            mapView.hideDynamicMarker("buttons", "addWaypointBtn");
            mapView.hideDynamicMarker("buttons", "removeWaypointBtn");

        }
    }

    public void insertWaypoint()
    {
        if (selectedWaypoint != null)
        {
            Waypoint waypoint = route.InsertWaypoint(selectedWaypoint);

            if (waypoint != null) {
                this.put(waypoint.UID, waypoint);

                selectedWaypoint = waypoint;
                removeRouteFromMap();
                drawRouteOnMap();

                mapView.hideDynamicMarker("buttons", "addWaypointBtn");
                mapView.hideDynamicMarker("buttons", "removeWaypointBtn");

                mapView.setDynamicMarkerImage("markers", selectedWaypoint.UID.toString(), blueDot);
            }
            else
            {
                UnSelectWaypoint();
                Toast toast = Toast.makeText(context, "Cannot add waypoint before departure airport!", Toast.LENGTH_SHORT);
                toast.show();
            }

        }
    }

}
