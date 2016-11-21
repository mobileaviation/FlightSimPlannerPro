package com.mobileaviationtools.flightsimplannerpro.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mobileaviationtools.flightsimplannerpro.Airports.Airport;
import com.mobileaviationtools.flightsimplannerpro.Airports.Frequency;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Rob Verhoef on 21-11-2016.
 */

public class FrequenciesDataSource {
    private SQLiteDatabase database;
    private NavigationDBHelper dbHelper;
    private String TAG = "FrequenciesDataSource";

    public FrequenciesDataSource(Context context) {
        dbHelper = new NavigationDBHelper(context);
    }

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

    public ArrayList<Frequency> loadFrequenciesByAirport(Airport airport)
    {
        ArrayList<Frequency> frequencies = null;

        String query = "SELECT * FROM " + NavigationDBHelper.FREQUENCIES_TABLE_NAME +
                " WHERE " + NavigationDBHelper.C_airport_ref + "=" + airport.id;

        Cursor cursor = database.rawQuery(query, null);

        if(cursor.getCount()>0)
        {
            frequencies = new ArrayList<Frequency>();
            cursor.moveToFirst();
            while(!cursor.isAfterLast())
            {
                Frequency frequency = cursorToFrequency(cursor);
                frequencies.add(frequency);
                cursor.moveToNext();
            }

        }

        return frequencies;
    }

    private Frequency cursorToFrequency(Cursor cursor)
    {
        Frequency frequency = new Frequency();
        frequency._id = cursor.getInt(cursor.getColumnIndex(NavigationDBHelper.C_airport_ref));
        frequency.id = cursor.getInt(cursor.getColumnIndex("_id"));
        frequency.airport_ref = cursor.getInt(cursor.getColumnIndex(NavigationDBHelper.C_airport_ref));
        frequency.airport_ident = cursor.getString(cursor.getColumnIndex(NavigationDBHelper.C_airport_ident));
        frequency.type = cursor.getString(cursor.getColumnIndex(NavigationDBHelper.C_type));
        frequency.description = cursor.getString(cursor.getColumnIndex(NavigationDBHelper.C_description));
        frequency.frequency_mhz = cursor.getDouble(cursor.getColumnIndex(NavigationDBHelper.C_frequency_mhz));


        return frequency;
    }
}
