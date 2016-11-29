package com.mobileaviationtools.flightsimplannerpro.Route;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;

import com.mobileaviationtools.flightsimplannerpro.Database.Helpers;
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
        location = new Location();
        UID = Helpers.generateUniqueId();
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
    public Integer UID;
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

    public Bitmap GetIcon(Integer airspeed, Context context)
    {
//        Bitmap groundcourseBitmap = Bitmap.createBitmap(200, 200,
//                Bitmap.Config.ARGB_8888);
//        Bitmap aircourseBitmap = Bitmap.createBitmap(200, 200,
//                Bitmap.Config.ARGB_8888);
//        Bitmap winddirectionBitmap = Bitmap.createBitmap(200, 200,
//                Bitmap.Config.ARGB_8888);

        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inMutable = false;
        Bitmap groundcourseBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.direction_marker_m, op);

        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(2f);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(15);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        Canvas groundcourseCanvas = new Canvas(groundcourseBitmap);
//        groundcourseCanvas.drawLine(100, 100, 100 - ground_speed, 100, paint);
        groundcourseCanvas.drawText(Integer.toString(Math.round(compass_heading)), 10, 10, textPaint);
//
//
//        Canvas aircourseCanvas = new Canvas(aircourseBitmap);
//        aircourseCanvas.drawLine(100, 100, 100 - airspeed, 100, paint);
//        aircourseCanvas.drawText(Integer.toString(Math.round(compass_heading))+"C", 120 - airspeed, 114, textPaint);
//
//
//        Canvas winddirectionCanvas = new Canvas(winddirectionBitmap);
//        winddirectionCanvas.drawLine(100, 100, 100 - wind_speed, 100, paint);
//        winddirectionCanvas.drawText(Integer.toString(Math.round(wind_direction))+"W", 100 - wind_speed, 98, textPaint);



//        Bitmap headingBitmap = Bitmap.createBitmap(200, 200,
//                Bitmap.Config.ARGB_8888);
//        Canvas headingCanvas = new Canvas(headingBitmap);
//        headingCanvas.drawColor(Color.BLUE, PorterDuff.Mode.ADD);
//        headingCanvas.save(Canvas.MATRIX_SAVE_FLAG);
//        headingCanvas.rotate(true_track+90,100,100);
//        headingCanvas.drawBitmap(groundcourseBitmap, 0, 0, null);
//        headingCanvas.restore();
//        headingCanvas.save(Canvas.MATRIX_SAVE_FLAG);
//        headingCanvas.rotate(compass_heading+90,100,100);
//        headingCanvas.drawBitmap(aircourseBitmap, 0, 0, null);
//        headingCanvas.restore();
//        headingCanvas.save(Canvas.MATRIX_SAVE_FLAG);
//        headingCanvas.rotate(wind_direction+90,100,100);
//        headingCanvas.drawBitmap(winddirectionBitmap, 0, 0, null);
//        headingCanvas.restore();

        return groundcourseBitmap;
    }

    @Override
    public int compareTo(Waypoint waypoint) {
        Integer s = waypoint.order;
        return this.order - s;
    }
}
