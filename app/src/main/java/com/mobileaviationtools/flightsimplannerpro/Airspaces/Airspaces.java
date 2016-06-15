package com.mobileaviationtools.flightsimplannerpro.Airspaces;

import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by Rob Verhoef on 15-6-2016.
 */
public class Airspaces extends ArrayList<Airspace>  {
    public Airspaces()
    {

    }

    public void Add(Airspace airspace) {
        this.add(airspace);
    }


    public void readFromDatabase(Cursor cursor)
    {
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Airspace airspace = new Airspace();
            airspace.AssignFromCursor(cursor);
            Add(airspace);
            cursor.moveToNext();
        }
    }
}