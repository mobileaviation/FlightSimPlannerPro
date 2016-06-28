package com.mobileaviationtools.flightsimplannerpro.Route;

import android.location.Location;
import android.util.Log;

import us.ba3.me.VectorMapDelegate;

/**
 * Created by Rob Verhoef on 28-6-2016.
 */
public class LineSegmentHit implements VectorMapDelegate {
    @Override
    public void lineSegmentHitDetected(String s, String s1, Location location, int i, int i1) {
        Log.i("Route", "LineSegmentHit: " + s + " : " + s1);
    }

    @Override
    public void vertexHitDetected(String s, String s1, Location location, int i) {
        Log.i("Route", "vertexHit: " + s + " : " + s1);
    }
}
