package com.mobileaviationtools.flightsimplannerpro.Route;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.Log;
import android.widget.Toast;

import com.mobileaviationtools.flightsimplannerpro.Airports.Airport;
import com.mobileaviationtools.flightsimplannerpro.Property;
import com.mobileaviationtools.flightsimplannerpro.R;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.operation.buffer.BufferOp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
public class Route extends HashMap<Integer, Waypoint> {
    public Route(MapView mapView, String name, Context context)
    {
        this.mapView = mapView;
        this.name = name;
        this.markerHit = new MarkerHit(this.mapView, this);
        this.context = context;
        waypoint_Id = 0;

        departure_airport = new Airport();
        destination_airport = new Airport();
        alternate_airport = new Airport();

        legWaypointIndex = 0;
        endPlan = false;
        showOnlyActive = false;
    }

    private MapView mapView;

    private MarkerHit markerHit;
    private Context context;
    public Waypoint selectedWaypoint;
    public Integer waypoint_Id;
    public Integer id;

    public String name;
    public Airport departure_airport;
    public Airport destination_airport;
    public Airport alternate_airport;
    public Integer altitude;
    public Integer indicated_airspeed;
    public Integer wind_speed;
    public float wind_direction;
    public Date date;
    public boolean endPlan;
    public boolean showOnlyActive;

    public Geometry buffer;
    public Property bufferProperty;

    //private Leg activeLeg;
    private int legWaypointIndex;

    public void CreateBuffer()
    {
        createBuffer();
    }

    private void createBuffer() {
        if (bufferProperty == null)
        {
            bufferProperty = new Property();
            bufferProperty.name = "BUFFER";
            bufferProperty.value1 = "0.3";
            bufferProperty.value2 = "true";
        }
        Coordinate[] coordinates = new Coordinate[this.size()];
        Integer i = 0;
        for (Waypoint w : this.values())
        {
            coordinates[i] = new Coordinate(w.location.longitude, w.location.latitude);
            i++;
        }
        Geometry g = new GeometryFactory().createLineString(coordinates);
        BufferOp bufOp = new BufferOp(g);
        bufOp.setEndCapStyle(BufferOp.CAP_ROUND);
        buffer = bufOp.getResultGeometry(Double.parseDouble(bufferProperty.value1));

        //Geometry e = buffer.getEnvelope();
    }

    private OnDistanceFromWaypoint onDistanceFromWaypoint = null;
    private OnNewActiveWaypoint onNewActiveWaypoint = null;

//    public boolean getFlightplanActive(){ return (activeLeg != null); }
//    public Leg getActiveLeg()
//    {
//        return activeLeg;
//    }
//    public int getActivetoWaypointIndex()
//    {
//        return legWaypointIndex+1;
//    }
//
//    public void setActiveLeg(Leg leg)
//    {
//        activeLeg = leg;
//    }

    public void nextLeg(android.location.Location currentLocation)
    {
        if (!endPlan) {
            if (legWaypointIndex < this.size() - 2) legWaypointIndex++;

            //activeLeg = new Leg(Waypoints.get(legWaypointIndex), Waypoints.get(legWaypointIndex + 1));
            //activeLeg.setCurrectLocation(currentLocation);

            if (onNewActiveWaypoint != null) {
                onNewActiveWaypoint.onOldWaypoint((Waypoint)this.values().toArray()[legWaypointIndex]);
                onNewActiveWaypoint.onActiveWaypoint((Waypoint)this.values().toArray()[legWaypointIndex + 1]);
            }
        }
    }

    public Leg updateActiveLeg(android.location.Location currentLocation)
    {
        if (activeLeg != null) {
            activeLeg.setOnDistanceFromWaypoint(new Leg.OnDistanceFromWaypoint() {
                @Override
                public void onArrivedDestination(Waypoint waypoint, boolean firstHit) {
                    distance = LegInfoView.Distance.smaller2000Meters;
                    endPlan = true;
                    if (onDistanceFromWaypoint != null)
                        onDistanceFromWaypoint.onArrivedDestination(waypoint, firstHit);
                }

                @Override
                public void on2000Meters(boolean firstHit) {
                    if (!endPlan) {
                        distance = LegInfoView.Distance.smaller2000Meters;
                        if (onDistanceFromWaypoint != null)
                            onDistanceFromWaypoint.on2000Meters(firstHit);
                    }
                }

                @Override
                public void on1000Meters(boolean firstHit) {
                    if (!endPlan) {
                        distance = LegInfoView.Distance.smaller1000Meters;
                        if (onDistanceFromWaypoint != null)
                            onDistanceFromWaypoint.on1000Meters(firstHit);
                    }
                }

                @Override
                public void on500Meters(boolean firstHit) {
                    if (!endPlan) {
                        distance = LegInfoView.Distance.smaller500Meters;
                        if (onDistanceFromWaypoint != null)
                            onDistanceFromWaypoint.on500Meters(firstHit);
                    }
                }

                @Override
                public void onMore2000Meters(boolean firstHit) {
                    if (!endPlan) {
                        distance = LegInfoView.Distance.larger2000Meters;
                        if (onDistanceFromWaypoint != null)
                            onDistanceFromWaypoint.onMore2000Meters(firstHit);
                    }
                }




            });

            activeLeg.setCurrectLocation(currentLocation);

        }
        return activeLeg;
    }

