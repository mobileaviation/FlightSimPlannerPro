package com.mobileaviationtools.flightsimplannerpro.LocationService;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.support.v7.widget.ThemedSpinnerAdapter;

import com.mobileaviationtools.flightsimplannerpro.Helpers;
import com.mobileaviationtools.flightsimplannerpro.R;

import us.ba3.me.HaloPulse;
import us.ba3.me.Location;
import us.ba3.me.MapView;
import us.ba3.me.markers.DynamicMarker;
import us.ba3.me.markers.DynamicMarkerMapInfo;

/**
 * Created by Rob Verhoef on 15-7-2016.
 */
public class PlaneMarker {
    public PlaneMarker(Context context, MapView mapView)
    {
        this.context = context;
        this.mapView = mapView;
        setupMaps();
    }

    private void setupMaps()
    {
        DynamicMarkerMapInfo mapInfo = new DynamicMarkerMapInfo();
        mapInfo.name = "currentLocation";
        mapInfo.zOrder = 200;
        mapInfo.hitTestingEnabled = false;
        mapView.addMapUsingMapInfo(mapInfo);

        planeBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.plane1);

        DynamicMarker planeMarker = new DynamicMarker();
        planeMarker.name = "plane";
        planeMarker.anchorPoint = new PointF(planeBitmap.getWidth()/2, planeBitmap.getHeight()/2);
        planeMarker.setImage(planeBitmap, false);
        mapView.addDynamicMarkerToMap("currentLocation", planeMarker);

        HaloPulse pulse = new HaloPulse();
        pulse.name = "locationRing";
        pulse.minRadius = Helpers.convertDpToPixel(20, this.context);
        pulse.maxRadius = planeBitmap.getWidth()/2;
        pulse.animationDuration = 2.5f;
        pulse.repeatDelay = 1;
        pulse.fade = true;
        pulse.fadeDelay = 1;
        pulse.zOrder = 999;
        pulse.lineStyle.strokeColor = Color.WHITE;
        pulse.lineStyle.strokeWidth = Helpers.convertDpToPixel(2, this.context);
        pulse.lineStyle.outlineColor = Color.rgb(30, 144, 255);
        pulse.lineStyle.outlineWidth = Helpers.convertDpToPixel(2, this.context);

        mapView.addHaloPulse(pulse);
    }

    public void setLocation(Location location, Double direction)
    {
        mapView.setDynamicMarkerLocation("currentLocation", "plane", location, 0);
        mapView.setDynamicMarkerRotation("currentLocation", "plane", direction, 0);
        mapView.setHaloPulseLocation("locationRing", location);
    }

    private Context context;
    private MapView mapView;
    private Bitmap planeBitmap;
}
