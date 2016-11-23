package com.mobileaviationtools.flightsimplannerpro.Airspaces;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mobileaviationtools.flightsimplannerpro.Database.AirspacesDB;
import com.mobileaviationtools.flightsimplannerpro.Database.DBFilesHelper;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import java.util.ArrayList;

/**
 * Created by Rob Verhoef on 23-11-2016.
 */

public class WithinAirspaceCheck extends AsyncTask {

    public WithinAirspaceCheck(Context context, Airspaces airpaces, Coordinate checkPoint)
    {
        this.context = context;
        foundAirspaces = airpaces;
        GeometryFactory factory = new GeometryFactory();
        point = factory.createPoint(checkPoint);
    }

    private Airspaces foundAirspaces;
    private Context context;
    private Point point;
    private String TAG = "WithinAirspaceCheck";

    @Override
    protected Object doInBackground(Object[] params) {

        ArrayList<String> airspacedbFiles = DBFilesHelper.CopyDatabases(context.getApplicationContext(), false);

        for (String a : airspacedbFiles) {
            Log.i(TAG, "read from db: " + a);
            AirspacesDB airspacesDB = new AirspacesDB(context);
            airspacesDB.Open(a);
            foundAirspaces.readFromDatabase(airspacesDB.GetAirspacesByCoordinate(point.getCoordinate()));
        }

        for (Airspace airspace : foundAirspaces)
        {
            if (airspace.getGeometry().contains(point)) Log.i(TAG, "Found: " + airspace.Name);
        }

        return null;
    }
}