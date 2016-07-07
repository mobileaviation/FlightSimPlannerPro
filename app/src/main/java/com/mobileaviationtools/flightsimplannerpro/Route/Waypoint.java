package com.mobileaviationtools.flightsimplannerpro.Route;

import com.mobileaviationtools.flightsimplannerpro.R;

import java.io.Serializable;
import java.util.Date;

import us.ba3.me.Location;
import us.ba3.me.markers.DynamicMarker;

/**
 * Created by Rob Verhoef on 28-6-2016.
 */
public class Waypoint  implements Comparable<Waypoint>, Serializable {
    public Waypoint()
    {
        fix_id = 0;
        navaid_id = 0;
        airport_id = 0;
    }

    public Location location;
    public android.location.Location getAndroidLocation()
    {
        android.location.Location l = new android.location.Location("Waypoint");
        l.setLatitude(location.latitude);
        l.setLongitude(location.longitude);
        return l;
    }
    public String name;
    public Integer Id;
    public DynamicMarker marker;
    public DynamicMarker selectedMarker;

    public Integer id;
    public Integer flightplan_id;
    public Integer order;
    public Integer airport_id;
    public Integer fix_id;
    public Integer navaid_id;
    public float frequency;
    public Date eto;
    public Date ato;
    public Date reto;
    public Integer time_leg;
    public Integer time_total;
    public float compass_heading;
    public float magnetic_heading;
    public float true_heading;
    public float true_track;
    public Integer distance_leg;
    public Integer distance_total;
    public Integer ground_speed;
    public Integer wind_speed;
    public float wind_direction;
    public WaypointType waypointType;

//    public BitmapDescriptor GetIcon()
//    {
//        return Bitmap.fromResource(R.drawable.waypoint);
//    }

    @Override
    public int compareTo(Waypoint waypoint) {
        Integer s = waypoint.order;
        return this.order - s;
    }
}
