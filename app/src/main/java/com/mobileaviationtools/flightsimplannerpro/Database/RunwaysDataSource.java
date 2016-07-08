package com.mobileaviationtools.flightsimplannerpro.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mobileaviationtools.flightsimplannerpro.Airports.Airport;
import com.mobileaviationtools.flightsimplannerpro.Airports.Runway;
import com.mobileaviationtools.flightsimplannerpro.Airports.Runways;

import java.io.IOException;

/**
 * Created by Rob Verhoef on 8-7-2016.
 */
public class RunwaysDataSource {
    private SQLiteDatabase database;
    private NavigationDBHelper dbHelper;
    private String TAG = "GooglemapsTest";

    public RunwaysDataSource(Context context) {
        dbHelper = new NavigationDBHelper(context);
    }

    private String[] runwayColumns = {
            NavigationDBHelper.C_airport_ref,
            NavigationDBHelper.C_airport_ident,
            NavigationDBHelper.C_length,
            NavigationDBHelper.C_width,
            NavigationDBHelper.C_surface,
            NavigationDBHelper.C_le_ident,
            NavigationDBHelper.C_le_latitude,
            NavigationDBHelper.C_le_longitude,
            NavigationDBHelper.C_le_elevation,
            NavigationDBHelper.C_le_heading,
            NavigationDBHelper.C_le_displaced_threshold,
            NavigationDBHelper.C_he_ident,
            NavigationDBHelper.C_he_latitude,
            NavigationDBHelper.C_he_longitude,
            NavigationDBHelper.C_he_elevation,
            NavigationDBHelper.C_he_heading,
            NavigationDBHelper.C_he_displaced_threshold
    };

    public void open(){
        try {
            dbHelper.createDataBase();
            database = dbHelper.openDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void close() {
        dbHelper.close();
    }

    public Integer GetRunwaysCount()
    {
        Integer count = 0;
        String query = "SELECT COUNT(*) c FROM " + NavigationDBHelper.RUNWAY_TABLE_NAME + ";";
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
            count = cursor.getInt(cursor.getColumnIndex("c"));
        }
        return count;
    }

    public Runways loadRunwaysByAirport(Airport airport)
    {
        Runways runways = null;

        String query = "SELECT * FROM " + NavigationDBHelper.RUNWAY_TABLE_NAME +
                " WHERE " + NavigationDBHelper.C_airport_ref + "=" + airport.id;


        Cursor cursor = database.rawQuery(query, null);

        if(cursor.getCount()>0)
        {
            runways = new Runways();
            cursor.moveToFirst();
            while(!cursor.isAfterLast())
            {
                Runway runway = cursorToRunway(cursor);
                runways.add(runway);
                cursor.moveToNext();
            }

        }

        return runways;
    }

    private Runway cursorToRunway(Cursor cursor)
    {
        Runway runway = new Runway();
        runway.airport_ref = cursor.getInt(cursor.getColumnIndex(NavigationDBHelper.C_airport_ref));
        runway.airport_ident = cursor.getString(cursor.getColumnIndex(NavigationDBHelper.C_airport_ident));
        runway.length_ft = cursor.getInt(cursor.getColumnIndex(NavigationDBHelper.C_length));
        runway.width_ft = cursor.getInt(cursor.getColumnIndex(NavigationDBHelper.C_width));
        runway.surface = cursor.getString(cursor.getColumnIndex(NavigationDBHelper.C_surface));
        runway.id = cursor.getInt(cursor.getColumnIndex("id"));
        runway.le_ident = cursor.getString(cursor.getColumnIndex(NavigationDBHelper.C_le_ident));
        runway.le_latitude_deg = cursor.getDouble(cursor.getColumnIndex(NavigationDBHelper.C_le_latitude));
        runway.le_longitude_deg = cursor.getDouble(cursor.getColumnIndex(NavigationDBHelper.C_le_longitude));
        runway.le_elevation_ft = cursor.getInt(cursor.getColumnIndex(NavigationDBHelper.C_le_elevation));
        runway.le_heading_degT = cursor.getDouble(cursor.getColumnIndex(NavigationDBHelper.C_le_heading));
        runway.le_displaced_threshold_ft = cursor.getInt(cursor.getColumnIndex(NavigationDBHelper.C_le_displaced_threshold));
        runway.he_ident = cursor.getString(cursor.getColumnIndex(NavigationDBHelper.C_he_ident));
        runway.he_latitude_deg = cursor.getDouble(cursor.getColumnIndex(NavigationDBHelper.C_he_latitude));
        runway.he_longitude_deg = cursor.getDouble(cursor.getColumnIndex(NavigationDBHelper.C_he_longitude));
        runway.he_elevation_ft = cursor.getInt(cursor.getColumnIndex(NavigationDBHelper.C_he_elevation));
        runway.he_heading_degT = cursor.getDouble(cursor.getColumnIndex(NavigationDBHelper.C_he_heading));
        runway.he_displaced_threshold_ft = cursor.getInt(cursor.getColumnIndex(NavigationDBHelper.C_he_displaced_threshold));

        return runway;
    }

    private ContentValues getRunwayValues(Runway runway)
    {
        ContentValues values = new ContentValues();
        values.put(NavigationDBHelper.C_airport_ref, runway.airport_ref);
        values.put(NavigationDBHelper.C_airport_ident, runway.airport_ident);
        values.put(NavigationDBHelper.C_length, runway.length_ft);
        values.put(NavigationDBHelper.C_width, runway.width_ft);
        values.put(NavigationDBHelper.C_surface, runway.surface);
        values.put(NavigationDBHelper.C_le_ident, runway.le_ident);
        values.put(NavigationDBHelper.C_le_latitude, runway.le_latitude_deg);
        values.put(NavigationDBHelper.C_le_longitude, runway.le_longitude_deg);
        values.put(NavigationDBHelper.C_le_elevation, runway.le_elevation_ft);
        values.put(NavigationDBHelper.C_le_heading, runway.le_heading_degT);
        values.put(NavigationDBHelper.C_le_displaced_threshold, runway.le_displaced_threshold_ft);
        values.put(NavigationDBHelper.C_he_ident, runway.he_ident);
        values.put(NavigationDBHelper.C_he_latitude, runway.he_latitude_deg);
        values.put(NavigationDBHelper.C_he_longitude, runway.he_longitude_deg);
        values.put(NavigationDBHelper.C_he_elevation, runway.he_elevation_ft);
        values.put(NavigationDBHelper.C_he_heading, runway.he_heading_degT);
        values.put(NavigationDBHelper.C_he_displaced_threshold, runway.he_displaced_threshold_ft);

        return values;
    }
}
