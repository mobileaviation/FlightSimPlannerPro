package com.mobileaviationtools.flightsimplannerpro.Airports;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

import com.google.android.gms.common.server.converter.ConverterWrapper;
import com.mobileaviationtools.flightsimplannerpro.Helpers;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequence;
import com.vividsolutions.jts.io.WKTWriter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Rob Verhoef on 6-7-2016.
 */
public class Airport implements Serializable {
    public Airport()
    {
        runways = new Runways();
        frequencies = new ArrayList<Frequency>();
        this.w = 0f;
        this.h = 0f;
    }


    public int id;
    public AirportType type;
    public String name;
    public String ident;
    public double latitude_deg;
    public double longitude_deg;
    public double elevation_ft;
    public String continent;
    public String iso_country;
    public String iso_region;
    public String municipality;
    public String scheduled_service;
    public String gps_code;
    public String iata_code;
    public String local_code;
    public String home_link;
    public String wikipedia_link;
    public String keywords;
    public int version;
    public Date modified;
    //public Marker marker;
    public double heading;
    public Integer MapLocation_ID;
    public Integer PID;

    public Runways runways;

    public ArrayList<Frequency> frequencies;

    //public LatLng getLatLng()
//    {
//        return new LatLng(latitude_deg, longitude_deg);
//    }


    private Float w;
    private Float h;

    public String getPointString()
    {
        Coordinate c = new Coordinate(longitude_deg, latitude_deg);
        return WKTWriter.toPoint(c);
    }

    public Bitmap GetIcon(float angle, String iata_code, Context context)
    {
        if (type == AirportType.large_airport) {
            //return getSmallAirportIcon(angle, iata_code);
            return getLargeAirportIcon(iata_code, context);
            //return BitmapDescriptorFactory.fromResource(R.drawable.large_airport);
        }
        //if (type == AirportType.heliport) return BitmapDescriptorFactory.fromResource(R.drawable.heliport);
        //if (type == AirportType.small_airport) return BitmapDescriptorFactory.fromResource(R.drawable.small_airport);
        //if (type == AirportType.medium_airport) return BitmapDescriptorFactory.fromResource(R.drawable.medium_airport);

        if (type == AirportType.medium_airport) return getSmallAirportIcon(angle, iata_code, context );
        if (type == AirportType.small_airport) return getSmallAirportIcon(angle, iata_code, context );


        //if (type == AirportType.closed) return null;

        return null;
    }

    private Bitmap getLargeAirportIcon(String iata_code, Context context)
    {
        this.w = 100f;
        this.h = 100f;
        Double width = Double.parseDouble(Float.toString(Helpers.convertDpToPixel(this.w, context)));
        Double height = Double.parseDouble(Float.toString(Helpers.convertDpToPixel(this.h, context)));
        Double centerX_deg = runways.getCenterX_deg();
        Double centerY_deg = runways.getCenterY_deg();

        Bitmap airportBitmap = Bitmap.createBitmap(width.intValue(), height.intValue(), Bitmap.Config.ARGB_8888);
        Canvas airportCanvas = new Canvas(airportBitmap);

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStrokeWidth(3);
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setColor(Color.argb(40,0,0,0));
        airportCanvas.drawCircle((width.floatValue()/2), (height.floatValue()/2), (width.floatValue()/2)-2, p);

        p.setStrokeWidth(3);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.BLUE);
        airportCanvas.drawCircle((width.floatValue()/2), (height.floatValue()/2), (width.floatValue()/2)-2, p);

        p.setStrokeWidth(2);
        p.setColor(Color.BLACK);

        Integer v1000 = Math.round(Helpers.convertDpToPixel(1000, context));
        Integer v500 = Math.round(Helpers.convertDpToPixel(500, context));
        Integer v50 = Math.round(Helpers.convertDpToPixel(50, context));
        Integer v2 = Math.round(Helpers.convertDpToPixel(2, context));
        Integer v6 = Math.round(Helpers.convertDpToPixel(6, context));
        Integer v20 = Math.round(Helpers.convertDpToPixel(20, context));
        Integer v21 = Math.round(Helpers.convertDpToPixel(21, context));

