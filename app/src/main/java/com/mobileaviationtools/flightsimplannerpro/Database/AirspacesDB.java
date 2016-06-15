package com.mobileaviationtools.flightsimplannerpro.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Rob Verhoef on 15-6-2016.
 */
public class AirspacesDB {
    public AirspacesDB(Context context)
    {
        this.context = context;
    }

    private Context context;

    private SQLiteDatabase database;

    public Boolean Open(String database)
    {
        String databasename = DBFilesHelper.DatabasePath(context) + database;

        try {
            this.database = SQLiteDatabase.openDatabase(databasename, null, SQLiteDatabase.OPEN_READWRITE);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Cursor GetAirspaces(String country)
    {
        String[] selectionarg = new String[1];
        selectionarg[0] = "NL";
        String sql = "SELECT * FROM tbl_airspaces WHERE country=?;";
        return this.database.rawQuery(sql, selectionarg);
    }


}