    public void startRoute(Location currentLocation)
    {
        legWaypointIndex = 0;
        if (this.size()>1) {
            //activeLeg = new Leg(Waypoints.get(0), Waypoints.get(1));
            //activeLeg.setCurrectLocation(currentLocation);
            if (onNewActiveWaypoint != null)
            {
                onNewActiveWaypoint.onOldWaypoint(Waypoints.get(0));
                onNewActiveWaypoint.onActiveWaypoint(Waypoints.get(1));
            }
            showOnlyActive = true;
        }
    }

    public void InsertWaypoint(Waypoint waypoint)
    {
        class Point implements Comparable<Point>
        {
            public int legIndex;
            public double distance;

            @Override
            public int compareTo(Point point) {
                return (int) Math.round(this.distance - point.distance);
            }
        }

        ArrayList<Point> points = new ArrayList<Point>();

        int legcount = this.Waypoints.size()-1;

        for (int i=0; i<legcount; i++)
        {
            Waypoint from = this.Waypoints.get(i);
            Waypoint to = this.Waypoints.get(i+1);

            float c1 = from.location.bearingTo(to.location);
            float d1 = from.location.distanceTo(to.location);
            float c2 = from.location.bearingTo(waypoint.location);
            float d2 = from.location.distanceTo(waypoint.location);
            float a1 = c1 - c2;
            float a2 = 180f - (Math.abs(a1) + 90f);
            double cosa2 = Math.cos(Math.toRadians(a2));
            double d3 = cosa2 * d2;
            Log.i(TAG, "90degree distance from: " + from.name + " -> " + to.name + " = " + Double.toString(d3));

            Point p = new Point();
            p.distance = d3;
            p.legIndex = i;
            points.add(p);
        }

        Collections.sort(points);
        Log.i(TAG, "leg closed to track: " + Integer.toString(points.get(0).legIndex) + " with distance: " + Double.toString(points.get(0).distance));

        Waypoint p1 = this.Waypoints.get(points.get(0).legIndex);
        Waypoint p2 = this.Waypoints.get(points.get(0).legIndex+1);

        Integer s = Math.abs((p2.order - p1.order) / 2);
        waypoint.order = p1.order + s;

        Waypoints.add(waypoint);

        Collections.sort(Waypoints);
        UpdateWaypointsData();
    }

    public void UpdateWaypointsData()
    {
        Waypoint fromWaypoint = null;
        Integer distanceTotal = 0;
        for (int i = 0; i<Waypoints.size(); i++)
        {
            if (i==0) fromWaypoint = Waypoints.get(i);
            else
            {
                Waypoint nextWaypoint = Waypoints.get(i);
                nextWaypoint.true_track = fromWaypoint.location.bearingTo(nextWaypoint.location);
                if (nextWaypoint.true_track<0) nextWaypoint.true_track = nextWaypoint.true_track + 360f;

                nextWaypoint.distance_leg = Math.round(fromWaypoint.location.distanceTo(nextWaypoint.location));
                distanceTotal = distanceTotal + nextWaypoint.distance_leg;
                nextWaypoint.distance_total = distanceTotal;

                nextWaypoint.wind_direction = this.wind_direction;
                nextWaypoint.wind_speed = this.wind_speed;

                double windrad = Math.toRadians(nextWaypoint.wind_direction);
                double dirrad = Math.toRadians(nextWaypoint.true_track);
                double zijwind = Math.sin(windrad-dirrad) * nextWaypoint.wind_speed;
                double langswind = Math.cos(windrad-dirrad) * nextWaypoint.wind_speed;
                double opstuurhoek = (zijwind/this.indicated_airspeed) * 60;
                double gs = this.indicated_airspeed - langswind;

                Log.i(TAG, "Wind: " + Float.toString(nextWaypoint.wind_direction) + "/" + nextWaypoint.wind_speed.toString() +
                        " TrueTrack: " + Float.toString(nextWaypoint.true_track) +
                        " ZijWind: " + Double.toString(zijwind) +
                        " LangsWind: " + Double.toString(langswind) +
                        " Opstuurhoek: " + Double.toString(opstuurhoek) +
                        " GS: " + Double.toString(gs));

                nextWaypoint.ground_speed = Math.round((float)gs);
                nextWaypoint.true_heading = nextWaypoint.true_track + Math.round(opstuurhoek);
                if (nextWaypoint.true_heading<0) nextWaypoint.true_heading = nextWaypoint.true_heading +360f;

                nextWaypoint.time_leg = Math.round(((float)(nextWaypoint.distance_leg/1852f) / (float)gs) * 60);
                if (fromWaypoint == null)
                    nextWaypoint.time_total = Math.round(((float)(nextWaypoint.distance_total/1852f) / (float)gs) * 60);
                else
                if (fromWaypoint.time_total == null)
                    nextWaypoint.time_total = Math.round(((float)(nextWaypoint.distance_total/1852f) / (float)gs) * 60);
                else
                    nextWaypoint.time_total = fromWaypoint.time_total + nextWaypoint.time_leg;

                fromWaypoint = nextWaypoint;
            }
        }
        createBuffer();
    }

