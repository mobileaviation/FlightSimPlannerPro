package com.mobileaviationtools.flightsimplannerpro.Weather;

import android.content.Context;
import android.util.Log;

import com.mobileaviationtools.flightsimplannerpro.Airports.Airport;
import com.mobileaviationtools.flightsimplannerpro.Database.AirportDataSource;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Rob Verhoef on 27-11-2016.
 */

public class Metar {
    public Metar(Context context)
    {
        this.context = context;
    }

    private String TAG = "Metar";
    private Context context;

    public String raw_text;

    public String station_id;
    public void setStation_id(String station_id)
    {
        this.station_id = station_id;
        AirportDataSource airportDataSource = new AirportDataSource(context);
        airportDataSource.open(-1);
        airport = airportDataSource.GetAirportByIDENT(station_id);
        airportDataSource.close();
    }

    public Airport airport;
    public String flight_category;
    public String metar_type;
    public String observation_time;
}
