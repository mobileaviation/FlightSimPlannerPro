package com.mobileaviationtools.flightsimplannerpro.Route;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.location.Location;
import android.util.Log;

import com.mobileaviationtools.flightsimplannerpro.Helpers;
import com.mobileaviationtools.flightsimplannerpro.R;

import java.util.HashSet;
import java.util.Set;

import us.ba3.me.markers.DynamicMarker;
import us.ba3.me.markers.MarkerRotationType;

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

    private String TAG = "Leg";
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
        DynamicMarker legMarker = new DynamicMarker();
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inMutable = true;
        blueDot = BitmapFactory.decodeResource(context.getResources(), R.drawable.direction_marker_ss, o);

        Bitmap drawBitmap = Bitmap.createBitmap(blueDot.getWidth(), blueDot.getWidth(),
                Bitmap.Config.ARGB_8888);
        Canvas drawCanvas = new Canvas(drawBitmap);

        drawCanvas.drawBitmap(blueDot, 0, (drawBitmap.getHeight()/2)-(blueDot.getHeight()/2), null);

        // arrow is 77 * 32
        int ty = (drawBitmap.getHeight()/2) + Math.round(Helpers.convertDpToPixel(5f, context));

        Paint textPaint = new Paint();
        textPaint.setColor(Color.argb(255,0,0,0));
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(Math.round(Helpers.convertDpToPixel(14, context )));
        textPaint.setTextAlign(Paint.Align.LEFT);

        String c = String.format("%1$03d", Math.round(toWaypoint.true_track));
        drawCanvas.drawText(c,
                Math.round(Helpers.convertDpToPixel(8, context )),
                ty
                //Math.round(Helpers.convertDpToPixel(ty, context ))
                ,textPaint);

        legMarker.name = toWaypoint.name;

        //float distanceBetween = fromWaypoint.getAndroidLocation().distanceTo(toWaypoint.getAndroidLocation());
        //float courseBetween = fromWaypoint.getAndroidLocation().bearingTo(toWaypoint.getAndroidLocation());
        PointF m = us.ba3.me.Math.pointBetween(new PointF(Float.parseFloat(Double.toString(fromWaypoint.location.longitude)),
                Float.parseFloat(Double.toString(fromWaypoint.location.latitude))),
                new PointF(Float.parseFloat(Double.toString(toWaypoint.location.longitude)),
                        Float.parseFloat(Double.toString(toWaypoint.location.latitude))), .5f);

        Log.i(TAG, "Set Leg Marker on LAT: " + m.y + " LON: " + m.x + " course: " +this.courseTo);
        legMarker.location = new us.ba3.me.Location(m.y, m.x);
        legMarker.setImage(drawBitmap, false);
        legMarker.rotationType = MarkerRotationType.kMarkerRotationTrueNorthAligned;
        legMarker.rotation = toWaypoint.true_track - 90;

        legMarker.anchorPoint = new PointF(drawBitmap.getWidth()/2, drawBitmap.getHeight()/2);

        return legMarker;
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

