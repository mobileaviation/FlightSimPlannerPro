package com.mobileaviationtools.flightsimplannerpro.Info;

import java.util.ArrayList;

/**
 * Created by Rob Verhoef on 8-7-2016.
 */
public class Station {
    public Station(){
        siteTypes = new ArrayList<SiteType>();
        fir = false;
    }

    public String station_id;
    public Integer wmo_id;
    public Double latitude;
    public Double longitude;
    public Double elevation_m;
    public String site;
    public String state;
    public Boolean fir;
    private ArrayList<SiteType> siteTypes;
    public void AddSiteType(String type)
    {
        if (type.equals("METAR")) siteTypes.add(SiteType.METAR);
        if (type.equals("TAF")) siteTypes.add(SiteType.TAF);
    }

    public enum SiteType
    {
        METAR,
        TAF
    }

}
