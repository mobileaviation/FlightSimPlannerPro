package com.mobileaviationtools.flightsimplannerpro;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

import com.mobileaviationtools.flightsimplannerpro.Route.RouteTest;

import us.ba3.me.ConvertPointCallback;
import us.ba3.me.Location;
import us.ba3.me.MapView;

/**
 * Created by Rob Verhoef on 20-6-2016.
 */
public class MyMapView extends MapView {
    public MyMapView(Context context) {
        super(context);
    }

    public void Init(RouteTest routeTest)
    {
        this.routeTest = routeTest;
        placingRoutePoint = false;
        movingRoutePoint = false;
    }

    private RouteTest routeTest;

    private Boolean placingRoutePoint;
    private Boolean movingRoutePoint;
    private Location addPointLocation;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
            {
                Log.i("OnTouchEvent", "This is a OnTouch (ACTION_DOWN) event...");
                break;
            }
            case MotionEvent.ACTION_MOVE:
            {
                Log.i("OnTouchEvent", "This is a OnTouch (ACTION_MOVE) event...");
                if (movingRoutePoint)
                    // update the route with the new location..
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                Log.i("OnTouchEvent", "This is a OnTouch (ACTION_UP) event...");
                placingRoutePoint = false;
                movingRoutePoint = false;
                addPointLocation = null;
                break;
            }

        }

        super.getLocationForPoint(new PointF(event.getX(), event.getY()), new ConvertPointCallback(){
            @Override
            public void convertComplete(Location loc) {
                Log.w("OnTouchEvent", "lon:" + loc.longitude + " lat:" + loc.latitude);

                addPointLocation = loc;

            }});
        return super.onTouchEvent(event);
    }


    @Override
    public void onLongPress(MotionEvent event) {

        // Place a new Route Point
        //
        switch (event.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
            {
                Log.i("onLongPress", "This is a onLongPress (ACTION_DOWN) event...");
                placingRoutePoint = true;
                break;
            }
        }

        super.getLocationForPoint(new PointF(event.getX(), event.getY()), new ConvertPointCallback(){
            @Override
            public void convertComplete(Location loc) {
                Log.w("onLongPress", "lon:" + loc.longitude + " lat:" + loc.latitude);

                addPointLocation = loc;

                if (placingRoutePoint) {
                    placingRoutePoint = false;
                    movingRoutePoint = true;
                    // insert the new point
                    routeTest.InsertNewPoint(addPointLocation);
                }


            }});

        super.onLongPress(event);
    }
}
