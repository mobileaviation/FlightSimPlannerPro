package com.mobileaviationtools.flightsimplannerpro.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mobileaviationtools.flightsimplannerpro.Route.Route;
import com.mobileaviationtools.flightsimplannerpro.Route.Waypoint;
import com.mobileaviationtools.flightsimplannerpro.Route.WaypointType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Rob Verhoef on 8-7-2016.
 */
public class RouteDataSource {
    private SQLiteDatabase database;
    private UserDBHelper dbHelper;
    private String TAG = "RouteDataSource";
    private Context context;

    public RouteDataSource(Context context) {
        this.context = context;
        dbHelper = new UserDBHelper(context);
    }

    public void open(){
        try {
            database = dbHelper.OpenDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void close() {
        dbHelper.CloseDatabase();
    }

    private ContentValues createFlightPlanContentValues(Route route)
    {
        ContentValues values = new ContentValues();
        values.put(UserDBHelper.C_name, route.name);
        values.put(UserDBHelper.C_departure_airport_id, route.departure_airport.id);
        values.put(UserDBHelper.C_destination_airport_id, route.destination_airport.id);
        values.put(UserDBHelper.C_alternate_airport_id, route.alternate_airport.id);
        values.put(UserDBHelper.C_altitude_ft, route.altitude);
        values.put(UserDBHelper.C_indicated_airspeed_kt, route.indicated_airspeed);
        values.put(UserDBHelper.C_wind_speed_kt, route.wind_speed);
        values.put(UserDBHelper.C_wind_direction_deg, route.wind_direction);
        values.put(UserDBHelper.C_date, (route.date==null)? -1 : route.date.getTime());
        return values;
    }

    private String[] allColumns = {
            "_id",
            UserDBHelper.C_name,
            UserDBHelper.C_departure_airport_id,
            UserDBHelper.C_destination_airport_id,
            UserDBHelper.C_alternate_airport_id,
            UserDBHelper.C_altitude_ft,
            UserDBHelper.C_indicated_airspeed_kt,
            UserDBHelper.C_wind_speed_kt,
            UserDBHelper.C_wind_direction_deg,
            UserDBHelper.C_date
    };

    private ContentValues createWayPointContentValues(Waypoint waypoint)
    {
        ContentValues values = new ContentValues();
        values.put(UserDBHelper.C_name, waypoint.name);
        values.put(UserDBHelper.C_airport_id, waypoint.airport_id);
        values.put(UserDBHelper.C_fix_id, waypoint.fix_id);
        values.put(UserDBHelper.C_navaid_id, waypoint.navaid_id);
        values.put(UserDBHelper.C_sortorder, waypoint.order);
        values.put(UserDBHelper.C_latitude_deg, waypoint.getAndroidLocation().getLatitude());
        values.put(UserDBHelper.C_longitude_deg, waypoint.getAndroidLocation().getLongitude());
        values.put(UserDBHelper.C_flightplan_id, waypoint.flightplan_id);
        values.put(UserDBHelper.C_frequency_khz, waypoint.frequency);
        values.put(UserDBHelper.C_eto, ((waypoint.eto == null) ? 0l : waypoint.eto.getTime()));
        values.put(UserDBHelper.C_ato, ((waypoint.ato == null) ? 0l : waypoint.ato.getTime()));
        values.put(UserDBHelper.C_reto,((waypoint.reto == null)? 0l: waypoint.reto.getTime()));
        values.put(UserDBHelper.C_time_leg, waypoint.time_leg);
        values.put(UserDBHelper.C_time_total, waypoint.time_total);
        values.put(UserDBHelper.C_compass_heading_deg, waypoint.compass_heading);
        values.put(UserDBHelper.C_magnetic_heading_deg, waypoint.magnetic_heading);
        values.put(UserDBHelper.C_true_heading_deg, waypoint.true_heading);
        values.put(UserDBHelper.C_true_track_deg, waypoint.true_track);
        values.put(UserDBHelper.C_distance_leg, waypoint.distance_leg);
        values.put(UserDBHelper.C_distance_total, waypoint.distance_total);
        values.put(UserDBHelper.C_ground_speed_kt, waypoint.ground_speed);
        values.put(UserDBHelper.C_wind_speed_kt, waypoint.wind_speed);
        values.put(UserDBHelper.C_wind_direction_deg, waypoint.wind_direction);
        return values;
    }

    private String[] allWaypointColumns = {
            UserDBHelper.C_name,
            UserDBHelper.C_airport_id,
            UserDBHelper.C_fix_id,
            UserDBHelper.C_navaid_id,
            UserDBHelper.C_sortorder,
            UserDBHelper.C_latitude_deg,
            UserDBHelper.C_longitude_deg,
            UserDBHelper.C_flightplan_id,
            UserDBHelper.C_frequency_khz,
            UserDBHelper.C_eto,
            UserDBHelper.C_ato,
            UserDBHelper.C_reto,
            UserDBHelper.C_time_leg,
            UserDBHelper.C_time_total,
            UserDBHelper.C_compass_heading_deg,
            UserDBHelper.C_magnetic_heading_deg,
            UserDBHelper.C_true_heading_deg,
            UserDBHelper.C_true_track_deg,
            UserDBHelper.C_distance_leg,
            UserDBHelper.C_distance_total,
            UserDBHelper.C_ground_speed_kt,
            UserDBHelper.C_wind_speed_kt,
            UserDBHelper.C_wind_direction_deg
    };

    public void UpdateFlightplanWind(Route route)
    {
        String[] args = {route.id.toString()};
        database.update(UserDBHelper.FLIGHTPLAN_TABLE_NAME,
                createFlightPlanContentValues(route),
                "_id=?", args);

    }

    public Integer InsertnewFlightPlan(Route route)
    {
        ContentValues flightPlanContentvalues = createFlightPlanContentValues(route);
        Integer insertId = (int)database.insert(UserDBHelper.FLIGHTPLAN_TABLE_NAME, null,
                flightPlanContentvalues);

        route.waypoints.get(0).flightplan_id = insertId;
        route.waypoints.get(1).flightplan_id = insertId;

        database.insert(UserDBHelper.USERWAYPOINT_TABLE_NAME, null,
                createWayPointContentValues(route.waypoints.get(0)));
        database.insert(UserDBHelper.USERWAYPOINT_TABLE_NAME, null,
                createWayPointContentValues(route.waypoints.get(1)));

        return insertId;
    }

    public void UpdateInsertWaypoints(ArrayList<Waypoint> waypoints)
    {
        for (Waypoint waypoint : waypoints)
        {
//            String[] args = {airport_id.toString()};
//            database.delete(DBHelper.RUNWAY_TABLE_NAME, DBHelper.C_airport_id + " =?", args);
            if (waypoint.id == null)
            {
                long id = database.insert(UserDBHelper.USERWAYPOINT_TABLE_NAME, null,
                        createWayPointContentValues(waypoint));
                waypoint.id = ((int) id);
            }
            else
            {
                updateWaypoint(waypoint);
            }
        }
    }

    private void updateWaypoint(Waypoint waypoint)
    {
        String[] args = {waypoint.id.toString()};
        database.update(UserDBHelper.USERWAYPOINT_TABLE_NAME,
                createWayPointContentValues(waypoint),
                "_id=?", args);
    }

    public void deleteWaypoint(Waypoint waypoint)
    {
        String[] args = {waypoint.id.toString()};
        database.delete(UserDBHelper.USERWAYPOINT_TABLE_NAME,
                "_id=?", args);
    }

    public Integer GetFlightplanCount()
    {
        Integer count = 0;
        String query = "SELECT COUNT(*) c FROM " + UserDBHelper.FLIGHTPLAN_TABLE_NAME + ";";
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
            count = cursor.getInt(cursor.getColumnIndex("c"));
        }
        return count;
    }

    public Route GetWaypointsByRoute(Route route)
    {
        route.waypoints = new ArrayList<Waypoint>();

        String query = "SELECT * FROM " + UserDBHelper.USERWAYPOINT_TABLE_NAME
                + " WHERE " + UserDBHelper.C_flightplan_id + "=" + Integer.toString(route.id)
                + " ORDER BY " + UserDBHelper.C_sortorder;

        Cursor cursor = database.rawQuery(query, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            Waypoint waypoint = cursorToWaypoint(cursor);
            route.waypoints.add(waypoint);
            cursor.moveToNext();
        }

        if (route.date == null)
        {
            Log.i(TAG, "Updating sortorder for flightplan: " +route.name);
            // update waypoint sortorder
            // update flightplan set date to today

            int orderInc = 1000 / (route.waypoints.size()-1);
            int order = orderInc;
            route.waypoints.get(0).order = 1;
            route.waypoints.get(route.waypoints.size()-1).order = 1000;
            for (int i=1; i<route.waypoints.size()-1; i++) {
                route.waypoints.get(i).order = order;
                order = order + orderInc;
            }

            UpdateInsertWaypoints(route.waypoints);
            route.date = new Date();
            UpdateFlightplanWind(route);
        }

        return route;
    }

    public void MoveWaypointDown(Route route, Waypoint waypoint)
    {
        int currentOrder = waypoint.order;
        int i = 0;
        int newOrder = 0;
        Waypoint p = null;
        while ((i<route.waypoints.size()))
        {
            int o = route.waypoints.get(i).order;

            if ((o>currentOrder) && p == null) {
                p = route.waypoints.get(i);
                newOrder = p.order;
            }
            i++;
        }

        if (p != null) {
            p.order = currentOrder;
            waypoint.order = newOrder;
            updateWaypoint(p);
            updateWaypoint(waypoint);
        }
    }

    public void MoveWaypointUp(Route route, Waypoint waypoint)
    {
        int currentOrder = waypoint.order;
        int i = 0;
        int newOrder = 0;
        Waypoint p = null;
        while (i<route.waypoints.size())
        {
            int o = route.waypoints.get(i).order;
            if (o<currentOrder) {
                p = route.waypoints.get(i);
                newOrder = p.order;
            }
            i++;
        }

        if (p != null) {
            p.order = currentOrder;
            waypoint.order = newOrder;
            updateWaypoint(p);
            updateWaypoint(waypoint);
        }
    }

    public void clearTimes(Route route, boolean doUpdate)
    {
        for (Waypoint w: route.waypoints)
        {
            w.eto = null;
            w.ato = null;
            w.reto = null;
        }

        if(doUpdate) UpdateInsertWaypoints(route.waypoints);
    }

    public void resetCourses(Route route, boolean doUpdate)
    {
        for (Waypoint w: route.waypoints)
        {
            w.magnetic_heading = w.true_heading;
            w.compass_heading = w.true_heading;
        }

        if (doUpdate) UpdateInsertWaypoints(route.waypoints);
    }

    public ArrayList<Route> GetAllRoutes()
    {
        ArrayList<Route> routes = new ArrayList<Route>();
        Cursor cursor = database.query(UserDBHelper.FLIGHTPLAN_TABLE_NAME,
                allColumns, null, null, null, null, "_id DESC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            Route route = new Route(this.context);
            cursorToRoute(cursor, route);
            routes.add(route);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return routes;
    }

    public Route GetRouteByID(Integer id, Route route)
    {
        String query = "SELECT * FROM " + UserDBHelper.FLIGHTPLAN_TABLE_NAME +
                " WHERE _id=" + Integer.toString(id) + ";";

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
            cursorToRoute(cursor, route);
        }

        return route;
    }

    private Route cursorToRoute(Cursor cursor, Route route)
    {
        route.id = cursor.getInt(cursor.getColumnIndex("_id"));
        route.departure_airport.id = cursor.getInt(cursor.getColumnIndex(UserDBHelper.C_departure_airport_id));
        route.destination_airport.id = cursor.getInt(cursor.getColumnIndex(UserDBHelper.C_destination_airport_id));
        route.alternate_airport.id = cursor.getInt(cursor.getColumnIndex(UserDBHelper.C_alternate_airport_id));
        route.name = cursor.getString(cursor.getColumnIndex(UserDBHelper.C_name));
        route.wind_speed = cursor.getInt(cursor.getColumnIndex(UserDBHelper.C_wind_speed_kt));
        route.wind_direction = cursor.getFloat(cursor.getColumnIndex(UserDBHelper.C_wind_direction_deg));
        route.altitude = cursor.getInt(cursor.getColumnIndex(UserDBHelper.C_altitude_ft));
        route.indicated_airspeed = cursor.getInt(cursor.getColumnIndex(UserDBHelper.C_indicated_airspeed_kt));
        long t = (cursor.isNull(cursor.getColumnIndex(UserDBHelper.C_date))) ? 0 :
                cursor.getLong(cursor.getColumnIndex(UserDBHelper.C_date));
        if (t>0) {
            route.date = new Date();
            route.date.setTime(t);
        }

        return route;
    }

    private Waypoint cursorToWaypoint(Cursor cursor)
    {
        Waypoint waypoint = new Waypoint();

        waypoint.id = cursor.getInt(cursor.getColumnIndex("_id"));
        waypoint.flightplan_id = cursor.getInt(cursor.getColumnIndex(UserDBHelper.C_flightplan_id));
        waypoint.airport_id = cursor.getInt(cursor.getColumnIndex(UserDBHelper.C_airport_id));
        waypoint.fix_id = cursor.getInt(cursor.getColumnIndex(UserDBHelper.C_fix_id));
        waypoint.navaid_id = cursor.getInt(cursor.getColumnIndex(UserDBHelper.C_navaid_id));
        waypoint.order = cursor.getInt(cursor.getColumnIndex(UserDBHelper.C_sortorder));
        waypoint.name = cursor.getString(cursor.getColumnIndex(UserDBHelper.C_name));
        waypoint.frequency = cursor.getFloat(cursor.getColumnIndex(UserDBHelper.C_frequency_khz));
        long eto = cursor.getLong(cursor.getColumnIndex(UserDBHelper.C_eto));
        waypoint.eto = (eto == 0) ? null : new Date(eto);
        long ato = cursor.getLong(cursor.getColumnIndex(UserDBHelper.C_ato));
        waypoint.ato = (ato == 0) ? null : new Date(ato);
        Long reto = cursor.getLong(cursor.getColumnIndex(UserDBHelper.C_reto));
        waypoint.reto = (reto == 0) ? null : new Date(reto);
        waypoint.time_leg = cursor.getInt(cursor.getColumnIndex(UserDBHelper.C_time_leg));
        waypoint.time_total = cursor.getInt(cursor.getColumnIndex(UserDBHelper.C_time_total));
        waypoint.compass_heading = cursor.getFloat(cursor.getColumnIndex(UserDBHelper.C_compass_heading_deg));
        waypoint.magnetic_heading = cursor.getFloat(cursor.getColumnIndex(UserDBHelper.C_magnetic_heading_deg));
        waypoint.true_heading = cursor.getFloat(cursor.getColumnIndex(UserDBHelper.C_true_heading_deg));
        waypoint.true_track = cursor.getFloat(cursor.getColumnIndex(UserDBHelper.C_true_track_deg));
        waypoint.distance_leg = cursor.getInt(cursor.getColumnIndex(UserDBHelper.C_distance_leg));
        waypoint.distance_total = cursor.getInt(cursor.getColumnIndex(UserDBHelper.C_distance_total));
        waypoint.ground_speed = cursor.getInt(cursor.getColumnIndex(UserDBHelper.C_ground_speed_kt));
        waypoint.wind_speed = cursor.getInt(cursor.getColumnIndex(UserDBHelper.C_wind_speed_kt));
        waypoint.wind_direction = cursor.getFloat(cursor.getColumnIndex(UserDBHelper.C_wind_direction_deg));

        waypoint.getAndroidLocation().setLatitude(cursor.getDouble(cursor.getColumnIndex(UserDBHelper.C_latitude_deg)));
        waypoint.getAndroidLocation().setLongitude(cursor.getDouble(cursor.getColumnIndex(UserDBHelper.C_longitude_deg)));

        if (waypoint.order==1) waypoint.waypointType = WaypointType.departudeAirport;
        else
        if (waypoint.order==1000) waypoint.waypointType = WaypointType.destinationAirport;
        else
        if (waypoint.navaid_id>0) waypoint.waypointType = WaypointType.navaid;
        else
        if (waypoint.fix_id>0) waypoint.waypointType = WaypointType.fix;
        else
            waypoint.waypointType = WaypointType.userwaypoint;

        return waypoint;
    }

    public void calculateMagneticHeading(Integer variation, Route route)
    {
        for (Waypoint waypoint : route.waypoints)
        {
            if (waypoint.order>1)
            {
                waypoint.magnetic_heading = waypoint.true_heading - (float)variation;
                if (waypoint.magnetic_heading<0) waypoint.magnetic_heading = waypoint.magnetic_heading + 360f;
                waypoint.compass_heading = waypoint.magnetic_heading;

//                String query = "UPDATE " + DBHelper.USERWAYPOINT_TABLE_NAME + " SET " + DBHelper.C_magnetic_heading
//                        + "=" + Float.toString(waypoint.magnetic_heading) +
//                        ", " + DBHelper.C_compass_heading
//                        + "=" + Float.toString(waypoint.magnetic_heading) +
//                        " WHERE _id=" + Integer.toString(waypoint.id);
//
//                database.rawQuery(query, null);
            }
        }

        UpdateInsertWaypoints(route.waypoints);
    }

    public void calculateETO(Date TakeoffDate, Route route)
    {
        for (Waypoint waypoint : route.waypoints)
        {
            if (waypoint.order == 1) waypoint.eto = TakeoffDate;
            if (waypoint.order>1)
            {
                Calendar c = Calendar.getInstance();
                c.setTime(TakeoffDate);
                c.add(Calendar.MINUTE, waypoint.time_total);
                waypoint.eto = c.getTime();
            }
        }

        UpdateInsertWaypoints(route.waypoints);
    }

    public void setATOcalculateRETO(Waypoint waypoint, Route route)
    {
        Date currentTime = new Date();

        waypoint.ato = currentTime;
        Boolean update = false;

        for (Waypoint w : route.waypoints)
        {
            if (update)
            {
                Calendar c = Calendar.getInstance();
                c.setTime(currentTime);
                c.add(Calendar.MINUTE, w.time_leg);
                w.reto = c.getTime();
            }

            if (waypoint.id == w.id) update = true;
        }

        UpdateInsertWaypoints(route.waypoints);
    }

    public void calculateCompassHeading(Integer deviation, Waypoint waypoint, Route route)
    {
        if (waypoint.order>1)
        {
            waypoint.compass_heading = waypoint.magnetic_heading - (float)deviation;
            if (waypoint.compass_heading<0) waypoint.compass_heading = waypoint.compass_heading + 360f;

//                String query = "UPDATE " + DBHelper.USERWAYPOINT_TABLE_NAME + " SET " + DBHelper.C_magnetic_heading
//                        + "=" + Float.toString(waypoint.magnetic_heading) +
//                        ", " + DBHelper.C_compass_heading
//                        + "=" + Float.toString(waypoint.magnetic_heading) +
//                        " WHERE _id=" + Integer.toString(waypoint.id);
//
//                database.rawQuery(query, null);
        }

        UpdateInsertWaypoints(route.waypoints);
    }
}
