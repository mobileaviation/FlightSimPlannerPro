package com.mobileaviationtools.flightsimplannerpro;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("OnTouchEvent", "This is a OnTouch event...");
        super.getLocationForPoint(new PointF(event.getX(), event.getY()), new ConvertPointCallback(){
            @Override
            public void convertComplete(Location loc) {
                Log.w("OnTouchEvent", "lon:" + loc.longitude + " lat:" + loc.latitude);


            }});
        return super.onTouchEvent(event);
    }

    @Override
    public void onLongPress(MotionEvent arg0) {
        Log.i("OnLongPress", "This is a OnLongPress event...");
        super.onLongPress(arg0);
    }
}
