package com.mobileaviationtools.flightsimplannerpro.Route;

import android.content.Context;
import android.util.Log;

import com.mobileaviationtools.flightsimplannerpro.Airports.Airport;
import com.mobileaviationtools.flightsimplannerpro.Database.AirportDataSource;
import com.mobileaviationtools.flightsimplannerpro.Database.PropertiesDataSource;
import com.mobileaviationtools.flightsimplannerpro.Database.RouteDataSource;
import com.mobileaviationtools.flightsimplannerpro.Property;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.operation.buffer.BufferOp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import us.ba3.me.Location;

/**
 * Created by Rob Verhoef on 28-6-2016.
 */
public class Route{
    public Route(Context context)
    {
        this.context = context;
        waypoints = new ArrayList<>();

        departure_airport = new Airport();
        destination_airport = new Airport();
        alternate_airport = new Airport();

        legWaypointIndex = 0;
        endPlan = false;
        showOnlyActive = false;
    }


    private Context context;
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

    private Leg activeLeg;
    private Distance distance;
    private int legWaypointIndex;
    public ArrayList<Waypoint> waypoints;

    private String TAG = "Route";

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
        Coordinate[] coordinates = new Coordinate[waypoints.size()];
        Integer i = 0;
        for (Waypoint w : this.waypoints)
        {
            coordinates[i] = new Coordinate(w.location.longitude, w.location.latitude);
            i++;
        }
        Geometry g = new GeometryFactory().createLineString(coordinates);
        BufferOp bufOp = new BufferOp(g);
        bufOp.setEndCapStyle(BufferOp.CAP_ROUND);
        buffer = bufOp.getResultGeometry(Double.parseDouble(bufferProperty.value1));
    }

    private OnDistanceFromWaypoint onDistanceFromWaypoint = null;
    private OnNewActiveWaypoint onNewActiveWaypoint = null;

    public boolean getRouteActive(){ return (activeLeg != null); }
    public Leg getActiveLeg()
    {
        return activeLeg;
    }
    public int getActivetoWaypointIndex()
    {
        return legWaypointIndex+1;
    }

    public void setActiveLeg(Leg leg)
    {
        activeLeg = leg;
    }

    public void nextLeg(android.location.Location currentLocation)
    {
        if (!endPlan) {
            if (legWaypointIndex < waypoints.size() - 2) legWaypointIndex++;

            //activeLeg = new Leg(Waypoints.get(legWaypointIndex), Waypoints.get(legWaypointIndex + 1));
            //activeLeg.setCurrectLocation(currentLocation);

            if (onNewActiveWaypoint != null) {
                onNewActiveWaypoint.onOldWaypoint(this.waypoints.get(legWaypointIndex));
                onNewActiveWaypoint.onActiveWaypoint(this.waypoints.get(legWaypointIndex + 1));
            }
        }
    }

    public Leg updateActiveLeg(android.location.Location currentLocation)
    {
        if (activeLeg != null) {
            activeLeg.setOnDistanceFromWaypoint(new Leg.OnDistanceFromWaypoint() {
                @Override
                public void onArrivedDestination(Waypoint waypoint, boolean firstHit) {
                    distance = Distance.smaller2000Meters;
                    endPlan = true;
                    if (onDistanceFromWaypoint != null)
                        onDistanceFromWaypoint.onArrivedDestination(waypoint, firstHit);
                }

                @Override
                public void on2000Meters(boolean firstHit) {
                    if (!endPlan) {
                        distance = Distance.smaller2000Meters;
                        if (onDistanceFromWaypoint != null)
                            onDistanceFromWaypoint.on2000Meters(firstHit);
                    }
                }

                @Override
                public void on1000Meters(boolean firstHit) {
                    if (!endPlan) {
                        distance = Distance.smaller1000Meters;
                        if (onDistanceFromWaypoint != null)
                            onDistanceFromWaypoint.on1000Meters(firstHit);
                    }
                }

                @Override
                public void on500Meters(boolean firstHit) {
                    if (!endPlan) {
                        distance = Distance.smaller500Meters;
                        if (onDistanceFromWaypoint != null)
                            onDistanceFromWaypoint.on500Meters(firstHit);
                    }
                }

                @Override
                public void onMore2000Meters(boolean firstHit) {
                    if (!endPlan) {
                        distance = Distance.larger2000Meters;
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
        if (waypoints.size()>1) {
            //activeLeg = new Leg(Waypoints.get(0), Waypoints.get(1));
            //activeLeg.setCurrectLocation(currentLocation);
            if (onNewActiveWaypoint != null)
            {
                onNewActiveWaypoint.onOldWaypoint(waypoints.get(0));
                onNewActiveWaypoint.onActiveWaypoint(waypoints.get(1));
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

        int legcount = this.waypoints.size()-1;

        for (int i=0; i<legcount; i++)
        {
            Waypoint from = this.waypoints.get(i);
            Waypoint to = this.waypoints.get(i+1);

            float c1 = from.getAndroidLocation().bearingTo(to.getAndroidLocation());
            float d1 = from.getAndroidLocation().distanceTo(to.getAndroidLocation());
            float c2 = from.getAndroidLocation().bearingTo(waypoint.getAndroidLocation());
            float d2 = from.getAndroidLocation().distanceTo(waypoint.getAndroidLocation());
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

        Waypoint p1 = this.waypoints.get(points.get(0).legIndex);
        Waypoint p2 = this.waypoints.get(points.get(0).legIndex+1);

        Integer s = Math.abs((p2.order - p1.order) / 2);
        waypoint.order = p1.order + s;

        waypoints.add(waypoint);

        Collections.sort(waypoints);
        UpdateWaypointsData();
    }

    public void UpdateWaypointsData()
    {
        Waypoint fromWaypoint = null;
        Integer distanceTotal = 0;
        for (int i = 0; i<waypoints.size(); i++)
        {
            if (i==0) fromWaypoint = waypoints.get(i);
            else
            {
                Waypoint nextWaypoint = waypoints.get(i);
                nextWaypoint.true_track = fromWaypoint.getAndroidLocation().bearingTo(nextWaypoint.getAndroidLocation());
                if (nextWaypoint.true_track<0) nextWaypoint.true_track = nextWaypoint.true_track + 360f;

                nextWaypoint.distance_leg = Math.round(fromWaypoint.getAndroidLocation().distanceTo(nextWaypoint.getAndroidLocation()));
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

    public void LoadRoute(Context context, Integer route_ID)
    {

        // First load the basis of the flightplan
        RouteDataSource routeDataSource = new RouteDataSource(context);
        routeDataSource.open();
        routeDataSource.GetRouteByID(route_ID, this);
        routeDataSource.close();

        // Second, load the airports information
        AirportDataSource airportDataSource = new AirportDataSource(context);
        airportDataSource.open(0);
        this.departure_airport = airportDataSource.GetAirportByID(this.departure_airport.id);
        this.destination_airport = airportDataSource.GetAirportByID(this.destination_airport.id);
        this.alternate_airport = airportDataSource.GetAirportByID(this.alternate_airport.id);
        airportDataSource.close();

        // Load the waypoint for this flightplan
        routeDataSource.open();
        routeDataSource.GetWaypointsByRoute(this);
        routeDataSource.clearTimes(this, true);
        routeDataSource.close();

        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(context);
        propertiesDataSource.open(true);
        bufferProperty = propertiesDataSource.GetProperty("BUFFER");
        propertiesDataSource.close(true);

        createBuffer();
    }

}
