package com.mobileaviationtools.flightsimplannerpro.Track;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.mobileaviationtools.flightsimplannerpro.Database.LocationTrackingDataSource;
import com.mobileaviationtools.flightsimplannerpro.Helpers;
import com.mobileaviationtools.flightsimplannerpro.Route.Route;

import java.text.SimpleDateFormat;
import java.util.Date;

import us.ba3.me.MapView;
import us.ba3.me.VectorMapInfo;
import us.ba3.me.styles.LineStyle;

/**
 * Created by Rob Verhoef on 14-7-2016.
 */
public class LocationTracking {
    public LocationTracking(Route route, Context context, MapView mapView)
    {
        this.route = route;
        this.context = context;
        this.mapView = mapView;

        String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if (this.route != null) Name = currentDateandTime + " : " + this.route.name;
        else Name = currentDateandTime;

        lineStyle = new LineStyle();
        lineStyle.outlineColor = Color.DKGRAY;
        lineStyle.outlineWidth = Helpers.convertDpToPixel(2, this.context);
        lineStyle.strokeColor = Color.CYAN;
        lineStyle.strokeWidth = Helpers.convertDpToPixel(10, this.context);

        VectorMapInfo vectorMapInfo = new VectorMapInfo();
        vectorMapInfo.name = "TrackLine";
        vectorMapInfo.zOrder = 105;
        vectorMapInfo.alpha = 0.75f;
        this.mapView.addMapUsingMapInfo(vectorMapInfo);

        insertDatabase();
    }

    private void insertDatabase()
    {
        LocationTrackingDataSource locationTrackingDataSource = new LocationTrackingDataSource(context);
        locationTrackingDataSource.open();
        Id = locationTrackingDataSource.insertNewLocationTracking(this);
        locationTrackingDataSource.close();
    }

    public void SetLocationPoint(Location location)
    {
        LocationTrackingDataSource locationTrackingDataSource = new LocationTrackingDataSource(context);
        locationTrackingDataSource.open();
        locationTrackingDataSource.setTrackPoint(this, location);
        locationTrackingDataSource.close();
    }

    private Context context;
    private Route route;
    private MapView mapView;
    public String Name;
    public Long Id;

    private LatLng oldPoint;
    private float pointDistance = 0;

    private LineStyle lineStyle;

    public float setTrackPoints(Location newPoint)
    {
        float b = newPoint.getBearing();
        if (oldPoint != null)
        {
            Location loc = new Location("loc old");
            loc.setLatitude(oldPoint.latitude);
            loc.setLongitude(oldPoint.longitude);

            Location locnew = new Location("loc new");
            locnew.setLatitude(newPoint.getLatitude());
            locnew.setLongitude(newPoint.getLongitude());

            float v = loc.distanceTo(locnew);

            if (v>100)
            {
                b = loc.bearingTo(locnew);

                addTracklineSegment(oldPoint, newPoint);

                oldPoint = new LatLng(newPoint.getLatitude(), newPoint.getLongitude());
                //map.addPolyline(trackOptions);

                SetLocationPoint(newPoint);
            }
//
//            if (locationTracking != null)
//                locationTracking.SetLocationPoint(newPoint);
        }
        else
        {
            oldPoint = new LatLng(newPoint.getLatitude(), newPoint.getLongitude());
        }

        return b;
    }

    private void addTracklineSegment(LatLng oldPoint, Location newPoint) {
        if (oldPoint != null)
        {
            us.ba3.me.Location[] loc = new us.ba3.me.Location[2];
            loc[0] = new us.ba3.me.Location(oldPoint.latitude, oldPoint.longitude);
            loc[1] = new us.ba3.me.Location(newPoint.getLatitude(), newPoint.getLongitude());
            mapView.addLineToVectorMap("TrackLine", loc, lineStyle);
        }
    }


}
