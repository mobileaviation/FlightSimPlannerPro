package com.mobileaviationtools.flightsimplannerpro.Test;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.LocationSource;
import com.mobileaviationtools.flightsimplannerpro.Database.LocationTrackingDataSource;
import com.mobileaviationtools.flightsimplannerpro.Track.TrackPoint;

import java.util.ArrayList;

/**
 * Created by Rob Verhoef on 19-7-2016.
 */
public class TrackTest {
    public TrackTest(Context context)
    {
        this.context = context;
    }

    private Context context;
    public ArrayList<TrackPoint> trackPoints;

    public void LoadTrackpoints()
    {
        Integer track_id = 60;
        LocationTrackingDataSource locationTrackingDataSource = new
                LocationTrackingDataSource(context);
        locationTrackingDataSource.open();
        trackPoints = locationTrackingDataSource.getTrackPoints(track_id);
        locationTrackingDataSource.close();
    }

    public void startRoute()
    {
        if (trackPoints != null) {
            for (TrackPoint t : trackPoints) {
                Location l = new Location("trackpoint");
                l.setLatitude(t.latitude);
                l.setLongitude(t.longitude);
                l.setAltitude(t.altitude);
                l.setBearing(t.true_heading);
                l.setSpeed(t.ground_speed);
                if (onLocationChangedListener != null) onLocationChangedListener.onLocationChanged(l);
            }
        }
    }

    private LocationSource.OnLocationChangedListener onLocationChangedListener;
    public void setOnLocationChangedListener(final LocationSource.OnLocationChangedListener l)
    {
        onLocationChangedListener = l;
    }
}
