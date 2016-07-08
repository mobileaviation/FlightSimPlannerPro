package com.mobileaviationtools.flightsimplannerpro;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.mobileaviationtools.flightsimplannerpro.Route.Route;
import com.mobileaviationtools.flightsimplannerpro.Route.RouteVisuals;
import com.mobileaviationtools.flightsimplannerpro.Route.Waypoint;
import com.mobileaviationtools.flightsimplannerpro.TileWorkers.TileProviderFormats;
import com.mobileaviationtools.flightsimplannerpro.TileWorkers.WmsTileWorker;

import us.ba3.me.ConvertPointCallback;
import us.ba3.me.Location;
import us.ba3.me.MapLoadingStrategy;
import us.ba3.me.MapView;
import us.ba3.me.virtualmaps.TileFactory;
import us.ba3.me.virtualmaps.VirtualMapInfo;

/**
 * Created by Rob Verhoef on 20-6-2016.
 */
public class MyMapView extends MapView {
    public MyMapView(Context context) {
        super(context);
    }

    public void Init(RouteVisuals route)
    {
        this.route = route;
        placingRoutePoint = false;
        movingRoutePoint = false;
    }

    private RouteVisuals route;

    private Boolean placingRoutePoint;
    private Boolean movingRoutePoint;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
            {
                //Log.i("OnTouchEvent", "This is a OnTouch (ACTION_DOWN) event...");
                //route.UnSelectWaypoint();
                break;
            }
            case MotionEvent.ACTION_MOVE:
            {
                //Log.i("OnTouchEvent", "This is a OnTouch (ACTION_MOVE) event...");
                if (movingRoutePoint) {
                    // update the route with the new location..
                    //event.setAction(MotionEvent.ACTION_CANCEL);
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                //Log.i("OnTouchEvent", "This is a OnTouch (ACTION_UP) event...");
                route.UnSelectWaypoint();
                placingRoutePoint = false;
                movingRoutePoint = false;
                break;
            }

        }

        super.getLocationForPoint(new PointF(event.getX(), event.getY()), new ConvertPointCallback(){
            @Override
            public void convertComplete(Location loc) {
                //Log.w("OnTouchEvent", "lon:" + loc.longitude + " lat:" + loc.latitude);
                if (movingRoutePoint)
                    route.dragSelectedWaypoint(loc);

            }});
        return super.onTouchEvent(event);
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        Log.i("onScaleEnd", "ScaleX: " + this.getLocation3D().altitude);
        super.onScale(detector);
    }

    @Override
    public void onLongPress(MotionEvent event) {

        // Place a new Route Point
        //
        switch (event.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
            {
                //Log.i("onLongPress", "This is a onLongPress (ACTION_DOWN) event...");
                if (route.selectedWaypoint != null) {
                    movingRoutePoint = true;
                    placingRoutePoint = false;
                }
                else {
                    placingRoutePoint = true;
                    movingRoutePoint = false;
                }
                break;
            }
        }

        super.getLocationForPoint(new PointF(event.getX(), event.getY()), new ConvertPointCallback(){
            @Override
            public void convertComplete(Location loc) {
                //Log.w("onLongPress", "lon:" + loc.longitude + " lat:" + loc.latitude);

                if (placingRoutePoint) {
                    String name = loc.longitude + "," + loc.latitude;
                    route.AddWaypoint(name, loc);

                    //placingRoutePoint = false;
                    movingRoutePoint = true;
                    // insert the new point

                }


            }});

        super.onLongPress(event);
    }

    public void AddMapquestMap()
    {
        this.addInternetMap("MapQuest Aerial",
        "http://otile1.mqcdn.com/tiles/1.0.0/map/{z}/{x}/{y}.jpg",
        "", 		//Subdomains
        20,			//Max Level
        2,			//zOrder
        3,			//Number of simultaneous downloads
        true,		//Use cache
        true		//No alpha
        );
    }

    public void AddSkylineAirspacesMap()
    {
        this.addInternetMap("SkylinesAirspaces",
        "https://skylines.aero/mapproxy/tiles/1.0.0/airspace/{z}/{x}/{y}.png",
        "",
        20,
        5,
        3,
        true,
        true);
    }

    public void AddFAASectionalsMap()
    {
        String mapname = "FAA_sec_3857";
        TileFactory chartBundleFactory = new TileFactory(this);
        WmsTileWorker chartBundleWorker = new WmsTileWorker(
                TileProviderFormats.CHARTBUNDLE_XYZ_FORMAT,
                "",
                TileProviderFormats.chartBundleLayer.sec_3857.toString(),
                mapname,
                getBaseDir(),
                getContext());

        chartBundleFactory.addWorker(chartBundleWorker);

        VirtualMapInfo chartBundlemapInfo = new VirtualMapInfo();
        chartBundlemapInfo.name = mapname;
        chartBundlemapInfo.zOrder = 7;
        chartBundlemapInfo.maxLevel = 20;
        chartBundlemapInfo.isSphericalMercator = false;
        chartBundlemapInfo.compressTextures = true;
        chartBundlemapInfo.setTileProvider(chartBundleFactory);
        this.addMapUsingMapInfo(chartBundlemapInfo);
    }

    public void AddMappyMap()
    {
        String mapname = "Mappy";
        TileFactory mappyFactory = new TileFactory(this);
        WmsTileWorker mappyWorker = new WmsTileWorker(
                TileProviderFormats.MAPPY_FORMAT,
                "",
                "",
                mapname,
                getBaseDir(),
                getContext());

        mappyFactory.addWorker(mappyWorker);

        VirtualMapInfo mappymapInfo = new VirtualMapInfo();
        mappymapInfo.name = mapname;
        mappymapInfo.zOrder = 7;
        mappymapInfo.maxLevel = 20;
        mappymapInfo.isSphericalMercator = false;
        mappymapInfo.mapLoadingStrategy = MapLoadingStrategy.kHighestDetailOnly;
        mappymapInfo.setTileProvider(mappyFactory);
        this.addMapUsingMapInfo(mappymapInfo);
    }

    private String getBaseDir()
    {
        String p = this.getContext().getFilesDir().getPath() + "/";
        return p;
    }
}