        Point lefTop = new Point(v1000,v1000);
        Point rightBotton = new Point(0,0);
        for (Runway r : this.runways)
        {
            double left = v50 + ((r.le_longitude_deg - centerX_deg) * v500);
            double top = v50 + ((centerY_deg - r.le_latitude_deg) * v1000);
            double right = v50 + ((r.he_longitude_deg - centerX_deg) * v500);
            double bottom = v50 + ((centerY_deg - r.he_latitude_deg) * v1000);

            if (lefTop.x>(int)left) lefTop.x = (int)left;
            if (lefTop.y>(int)top) lefTop.y = (int)top;
            if (rightBotton.x<(int)right) rightBotton.x = (int)right;
            if (rightBotton.y<(int)bottom) rightBotton.y = (int)bottom;

            p.setStrokeWidth(v6);
            p.setColor(Color.argb(255,110,245,78));
            airportCanvas.drawLine((float)left, (float)top, (float)right, (float)bottom, p);
            p.setStrokeWidth(v2);
            p.setColor(Color.BLACK);
            airportCanvas.drawLine((float)left, (float)top, (float)right, (float)bottom, p);
        }

//        int w = rightBotton.x - lefTop.x;
//        w = (w<50) ? 50 : w;
//        int h = rightBotton.y - lefTop.y;
//        h = (h<50) ? 50 : h;
//        if (w>h) h=w; else w=h;
//        Bitmap completeBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//        Canvas completeCanvas = new Canvas(completeBitmap);
//        Rect srcRect = new Rect(lefTop.x, lefTop.y, rightBotton.x, rightBotton.y);
//        RectF dstRect = new RectF(0f,0f,w, h);
//        completeCanvas.drawBitmap(airportBitmap, srcRect,dstRect, p);



        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(v20);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);

        airportCanvas.drawText(iata_code, (width.intValue()/2)+1 ,v21,textPaint);
        textPaint.setColor(Color.argb(255,246,249,89));
        airportCanvas.drawText(iata_code, (width.intValue()/2),v20,textPaint);

        return airportBitmap;
    }

    private Bitmap getSmallAirportIcon(float angle, String ICAOCode, Context context)
    {
        this.w = 60f;
        this.h = 45f;

        Integer s = Math.round(Helpers.convertDpToPixel(this.w/2, context ));

        Bitmap rotateBitmap = Bitmap.createBitmap(s, s,
                Bitmap.Config.ARGB_8888);
        Canvas rotateCanvas = new Canvas(rotateBitmap);
        rotateCanvas.save();
        rotateCanvas.rotate(angle,(s/2),(s/2));

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStrokeWidth(Math.round(Helpers.convertDpToPixel(3, context )));
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.BLACK);

        rotateCanvas.drawCircle((s/2),(s/2),Math.round(Helpers.convertDpToPixel(11, context )),p);

        RectF r = new RectF(Math.round(Helpers.convertDpToPixel(12, context )),0,
                Math.round(Helpers.convertDpToPixel(18, context )),s);

        p.setColor(Color.WHITE);
        p.setStrokeWidth(Math.round(Helpers.convertDpToPixel(1, context )));
        p.setStyle(Paint.Style.FILL);
        rotateCanvas.drawRect(r, p);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.BLACK);
        //rotateCanvas.drawRect(11,0,19,30, p);

        rotateCanvas.drawRect(r, p);


        rotateCanvas.restore();

        Bitmap textBitmap = Bitmap.createBitmap((s*2),
                Math.round(Helpers.convertDpToPixel(this.h, context )),
                Bitmap.Config.ARGB_8888);
        Canvas textCanvas = new Canvas(textBitmap);
        textCanvas.drawBitmap(rotateBitmap, (s/2), (s/2), null);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.argb(50,0,0,0));
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(Math.round(Helpers.convertDpToPixel(14, context )));
        textPaint.setTextAlign(Paint.Align.CENTER);

        textCanvas.drawText(ICAOCode, Math.round(Helpers.convertDpToPixel(31, context )),
                Math.round(Helpers.convertDpToPixel(14, context )),textPaint);
        textPaint.setColor(Color.BLUE);
        textCanvas.drawText(ICAOCode, Math.round(Helpers.convertDpToPixel(30, context )),
                Math.round(Helpers.convertDpToPixel(13, context )),textPaint);

        return textBitmap;


    }

    public PointF getAnchorPoint(Context context)
    {
        return new PointF(Helpers.convertDpToPixel((this.w/2), context), Helpers.convertDpToPixel((this.h/2), context));
    }

    public static AirportType ParseAirportType(String type)
    {
        AirportType a = AirportType.closed;
        int i = 0;
        if (type.equals("closed")) a = AirportType.closed;
        if (type.equals("heliport")) a = AirportType.heliport;
        if (type.equals("small_airport")) a = AirportType.small_airport;
        if (type.equals("medium_airport")) a = AirportType.medium_airport;
        if (type.equals("large_airport")) a = AirportType.large_airport;

        return a;
    }

    public String getAirportInfoString()
    {
        String info;

        info = "Airport Information................\n" +
                "Name : " + this.name + "\n" +
                "Code : " + this.ident + "\n" +
                "Type : " + this.type.toString() + "\n" +
                "\n" +
                "Location..........................\n" +
                "Latitude  : " + Double.toString(this.latitude_deg) + "\n" +
                "Longitude : " + Double.toString(this.longitude_deg) + "\n" +
                "Elevation : " + Long.toString(Math.round(this.elevation_ft)) + " ft";

        return info;
    }

    public String getRunwaysInfo()
    {
        String info = "No runway information.";
        if (this.runways != null)
        {
            if (this.runways.size()>0)
            {
                info = "Runways...........................\n";
                for(Runway r : this.runways)
                {
                    info = info + "No      : " + r.le_ident + "," + r.he_ident + "\n" +
                            "Length  : " + Long.toString(Math.round(r.length_ft)) + " ft\n" +
                            "Heading : " + Long.toString(Math.round(r.le_heading_degT)) + " : " + Long.toString(Math.round(r.he_heading_degT)) + "\n" +
                            "Displaced threshold: " + Long.toString(r.le_displaced_threshold_ft) + " ft : "  + Long.toString(r.he_displaced_threshold_ft) + " ft\n" +
                            "Suface  : " + r.surface + "\n" +
                            "----------------------------------------------\n" ;

                }
            }
        }

        return info;
    }

    public String getFrequenciesInfo()
    {
        String info = "No frequencies information.";
        if (this.frequencies != null)
        {
            if (this.frequencies.size()>0)
            {
                info = "Frequencies......................\n";
                for (Frequency f : this.frequencies)
                {
                    info = info + Double.toString(f.frequency_mhz) + " MHz : " + f.description + "\n";
                }
            }
        }

        return info;
    }
}
