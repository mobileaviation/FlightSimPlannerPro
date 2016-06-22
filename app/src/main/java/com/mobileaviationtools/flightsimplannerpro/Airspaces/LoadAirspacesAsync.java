package com.mobileaviationtools.flightsimplannerpro.Airspaces;

import android.content.Context;
import android.os.AsyncTask;

import com.mobileaviationtools.flightsimplannerpro.Database.AirspacesDB;

import us.ba3.me.MapView;

/**
 * Created by Rob Verhoef on 22-6-2016.
 */
public class LoadAirspacesAsync extends AsyncTask {
    public MapView mapView;
    public String country;
    public Context context;

    @Override
    protected Object doInBackground(Object[] params) {
        AirspacesDB airspacesDB = new AirspacesDB(context);
        airspacesDB.Open("airspaces.db");
        Airspaces airspaces = new Airspaces();
        airspaces.readFromDatabase(airspacesDB.GetAirspaces(country));
        airspaces.createAirspacesLayer(mapView, country);
        return null;
    }
}
