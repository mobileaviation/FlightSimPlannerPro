package com.mobileaviationtools.flightsimplannerpro;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.mobileaviationtools.flightsimplannerpro.Database.MarkerProperties;
import com.mobileaviationtools.flightsimplannerpro.Route.RouteVisuals;
import com.mobileaviationtools.flightsimplannerpro.TileWorkers.AirportsTileWorker;
import com.mobileaviationtools.flightsimplannerpro.TileWorkers.TileProviderFormats;
import com.mobileaviationtools.flightsimplannerpro.TileWorkers.WmsTileWorker;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

//import us.ba3.me.;
import us.ba3.me.Location;
import us.ba3.me.Location3D;
import us.ba3.me.LocationReceiver;
import us.ba3.me.MapLoadingStrategy;
import us.ba3.me.MapView;
import us.ba3.me.virtualmaps.TileFactory;
import us.ba3.me.virtualmaps.VirtualMapInfo;

/**
 * Created by Rob Verhoef on 20-6-2016.
 */
public class MyMapView extends MapView {
    private String TAG = "MyMapView";
    public MyMapView(Context context) {
        super(context);
    }

    public void Init(RouteVisuals route)
    {
        this.routeVisuals = route;
        placingRoutePoint = false;
        movingRoutePoint = false;
        scrolling = false;
    }

    private RouteVisuals routeVisuals;
    private Boolean placingRoutePoint;
    private Boolean movingRoutePoint;
    private Boolean scrolling;
    private Integer PID;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        Log.i(TAG, "onSurfaceCreated");
//        if (routeVisuals != null)
//		{
//			routeVisuals.drawRoute();
//		}
    }



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
                routeVisuals.UnSelectWaypoint();
                placingRoutePoint = false;
                movingRoutePoint = false;
                break;
            }

        }

//        super.getLocationForPoint(new PointF(event.getX(), event.getY()), new LocationReceiver() {
//            @Override
//            public void receiveLocation(Location location) {
//
//            }
//        });
        super.getLocationForPoint(new PointF(event.getX(), event.getY()), new LocationReceiver(){
            @Override
            public void receiveLocation(Location loc) {
                //Log.w("OnTouchEvent", "lon:" + loc.longitude + " lat:" + loc.latitude);
                if (movingRoutePoint)
                    routeVisuals.dragSelectedWaypoint(loc);

            }});
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
        Log.i("onScroll", "Action arg0: " + arg0.getAction() + " arg1: "  +
                arg1.getAction());
        Location3D location3D = this.getLocation3D();
        Log.i("onScroll", "3D: lat:" + location3D.latitude + "  lon:" + location3D.longitude + "  alt:" + location3D.altitude);
        super.getLocationForPoint(new PointF(arg1.getX(), arg1.getY()), new LocationReceiver() {
            @Override
            public void receiveLocation (Location location) {
                scrolling = true;
                // Get bounds of current screen
                //MyMapView.this.get
            }
        });
        return super.onScroll(arg0, arg1, arg2, arg3);
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        Location3D location3D = this.getLocation3D();
        Log.i("onScaleEnd", "3D: lat:" + location3D.latitude + "  lon:" + location3D.longitude + "  alt:" + location3D.altitude);
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
                if (routeVisuals.selectedWaypoint != null) {
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

//        super.getLocationForPoint(new PointF(event.getX(), event.getY()), new ConvertPointCallback(){
//            @Override
//            public void convertComplete(Location loc) {
//                //Log.w("onLongPress", "lon:" + loc.longitude + " lat:" + loc.latitude);
//
//                if (placingRoutePoint) {
//                    String name = loc.longitude + "," + loc.latitude;
//                    routeVisuals.AddWaypoint(name, loc);
//
//                    //placingRoutePoint = false;
//                    movingRoutePoint = true;
//                    // insert the new point
//
//                }
//
//
//            }});

        super.onLongPress(event);
    }

    public void AddMappyMap()
    {
        this.addStreamingRasterMap("Mappy",
        //"http://otile1.mqcdn.com/tiles/1.0.0/map/{z}/{x}/{y}.jpg",
                "https://map1.mappy.net/map/1.0/slab/standard/256/{z}/{x}/{y}",
        "",
        "",
        "", 		//Subdomains
        20,			//Max Level
        200,			//zOrder
        3,			//Number of simultaneous downloads
        true,		//Use cache
        false		//No alpha
        );
    }

    public void AddSkylineAirspacesMap()
    {
//        this.addInternetMap("SkylinesAirspaces",
//        "https://skylines.aero/mapproxy/tiles/1.0.0/airspace/{z}/{x}/{y}.png",
//        "",
//        20,
//        5,
//        3,
//        true,
//        true);
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
        chartBundlemapInfo.zOrder = 180;
        chartBundlemapInfo.maxLevel = 20;
        //chartBundlemapInfo.isSphericalMercator = false;
        chartBundlemapInfo.compressTextures = true;
        chartBundlemapInfo.tileProvider = chartBundleFactory;
        //chartBundlemapInfo.setTileProvider(chartBundleFactory);
        this.addMapUsingMapInfo(chartBundlemapInfo);
    }

    private Boolean added = false;
    public void AddMarkersMap(MarkerProperties markerProperties, Integer PID)
    {
        this.PID = PID;
        added = true;
        String mapmame = "markersTest";
        TileFactory markersMapFactory = new TileFactory(this);

//        DynamicMarkerMapInfo airportMarkerMap = new DynamicMarkerMapInfo();
//        airportMarkerMap.name = "airportMarkerMap";
//        airportMarkerMap.zOrder = 200;
//        airportMarkerMap.mapLoadingStrategy = MapLoadingStrategy.kHighestDetailOnly;
//        this.addMapUsingMapInfo(airportMarkerMap);

        AirportsTileWorker markersTileWorker = new AirportsTileWorker(this, getContext(), "airportMarkerMap", markerProperties, PID);

        markersMapFactory.addWorker(markersTileWorker);

        VirtualMapInfo markersTestMapInfo = new VirtualMapInfo();
        markersTestMapInfo.name = mapmame;
        markersTestMapInfo.zOrder = 102;
        markersTestMapInfo.maxLevel = 20;
        markersTestMapInfo.alpha = 0;
        //markersTestMapInfo.isSphericalMercator = false;
        markersTestMapInfo.mapLoadingStrategy = MapLoadingStrategy.kHighestDetailOnly;
        //markersTestMapInfo.setTileProvider(markersMapFactory);
        markersTestMapInfo.tileProvider = markersMapFactory;
        this.addMapUsingMapInfo(markersTestMapInfo);

        Log.i(TAG, "Markersmap added!!");

    }

    public void AddMappyMapWMS()
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
        mappymapInfo.zOrder = 1;
        mappymapInfo.maxLevel = 20;
        //mappymapInfo.isSphericalMercator = false;
        mappymapInfo.mapLoadingStrategy = MapLoadingStrategy.kHighestDetailOnly;
        //mappymapInfo.setTileProvider(mappyFactory);
        mappymapInfo.tileProvider = mappyFactory;
        this.addMapUsingMapInfo(mappymapInfo);
    }

    private String getBaseDir()
    {
        String p = this.getContext().getFilesDir().getPath() + "/";
        return p;
    }
}
