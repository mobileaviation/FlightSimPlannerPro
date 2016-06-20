package com.mobileaviationtools.flightsimplannerpro.Route;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.Log;

import com.mobileaviationtools.flightsimplannerpro.R;

import java.util.ArrayList;

import us.ba3.me.ConvertPointCallback;
import us.ba3.me.Location;
import us.ba3.me.MapView;
import us.ba3.me.VectorMapInfo;
import us.ba3.me.markers.DynamicMarker;
import us.ba3.me.markers.DynamicMarkerMapDelegate;
import us.ba3.me.markers.DynamicMarkerMapInfo;
import us.ba3.me.markers.MarkerInfo;
import us.ba3.me.markers.MarkerMapDelegate;
import us.ba3.me.markers.MarkerMapInfo;
import us.ba3.me.styles.LineStyle;

/**
 * Created by Rob Verhoef on 17-6-2016.
 */
public class RouteTest implements DynamicMarkerMapDelegate {
    public RouteTest(Context context, MapView mapView)
    {
        this.context = context;
        this.mapView = mapView;
    }

    private Context context;
    private MapView mapView;

    public void placeRouteOnMap()
    {
        VectorMapInfo vectorMapInfo = new VectorMapInfo();
        vectorMapInfo.name = "route";
        vectorMapInfo.zOrder = 10;
        vectorMapInfo.alpha = 0.75f;
        mapView.addMapUsingMapInfo(vectorMapInfo);

        //Create a style for the route line
        LineStyle lineStyle = new LineStyle();
        lineStyle.outlineColor = Color.BLACK;
        lineStyle.outlineWidth = 3;
        lineStyle.strokeColor = Color.RED;
        lineStyle.strokeWidth = 9;

        //Get the route points
        Location[] routePoints = createRoutePoints();

        //Add the line to the vector layer
        mapView.addLineToVectorMap("route", routePoints, lineStyle);

        //Add markers
        this.addMarkers(routePoints);

    }

    private void addMarkers(Location[] routePoints)
    {
        DynamicMarkerMapInfo mapInfo = new DynamicMarkerMapInfo();
        mapInfo.name = "Markers";
        mapInfo.zOrder = 11;
        mapInfo.hitTestingEnabled = true;
        mapInfo.delegate = this;
        mapView.addMapUsingMapInfo(mapInfo);

        //Add a markers
        DynamicMarker startPoint = new DynamicMarker();
        startPoint.name = "startPoint";
        startPoint.setImage(getBitmapFromDrawable(R.drawable.greendot), false);
        startPoint.anchorPoint = new PointF(16,32);
        startPoint.location = routePoints[0];
        mapView.addDynamicMarkerToMap("Markers", startPoint);

        DynamicMarker endPoint = new DynamicMarker();
        endPoint.name = "endPoint";
        endPoint.setImage(getBitmapFromDrawable(R.drawable.bluedot), false);
        endPoint.anchorPoint = new PointF(16,32);
        endPoint.location = routePoints[routePoints.length-1];
        mapView.addDynamicMarkerToMap("Markers", endPoint);

        
    }

    private Bitmap getBitmapFromDrawable(Integer name)
    {
        return BitmapFactory.decodeResource(context.getResources(), name);
    }

    private Location[] createRoutePoints(){
        ArrayList<Location> locations = new ArrayList<Location>();
        locations.add(new Location(52.472409,4.320374));
        locations.add(new Location(52.7399285, 7.182312));
        //locations.add(new Location(35.77431930112859,-78.64204765649093));
        //locations.add(new Location(35.77567608136691,-78.64196220184964));
        //locations.add(new Location(35.77700395100487,-78.64187508617212));
        //locations.add(new Location(35.77832583463119,-78.64183947388348));
        //locations.add(new Location(35.77827906552807,-78.64020036607604));
        //locations.add(new Location(35.77818133831267,-78.63823940725123));
        //locations.add(new Location(35.7781182250745,-78.63659407693116));
        //locations.add(new Location(35.77684002315468,-78.63668612449102));
        //locations.add(new Location(35.77552738790911,-78.63672620342922));
        Location[] locationArray = locations.toArray(new Location[locations.size()]);
        return locationArray;
    }




    @Override
    public void tapOnMarker(String mapName, final String markerName, final PointF screenPoint, final PointF markerPoint) {

        mapView.getLocationForPoint(new PointF(screenPoint.x, screenPoint.y), new ConvertPointCallback() {
            @Override
            public void convertComplete(Location location) {
                Log.w("WorldVectorLabelsTest","Marker was tapped on" +
                        " Name:" + markerName +
                        " location:" + screenPoint.x + "," + screenPoint.y +
                        " markerPoint:" + markerPoint.x + "," + markerPoint.y +
                        " location:" + location.longitude + "," + location.latitude);
            }
        });


    }
}
