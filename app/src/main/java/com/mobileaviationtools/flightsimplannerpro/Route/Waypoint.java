package com.mobileaviationtools.flightsimplannerpro.Route;

import us.ba3.me.Location;
import us.ba3.me.markers.DynamicMarker;

/**
 * Created by Rob Verhoef on 28-6-2016.
 */
public class Waypoint {
    public Waypoint()
    {

    }

    public Location location;
    public String name;
    public Integer Id;
    public DynamicMarker marker;
    public DynamicMarker selectedMarker;
}
