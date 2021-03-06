package com.mobileaviationtools.flightsimplannerpro.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.vividsolutions.jts.geom.Coordinate;

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
        selectionarg[0] = country;
        String sql = "SELECT * FROM tbl_airspaces WHERE country=?;";// AND _id=6;";// (_id=7 OR _id=9 OR _id=6);";
        return this.database.rawQuery(sql, selectionarg);
    }

    public Cursor GetAirspacesByCoordinate(Coordinate coordinate)
    {
        String sql = "SELECT * FROM tbl_Airspaces " +
                " WHERE #LAT#<lat_top_left " +
                "AND #LAT#>lat_bottom_right " +
                "AND #LON#>lon_top_left " +
                "AND #LON#<lot_bottom_right;";
        sql = sql.replace("#LON#", Double.toString(coordinate.x));
        sql = sql.replace("#LAT#", Double.toString(coordinate.y));
        Log.i("AirspacesDB", sql);
        return this.database.rawQuery(sql, null);
    }

}
