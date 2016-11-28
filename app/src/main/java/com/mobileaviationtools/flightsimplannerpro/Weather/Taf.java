package com.mobileaviationtools.flightsimplannerpro.Weather;

import android.content.Context;

import com.mobileaviationtools.flightsimplannerpro.Airports.Airport;
import com.mobileaviationtools.flightsimplannerpro.Database.AirportDataSource;

/**
 * Created by Rob Verhoef on 27-11-2016.
 */

public class Taf {
    public Taf(Context context) {
        this.context = context;
    }

    private String TAG = "Metar";
    private Context context;

    public String raw_text;

    public String station_id;

    public String issue_time;

    public void setStation_id(String station_id) {
        this.station_id = station_id;
        AirportDataSource airportDataSource = new AirportDataSource(context);
        airportDataSource.open(-1);
        airport = airportDataSource.GetAirportByIDENT(station_id);
        airportDataSource.close();
    }

    public Airport airport;
}
