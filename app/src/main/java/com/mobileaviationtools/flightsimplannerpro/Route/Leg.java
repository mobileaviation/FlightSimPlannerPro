package com.mobileaviationtools.flightsimplannerpro.Route;

import android.location.Location;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Rob Verhoef on 7-7-2016.
 */
public class Leg {
    public Leg(Waypoint from, Waypoint to)
    {
        this.toWaypoint = to;
        this.fromWaypoint = from;
        distancesAchived = new HashSet<Distance>();
        distance = Distance.larger2000Meters;
        endPlan = false;
    }

    private OnDistanceFromWaypoint onDistanceFromWaypoint = null;

    private Set<Distance> distancesAchived;

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

