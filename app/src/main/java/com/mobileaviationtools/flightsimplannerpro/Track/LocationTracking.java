package com.mobileaviationtools.flightsimplannerpro.Track;

import android.content.Context;
import android.location.Location;

import com.mobileaviationtools.flightsimplannerpro.Database.LocationTrackingDataSource;
import com.mobileaviationtools.flightsimplannerpro.Route.Route;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Rob Verhoef on 14-7-2016.
 */
public class LocationTracking {
    public LocationTracking(Route route, Context context)
    {
        this.route = route;
        this.context = context;

        String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if (this.route != null) Name = currentDateandTime + " : " + this.route.name;
        else Name = currentDateandTime;

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
    public String Name;
    public Long Id;
}
