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

    public WithinAirspaceCheck(Context context, Coordinate checkPoint)
    {
        this.context = context;
        foundAirspaces = new Airspaces(this.context);
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

        Airspaces as = new Airspaces(context);

        for (String a : airspacedbFiles) {
            Log.i(TAG, "read from db: " + a);
            AirspacesDB airspacesDB = new AirspacesDB(context);
            airspacesDB.Open(a);
            as.readFromDatabase(airspacesDB.GetAirspacesByCoordinate(point.getCoordinate()));
        }

        for (Airspace airspace : as)
        {
            if (airspace.getGeometry().contains(point)) {
                foundAirspaces.add(airspace);
                publishProgress(airspace);
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        Airspace airspace = (Airspace)values[0];
        if (onFoundAirspace != null) onFoundAirspace.OnFoundAirspace(airspace);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Object o) {
        if (onFoundAirspace != null) onFoundAirspace.OnFoundAllAirspaces(foundAirspaces);
        super.onPostExecute(o);
    }

    private OnFoundAirspace onFoundAirspace;
    public void SetOnFoundAirspace(OnFoundAirspace d) { onFoundAirspace = d; }
    public interface OnFoundAirspace
    {
        public void OnFoundAirspace(Airspace airspace);
        public void OnFoundAllAirspaces(Airspaces airspaces);
    }
}