package com.mobileaviationtools.flightsimplannerpro.Weather;

import com.mobileaviationtools.flightsimplannerpro.Route.Route;
import com.mobileaviationtools.flightsimplannerpro.Route.Waypoint;

/**
 * Created by Rob Verhoef on 27-11-2016.
 */

public class WeatherURL {

    private String baseUrl = "https://www.aviationweather.gov/adds/dataserver_current/httpparam?dataSource=#DATASOURCE#" +
            "&requestType=retrieve&format=xml&#COMMAND#&hoursBeforeNow=1";


    public String getUrlByStation(datasource source, String station)
    {
        String url = baseUrl.replace("#DATASOURCE#", source.toString());
        String stationString  = "stationString="+station;
        url = url.replace("#COMMAND#", stationString);
        return url;
    }

    public String getUrlByRoute(datasource source, Route route)
    {
        String url = baseUrl.replace("#DATASOURCE#", source.toString());
        String flightpath = "flightPath=10;";
        for (Waypoint waypoint: route.waypoints)
        {
            flightpath = flightpath + waypoint.location.longitude + "," + waypoint.location.latitude + ";";
        }
        flightpath = flightpath.substring(0, flightpath.length()-1);
        url = url.replace("#COMMAND#", flightpath);
        return url;
    }

    public enum datasource
    {
        metars,
        aircraftreports,
        tafs,
        airsigmets,
        gairmets,
        stations
    }

    public enum command
    {

    }
}
