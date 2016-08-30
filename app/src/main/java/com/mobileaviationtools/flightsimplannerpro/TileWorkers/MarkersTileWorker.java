package com.mobileaviationtools.flightsimplannerpro.TileWorkers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.support.annotation.IntegerRes;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.mobileaviationtools.flightsimplannerpro.Airports.Airport;
import com.mobileaviationtools.flightsimplannerpro.Database.AirportDataSource;
import com.mobileaviationtools.flightsimplannerpro.Database.MarkerProperties;
import com.mobileaviationtools.flightsimplannerpro.Helpers;
import com.mobileaviationtools.flightsimplannerpro.MyMapView;
import com.mobileaviationtools.flightsimplannerpro.R;

import java.util.HashMap;

import us.ba3.me.Location;
import us.ba3.me.MapLoadingStrategy;
import us.ba3.me.markers.DynamicMarker;
import us.ba3.me.markers.DynamicMarkerMapInfo;
import us.ba3.me.virtualmaps.InternetTileProvider;
import us.ba3.me.virtualmaps.TileProviderRequest;
import us.ba3.me.virtualmaps.TileProviderResponse;
import us.ba3.me.virtualmaps.TileWorker;

/**
 * Created by Rob Verhoef on 25-8-2016.
 */
public class MarkersTileWorker extends TileWorker {
    public MarkersTileWorker(MyMapView mapView, Context context, String airportMapName, MarkerProperties markerProperties) {
        super();

        this.context = context;
        this.airportMapName = airportMapName;
        this.mapView = mapView;
        curAirports = new HashMap<Integer, Airport>();
        this.markerProperties = markerProperties;
        Log.i("MarkersTileWorker", "MarkersTileWorker created");
    }

    private Context context;
    private MyMapView mapView;
    private String airportMapName;
    private HashMap<Integer, Airport> curAirports;
    private MarkerProperties markerProperties;
    private Bitmap blueDot;

    @Override
    public void doWork(TileProviderRequest request) {

//        Double zd = (360/(request.bounds.max.longitude - request.bounds.min.longitude));
//        Double z1 = Math.log(zd) / Math.log(2);
//        Double n = Math.pow(2, z1);
//        Double x = ((request.bounds.min.longitude + 180) / 360 * n) ;
//        Double lat_rad = Math.toRadians(request.bounds.min.latitude);
//        Double y = (1 - Math.log(Math.tan(lat_rad) + (1 / Math.cos(lat_rad))) / Math.PI ) / 2 * n;
//
//        Long zi = Math.round(z1);
//        Long xi = Math.round(x);
//        Long yi = Math.round(y-1);
//
//        Log.i("MarkersTileWorker", "Zoom: " + yi + " X: " + xi + " Y: " + yi);

        String mapname = Double.toString(request.bounds.min.latitude) + Double.toString(request.bounds.min.longitude)+"Airports";
        DynamicMarkerMapInfo airportMarkerMap = new DynamicMarkerMapInfo();
        airportMarkerMap.name = mapname;
        airportMarkerMap.zOrder = 200;
        //airportMarkerMap.mapLoadingStrategy = MapLoadingStrategy.kHighestDetailOnly;
        mapView.addMapUsingMapInfo(airportMarkerMap);

        HashMap<Integer, Airport> airports = getAirports(request);
        blueDot = BitmapFactory.decodeResource(context.getResources(), R.drawable.bluedot);

        for (Airport airport: airports.values())
        {
            DynamicMarker airportMarker = new DynamicMarker();
            airportMarker.name = airport.ident;
            airportMarker.anchorPoint = new PointF(Helpers.convertDpToPixel(15, context),
                    Helpers.convertDpToPixel(15, context));
            airportMarker.location = new Location(airport.latitude_deg, airport.longitude_deg);
            airportMarker.setImage(blueDot, false);
            //airportMarker.setImage(airport.GetIcon(0, airport.ident), false);
            mapView.addDynamicMarkerToMap(mapname,  airportMarker);
            //mapView.showDynamicMarker(airportMapName, airport.ident);

        }

        request.responseType = TileProviderResponse.kTileResponseRenderImage;
        request.isOpaque = true;

        //super.doWork(request);
    }

    private LatLng getLatLng(Location location)
    {
        return new LatLng(location.latitude, location.longitude);
    }

    private HashMap<Integer, Airport> getAirports(TileProviderRequest request)
    {
        HashMap<Integer, Airport> airports;
        Float zoom = 15f;

        AirportDataSource airportDataSource = new AirportDataSource(context);

        airportDataSource.open(0);
        LatLngBounds bounds = new LatLngBounds(getLatLng(request.bounds.min), getLatLng(request.bounds.max));
        airports = (HashMap<Integer, Airport>) airportDataSource.getAirportsByCoordinateAndZoomLevel(bounds, zoom, curAirports,  markerProperties);
        airportDataSource.close();

        return airports;
    }
}
