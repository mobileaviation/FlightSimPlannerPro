package com.mobileaviationtools.flightsimplannerpro.Track;

import java.util.Date;

/**
 * Created by Rob Verhoef on 14-7-2016.
 */
public class TrackPoint
{
    public TrackPoint() {
        time = new Date();
    }

    public Integer id;
    public String name;
    public Float longitude;
    public Float latitude;
    public Date time;
    public Float ground_speed;
    public Float altitude;
    public Float true_heading;
}
