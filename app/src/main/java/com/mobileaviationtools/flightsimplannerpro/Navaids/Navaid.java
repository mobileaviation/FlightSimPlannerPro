package com.mobileaviationtools.flightsimplannerpro.Navaids;

/**
 * Created by Rob Verhoef on 6-7-2016.
 */
public class Navaid {
    public Navaid()
    {

    }

    public Integer id;
    public String filename;
    public String ident;
    public String name;
    public NavaidType type;
    public double frequency_khz;
    public double latitude_deg;
    public double longitude_deg;
    public Integer elevation_ft;
    public String iso_country;
    public double dme_frequency_khz;
    public double dme_channel;
    public double dme_latitude_deg;
    public double dme_longitude_deg;
    public Integer dme_elevation_ft;
    public double slaved_variation_deg;
    public double magnetic_variation_deg;
    public String usageType;
    public String power;
    public String associated_airport;
    public Integer associated_airport_id;
    //public Marker marker;

    public static NavaidType ParseNavaidType(String type)
    {
        NavaidType a = NavaidType.VOR;
        if (type.equals("DME")) a = NavaidType.DME;
        if (type.equals("NDB")) a = NavaidType.NDB;
        if (type.equals("NDB-DME")) a = NavaidType.NDB_DME;
        if (type.equals("TACAN")) a = NavaidType.TACAN;
        if (type.equals("VOR-DME")) a = NavaidType.VOR_DME;
        if (type.equals("VORTAC")) a = NavaidType.VORTAC;

        return a;
    }

//    public BitmapDescriptor GetIcon()
//    {
//        if (type == NavaidType.DME) return BitmapDescriptorFactory.fromResource(R.drawable.dme);
//        if (type == NavaidType.NDB) return BitmapDescriptorFactory.fromResource(R.drawable.ndb);
//        if (type == NavaidType.NDB_DME) return BitmapDescriptorFactory.fromResource(R.drawable.ndb);
//        if (type == NavaidType.TACAN) return BitmapDescriptorFactory.fromResource(R.drawable.tacan);
//        if (type == NavaidType.VOR) return BitmapDescriptorFactory.fromResource(R.drawable.vor);
//        if (type == NavaidType.VOR_DME) return BitmapDescriptorFactory.fromResource(R.drawable.vordme);
//        if (type == NavaidType.VORTAC) return BitmapDescriptorFactory.fromResource(R.drawable.vortac);
//
//        return null;
//    }

    public String getNavaidInfo()
    {
        String info = "No navaid information.";

        info = "Navaid Information................\n" +
                "Name : " + this.name + "\n" +
                "Code : " + this.ident + "\n" +
                "Type : " + this.type.toString() + "\n" +
                "Frequency : " + Double.toString(this.frequency_khz) + " KHz\n" +
                "\n" +
                "Location..........................\n" +
                "Latitude  : " + Double.toString(this.latitude_deg) + "\n" +
                "Longitude : " + Double.toString(this.longitude_deg) + "\n" +
                "Elevation : " + Long.toString(Math.round(this.elevation_ft)) + " ft\n" +
                "\n" +
                "Associated Airport : " + this.associated_airport;

        return info;
    }
}