    public void setOnDistanceFromWaypoint( final OnDistanceFromWaypoint d) {onDistanceFromWaypoint = d; }
    public interface OnDistanceFromWaypoint {
        public void on2000Meters(boolean firstHit);
        public void on1000Meters(boolean firstHit);
        public void on500Meters(boolean firstHit);
        public void onMore2000Meters(boolean firstHit);
        public void onArrivedDestination(Waypoint waypoint, boolean firstHit);
    }

    public void setOnNewActiveWaypoint(final OnNewActiveWaypoint newActiveWaypoint) {onNewActiveWaypoint = newActiveWaypoint;}
    public interface OnNewActiveWaypoint
    {
        public void onOldWaypoint(Waypoint waypoint);
        public void onActiveWaypoint(Waypoint waypoint);
    }

    public void LoadRoute(Context context, Integer flightPlan_ID, Integer uniqueID)
    {

//        // First load the basis of the flightplan
//        FlightPlanDataSource flightPlanDataSource = new FlightPlanDataSource(context);
//        flightPlanDataSource.open();
//        flightPlanDataSource.GetFlightplanByID(flightPlan_ID, this);
//        flightPlanDataSource.close();
//
//        // Second, load the airports information
//        AirportDataSource airportDataSource = new AirportDataSource(context);
//        airportDataSource.open(uniqueID);
//        this.departure_airport = airportDataSource.GetAirportByID(this.departure_airport.id);
//        this.destination_airport = airportDataSource.GetAirportByID(this.destination_airport.id);
//        this.alternate_airport = airportDataSource.GetAirportByID(this.alternate_airport.id);
//        airportDataSource.close();
//
//        // Load the waypoint for this flightplan
//        flightPlanDataSource.open();
//        flightPlanDataSource.GetWaypointsByFlightPlan(this);
//        flightPlanDataSource.clearTimes(this, true);
//        flightPlanDataSource.close();
//
//        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(context);
//        propertiesDataSource.open(true);
//        bufferProperty = propertiesDataSource.GetProperty("BUFFER");
//        propertiesDataSource.close(true);
//
//        createBuffer();
    }


    private void removeRouteFromMap()
    {
        mapView.removeMap(name + "markers", true);
        mapView.removeMap(name + "lines", true);
    }

    private void drawRouteOnMap(Boolean createMarkermap)
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
            mapView.removeDynamicMarkerFromMap(this.name+"markers", selectedWaypoint.Id.toString());
            mapView.removeDynamicMarkerFromMap(this.name+"markers", selectedWaypoint.Id.toString() + "s");

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
        mapView.hideDynamicMarker(this.name+"markers", newSelectedwaypoint.Id.toString());
        mapView.showDynamicMarker(this.name+"markers", newSelectedwaypoint.Id.toString() + "s");
        selectedWaypoint = newSelectedwaypoint;
    }

    private void resetSelectedWaypointMarker()
    {
        if (selectedWaypoint != null)
        {
            mapView.hideDynamicMarker(this.name+"markers", selectedWaypoint.Id.toString() + "s");
            mapView.showDynamicMarker(this.name+"markers", selectedWaypoint.Id.toString());
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
        waypoint.marker.name = waypoint.Id.toString();
        waypoint.marker.setImage(getBitmapFromDrawable(drawable), false);
        waypoint.marker.anchorPoint = new PointF(16,16);
        waypoint.marker.location = waypoint.location;
        waypoint.selectedMarker = new DynamicMarker();
        waypoint.selectedMarker.name = waypoint.Id.toString() + "s";
        waypoint.selectedMarker.setImage(getBitmapFromDrawable(R.drawable.greendot), false);
        waypoint.selectedMarker.anchorPoint = new PointF(16,16);
        waypoint.selectedMarker.location = waypoint.location;
        mapView.addDynamicMarkerToMap(this.name+"markers", waypoint.marker);
        mapView.addDynamicMarkerToMap(this.name+"markers", waypoint.selectedMarker);
        mapView.showDynamicMarker(this.name+"markers", waypoint.Id.toString());
        mapView.hideDynamicMarker(this.name+"markers", waypoint.Id.toString() + "s");
    }

    public void AddWaypoint(String name, Location location)
    {
        Log.i("Route", "Adding waypoint: " + name);

        Toast toast = Toast.makeText(context, "Waypoint Added!!", Toast.LENGTH_SHORT);
        toast.show();

        Waypoint waypoint = new Waypoint();
        waypoint.location = location;
        waypoint.name = name;
        waypoint.Id = waypoint_Id;
        this.put(waypoint_Id, waypoint);
        waypoint_Id++;
        selectedWaypoint = waypoint;
        // redraw route on map
        removeRouteFromMap();
        drawRouteOnMap(true);
    }

}
