package com.mobileaviationtools.flightsimplannerpro.Route;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.location.Location;

import com.mobileaviationtools.flightsimplannerpro.R;

import java.util.HashSet;
import java.util.Set;

import us.ba3.me.markers.DynamicMarker;

/**
 * Created by Rob Verhoef on 7-7-2016.
 */
public class Leg {
    public Leg(Waypoint from, Waypoint to, Context context)
    {
        this.context = context;
        this.toWaypoint = to;
        this.fromWaypoint = from;
        distancesAchived = new HashSet<Distance>();
        distance = Distance.larger2000Meters;
        endPlan = false;
    }

    private OnDistanceFromWaypoint onDistanceFromWaypoint = null;

    private Set<Distance> distancesAchived;
    private Context context;

    private Waypoint toWaypoint;
    public Waypoint getToWaypoint()
    {
        return this.toWaypoint;
    }
    private Waypoint fromWaypoint;
    public Waypoint getFromWaypoint()
    {
        return this.fromWaypoint;
    }

    private boolean endPlan;

    private Location currectLocation;
    private float distanceTo;
    private float courseTo;
    private float speed;
    private float deviationFromTrack;
    private Bitmap blueDot;

    public float getDeviationFromTrack()
    {
        return currectLocation.bearingTo(toWaypoint.getAndroidLocation()) - fromWaypoint.getAndroidLocation().bearingTo(toWaypoint.getAndroidLocation());
    }

    private Distance distance;

    public Integer getBearing()
    {
        Integer b = Math.round(courseTo);
        return (b<0) ? 360+b : b ;
    }

    public Integer getTrueTrack()
    {
        Integer t = Math.round(toWaypoint.true_track);
        return (t<0) ? 360+t : t ;
    }

    public float getDistanceNM()
    {
        return distanceTo / 1852f;
    }

    public int getSecondsToGo() {
        // distanceTo = meters
        // speed = meters/second

        if (speed<25) speed = ((float)toWaypoint.ground_speed * 0.514444444f);
        int seconds = Math.round(distanceTo/speed);

        return seconds;
    }

    public Integer getCourseTo()
    {
        Integer c =  Math.round(this.currectLocation.bearingTo(toWaypoint.getAndroidLocation()));
        return (c<0) ? 360+c : c;
    }

    public DynamicMarker getLegInfoMarker()
    {
        DynamicMarker airportMarker = new DynamicMarker();
        blueDot = BitmapFactory.decodeResource(context.getResources(), R.drawable.bluedot);

        airportMarker.name = toWaypoint.name;

        //float distanceBetween = fromWaypoint.getAndroidLocation().distanceTo(toWaypoint.getAndroidLocation());
        //float courseBetween = fromWaypoint.getAndroidLocation().bearingTo(toWaypoint.getAndroidLocation());
        PointF m = us.ba3.me.Math.pointBetween(new PointF(Float.parseFloat(Double.toString(fromWaypoint.location.longitude)),
                Float.parseFloat(Double.toString(fromWaypoint.location.latitude))),
                new PointF(Float.parseFloat(Double.toString(fromWaypoint.location.longitude)),
                        Float.parseFloat(Double.toString(fromWaypoint.location.latitude))), .5f);

        airportMarker.location = new us.ba3.me.Location(m.y, m.x);
        airportMarker.setImage(blueDot, false);

        airportMarker.anchorPoint = new PointF(blueDot.getWidth()/2, blueDot.getHeight()/2);

        return airportMarker;
    }

    public void setCurrectLocation(Location currentLocation)
    {
        this.currectLocation = currentLocation;
        distanceTo = currentLocation.distanceTo(toWaypoint.getAndroidLocation());
        courseTo = currentLocation.bearingTo(toWaypoint.getAndroidLocation());
        speed = currentLocation.getSpeed();

        //courseTo = (courseTo<0) ? 360+courseTo : courseTo;

        deviationFromTrack = getBearing() - toWaypoint.true_track;

        if (distanceTo>=2000) distance = Distance.larger2000Meters;
        if (distanceTo<2000) distance = Distance.smaller2000Meters;
        if (distanceTo<1000) distance = Distance.smaller1000Meters;
        if (distanceTo<500) distance = Distance.smaller500Meters;

        if ((distance == Distance.smaller1000Meters) &&
                ((toWaypoint.waypointType == WaypointType.destinationAirport) || (toWaypoint.waypointType == WaypointType.alternateAirport)))
        {
            endPlan = true;
        }

        if (distance == Distance.larger2000Meters) {
            distancesAchived.clear();
            if (onDistanceFromWaypoint != null)
                onDistanceFromWaypoint.onMore2000Meters(distancesAchived.add(Distance.larger2000Meters));
        }
        if (!endPlan) {
            if (distance == Distance.smaller2000Meters)
                if (onDistanceFromWaypoint != null)
                    onDistanceFromWaypoint.on2000Meters(distancesAchived.add(Distance.smaller2000Meters));
            if (distance == Distance.smaller1000Meters)
                if (onDistanceFromWaypoint != null)
                    onDistanceFromWaypoint.on1000Meters(distancesAchived.add(Distance.smaller1000Meters));
            if (distance == Distance.smaller500Meters)
                if (onDistanceFromWaypoint != null)
                    onDistanceFromWaypoint.on500Meters(distancesAchived.add(Distance.smaller500Meters));
        } else
        {
            if (onDistanceFromWaypoint != null)
                onDistanceFromWaypoint.onArrivedDestination(toWaypoint,
                        distancesAchived.add(Distance.reachedDestination));
        }
    }

    // 100 meter listener
    // 50 meter listener
    // 25 meter listener
    public void setOnDistanceFromWaypoint( final OnDistanceFromWaypoint d) {onDistanceFromWaypoint = d; }
    public interface OnDistanceFromWaypoint {
        public void on2000Meters(boolean firstHit);
        public void on1000Meters(boolean firstHit);
        public void on500Meters(boolean firstHit);
        public void onMore2000Meters(boolean firstHit);
        public void onArrivedDestination(Waypoint waypoint, boolean FirstHit);
    }
}

